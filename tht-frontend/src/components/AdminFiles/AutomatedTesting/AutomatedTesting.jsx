import React, { useEffect, useState } from "react";
import { Breadcrumb, notification } from "antd";
import "./automatedtesting.scss";
import "././TestcaseResultRow/TestcaseResultRow.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import { useLoader } from "../../loader/LoaderContext";
import { useNavigate, useParams } from "react-router-dom";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import WebSocketService from "../../../api/WebSocketService";
import TestcaseResultRow from "./TestcaseResultRow/TestcaseResultRow";
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

  const { TESTREQUEST_REFOBJURI } = RefObjUriConstants;
  const [stopLoader, setStopLoader] = useState(false);
  const [stopAndResetLoader, setStopAndResetLoader] = useState(false);
  const [finishedTestcaseCount, setFinishedTestcaseCount] = useState(0);
  const [resultFlag, setResultFlag]=useState(false);

  const [data, setData] = useState([]);
  const navigate = useNavigate();
  const { stompClient, webSocketConnect, webSocketDisconnect } =
    WebSocketService();
  const dispatch = useDispatch();
  const clickHandler = () => {
    notification.info({
      className:"notificationInfo",
      message:"Info",
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
        message: "Success",
        description: "Test results have been reset successfully!",
        });
      }).catch((error) => {
        
      });
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
        message: "Success",
        description: `Testing has been stopped ${reset ? 'and reset' : ''} successfully! Please allow some time for processing.`,
      });
    }).catch((error) => {
        
    });
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
        const message = `Testing has been ${isResume ? 'resumed' : 'started'} successfully!`;
        notification.success({
        className: "notificationSuccess",
        placement: "top",
        message: "Success",
        description: message,
        });
      }).catch((error) => {
        
      });
  };

  // This function fetches data from the server and initiates web socket connection and receives real time updates.
  // The data recieved in grouped on the basis of the type of testcase.
  // Tracks completed testcases and displays them on the UI.
  const fetchTestCaseResultDataAndStartWebSocket = async () => {
    showLoader();
    try {
      const response = await TestResultAPI.getMultipleTestcaseResultStatus({testRequestId,automated:true});
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
          if(item.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED) {
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

  const toggleTestCaseRow = (testcaseId) => {
    const newData = data.map((component) => ({
      ...component,
      specifications: component.specifications.map((specification) => ({
        ...specification,
        testCases: specification.testCases.map((testcase) => ({
          ...testcase,
          class:
            testcase.id === testcaseId
              ? testcase.class === "show"
                ? "hide"
                : "show"
              : testcase.class,
        })),
      })),
    }));

    setData(newData);
  };

  // This function toggles the visibility of a specification row based on its ID.
  const toggleSpecificationRow = (specificationId) => {
    const newData = data.map((component) => ({
      ...component,
      specifications: component.specifications.map((specification) => ({
        ...specification,
        class:
          specification.id === specificationId
            ? specification.class === "show"
              ? "hide"
              : "show"
            : specification.class,
      })),
    }));
    setData(newData);
  };

  // This function toggles the visibility of a component row based on its ID.
  const toggleComponentRow = (componentId) => {
    const newData = data.map((component) => ({
      ...component,
      class:
        component.id === componentId
          ? component.class === "show"
            ? "hide"
            : "show"
          : component.class,
    }));
    setData(newData);
  };

  // The below function fetches the testcaseinfo based on test request Id.
  const testCaseInfo = () => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        setTestCaseName(res.name);
      }).catch((error) => {
       
      });
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
    if(oldState === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_INPROGRESS || oldState === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED) {   
      if(newState === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED) {
        setFinishedTestcaseCount(1)
      } else if(newState === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT) {
        setFinishedTestcaseCount(0)
      }
    }
  }

  useEffect(() => {
    // Close the connection once the request is finished
    if (stompClient && stompClient.connected) {
      const destination = "/testcase-result/automated/" + testcaseRequestResult.id;
      stompClient.subscribe(destination, (msg) => {
        const parsedTestcaseResult = JSON.parse(msg.body);
        if(!!parsedTestcaseResult){
          setResultFlag(true);
          setTestcaseRequestResult(parsedTestcaseResult);
        }
      });
      const destination2 = "/testcase-result/attribute/" + testcaseRequestResult.id;
      stompClient.subscribe(destination2, (msg) => {
        const parsedTestcaseResult = JSON.parse(msg.body);
        if (!!parsedTestcaseResult.testcaseResultAttributesEntities) {
          var isInterrupted = false;
          var reset = false;
          for(var attribute of parsedTestcaseResult.testcaseResultAttributesEntities) {
            if(attribute.key === "is_interrupted") {
              isInterrupted = attribute.value === "true";
            }
            if(attribute.key === "reset") {
              reset = attribute.value === "true";
            }
          }
          if (isInterrupted) {
            if(reset) {
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
      <div className="container">
        <div className="col-12">
          <div className="d-flex justify-content-between">
            <Breadcrumb className="custom-breadcrumb">
              <Breadcrumb.Item href="" onClick={() => { navigate(`/applications`); }} className="breadcrumb-item">Applications</Breadcrumb.Item>
              <Breadcrumb.Item href="" onClick={() => { navigate(`/choose-test/${testRequestId}`); }} className="breadcrumb-item">{testcaseName}</Breadcrumb.Item>
              <Breadcrumb.Item className="breadcrumb-item">Automated Verification</Breadcrumb.Item>
            </Breadcrumb>
            {(
              <div className="d-flex gap-2">
                <>
                  {" "}
                  {testcaseRequestResult?.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_INPROGRESS && ( <>
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
                  {(testcaseRequestResult?.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED || (testcaseRequestResult?.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT && !!finishedTestcaseCount)) && (<>
                    <button
                        className={`btn small btn-sm mt-0 btn-primary px-4 abtn`}
                        onClick={() => handleResetButton()}
                      >
                        <ReloadOutlined />
                        <span className="mx-2">Reset</span>
                      </button>
                  </>)}
                  {(testcaseRequestResult?.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT && !!finishedTestcaseCount) && (<>
                      <button
                        className={`btn small btn-sm mt-0 btn-success px-4 abtn`}
                        onClick={() => handleStartTesting(true)}
                      >
                        <PlayCircleOutlined />
                        <span className="m-1">Resume</span>
                      </button>
                  </>)}
                  {testcaseRequestResult?.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT && !finishedTestcaseCount && (<>
                      <button
                        className="btn small btn-sm mt-0 px-2 btn-success abtn"
                        onClick={() => handleStartTesting()}
                        disabled={!resultFlag}
                      >
                        <PlayCircleOutlined />
                        <span className="m-1">Start</span>
                        {!resultFlag && <Spin size="small" />}
                      </button>
                  </>)}
                </>
              </div>
            )}
          </div>
          <div className="table-responsive mb-5">
            <table className="data-table capitalize-words">
              <thead>
                <tr>
                  <th>Component</th>
                  <th>Specification</th>
                  <th style={{ width: "40%" }}>Test Cases</th>
                  <th>Result</th>
                  <th>Duration</th>
                  <th></th>
                </tr>
              </thead>
              <tbody id="testcaseResultRow">
                {!!data &&
                  data.map((component) => [
                    <TestcaseResultRow
                      key={`component-result-${component?.id}`}
                      testcaseResultType={"component"}
                      testcaseResultItem={component}
                      stompClient={stompClient}
                      toggleClass={component?.class}
                      toggleFunction={toggleComponentRow}
                    ></TestcaseResultRow>,
                    component?.specifications?.map((specification) => [
                      <TestcaseResultRow
                        key={`specification-result-${specification?.id}`}
                        testcaseResultType={"specification"}
                        testcaseResultItem={specification}
                        stompClient={stompClient}
                        toggleClass={specification?.class}
                        toggleFunction={toggleSpecificationRow}
                      ></TestcaseResultRow>,
                      specification.testCases?.map((testcase) => [
                        <TestcaseResultRow
                          key={`testcase-result-${testcase?.id}`}
                          testcaseResultType={"testcase"}
                          testcaseResultItem={testcase}
                          stompClient={stompClient}
                          toggleClass={testcase?.class}
                          toggleFunction={toggleTestCaseRow}
                          changeState={changeState}
                        ></TestcaseResultRow>,
                      ]),
                    ]),
                  ])}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    </div>
  );
}
