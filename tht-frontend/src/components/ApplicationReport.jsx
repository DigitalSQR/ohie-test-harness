import React from "react";
import Header from "./Header";
import "../scss/application-report.scss";
import Footer from "./Footer";
const ApplicationReport = () => {
  return (
    <div>
      <Header/>
      <div>
        <div className="application-report">
          <div className="container">
            <div className="col-12">
              <div className="d-flex align-items-center justify-content-between flex-wrap mb-4">
                <h2>
                  Application Report - <span>MedPlat</span>{" "}
                </h2>
                <div>
                  <button className="btn btn-link  py-2 font-size-14">BACK</button>
                  <button className="btn btn-primary btn-blue py-2 font-size-14">
                    DOWNLOAD
                  </button>
                </div>
              </div>
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
                        <span className="comp-badge">Client Registry (CR)</span>
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
                        <b> Client Registry and Non-functional requirements</b>
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
                          <th>COMPONENTS</th>
                          <th>SPECIFICATIONS</th>
                          <th>RESULTS</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>
                            <b>Client Registry(CR)</b>
                          </td>
                          <td>CRWF-1</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                        </tr>
                        <tr>
                          <td></td>
                          <td>CRWF-2</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                        </tr>
                        <tr>
                          <td></td>
                          <td>CRWF-3</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                        </tr>
                        <tr>
                          <td></td>
                          <td>CRF-8</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                        </tr>
                        <tr className="result">
                          <td>Final Result</td>
                          <td></td>
                          <td>PASS </td>
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
                          <th>COMPONENTS</th>
                          <th>SPECIFICATIONS</th>
                          <th>RESULTS</th>
                          <th>GRADE</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>
                            <b>Non-Functional requirements(NFR)</b>
                          </td>
                          <td>NFR-1</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                          <td>
                            {" "}
                            <span className="gradeA">GRADE A</span>
                          </td>
                        </tr>
                        <tr>
                          <td></td>
                          <td>NFR-2</td>
                          <td className="fail">
                            FAIL <i className="bi bi-x-circle-fill"></i>
                          </td>
                          <td>-</td>
                        </tr>
                        <tr>
                          <td></td>
                          <td>NFR-3</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                          <td>-</td>
                        </tr>
                        <tr>
                          <td></td>
                          <td>NFR-4</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                          <td>-</td>
                        </tr>
                        <tr>
                          <td>
                            <b>Client Registry(CR)</b>
                          </td>
                          <td>CRF-1</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                          <td>
                            <span className="gradeB">GRADE B</span>
                          </td>
                        </tr>
                        <tr>
                          <td></td>
                          <td>CRF-3</td>
                          <td className="pass">
                            PASS <i className="bi bi-check-circle-fill"></i>
                          </td>
                          <td>-</td>
                        </tr>
                        <tr className="result">
                          <td>Final Result</td>
                          <td></td>
                          <td>PASS </td>
                          <td>GRADE B</td>
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
      <Footer/>
    </div>
  );
};

export default ApplicationReport;
