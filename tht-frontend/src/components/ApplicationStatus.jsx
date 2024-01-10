import React from "react";
import "../scss/application-status.scss";
import "../scss/_table.scss";
const ApplicationStatus = () => {
  return (
    <div>
      <div id="wrapper">
        <div className="col-12 pt-3">
          <div className="table-responsive">
            <table className=" data-table">
              <thead>
                <tr>
                  <th>APP NAME</th>
                  <th>APP URL</th>
                  <th>COMPONENTS</th>
                  <th>DATE OF APPLICATION</th>
                  <th>STATUS</th>
                  <th>RESULT</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>MedPlat</td>
                  <td>www.medPlat.com</td>
                  <td>Client Registry (CR)</td>
                  <td>12 October 2023</td>
                  <td>
                    <span className="badges-green-dark">Active</span>
                  </td>
                  <td>
                    <span className="badges-green">Pass</span>{" "}
                    <i className="bi bi-download font-size-16 cursor-pointer ps-4"></i>
                  </td>
                </tr>
                <tr>
                  <td>MedPlat</td>
                  <td>www.medPlat.com</td>
                  <td>Client Registry (CR)</td>
                  <td>12 October 2023</td>
                  <td>
                    <span className="badges-orange">Pending</span>
                  </td>
                  <td></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ApplicationStatus;
