import { Fragment, useEffect, useState } from "react";
import { TestResultAPI } from "../../../../api/TestResultAPI";
import passImg from "../../../../styles/images/success.svg";
import failImg from "../../../../styles/images/failure.svg";
import skipImg from "../../../../styles/images/skip.svg";
import stopImg from "../../../../styles/images/stop.svg";
import { TestcaseResultStateConstants } from "../../../../constants/testcaseResult_constants";

/*
    TestcaseResultRow Component: Child component of AutomatedTesting Component.

    The results of automated testcases are rendered in this component.

    Props received from parent(AutomatedTesting):-
        1. testcaseResultItem :- The individual items in the result array. Contains the data to be displayed in the rows. 
        2. stompClient :- it is an instance of Simple Text Oriented Messaging Protocol, used here for real-time communication regarding results and status of automated testcases.
        3. toggleFunction :- This function is used to toggle the dropdown rows.
        4. toggleClass :- This const is used to store the current state of the dropdown.
        5. TestCaseResultType:- Defines the types of the result i.e. component/specification/testcase.
*/

/* 
    Usage Example:
    <TestcaseResultRow testcaseResultItem={{message:"Component",status:"active"}} 
    stompClient={{url:"production/api",heartbeatdelay:3000}}
    toggleFunction={()=>{setToggle(false)}} 
    toggleClass={open}
    testcaseResultType={"specification0"}/> 
*/
export default function TestcaseResultRow({ testcaseResultItem, stompClient, toggleFunction, toggleClass, testcaseResultType, changeState }) {
    const [testcaseResult, setTestcaseResult] = useState(testcaseResultItem);

    //   Function to determine the dropdown toggle classes visibility and its icon
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

    //   This function is used to fetch the error message and display it properly. It replaces the script tag with an empty string.
    const getErrorDisplay = () => {
        const messageSanitize = testcaseResult?.message?.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
        return (
            <div className={" expanded-row"}>
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


    //   This function defines the structure of the table data, depending upon the type of the result. 
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

    //   This function determines the result icon of the testcases.
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

    // Effect to subscribe to updates on the testcaseResult via WebSocket using the stompClient.
    useEffect(() => {
        let oldTestcaseResultState = testcaseResult.state;
        if (stompClient && stompClient.connected) {
            const destination = '/testcase-result/automated/' + testcaseResult.id;
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
        testcaseResult && <Fragment>
            <tr key={`testcase-result-${testcaseResult?.id}`} className="testcase-result-row">
                {displayTestName()}
                <td className="padding-x-12">{getResultDisplay(testcaseResult)}</td>
                <td>{!!testcaseResult?.duration ? testcaseResult?.duration + ' ms' : '-'}</td>
                <td>{getButtonDisplay()}
                </td>
            </tr>
            <tr className={`collapse ${toggleClass}`}>
                <td colSpan={6} className="text-center hiddenRow m-0 field-box padding-x-12">
                    {getErrorDisplay()}
                </td>
            </tr>
        </Fragment>

    )

}