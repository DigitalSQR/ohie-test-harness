import { useEffect, useState } from "react";
import { TestResultAPI } from "../../../../api/TestResultAPI";
import passImg from "../../../../styles/images/success.svg";
import failImg from "../../../../styles/images/failure.svg";
import skipImg from "../../../../styles/images/skip.svg";
import stopImg from "../../../../styles/images/stop.svg";
import "./TestcaseResultRow.scss";

export default function TestcaseResultRow({ testResultId, stompClient, toggleFunction, toggleClass, testcaseResultType }) {
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
        return (
            <div className={"collapse " + toggleClass + " expanded-row"}>
                {testcaseResult?.hasSystemError ? (
                    <p>
                        <b>Failed due to System Error</b>
                    </p>
                ) : (
                    <p>
                        <b>{testcaseResult?.message}</b>
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
        let state = testcaseResult?.state;
        let success = testcaseResult?.success;
        if (state === "testcase.result.status.finished") {
            if (!!success) {
                return <img className="finished" src={passImg} alt="PASS" />;
            } else {
                return <img className="finished" src={failImg} alt="FAIL" />;
            }
        } else if (state === "testcase.result.status.pending") {
            return <img className="finished" src={stopImg} alt="PENDING" />;
        } else if (state === "testcase.result.status.skip") {
            return <img className="finished" src={skipImg} alt="SKIP" />;
        } else if (state === "testcase.result.status.inprogress") {
            return <div className="spinner-border" role="status"></div>;
        }
    };

    useEffect(() => {
        TestResultAPI.getTestcaseResultStatus(testResultId, { automated: true }).then((response) => {
            setTestcaseResult(response);
            if (stompClient
                && stompClient.connected ) {
                const destination = '/testcase-result/' + testResultId;
                const subscription = stompClient.subscribe(destination, (msg) => {
                    setTestcaseResult(JSON.parse(msg.body));
                    if (testcaseResult?.state === "testcase.result.status.finished") {
                        subscription.unsubscribe();
                    }
                });
            }
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