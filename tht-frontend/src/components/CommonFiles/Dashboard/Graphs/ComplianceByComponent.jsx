import React from "react";
import {
    TrophyOutlined,
    PlayCircleOutlined,
  } from "@ant-design/icons";
const ComplianceByComponent = (props) => {
  return (
    <div
      className="row align-items-center"
      style={{ width: "30vw", border: "1px solid black" }}
    >
      <div className="col-auto">
        <TrophyOutlined style={{ fontSize: "48px" }} />
      </div>
      <div className="col-auto">
        <p className="mb-0" style={{ fontSize: "18px" }}>
          {props.component}
        </p>
      </div>
      <div className="col">
        <p
          className="fw-bold"
          style={{ fontSize: "20px", marginBottom: "8px" }}
        >
          {props.appName}
        </p>
        <p style={{ fontSize: "18px", marginTop: "-8px" }}>{props.compliancePercentage}% Compliance</p>
      </div>
      <div className="col-auto" style={{ fontSize: "30px" }}>
        <PlayCircleOutlined />
      </div>
    </div>
  );
};

export default ComplianceByComponent;
