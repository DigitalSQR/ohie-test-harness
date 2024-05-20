import { Fragment, useEffect, useState } from "react";
import skipImg from "../../../../styles/images/skip.svg";
import stopImg from "../../../../styles/images/stop.svg";
import { TestcaseResultStateConstants } from "../../../../constants/testcaseResult_constants";
export default function TestcaseResultRow({
  testcaseResultItem,
  stompClient,
  testcaseResultType,
  changeState,
  caseIndex,
  toggleTestCaseErrorAccordian,
}) {
  const [testcaseResult, setTestcaseResult] = useState(testcaseResultItem);

  //   Function to determine the dropdown toggle classes visibility and its icon
  const getButtonDisplay = () => {
    return (
      <div>
        {testcaseResult?.state === "testcase.result.status.finished" &&
        testcaseResult?.success === false &&
        testcaseResultType === "testcase" ? (
          <div className="d-flex align-items-center">
            <a class="detail-link">Details </a>
          </div>
        ) : (
          " "
        )}
      </div>
    );
  };

  //   This function determines the result icon of the testcases.
  const getResultDisplay = (testcaseResult) => {
    const state = testcaseResult?.state;
    const success = testcaseResult?.success;

    switch (state) {
      case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED:
        return success ? (
          testcaseResultType == "component" ? (
            <span className="result-pass">
              <i className="bi bi-check2"></i>Pass
            </span>
          ) : (
            <span class="result-pass-label">
              <i class="bi bi-check-circle-fill"></i>Pass
            </span>
          )
        ) : testcaseResultType == "component" ? (
          <span className="result-fail">
            <i className="bi bi-x"></i>Fail
          </span>
        ) : (
          <span class="result-pass-label fail">
            <i class="bi bi-x-circle-fill"></i>Fail
          </span>
        );
      case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT:
      case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_PENDING:
        return (
          <img
            className="finished"
            src={stopImg}
            alt="PENDING"
            width="20"
            height="20"
          />
        );
      case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_SKIP:
        return (
          <img
            className="finished"
            src={skipImg}
            alt="SKIP"
            width="20"
            height="20"
          />
        );
      case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_INPROGRESS:
        return (
          <div
            className="spinner-border spinner-border-sm"
            role="status"
            style={{ marginRight: "0.5rem" }}
          ></div>
        );
      default:
        return null;
    }
  };

  // Effect to subscribe to updates on the testcaseResult via WebSocket using the stompClient.
  useEffect(() => {
    let oldTestcaseResultState = testcaseResult.state;
    if (stompClient && stompClient.connected) {
      const destination = "/testcase-result/automated/" + testcaseResult.id;
      const subscription = stompClient.subscribe(destination, (msg) => {
        const parsedMessage = JSON.parse(msg.body);
        setTestcaseResult(parsedMessage);
        if (!!changeState) {
          changeState(parsedMessage.state, oldTestcaseResultState);
        }
        oldTestcaseResultState = parsedMessage.state;
      });
    }
  }, [stompClient]);

  return (
    testcaseResult && (
      <Fragment>
        <p className="d-flex align-items-center mb-0 test-result-row">
          <span>{getResultDisplay(testcaseResult)}</span>
          <span className="time-label">
            {!!testcaseResult?.duration
              ? testcaseResult?.duration + " ms"
              : ""}
          </span>
          {getButtonDisplay()}
        </p>
        {testcaseResult?.state === "testcase.result.status.finished" &&
          testcaseResult?.success === false &&
          testcaseResultType ===
            "testcase" && (
              <a
                id={`failure-reason-${testcaseResultItem.refId}`}
                className={`detail-link`}
                onClick={() =>
                  toggleTestCaseErrorAccordian(testcaseResult?.id, caseIndex)
                }
              >
                Details{" "}
              </a>
            )}
      </Fragment>
    )
  );
}
