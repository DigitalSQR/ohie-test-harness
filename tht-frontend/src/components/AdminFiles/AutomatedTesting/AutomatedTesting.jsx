import React, { useEffect, useState } from "react";
import { Breadcrumb, notification } from "antd";
import "./automatedtesting.scss";
import "././TestcaseResultRow/TestcaseResultRow.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import { useLoader } from "../../loader/LoaderContext";
import { Link, useParams } from "react-router-dom";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import WebSocketService from "../../../api/WebSocketService";
import TestcaseResultRow from "./TestcaseResultRow/TestcaseResultRow";
import TestCaseErrorRow from "./TestCaseErrorRow/TestCaseErrorRow";
import { set_header } from "../../../reducers/homeReducer";
import { handleErrorResponse } from "../../../utils/utils";
import { useDispatch } from "react-redux";
import { TestProcessAPI } from "../../../api/TestProcessAPI";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { Spin } from "antd";
import { TestcaseResultStateConstants } from "../../../constants/testcaseResult_constants";
import {
  StopOutlined,
  PlayCircleOutlined,
  InfoCircleOutlined,
  ReloadOutlined,
} from "@ant-design/icons";

/*
  AutomatedTesting Component 

  This page displays the results of the automated testcaseresults. 
*/
export default function AutomatedTesting() {
  const { testRequestId } = useParams();
  const [testcaseName, setTestCaseName] = useState();
  const [testcaseRequestResult, setTestcaseRequestResult] = useState();
  const { showLoader, hideLoader } = useLoader();
  const [expandedIndexes, setExpandedIndexes] = useState([]);
  const [expandedSpecIndexes, setExpandedSpecIndexes] = useState([]);
  const [expandedErrorIndex, setExpandedErrorIndex] = useState([]);
  const { TESTREQUEST_REFOBJURI } = RefObjUriConstants;
  const [stopLoader, setStopLoader] = useState(false);
  const [stopAndResetLoader, setStopAndResetLoader] = useState(false);
  const [finishedTestcaseCount, setFinishedTestcaseCount] = useState(0);
  // const [resultFlag, setResultFlag] = useState(false);

  const [data, setData] = useState([]);
  const { stompClient, webSocketConnect, webSocketDisconnect } =
    WebSocketService();
  const dispatch = useDispatch();
  const clickHandler = () => {
    notification.info({
      className: "notificationInfo",
      message: "Info",
      placement: "bottom-right",
      description: "No actions yet",
    });
  };

  // Function to resets all tests
  const handleResetButton = () => {
    const params = {
      refObjUri: RefObjUriConstants.TESTREQUEST_REFOBJURI,
      refId: testRequestId,
      automated: true,
      reset: true,
    };
    TestProcessAPI.stopTestProcess(testRequestId, params)
      .then(() => {
        notification.success({
          className: "notificationSuccess",
          placement: "top",
          message: "Test results have been reset successfully!",
        });
      })
      .catch((error) => {});
  };

  // Function handles stopping ongoing testcases and resetting all the testcases.
  const handleInterruptButton = (reset) => {
    if (reset) {
      setStopAndResetLoader(true);
    } else {
      setStopLoader(true);
    }
    const params = {
      refObjUri: RefObjUriConstants.TESTREQUEST_REFOBJURI,
      refId: testRequestId,
      automated: true,
      reset,
    };
    TestProcessAPI.stopTestProcess(testRequestId, params)
      .then(() => {
        notification.success({
          className: "notificationSuccess",
          placement: "top",
          message: `Testing has been stopped ${
            reset ? "and reset" : ""
          } successfully! Please allow some time for processing.`,
        });
      })
      .catch((error) => {});
  };

  const toggleComponentAccordian = (id, index) => {
    const headerElement = document.getElementById(`header-${id}`);
    if (headerElement) {
      headerElement.classList.toggle("active");
    }
    setExpandedIndexes((prevExpandedIndexes) =>
      prevExpandedIndexes.includes(`${id}-${index}`)
        ? prevExpandedIndexes.filter((i) => i !== `${id}-${index}`)
        : [...prevExpandedIndexes, `${id}-${index}`]
    );
  };

  const toggleSpecificationAccordian = (id, index) => {
    const headerElement = document
      .getElementById(`spec-${id}`)
      .querySelector(".name");
    if (headerElement) {
      headerElement.classList.toggle("active");
    }
    setExpandedSpecIndexes((prevExpandedIndexes) =>
      prevExpandedIndexes.includes(`${id}-${index}`)
        ? prevExpandedIndexes.filter((i) => i !== `${id}-${index}`)
        : [...prevExpandedIndexes, `${id}-${index}`]
    );
  };

  const toggleTestCaseErrorAccordian = (id, index) => {
    const headerElement = document.getElementById(`error-${id}`);
    if (headerElement) {
      headerElement.classList.toggle("active");
    }
    setExpandedErrorIndex((prevExpandedIndexes) =>
      prevExpandedIndexes.includes(`${id}-${index}`)
        ? prevExpandedIndexes.filter((i) => i !== `${id}-${index}`)
        : [...prevExpandedIndexes, `${id}-${index}`]
    );
  };

  // This function starts all the tests once all the testcases have been reset.
  const handleStartTesting = (isResume = false) => {
    const params = {
      testRequestId,
      refObjUri: TESTREQUEST_REFOBJURI,
      automated: true,
      refId: testRequestId,
    };
    TestResultAPI.startTests(params)
      .then(() => {
        const message = `Testing has been ${
          isResume ? "resumed" : "started"
        } successfully!`;
        notification.success({
          className: "notificationSuccess",
          placement: "top",
          message: message
        });
      })
      .catch((error) => {});
  };

  // This function fetches data from the server and initiates web socket connection and receives real time updates.
  // The data recieved in grouped on the basis of the type of testcase.
  // Tracks completed testcases and displays them on the UI.
  const fetchTestCaseResultDataAndStartWebSocket = async () => {
    showLoader();
    try {
      const response = await TestResultAPI.getMultipleTestcaseResultStatus({
        testRequestId,
        automated: true,
      });
      var testcaseCount = 0;
      const grouped = [];
      for (let item of response) {
        if (item.refObjUri.split(".").pop() === "ComponentInfo") {
          grouped.push({
            ...item,
            specifications: [],
          });
        } else if (item.refObjUri.split(".").pop() === "SpecificationInfo") {
          grouped[grouped.length - 1].specifications.push({
            ...item,
            testCases: [],
          });
        } else if (item.refObjUri.split(".").pop() === "TestcaseInfo") {
          grouped[grouped.length - 1].specifications[
            grouped[grouped.length - 1].specifications.length - 1
          ].testCases.push(item);
          if (
            item.state ===
            TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
          ) {
            testcaseCount++;
          }
        } else {
          setTestcaseRequestResult(item);
          // Start the webSocket connection
          webSocketConnect();
        }
      }
      setFinishedTestcaseCount(testcaseCount);
      setData(grouped);
      hideLoader();
    } catch (error) {
      hideLoader();
    }
  };

  // The below function fetches the testcaseinfo based on test request Id.
  const testCaseInfo = () => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        setTestCaseName(res.name);
      })
      .catch((error) => {});
  };

  // Use Effect which fetches testcase info and initiates web WebSocketService.
  useEffect(() => {
    dispatch(set_header("Automated Verification"));
    fetchTestCaseResultDataAndStartWebSocket();
    testCaseInfo();
    return () => {
      // Disconnect WebSocket when component unmounts
      webSocketDisconnect();
    };
  }, []);

  // Method to track the number of finishes testcases.
  let changeState = (newState, oldState) => {
    if (
      oldState ===
        TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_INPROGRESS ||
      oldState === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
    ) {
      if (
        newState ===
        TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
      ) {
        setFinishedTestcaseCount(1);
      } else if (
        newState === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT
      ) {
        setFinishedTestcaseCount(0);
      }
    }
  };

  useEffect(() => {
    // Close the connection once the request is finished
    if (stompClient && stompClient.connected) {
      const destination =
        "/testcase-result/automated/" + testcaseRequestResult.id;
      stompClient.subscribe(destination, (msg) => {
        const parsedTestcaseResult = JSON.parse(msg.body);
        if (!!parsedTestcaseResult) {
          // setResultFlag(true);
          setTestcaseRequestResult(parsedTestcaseResult);
        }
      });
      const destination2 =
        "/testcase-result/attribute/" + testcaseRequestResult.id;
      stompClient.subscribe(destination2, (msg) => {
        const parsedTestcaseResult = JSON.parse(msg.body);
        if (!!parsedTestcaseResult.testcaseResultAttributesEntities) {
          var isInterrupted = false;
          var reset = false;
          for (var attribute of parsedTestcaseResult.testcaseResultAttributesEntities) {
            if (attribute.key === "is_interrupted") {
              isInterrupted = attribute.value === "true";
            }
            if (attribute.key === "reset") {
              reset = attribute.value === "true";
            }
          }
          if (isInterrupted) {
            if (reset) {
              setStopAndResetLoader(true);
              setStopLoader(false);
            } else {
              setStopLoader(true);
              setStopAndResetLoader(false);
            }
          } else {
            setStopLoader(false);
            setStopAndResetLoader(false);
          }
        }
      });
    }
  }, [stompClient]);

  return (
    <div id="automatedTesting">
      <div id="wrapper">
        <div className="container automation-testing">
          <div className="col-12">
            <div className="d-flex justify-content-between">
              <Breadcrumb className="custom-breadcrumb mb-3">
                <Breadcrumb.Item>
                  <Link to="/applications" className="breadcrumb-item">
                    Applications
                  </Link>
                </Breadcrumb.Item>
                <Breadcrumb.Item>
                  <Link
                    to={`/choose-test/${testRequestId}`}
                    className="breadcrumb-item"
                  >
                    {testcaseName}
                  </Link>
                </Breadcrumb.Item>
                <Breadcrumb.Item className="breadcrumb-item">
                  Automated Verification
                </Breadcrumb.Item>
              </Breadcrumb>
              {
                <div className="d-flex gap-2">
                  <>
                    {" "}
                    {testcaseRequestResult?.state ===
                      TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_INPROGRESS && (
                      <>
                        <div>
                          <button
                            className={`btn small btn-sm mt-0 btn-danger px-4 abtn`}
                            onClick={() => handleInterruptButton(true)}
                            disabled={stopAndResetLoader || stopLoader}
                          >
                            <ReloadOutlined />
                            <span className="mx-2">Stop and Reset</span>

                            {stopAndResetLoader && <Spin size="small" />}
                          </button>
                        </div>
                        <div>
                          <button
                            className="btn small btn-sm mt-0 btn-danger px-2 abtn"
                            onClick={() => handleInterruptButton(false)}
                            disabled={stopAndResetLoader || stopLoader}
                          >
                            <StopOutlined />
                            <span className="m-1">Stop</span>

                            {stopLoader && <Spin size="small" />}
                          </button>
                        </div>
                      </>
                    )}
                    {(testcaseRequestResult?.state ===
                      TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED ||
                      (testcaseRequestResult?.state ===
                        TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT &&
                        !!finishedTestcaseCount)) && (
                      <>
                        <button
                          className={`btn small btn-sm mt-0 btn-primary px-4 abtn`}
                          style={{ backgroundColor: "#009fc8" }}
                          onClick={() => handleResetButton()}
                        >
                          <ReloadOutlined />
                          <span className="mx-2">Reset</span>
                        </button>
                      </>
                    )}
                    {testcaseRequestResult?.state ===
                      TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT &&
                      !!finishedTestcaseCount && (
                        <>
                          <button
                            className={`btn small btn-sm mt-0 btn-success px-4 abtn`}
                            onClick={() => handleStartTesting(true)}
                          >
                            <PlayCircleOutlined />
                            <span className="m-1">Resume</span>
                          </button>
                        </>
                      )}
                    {testcaseRequestResult?.state ===
                      TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT &&
                      !finishedTestcaseCount && (
                        <>
                          <button
                            className="btn small btn-sm mt-0 px-2 btn-success abtn"
                            onClick={() => handleStartTesting()}
                            // disabled={!resultFlag}
                          >
                            <PlayCircleOutlined />
                            <span className="m-1">Start</span>
                            {/* {!resultFlag && <Spin size="small" />} */}
                          </button>
                        </>
                      )}
                  </>
                </div>
              }
            </div>
            <div class="col-12 test-wrapper">
              <div class=" d-flex justify-content-between width-90">
                <div class="">
                  <h3>COMPONENT</h3>
                </div>
                <div class="test-result-row ">
                  <h3>RESULT</h3>
                </div>
              </div>
            </div>
            <div>
              <ul class="accordion-list">
                {data &&
                  data.map((component, index) => (
                    <li className="ac-list-item" key={index}>
                      <h3
                        onClick={() =>
                          toggleComponentAccordian(component?.id, index)
                        }
                        id={`header-${component?.id}`}
                      >
                        {component?.name}{" "}
                        <TestcaseResultRow
                          key={`component-result-${component?.id}`}
                          testcaseResultType={"component"}
                          testcaseResultItem={component}
                          stompClient={stompClient}
                        ></TestcaseResultRow>
                      </h3>
                      {/* <div className="collapse-icon"></div> */}
                      {component?.specifications.map(
                        (specification, specIndex) => (
                          <div key={specIndex}>
                            <div
                              className={`answer-wrapper ${
                                expandedIndexes.includes(
                                  `${component?.id}-${index}`
                                )
                                  ? ""
                                  : "expanded"
                              }`}
                              id={`index-${index}`}
                            >
                              <div className="answer pt-3">
                                <div
                                  className="collapse-sublist1 d-flex align-items-center width-100"
                                  id={`spec-${specification?.id}`}
                                >
                                  <div className="subtitle-box d-flex align-items-center width-100"   onClick={() =>
                                        toggleSpecificationAccordian(
                                          specification?.id,
                                          specIndex
                                        )
                                      }>
                                    <h4
                                      className="name"
                                    
                                    >
                                      {specification?.name}
                                    </h4>
                                    <span className="list-border"></span>
                                  </div>
                                  <TestcaseResultRow
                                    key={`specification-result-${specification?.id}`}
                                    testcaseResultType={"specification"}
                                    testcaseResultItem={specification}
                                    stompClient={stompClient}
                                  ></TestcaseResultRow>
                                </div>
                                {specification.testCases?.map(
                                  (testcase, caseIndex) => (
                                    <ul
                                      key={caseIndex}
                                      className={`sublist-wrapper sw-1 ${
                                        expandedSpecIndexes.includes(
                                          `${specification?.id}-${specIndex}`
                                        )
                                          ? ""
                                          : "expanded"
                                      }`}
                                    >
                                      <li className="sub-list fail-detail">
                                        <div className="d-flex align-items-center width-100">
                                          <h6>{testcase?.name}</h6>
                                          <span className="sub-list-border"></span>
                                        </div>
                                        <TestcaseResultRow
                                          key={`testcase-result-${testcase?.id}`}
                                          testcaseResultType={"testcase"}
                                          testcaseResultItem={testcase}
                                          stompClient={stompClient}
                                          changeState={changeState}
                                          caseIndex={caseIndex}
                                          toggleTestCaseErrorAccordian={toggleTestCaseErrorAccordian}
                                        ></TestcaseResultRow>
                                      </li>
                                        <TestCaseErrorRow
                                          testcaseResultItem={testcase}
                                          stompClient={stompClient}
                                          changeState={changeState}
                                          expandedErrorIndex={expandedErrorIndex}
                                          caseIndex={caseIndex}
                                        />
                                    </ul>
                                  )
                                )}
                              </div>
                            </div>
                          </div>
                        )
                      )}
                    </li>
                  ))}
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
