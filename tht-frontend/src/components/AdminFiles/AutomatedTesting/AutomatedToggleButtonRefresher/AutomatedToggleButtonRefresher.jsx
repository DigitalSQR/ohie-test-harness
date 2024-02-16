import React, { useEffect, useState } from "react";
import { TestResultAPI } from "../../../../api/TestResultAPI";
import "./AutomatedToggleButtonRefresher.scss"
const AutomatedToggleButtonRefresher = ({
  testResultId,
  toggleFunction,
  ErrorStatement,
  toggleClass,
}) => {
  const [item, setItem] = useState();
  const getButtonDisplay = () => {
    return (
      <div>
        {item?.state == "testcase.result.status.finished" &&
        item?.success == false ? (
          <span
            onClick={() => toggleFunction(item?.id)}
            type="button"
            className="approval-action-button float-end my-auto display"
          >
            {toggleClass === "show" ? (
              <i className="bi bi-chevron-double-down"></i>
            ) : (
              <i className="bi bi-chevron-double-right"></i>
            )}
          </span>
        ) : (
          " "
        )}
      </div>
    );
  };

  const getErrorDisplay = () => {
    return (
      <div className={"collapse " + toggleClass + " expanded-row"}>
        {item?.hasSystemError ? (
          <p>
            <b>Failed due to System Error</b>
          </p>
        ) : (
          <p>
            <b>{item?.message}</b>
          </p>
        )}
      </div>    
    );
  };

  useEffect(() => {
    const fetchToggleButtonStatus = async () => {
      const response = await TestResultAPI.getTestcaseResultStatus(
        testResultId,
        { automated: true }
      );
      setItem(response);
      if (response?.state !== "testcase.result.status.finished") {
        setTimeout(fetchToggleButtonStatus, 1000);
      }
    };
    fetchToggleButtonStatus();
  }, [testResultId]);
  return <>{!ErrorStatement ? getButtonDisplay() : getErrorDisplay()}</>;
};

export default AutomatedToggleButtonRefresher;
