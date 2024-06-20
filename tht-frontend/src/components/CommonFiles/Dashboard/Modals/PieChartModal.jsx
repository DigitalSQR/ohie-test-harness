import React, {Fragment, useEffect, useState } from "react";
import { Modal } from "antd";
import moment from "moment"; // Import moment for date formatting
import { TestRequestAPI } from "../../../../api/TestRequestAPI";
import UserIdNameEmailConnector from "../../../connectors/UserIdNameEmailConnector/UserIdNameEmailConnector";
import "./PieChartModal.scss"
import "../../../../styles/scss/_table.scss"
import {
    StateBadgeClasses,
    TestRequestStateConstantNames,
  } from "../../../../constants/test_requests_constants";
const PieChartModal = ({ isModalOpen, setIsModalOpen, clickedValue }) => {
  const handleCancel = () => {
    setIsModalOpen(false);
  };

  const [gridData, setGridData] = useState([]);
  const getTestRequestsByClickedValue = () => {
    TestRequestAPI.getTestRequestsByState()
      .then((response) => {
        const data = response.content.filter(
          (item) => item.state === clickedValue
        );
        setGridData(data);
      })
      .catch((err) => {});
  };

  useEffect(() => {
    if (!!isModalOpen) {
      getTestRequestsByClickedValue();
    }
  }, [isModalOpen]);

  return (
    <div id="pieChart">
      <Modal
        cancelButtonProps={{id:"piechart-cancelButton"}}
        okButtonProps={{id:"piechart-okButton"}}
        open={isModalOpen}
        footer={null}
        onCancel={handleCancel}
        width={900}
        bodyStyle={{ maxHeight: "25rem", overflowY: "auto" }}
      >
        <div className="table-responsive">
          <h5 className="pieModalHeading">Application Requests</h5>
          <table className="data-table pie-grid">
            <thead>
              <tr>
                <th style={{ width: "12%" }}>APPLICATION NAME</th>
                <th style={{ width: "12%" }}>Assessee</th>
                <th style={{ width: "12%" }}>Company</th>
                <th style={{ width: "16%" }}>EMAIL ID</th>
                <th style={{ width: "15%" }}>Status</th>
                <th style={{ width: "15%" }}>REQUEST DATE</th>
              </tr>
            </thead>
            <tbody>
              {gridData?.map((testRequest, index) => {
                const formattedDate = moment(testRequest.meta.createdAt).format(
                  "Do MMMM, YYYY"
                );
                return (
                  <tr
                    className={index % 2 === 0 ? "even" : "odd"}
                    key={testRequest.id}
                  >
                    <td className="fw-bold">{testRequest.name}</td>
                    <UserIdNameEmailConnector
                      className="fw-bold"
                      isLink={true}
                      userId={testRequest.assesseeId}
                    />
                    <td>
                      <Fragment>
                        <span
                          className={`status badge ${
                            StateBadgeClasses[testRequest.state]
                          }`}
                        >
                          {TestRequestStateConstantNames[
                            testRequest.state
                          ].toLowerCase()}
                        </span>
                      </Fragment>
                    </td>
                    <td>{formattedDate}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </Modal>
    </div>
  );
};

export default PieChartModal;
