import { useEffect, useState } from "react";
import { TestResultAPI } from "../../../../api/TestResultAPI";
import passImg from "../../../../styles/images/success.svg";
import failImg from "../../../../styles/images/failure.svg";
import skipImg from "../../../../styles/images/skip.svg";
import stopImg from "../../../../styles/images/stop.svg";
import "./TestcaseResultRow.scss";
import { TestcaseResultStateConstants } from "../../../../constants/testcaseResult_constants";

export default function TestcaseResultRow({ testResultId, stompClient, toggleFunction, toggleClass, testcaseResultType, changeState }) {
    const [testcaseResult, setTestcaseResult] = useState();

    const getButtonDisplay = () => {
        return (
            <div>
                {testcaseResult?.state === "testcase.result.status.finished" &&
                    testcaseResult?.success === false ? (
                    <span
                        onClick={() => toggleFunction(testcaseResult?.id)}
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
        const messageSanitize = testcaseResult?.message.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
        return (
            <div className={"collapse " + toggleClass + " expanded-row"}>
                {testcaseResult?.hasSystemError ? (
                    <p>
                        <b>Failed due to System Error</b>
                    </p>
                ) : (
                    <p dangerouslySetInnerHTML={{ __html: messageSanitize}}>
                    </p>
                )}
            </div>
        );
    };

    const displayTestName = () => {
        switch (testcaseResultType) {
            case "component":
              return ( <><td>{testcaseResult.name}</td><td></td><td></td></> );
            case "specification":
              return ( <><td></td><td>{testcaseResult.name}</td><td></td></> );
            case "testcase":
              return ( <><td></td><td></td><td>{testcaseResult.name}</td></> );
            default:
              return null;
        }      
    };

    const getResultDisplay = (testcaseResult) => {
        const state = testcaseResult?.state;
        const success = testcaseResult?.success;
      
        switch (state) {
          case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED:
            return success ? (
              <img className="finished" src={passImg} alt="PASS" />
            ) : (
              <img className="finished" src={failImg} alt="FAIL" />
            );
          case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT:
          case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_PENDING:
            return <img className="finished" src={stopImg} alt="PENDING" />;
          case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_SKIP:
            return <img className="finished" src={skipImg} alt="SKIP" />;
          case TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_INPROGRESS:
            return <div className="spinner-border" role="status"></div>;
          default:
            return null;
        }
    };

    useEffect(() => {
        TestResultAPI.getTestcaseResultStatus(testResultId, { automated: true }).then((response) => {
            setTestcaseResult(response);
            let oldTestcaseResultState = response.state;                    
            if (stompClient
                && stompClient.connected ) {
                const destination = '/testcase-result/automated/' + testResultId;
                const subscription = stompClient.subscribe(destination, (msg) => {
                    const testcaseResult = JSON.parse(msg.body);
                    setTestcaseResult(testcaseResult);
                    if(!!changeState) {
                        changeState(testcaseResult.state, oldTestcaseResultState);
                    }
                    oldTestcaseResultState = testcaseResult.state;
                });
            }
        }).catch((error) => {
       
        });
    }, []);

    return (
        testcaseResult && <>
            <tr key={`testcase-result-${testcaseResult?.id}`} className="testcase-result-row">
                {displayTestName()}
                <td>{getResultDisplay(testcaseResult)}</td>
                <td>{!!testcaseResult?.duration ? testcaseResult?.duration + ' ms' : '-'}</td>
                <td>{getButtonDisplay()}
                </td>
            </tr>
            <tr>
                <td colSpan={6} className="text-center hiddenRow m-0 field-box">
                    {getErrorDisplay()}
                </td>
            </tr>
        </>

    )

}