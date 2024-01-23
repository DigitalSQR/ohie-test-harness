import React, { useEffect, useState } from "react";
import Header from "../../CommonFiles/Header/Header";
import "./application-report.scss";
import Footer from "../../CommonFiles/Footer/Footer";
import { useNavigate } from "react-router-dom";
import api from "../../../api/configs/axiosConfigs";
import html2pdf from "html2pdf.js";
const ApplicationReport = () => {
  const navigate = useNavigate();
  const [groupedData, setGroupedData] = useState({});
  useEffect(() => {
    fetchData();
  }, []);

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

  const fetchData = async () => {
    try {
      const apiUrl =
        "testcase-result?testRequestId=d9262cea-cc1a-4ab2-af65-57a77a3b77bd";
      const response = await api.request({
        url: apiUrl,
        method: "GET",
      });
      const grouped = response.data.content.reduce((acc, item) => {
        if (item.refObjUri.split(".").pop() === "ComponentInfo") {
          if (!acc[item.id]) {
            acc[item.id] = {
              ...item,
              requiredSpecification: [],
              nonRequiredSpecifications: [],
            };
          }
        } else if (item.refObjUri.split(".").pop() === "SpecificationInfo") {
          const componentId = item.parentTestcaseResultId;
          if (item.required) {
            acc[componentId].requiredSpecification.push(item);
          } else {
            acc[componentId].nonRequiredSpecifications.push(item);
          }
        }
        return acc;
      }, {});

      const sortedComponents = Object.values(grouped).sort(
        (a, b) => a.rank - b.rank
      );

      // Sort the specifications array within each component based on rank
      sortedComponents.forEach((component) => {
        component.requiredSpecification.sort((a, b) => a.rank - b.rank);
        component.nonRequiredSpecifications.sort((a, b) => a.rank - b.rank);
      });

      setGroupedData(sortedComponents);
    } catch (error) {
      console.log(error);
    }
  };
  return (
    <div>
      <Header />
      <div>
        <div className="application-report">
          <div className="container">
            <div className="col-12">
              <div className="d-flex align-items-center justify-content-between flex-wrap mb-4">
                <h2>
                  Application Report - <span>MedPlat</span>{" "}
                </h2>
                <div>
                  <button
                    onClick={() => {
                      navigate("/dashboard/");
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
                          Assessee Name:<span>Ravi Shankar</span>
                        </p>
                      </div>
                      <div className="col-lg-3 col-md-6 col-12 mb-4">
                        <p>
                          Email ID:<span>rs@gmail.com</span>
                        </p>
                      </div>
                      <div className="col-lg-3 col-md-6 col-12 mb-4">
                        <p>
                          Company name:<span>ARGUSOFT</span>
                        </p>
                      </div>
                      <div className="col-lg-3 col-md-6 col-12 mb-4">
                        <p>
                          Application Date:<span>12 November 2023</span>
                        </p>
                      </div>
                      <div className="col-12">
                        <p>Components applied for testing:</p>
                        <div>
                          <span className="comp-badge">
                            Non-Functional Requirements
                          </span>
                          <span className="comp-badge">
                            Client Registry (CR)
                          </span>
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
                          The <b> Medplat </b> system is <b> Compliant</b> to{" "}
                          <b>
                            {" "}
                            Client Registry and Non-functional requirements{" "}
                          </b>
                          components and has been awarded a <b>
                            {" "}
                            Conformance{" "}
                          </b>{" "}
                          Grade B
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
                            <th className="col-3">COMPONENTS</th>
                            <th>SPECIFICATIONS</th>
                            <th>RESULTS</th>
                          </tr>
                        </thead>
                        <tbody>
                          {Object.values(groupedData).map(
                            (component, index) =>
                              component.requiredSpecification.length > 0 && (
                                <React.Fragment key={component.id}>
                                  <tr
                                    className={
                                      index % 2 === 0 ? "even-row" : "odd-row"
                                    }
                                  >
                                    <td>
                                      {index % 2 === 0 ? (
                                        <b>{component.name}</b>
                                      ) : null}
                                    </td>
                                    <td>
                                      {component.requiredSpecification[0]?.name}
                                    </td>
                                    <td
                                      className={
                                        component.requiredSpecification[0]
                                          .success
                                          ? "pass"
                                          : "fail"
                                      }
                                    >
                                      {component.requiredSpecification[0]
                                        .success ? (
                                        <>
                                          PASS{" "}
                                          <i className="bi bi-check-circle-fill"></i>
                                        </>
                                      ) : (
                                        <>
                                          FAIL{" "}
                                          <i className="bi bi-x-circle-fill"></i>
                                        </>
                                      )}
                                    </td>
                                  </tr>
                                  {component.requiredSpecification
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
                                            specification.success
                                              ? "pass"
                                              : "fail"
                                          }
                                        >
                                          {specification.success ? (
                                            <>
                                              PASS{" "}
                                              <i className="bi bi-check-circle-fill"></i>
                                            </>
                                          ) : (
                                            <>
                                              FAIL{" "}
                                              <i className="bi bi-x-circle-fill"></i>
                                            </>
                                          )}
                                        </td>
                                      </tr>
                                    ))}
                                </React.Fragment>
                              )
                          )}
                          <tr className="result">
                            <td colSpan={3}>Final Result</td>
                          </tr>
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
                            <th className="col-3">COMPONENTS</th>
                            <th>SPECIFICATIONS</th>
                            <th>RESULTS</th>
                            <th>GRADE</th>
                          </tr>
                        </thead>
                        <tbody>
                          {Object.values(groupedData).map(
                            (component, index) =>
                              component.nonRequiredSpecifications.length >
                                0 && (
                                <React.Fragment key={component.id}>
                                  <tr
                                    className={
                                      index % 2 === 0 ? "even-row" : "odd-row"
                                    }
                                  >
                                    <td>
                                      {index % 2 === 0 ? (
                                        <b>{component.name}</b>
                                      ) : null}
                                    </td>
                                    <td>
                                      {
                                        component.nonRequiredSpecifications[0]
                                          ?.name
                                      }
                                    </td>
                                    <td
                                      className={
                                        component.success ? "pass" : "fail"
                                      }
                                    >
                                      {component.nonRequiredSpecifications[0]
                                        .success ? (
                                        <>
                                          PASS{" "}
                                          <i className="bi bi-check-circle-fill"></i>
                                        </>
                                      ) : (
                                        <>
                                          FAIL{" "}
                                          <i className="bi bi-x-circle-fill"></i>
                                        </>
                                      )}
                                    </td>
                                    <td>
                                      {component.nonRequiredSpecifications[0]
                                        .success
                                        ? null
                                        : "-"}
                                    </td>
                                  </tr>
                                  {component.nonRequiredSpecifications
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
                                            specification.success
                                              ? "pass"
                                              : "fail"
                                          }
                                        >
                                          {specification.success ? (
                                            <>
                                              PASS{" "}
                                              <i className="bi bi-check-circle-fill"></i>
                                            </>
                                          ) : (
                                            <>
                                              FAIL{" "}
                                              <i className="bi bi-x-circle-fill"></i>
                                            </>
                                          )}
                                        </td>
                                        <td>
                                          {component.success ? null : "-"}
                                        </td>
                                      </tr>
                                    ))}
                                </React.Fragment>
                              )
                          )}
                          <tr className="result">
                            <td colSpan={4}>Final Result</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
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
