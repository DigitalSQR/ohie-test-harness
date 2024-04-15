import React, { useEffect, useState } from "react";
import { Modal } from "antd";
import "../../../../styles/scss/_table.scss"
import "./ComplianceByComponent.scss";
const ComplianceByComponentModal = ({
  isModalOpen,
  setIsModalOpen,
  component,
  data,
}) => {
  const handleCancel = () => {
    setIsModalOpen(false);
  };

  useEffect(() => {}, [isModalOpen]);

  return (
    <div>
      <Modal
        open={isModalOpen}
        footer={null}
        onCancel={handleCancel}
        width={600}
        bodyStyle={{ maxHeight: "25rem", overflowY: "auto" }}
      >
        <div className="table-responsive">
          <h5 className="pieModalHeading">{component}</h5>
          <table className="data-table pie-grid">
            <thead>
              <tr>
                <th style={{ width: "12%" }}>APPLICATION NAME</th>
                <th style={{ width: "12%" }}>COMPLIANCE</th>
              </tr>
            </thead>
            <tbody>
              {data.appName?.map((appName, index) => {
                return (
                  <tr
                    className={index % 2 === 0 ? "even" : "odd"}
                    key={index}
                  >
                    <td className="fw-bold">{appName}</td>
                    <td>{data.compliancePercentage[index]}%</td>
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

export default ComplianceByComponentModal;
