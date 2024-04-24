import React, { useState, useEffect } from "react";

const TestCaseErrorRow = ({
  testcaseResultItem,
  changeState,
  stompClient,
  caseIndex,
  expandedErrorIndex,
}) => {
  // This function is used to fetch the error message and display it properly. It replaces the script tag with an empty string.
  const [testcaseResult, setTestcaseResult] = useState(testcaseResultItem);
  const getErrorDisplay = () => {
    const messageSanitize = testcaseResult?.message?.replace(
      /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
      ""
    );
    return (
      <div id="testCaseErrorRow" >
        {testcaseResult?.state === "testcase.result.status.finished" &&
          testcaseResult?.success === false && (
            <div
              className={`fail-detail-box ${
                expandedErrorIndex.includes(
                  `${testcaseResultItem?.id}-${caseIndex}`
                )
                  ? "expanded"
                  : ""
              }`}
            >
              {testcaseResult?.hasSystemError ? (
                <p>
                  <i class="bi bi-x-octagon-fill"></i>
                  <b>Failed due to System Error</b>
                </p>
              ) : (
                <p
                  dangerouslySetInnerHTML={{
                    __html: `<i class="bi bi-x-octagon-fill"></i>${messageSanitize}`,
                  }}
                ></p>
              )}
            </div>
          )}
      </div>
    );
  };

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

  // Return getErrorDisplay if the condition is true, otherwise return an empty fragment
  return (
    <>
      {testcaseResult?.state === "testcase.result.status.finished" &&
      testcaseResult?.success === false ? (
        getErrorDisplay()
      ) : (
        <></>
      )}
    </>
  );
};

export default TestCaseErrorRow;
