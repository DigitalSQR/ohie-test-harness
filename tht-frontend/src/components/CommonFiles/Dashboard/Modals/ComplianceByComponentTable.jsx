import React from "react";
import "../../../../styles/scss/_table.scss";
import "./ComplianceByComponent.scss";
const ComplianceByComponentTable = ({ data }) => {
  return (
    <div id="complianceByComponentTable">
    <div className="table-responsive">
      <table className="data-table">
        <thead>
          <tr className="heading" >
            <th style={{ width: "3%" }}>APPLICATION NAME</th>
            <th style={{ width: "20%" }}>COMPLIANCE</th>
          </tr>
        </thead>
        <tbody>
          {data.appName?.map((appName, index) => {
            const rowClass = index % 2 === 0 ? "even" : "odd"; 
            console.log(rowClass)
            return (
              <tr className={rowClass} key={index}>
                <td>{appName}</td>
                <td>{data.compliancePercentage[index]}%</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
    </div>
  );
};

export default ComplianceByComponentTable;
