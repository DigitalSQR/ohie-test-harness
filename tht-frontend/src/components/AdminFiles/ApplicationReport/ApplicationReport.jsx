import React, { useEffect, useState } from "react";
import Header from "../../CommonFiles/Header/Header";
import "./application-report.scss";
import Footer from "../../CommonFiles/Footer/Footer";
import { useNavigate, useParams } from "react-router-dom";
import html2pdf from "html2pdf.js";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import { TestResultAPI } from "../../../api/TestResultAPI";
import { GradeAPI } from "../../../api/GradeAPI";
import { UserAPI } from "../../../api/UserAPI";
import { formatDate } from "../../../utils/utils.js";
import { useLoader } from "../../loader/LoaderContext";
import { USER_ROLES } from "../../../constants/role_constants";
import { store } from "../../../store/store";

import { Table, Tooltip } from "antd";
import {
  TestcaseResultStateConstants,
  StateClasses,
} from "../../../constants/testcaseResult_constants";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";

/**
 * ApplicationReport Component:
 * This component displays the report of a specific test application,
 * including test case results, user details, and grade information.
 * It fetches data from APIs and renders the report along with an option to generate a PDF.
 */

const ApplicationReport = () => {
  const { testRequestId } = useParams();
  const navigate = useNavigate();
  const [
    requiredTestRequestTestcaseResult,
    setRequiredTestRequestTestcaseResult,
  ] = useState({});
  const [
    recommendedTestRequestTestcaseResult,
    setRecommendedTestRequestTestcaseResult,
  ] = useState({});
  const [requiredTestcaseResults, setRequiredTestcaseResults] = useState({});
  const [recommendedTestcaseResults, setRecommendedTestcaseResults] = useState(
    {}
  );
  const [componentNames, setComponentNames] = useState([]);
  const [passReqCompNames, setPassReqCompNames] = useState([]);
  const [testRequest, setTestRequest] = useState();
  const [user, setUser] = useState();
  const [rangedGradeData, setRangedGradeData] = useState();
  const [columns, setColumns] = useState([]);
  const [loggedInUser, setLoggedInUser] = useState();

  //UseEffect to call fetchTestCaseResultData, fetchTestCaseRequestData and fetchAllGrades functions when the component initially loads
  useEffect(() => {
    setLoggedInUser(store.getState().userInfoSlice);
    fetchTestCaseResultData();
    fetchTestCaseRequestData();
    fetchAllGrades();
  }, []);

  //Function to fetch the user details corresponding to the userId passed as a parameter
  const fetchUserDetails = async (userId) => {
    const response = await UserAPI.getUserById(userId);
    setUser({ name: response.name, email: response.email, companyName: response.companyName });
  };

  //Function used to generate the PDF of the report
  const generatePDF = () => {  
    const element = document.getElementById("reportprint");
  
    const pdfConfig = {
        margin: [13, 5, 10, 5], // Top, Left, Bottom, Right
        filename: `${testRequest?.name}_report.pdf`,
        image: { type: "jpeg", quality: 1.0 },
        html2canvas: { scale: 3, letterRendering: true },
        jsPDF: {
            unit: "mm",
            format: "a3",
            orientation: "portrait",
            compress: true,
        },
    };
  
    html2pdf()
        .from(element)
        .set(pdfConfig)
        .toPdf()
        .get("pdf")
        .then(function (pdf) {
            pdf.filename = pdfConfig.filename;

            var totalPages = pdf.internal.getNumberOfPages();
            for (let i = 1; i <= totalPages; i++) {
                pdf.setPage(i);

                // Add header text to all pages except the first one
                if (i > 1) {
                    // Draw header strip
                    pdf.setFillColor("#f6f9fc"); // White shade
                    pdf.rect(0, 0, pdf.internal.pageSize.getWidth(), 10, "F");
                    
                    pdf.setDrawColor("#F0F0F0"); // Very light black color (RGB 50, 50, 50)
                    pdf.line(5, 10, pdf.internal.pageSize.getWidth() - 5, 10); // Draw a very light black line at the bottom of the header rectangle

                    pdf.setFont("helvetica", "bold").setFontSize(12).setTextColor('black'); // White color for header text
                    const headerTextLeft = `Assessee : ${user?.name}`; // Assessee name
                    const headerTextCenter = `Application: ${testRequest?.name}`; // Application name
                    const headerTextRight = `Company: ${user?.companyName}`; // Company name
                    
                    // Calculate the width of each header text
                    const headerTextWidthCenter = pdf.getStringUnitWidth(headerTextCenter) * pdf.internal.getFontSize() / pdf.internal.scaleFactor;
                    const headerTextWidthRight = pdf.getStringUnitWidth(headerTextRight) * pdf.internal.getFontSize() / pdf.internal.scaleFactor;
  
                    // Calculate the positions for each header text
                    const marginLeft = 5;
                    const marginTop = 6; // Adjust the margin to position the text within the header strip
                    const marginRight = pdf.internal.pageSize.getWidth() - headerTextWidthRight - 5;
  
                    // Add header text
                    pdf.text(headerTextLeft, marginLeft, marginTop);
                    pdf.text(headerTextCenter, (pdf.internal.pageSize.getWidth() - headerTextWidthCenter) / 2, marginTop);
                    pdf.text(headerTextRight, marginRight, marginTop);
                }
  
                // Add page number text
                pdf.setFont("helvetica").setFontSize(10).setTextColor(100);
                pdf.text(
                    "Page " + i + " of " + totalPages,
                    pdf.internal.pageSize.getWidth() / 2.3,
                    pdf.internal.pageSize.getHeight() - 3
                );
            }
            pdf.save(pdfConfig.filename); // Save the PDF after all modifications
        })
        .catch(function (error) {
            console.error("Error generating PDF:", error);
        });
};


  //Function to fetch all grades
  const fetchAllGrades = () => {
    GradeAPI.getAllGrades()
      .then((res) => {
        const columns = res.data.map((item) => ({
          title: `Grade ${item.grade}`,
          dataIndex: item.grade,
          key: item.grade,
          width: 150,
          align: "center",
          render: (text, record) => rangedGradeData[item.grade],
        }));

        const rangedGradeData = {};
        res.data.forEach((item, index) => {
          const endPercentage =
            index === 0 ? 100 : res.data[index - 1].percentage - 1;
          rangedGradeData[
            item.grade
          ] = `${item.percentage}% - ${endPercentage}%`;
        });

        setColumns(columns);
        setRangedGradeData(rangedGradeData);
      })
      .catch((err) => {});
  };

  //Function to fetch the testcase request data for a given test request ID
  const fetchTestCaseRequestData = async () => {
    const response = await TestRequestAPI.getTestRequestsById(testRequestId);
    fetchUserDetails(response.assesseeId);
    setTestRequest(response);
  };

  //Function to handle navigation on hitting the "back" button
  const goBackOrRedirect = () => {
    if (navigate.length > 3) {
      navigate(-1);
    } else {
      if (
        loggedInUser?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) ||
        loggedInUser?.roleIds?.includes(USER_ROLES.ROLE_ID_TESTER)
      ) {
        navigate("/applications");
      } else {
        navigate("/testing-requests");
      }
    }
  };

  //Function to fetch test case result data
  const fetchTestCaseResultData = async () => {
    try {
      // Fetch required and recommended test case results concurrently
      const [requiredResponse, recommendedResponse] = await Promise.all([
        TestResultAPI.getMultipleTestcaseResultStatus({
          testRequestId,
          required: true,
        }),
        TestResultAPI.getMultipleTestcaseResultStatus({
          testRequestId,
          recommended: true,
        }),
      ]);
      // Initialize arrays to store required and recommended test case results
      const requiredTestcaseResults = [];
      const recommendedTestcaseResults = [];
      // Initialize a set to store unique component names
      const compNames = new Set();
      // Initialize an array to store passed required component names
      const passedRequiredComponentNames = [];
      for (let testcaseResult of requiredResponse) {
        if (
          testcaseResult.refObjUri === RefObjUriConstants.TESTREQUEST_REFOBJURI
        ) {
          setRequiredTestRequestTestcaseResult(testcaseResult);
        } else if (
          testcaseResult.refObjUri === RefObjUriConstants.COMPONENT_REFOBJURI
        ) {
          compNames.add(testcaseResult.name);
          if (!!testcaseResult.success) {
            passedRequiredComponentNames.push(testcaseResult.name);
          }
          requiredTestcaseResults.push({
            ...testcaseResult,
            specifications: [],
          });
        } else if (
          testcaseResult.refObjUri ===
          RefObjUriConstants.SPECIFICATION_REFOBJURI
        ) {
          requiredTestcaseResults[
            requiredTestcaseResults.length - 1
          ].specifications.push(testcaseResult);
        }
      }
      for (let testcaseResult of recommendedResponse) {
        if (
          testcaseResult.refObjUri === RefObjUriConstants.TESTREQUEST_REFOBJURI
        ) {
          setRecommendedTestRequestTestcaseResult(testcaseResult);
        } else if (
          testcaseResult.refObjUri === RefObjUriConstants.COMPONENT_REFOBJURI
        ) {
          compNames.add(testcaseResult.name);
          recommendedTestcaseResults.push({
            ...testcaseResult,
            specifications: [],
          });
        } else if (
          testcaseResult.refObjUri ===
          RefObjUriConstants.SPECIFICATION_REFOBJURI
        ) {
          recommendedTestcaseResults[
            recommendedTestcaseResults.length - 1
          ].specifications.push(testcaseResult);
        }
      }
      setRequiredTestcaseResults(requiredTestcaseResults);
      setRecommendedTestcaseResults(recommendedTestcaseResults);
      setPassReqCompNames(passedRequiredComponentNames);
      setComponentNames([...compNames]);
    } catch (error) {
      console.log(error);
    }
  };
  return (
    <div>
      <Header />
      <div id="report">
        <div className="application-report">
          <div className="container">
            <div className="col-12">
              <div className="d-flex align-items-center justify-content-between flex-wrap mb-4">
                <h2>
                  Application Report - <span>{testRequest?.name}</span>{" "}
                </h2>
                <div>
                  <button
                    onClick={() => {
                      goBackOrRedirect();
                    }}
                    className="btn btn-link  py-2 font-size-14"
                  >
                    BACK
                  </button>
                  <button
                    className="btn btn-primary btn-blue py-2 font-size-14"
                    onClick={generatePDF}
                  >
                    DOWNLOAD
                  </button>
                </div>
              </div>
              <div id="reportprint">
                <div id="report">
                  <div className="report-details">
                    <div className="container-fluid">
                      <div className="row">
                        <div className="col-lg-3 col-md-6 col-12 mb-4">
                          <p>
                            Assessee Name:<span>{user?.name}</span>
                          </p>
                        </div>
                        <div className="col-lg-3 col-md-6 col-12 mb-4">
                          <p>
                            Email:
                            <span>{user?.email}</span>
                          </p>
                        </div>
                        <div className="col-lg-3 col-md-6 col-12 mb-4">
                          <p>
                            Company name:<span>{user?.companyName}</span>
                          </p>
                        </div>
                        <div className="col-lg-3 col-md-6 col-12 mb-4">
                          <p>
                            Application Date:
                            <span>
                              {formatDate(testRequest?.meta?.createdAt)}
                            </span>
                          </p>
                        </div>
                        <div className="col-12">
                          <p>Components applied for verification:</p>
                          <div>
                            {componentNames?.map((item) => (
                              <span key={item} className="comp-badge">
                                {item}
                              </span>
                            ))}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div className="result-box mb-4">
                    <div className="container-fluid">
                      <div className="row">
                        <div className="col-lg-1 col-md-2 col-12 title">
                          <p>Result:</p>
                        </div>
                        <div className="col-lg-11 col-md-10 col-12 details">
                          <p>
                            The <b> {testRequest?.name} </b> system {" "}
                            {requiredTestcaseResults?.length > 0 && (
                              <>
                                {passReqCompNames?.length > 0 ? (
                                  <>
                                    <b>is compliant</b> to{" "}
                                    {passReqCompNames?.map((item, index) => (
                                      <span key={index}>
                                        {index === passReqCompNames.length - 1 ? (
                                          <>
                                            <b>{item}</b>
                                            <span> </span>
                                          </>
                                        ) : (
                                          <>
                                            <b>{item}</b>
                                            <span>{", "}</span>
                                          </>
                                        )}
                                      </span>
                                    ))}
                                  </>
                                ) : (
                                  <>
                                    <b>is not compliant</b> to any component{" "}
                                  </>
                                )}
                              </>
                            )}
                            {requiredTestcaseResults?.length>0 && recommendedTestcaseResults.length>0 && (
                              <>and </>
                            )}
                            {recommendedTestcaseResults?.length > 0 && (
                              <>
                                has been awarded a{" "}
                                <b>
                                  Conformance Grade{" "}
                                  {!!recommendedTestRequestTestcaseResult.grade
                                    ? recommendedTestRequestTestcaseResult.grade
                                    : "-"}
                                </b>
                              </>
                            )}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>

                  {requiredTestcaseResults.length > 0 && (
                    <div className="compliance-card">
                      <div className="c-card-header">
                        Compliance
                        <div className="header-description">
                          Compliance refers to whether the system meets the{" "}
                          <b>required</b> specifications of the applied
                          components, determining its adherence to OpenHIE
                          standards.
                        </div>
                      </div>
                      <div className="c-card-body">
                        <div className="table-responsive">
                          <table className="data-table capitalize-words">
                            <thead>
                              <tr>
                                <th>COMPONENTS</th>
                                <th>SPECIFICATIONS</th>
                                <th>RESULTS</th>
                              </tr>
                            </thead>
                            <tbody>
                              {Object.values(requiredTestcaseResults).map(
                                (component, index) =>
                                  !!component &&
                                  !!component.specifications[0] && (
                                    <React.Fragment key={component.id}>
                                      <tr
                                        className={
                                          index % 2 === 0 ? "even-row" : "odd-row"
                                        }
                                      >
                                        <td>
                                          <b>{component.name}</b>
                                        </td>
                                        <td>
                                          {component.specifications[0]?.name}
                                        </td>
                                        <td
                                          className={
                                            component.specifications[0].state ===
                                              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                              ? !!component.specifications[0]
                                                .success
                                                ? StateClasses[
                                                  "testcase.result.status.finished.pass"
                                                ].cardClass
                                                : StateClasses[
                                                  "testcase.result.status.finished.fail"
                                                ].cardClass
                                              : StateClasses[
                                                component.specifications[0]
                                                  .state
                                              ].cardClass
                                          }
                                        >
                                          {
                                            <>
                                              {component.specifications[0]
                                                .state ===
                                                TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                ? !!component.specifications[0]
                                                  .success
                                                  ? StateClasses[
                                                    "testcase.result.status.finished.pass"
                                                  ].text
                                                  : StateClasses[
                                                    "testcase.result.status.finished.fail"
                                                  ].text
                                                : StateClasses[
                                                  component.specifications[0]
                                                    .state
                                                ].text}{" "}
                                              <i
                                                className={
                                                  component.specifications[0]
                                                    .state ===
                                                    TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                    ? !!component
                                                      .specifications[0].success
                                                      ? StateClasses[
                                                        "testcase.result.status.finished.pass"
                                                      ].iconClass
                                                      : StateClasses[
                                                        "testcase.result.status.finished.fail"
                                                      ].iconClass
                                                    : StateClasses[
                                                      component
                                                        .specifications[0].state
                                                    ].iconClass
                                                }
                                              ></i>
                                            </>
                                          }
                                        </td>
                                      </tr>
                                      {component.specifications
                                        .slice(1)
                                        .map((specification, specIndex) => (
                                          <tr
                                            key={specification.id}
                                            className={
                                              specIndex % 2 === 0
                                                ? "even-row"
                                                : "odd-row"
                                            }
                                          >
                                            <td></td>
                                            <td>{specification.name}</td>
                                            <td
                                              className={
                                                specification.state ===
                                                  TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                  ? !!specification.success
                                                    ? StateClasses[
                                                      "testcase.result.status.finished.pass"
                                                    ].cardClass
                                                    : StateClasses[
                                                      "testcase.result.status.finished.fail"
                                                    ].cardClass
                                                  : StateClasses[
                                                    specification.state
                                                  ].cardClass
                                              }
                                            >
                                              {
                                                <>
                                                  {specification.state ===
                                                    TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                    ? !!specification.success
                                                      ? StateClasses[
                                                        "testcase.result.status.finished.pass"
                                                      ].text
                                                      : StateClasses[
                                                        "testcase.result.status.finished.fail"
                                                      ].text
                                                    : StateClasses[
                                                      specification.state
                                                    ].text}{" "}
                                                  <i
                                                    className={
                                                      specification.state ===
                                                        TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                        ? !!specification.success
                                                          ? StateClasses[
                                                            "testcase.result.status.finished.pass"
                                                          ].iconClass
                                                          : StateClasses[
                                                            "testcase.result.status.finished.fail"
                                                          ].iconClass
                                                        : StateClasses[
                                                          specification.state
                                                        ].iconClass
                                                    }
                                                  ></i>
                                                </>
                                              }
                                            </td>
                                          </tr>
                                        ))}
                                    </React.Fragment>
                                  )
                              )}
                            </tbody>
                            <tbody>
                              <tr className="result">
                                <td colSpan={2}>Final Result</td>
                                {!requiredTestRequestTestcaseResult ||
                                  !requiredTestRequestTestcaseResult.state ? (
                                  <td />
                                ) : (
                                  <td
                                    className={
                                      requiredTestRequestTestcaseResult.state ===
                                        TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                        ? !!requiredTestRequestTestcaseResult.success
                                          ? StateClasses[
                                            "testcase.result.status.finished.pass"
                                          ].cardClass
                                          : StateClasses[
                                            "testcase.result.status.finished.fail"
                                          ].cardClass
                                        : StateClasses[
                                          requiredTestRequestTestcaseResult
                                            .state
                                        ].cardClass
                                    }
                                  >
                                    {requiredTestRequestTestcaseResult.state ===
                                      TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                      ? !!requiredTestRequestTestcaseResult.success
                                        ? StateClasses[
                                          "testcase.result.status.finished.pass"
                                        ].text
                                        : StateClasses[
                                          "testcase.result.status.finished.fail"
                                        ].text
                                      : StateClasses[
                                        requiredTestRequestTestcaseResult.state
                                      ].text}{" "}
                                    <i
                                      className={
                                        requiredTestRequestTestcaseResult.state ===
                                          TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                          ? !!requiredTestRequestTestcaseResult.success
                                            ? StateClasses[
                                              "testcase.result.status.finished.pass"
                                            ].iconClass
                                            : StateClasses[
                                              "testcase.result.status.finished.fail"
                                            ].iconClass
                                          : StateClasses[
                                            requiredTestRequestTestcaseResult
                                              .state
                                          ].iconClass
                                      }
                                    ></i>
                                  </td>
                                )}
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  )}

                  {recommendedTestcaseResults.length > 0 && (
                    <div className="compliance-card">
                      <div className="c-card-header">
                        Conformance
                        <div className="header-description">
                          Conformance refers to the degree to which the system
                          aligns with the <b>recommended</b> specifications of the
                          applied components within OpenHIE standards.
                        </div>
                      </div>
                      <div className="c-card-body">
                        <div className="table-responsive">
                          <table className="data-table capitalize-words">
                            <thead>
                              <tr>
                                <th>COMPONENTS</th>
                                <th>SPECIFICATIONS</th>
                                <th>RESULTS</th>
                                <th>GRADE</th>
                              </tr>
                            </thead>
                            <tbody>
                              {Object.values(recommendedTestcaseResults).map(
                                (component, index) =>
                                  !!component &&
                                  !!component.specifications[0] && (
                                    <React.Fragment key={component.id}>
                                      <tr
                                        className={
                                          index % 2 === 0 ? "even-row" : "odd-row"
                                        }
                                      >
                                        <td>
                                          <b>{component.name}</b>
                                        </td>
                                        <td>
                                          {component.specifications[0]?.name}
                                        </td>
                                        <td
                                          className={
                                            component.specifications[0].state ===
                                              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                              ? !!component.specifications[0]
                                                .success
                                                ? StateClasses[
                                                  "testcase.result.status.finished.pass"
                                                ].cardClass
                                                : StateClasses[
                                                  "testcase.result.status.finished.fail"
                                                ].cardClass
                                              : StateClasses[
                                                component.specifications[0]
                                                  .state
                                              ].cardClass
                                          }
                                        >
                                          {
                                            <>
                                              {component.specifications[0]
                                                .state ===
                                                TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                ? !!component.specifications[0]
                                                  .success
                                                  ? StateClasses[
                                                    "testcase.result.status.finished.pass"
                                                  ].text
                                                  : StateClasses[
                                                    "testcase.result.status.finished.fail"
                                                  ].text
                                                : StateClasses[
                                                  component.specifications[0]
                                                    .state
                                                ].text}{" "}
                                              <i
                                                className={
                                                  component.specifications[0]
                                                    .state ===
                                                    TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                    ? !!component
                                                      .specifications[0].success
                                                      ? StateClasses[
                                                        "testcase.result.status.finished.pass"
                                                      ].iconClass
                                                      : StateClasses[
                                                        "testcase.result.status.finished.fail"
                                                      ].iconClass
                                                    : StateClasses[
                                                      component
                                                        .specifications[0].state
                                                    ].iconClass
                                                }
                                              ></i>
                                            </>
                                          }
                                        </td>
                                        <td>
                                          <span className="grade-badge">
                                            {!!component.grade
                                              ? component.grade
                                              : "-"}
                                          </span>
                                        </td>
                                      </tr>
                                      {component.specifications
                                        .slice(1)
                                        .map((specification, specIndex) => (
                                          <tr
                                            key={specification.id}
                                            className={
                                              specIndex % 2 === 0
                                                ? "even-row"
                                                : "odd-row"
                                            }
                                          >
                                            <td></td>
                                            <td>{specification.name}</td>
                                            <td
                                              className={
                                                specification.state ===
                                                  TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                  ? !!specification.success
                                                    ? StateClasses[
                                                      "testcase.result.status.finished.pass"
                                                    ].cardClass
                                                    : StateClasses[
                                                      "testcase.result.status.finished.fail"
                                                    ].cardClass
                                                  : StateClasses[
                                                    specification.state
                                                  ].cardClass
                                              }
                                            >
                                              {
                                                <>
                                                  {specification.state ===
                                                    TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                    ? !!specification.success
                                                      ? StateClasses[
                                                        "testcase.result.status.finished.pass"
                                                      ].text
                                                      : StateClasses[
                                                        "testcase.result.status.finished.fail"
                                                      ].text
                                                    : StateClasses[
                                                      specification.state
                                                    ].text}{" "}
                                                  <i
                                                    className={
                                                      specification.state ===
                                                        TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
                                                        ? !!specification.success
                                                          ? StateClasses[
                                                            "testcase.result.status.finished.pass"
                                                          ].iconClass
                                                          : StateClasses[
                                                            "testcase.result.status.finished.fail"
                                                          ].iconClass
                                                        : StateClasses[
                                                          specification.state
                                                        ].iconClass
                                                    }
                                                  ></i>
                                                </>
                                              }
                                            </td>
                                            <td></td>
                                          </tr>
                                        ))}
                                    </React.Fragment>
                                  )
                              )}
                            </tbody>
                            <tbody>
                              <tr className="result">
                                <td colSpan={2}>Final Result</td>
                                <td></td>
                                <td>
                                  <span className="grade-badge">
                                    {" "}
                                    {recommendedTestRequestTestcaseResult.grade
                                      ? recommendedTestRequestTestcaseResult.grade
                                      : "-"}
                                  </span>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  )}

                  {recommendedTestcaseResults.length > 0 && (
                    <div className="compliance-card">
                      <div className="c-card-header">Grade Legend</div>
                      <div className="c-card-body">
                        <Table
                          dataSource={[{ key: 1 }]} // Providing a dummy data source with one row
                          columns={columns}
                          pagination={false}
                        />
                      </div>
                    </div>
                  )}

                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ApplicationReport;
