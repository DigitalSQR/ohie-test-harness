import React, { useState } from "react";
import { TrophyOutlined, PlayCircleOutlined } from "@ant-design/icons";
const ComplianceByComponent = (props) => {
  const [show, setShow] = useState(false);
  const toggleShow = () => {
    setShow(!show);
  };
  return (
    <>
      <div
        className="row align-items-center"
        style={{ border: "1px solid black" }}
      >
        <div className="col-auto">
          <TrophyOutlined style={{ fontSize: "48px" }} />
        </div>

        <div className="col-auto">
          <p className="mb-0" style={{ fontSize: "18px" }}>
            {props?.component}
          </p>
        </div>

        <div className="col">
          {props.data.appName.map((appName, index) => (
            <div key={index}>
              {index === 0 && (
                <>
                  <p
                    className="fw-bold"
                    style={{ fontSize: "20px", marginBottom: "8px" }}
                  >
                    {appName}
                  </p>
                  <p style={{ fontSize: "18px", marginTop: "-8px" }}>
                    {props.data.compliancePercentage[index]}% Compliance
                  </p>
                </>
              )}
              {index !== 0 && !!show && (
                <>
                  <p
                    className="fw-bold"
                    style={{ fontSize: "20px", marginBottom: "8px" }}
                  >
                    {appName}
                  </p>
                  <p style={{ fontSize: "18px", marginTop: "-8px" }}>
                    {props.data.compliancePercentage[index]}% Compliance
                  </p>
                </>
              )}
            </div>
          ))}
        </div>

        <div
          className="col-auto cursor-pointer"
          style={{ fontSize: "30px" }}
          onClick={toggleShow}
        >
          <PlayCircleOutlined />
        </div>
      </div>
    </>
  );
};

export default ComplianceByComponent;
