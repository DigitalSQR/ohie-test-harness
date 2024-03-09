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

import { Table, Tooltip } from "antd";
import {
  TestcaseResultStateConstants,
  StateClasses,
} from "../../../constants/testcaseResult_constants";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
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

  useEffect(() => {
    fetchTestCaseResultData();
    fetchTestCaseRequestData();
    fetchAllGrades();
  }, []);

  const fetchUserDetails = async (userId) => {
    const response = await UserAPI.getUserById(userId);
    setUser({ name: response.name, email: response.email });
  };
  const generatePDF = () => {
    const element = document.getElementById("report");

    html2pdf(element, {
      margin: 5,
      filename: "report.pdf",
      image: { type: "jpeg", quality: 1.0 },
      html2canvas: { scale: 3, letterRendering: true },
      jsPDF: {
        unit: "mm",
        format: "a3",
        orientation: "portrait",
        compress: true,
      },
    });
  };

  const fetchAllGrades = () => {
    GradeAPI.getAllGrades()
      .then((res) => {
        const columns = res.data.map(item => ({
          title: `Grade ${item.grade}`,
          dataIndex: item.grade,
          key: item.grade,
          width: 150,
          align: 'center',
          render: (text, record) => rangedGradeData[item.grade],
        }));

        const rangedGradeData = {};
        res.data.forEach((item, index) => {
          const startPercentage = index === 0 ? 0 : res.data[index - 1].percentage + 1;
          const endPercentage = index === res.data.length - 1 ? 100 : item.percentage;
          rangedGradeData[item.grade] = `${startPercentage}% - ${endPercentage}%`;
        });

        setColumns(columns);
        setRangedGradeData(rangedGradeData);
      })
      .catch((err) => {});
  };

  const fetchTestCaseRequestData = async () => {
    const response = await TestRequestAPI.getTestRequestsById(testRequestId);
    fetchUserDetails(response.assesseeId);
    setTestRequest(response);
  };

  const fetchTestCaseResultData = async () => {
    try {
      const [requiredResponse, recommendedResponse] = await Promise.all([
        TestResultAPI.getMultipleTestcaseResultStatus({ testRequestId, required: true }),
        TestResultAPI.getMultipleTestcaseResultStatus({ testRequestId, recommended: true })
      ]);
      const requiredTestcaseResults = [];
      const recommendedTestcaseResults = [];
      const compNames = new Set();
      const passedRequiredComponentNames = [];
      for (let testcaseResult of requiredResponse) {
        if (testcaseResult.refObjUri === RefObjUriConstants.TESTREQUEST_REFOBJURI) {
          setRequiredTestRequestTestcaseResult(testcaseResult);
        }
        else if (testcaseResult.refObjUri === RefObjUriConstants.COMPONENT_REFOBJURI) {
          compNames.add(testcaseResult.name);
          if (!!testcaseResult.success) {
            passedRequiredComponentNames.push(testcaseResult.name);
          }
          requiredTestcaseResults.push({
            ...testcaseResult,
            specifications: [],
          });
        }
        else if (testcaseResult.refObjUri === RefObjUriConstants.SPECIFICATION_REFOBJURI) {
          requiredTestcaseResults[requiredTestcaseResults.length - 1].specifications.push(testcaseResult);
        }
      }
      for (let testcaseResult of recommendedResponse) {
        if (testcaseResult.refObjUri === RefObjUriConstants.TESTREQUEST_REFOBJURI) {
          setRecommendedTestRequestTestcaseResult(testcaseResult);
        }
        else if (testcaseResult.refObjUri === RefObjUriConstants.COMPONENT_REFOBJURI) {
          compNames.add(testcaseResult.name);
          recommendedTestcaseResults.push({
            ...testcaseResult,
            specifications: [],
          });
        }
        else if (testcaseResult.refObjUri === RefObjUriConstants.SPECIFICATION_REFOBJURI) {
          recommendedTestcaseResults[recommendedTestcaseResults.length - 1].specifications.push(testcaseResult);
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
                      navigate("/applications");
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
                          Company name:<span>{testRequest?.productName}</span>
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
                        <p>Components applied for testing:</p>
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
                          The <b> {testRequest?.name} </b> system is{" "}
                          {passReqCompNames?.length > 0 ? (
                            <>
                              <b>compliant</b> to{" "}
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
                              not <b>compliant</b> to any component{" "}
                            </>
                          )}
                          and has been awarded a{" "}
                          <b>
                            Conformance Grade{" "}
                            {!!recommendedTestRequestTestcaseResult.grade
                              ? recommendedTestRequestTestcaseResult.grade
                              : "-"}
                          </b>
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="compliance-card">
                  <div className="c-card-header">Compliance</div>
                  <div className="c-card-body">
                    <div className="table-responsive">
                      <table className="data-table">
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
                                    <td>{component.specifications[0]?.name}</td>
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
                                              component.specifications[0].state
                                            ].cardClass
                                      }
                                    >
                                      {
                                        <>
                                          {component.specifications[0].state ===
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
                                                ? !!component.specifications[0]
                                                    .success
                                                  ? StateClasses[
                                                      "testcase.result.status.finished.pass"
                                                    ].iconClass
                                                  : StateClasses[
                                                      "testcase.result.status.finished.fail"
                                                    ].iconClass
                                                : StateClasses[
                                                    component.specifications[0]
                                                      .state
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
                      </table>
                    </div>
                  </div>
                </div>

                <div className="compliance-card">
                  <div className="c-card-header">Conformance</div>
                  <div className="c-card-body">
                    <div className="table-responsive">
                      <table className="data-table">
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
                                    <td>{component.specifications[0]?.name}</td>
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
                                              component.specifications[0].state
                                            ].cardClass
                                      }
                                    >
                                      {
                                        <>
                                          {component.specifications[0].state ===
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
                                                ? !!component.specifications[0]
                                                    .success
                                                  ? StateClasses[
                                                      "testcase.result.status.finished.pass"
                                                    ].iconClass
                                                  : StateClasses[
                                                      "testcase.result.status.finished.fail"
                                                    ].iconClass
                                                : StateClasses[
                                                    component.specifications[0]
                                                      .state
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
                                        requiredTestRequestTestcaseResult.state
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
                            <td>
                              <span className="grade-badge">
                                {" "}
                                {!!recommendedTestRequestTestcaseResult.grade
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

                <div className="compliance-card">
                  <div className="c-card-header">Grade Ranges</div>
                  <div className="c-card-body">
                    <Table
                      dataSource={[{ key: 1 }]} // Providing a dummy data source with one row
                      columns={columns}
                      pagination={false}
                    />
                  </div>
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
