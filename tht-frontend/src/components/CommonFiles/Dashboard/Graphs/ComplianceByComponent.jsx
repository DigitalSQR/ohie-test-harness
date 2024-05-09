import React from "react";
import ComplianceByComponentTable from "../Modals/ComplianceByComponentTable";
import { Empty } from "antd";
const ComplianceByComponent = (props) => {
  return (
    <div className="row align-items-center">
      <div>
        <ComplianceByComponentTable data={props?.data} />
      </div>
      {props.data.appName && props.data.appName.length > 0 ? (
        <div className="col">
          {props.data.appName.map((appName, index) => (
            <div key={index}>
              {index !== 0 && !!props.show && (
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
      ) : (
        <div className="col">
          <Empty
            description={
              <span>
                No Application tested for component{" "}
                <strong>"{props.component}"</strong>
              </span>
            }
            imageStyle={{
              height: 200,
            }}
          />
        </div>
      )}
    </div>
  );
};

export default ComplianceByComponent;
