import React, { useEffect, useState } from "react";
import { notification } from "antd";
import "./automatedtesting.scss";
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

export default function AutomatedTesting() {
  const { testRequestId } = useParams();
  const [testcaseName, setTestCaseName] = useState();
  const [testcaseRequestResult, setTestcaseRequestResult] = useState();
  const { showLoader, hideLoader } = useLoader();

  const { TESTREQUEST_REFOBJURI } = RefObjUriConstants;
  const [stopLoader, setStopLoader] = useState(false);
  const [stopAndResetLoader, setStopAndResetLoader] = useState(false);
  const [finishedTestcaseCount, setFinishedTestcaseCount] = useState(0);

  const [data, setData] = useState([]);
  const navigate = useNavigate();
  const { stompClient, webSocketConnect, webSocketDisconnect } =
    WebSocketService();
  const dispatch = useDispatch();
  const clickHandler = () => {
    notification.info({
      placement: "bottom-right",
      description: "No actions yet",
    });
  };

  const handleResetButton = () => {
    const params = {
      refObjUri: RefObjUriConstants.TESTREQUEST_REFOBJURI,
      refId: testRequestId,
      automated: true,
      reset: true,
    };
    notification.info({
      description: "Reset Process has been initiated",
      placement: "bottomRight",
    });
    TestProcessAPI.stopTestProcess(testRequestId, params)
      .then(() => {
        notification.success({
          description: "Testing Process has been Reset successully",
          placement: "bottomRight",
        });
      }).catch((error) => {
        
      });
  };

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
    notification.info({
      description: reset
        ? "Stop and Reset process has been initiated. Please wait for some time"
        : "Stop process has been reinitiated. Please wait for some time",
      placement: "bottomRight",
    });
    TestProcessAPI.stopTestProcess(testRequestId, params)
    .then(() => {
      notification.success({
        description: "Process to interrupt has been started successully. Please wait for some time",
        placement: "bottomRight",
      });
    }).catch((error) => {
        
    });
  };

  const handleStartTesting = () => {
    const params = {
      testRequestId,
      refObjUri: TESTREQUEST_REFOBJURI,
      automated: true,
      refId: testRequestId,
    };
    TestResultAPI.startTests(params)
      .then(() => {
        notification.success({
          description: "Testing Process has been started successully",
          placement: "bottomRight",
        });
      }).catch((error) => {
        
      });
  };

  const fetchTestCaseResultDataAndStartWebSocket = async () => {
    showLoader();
    try {
      const response = await TestResultAPI.getTestCaseResultByTestRequestId(
        testRequestId,
        null,
        true
      );
      var testcaseCount = 0;
      const grouped = [];
      for (let item of response.content) {
        item = await TestResultAPI.getTestcaseResultStatus(item.id, {
          automated: true,
        });
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

  const testCaseInfo = () => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        setTestCaseName(res.name);
      }).catch((error) => {
       
      });
  };
  useEffect(() => {
    dispatch(set_header("Automated Testing"));
    fetchTestCaseResultDataAndStartWebSocket();
    testCaseInfo();
    return () => {
      // Disconnect WebSocket when component unmounts
      webSocketDisconnect();
    };
  }, []);

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
        setTestcaseRequestResult(parsedTestcaseResult);
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
    <div className="Workflow-testing-wrapper">
      <div className="container">
        <div className="col-12">
          <div className="d-flex justify-content-between">
            <div className="bcca-breadcrumb">
              <div className="bcca-breadcrumb-item">Automated Testing</div>
              <div
                className="bcca-breadcrumb-item"
                onClick={() => {
                  navigate(`/choose-test/${testRequestId}`);
                }}
              >
                {testcaseName}
              </div>
              <div
                className="bcca-breadcrumb-item"
                onClick={() => {
                  navigate(`/applications`);
                }}
              >
                Applications
              </div>
            </div>
            {(
              <div className="d-flex gap-2">
                <>
                  {" "}
                  {testcaseRequestResult?.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_INPROGRESS && ( <>
                    <div>
                        <button
                          className={`btn small btn-sm mt-0 display btn-danger px-4 abtn`}
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
                          className="btn small btn-sm mt-0 display btn-danger px-2 btn-success abtn"
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
                        className={`btn small btn-sm mt-0 display btn-primary px-4 abtn`}
                        onClick={() => handleResetButton()}
                      >
                        <ReloadOutlined />
                        <span className="mx-2">Reset</span>
                      </button>
                  </>)}
                  {(testcaseRequestResult?.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT && !!finishedTestcaseCount) && (<>
                      <button
                        className={`btn small btn-sm mt-0 display btn-success px-4 abtn`}
                        onClick={handleStartTesting}
                      >
                        <PlayCircleOutlined />
                        <span className="m-1">Resume</span>
                      </button>
                  </>)}
                  {testcaseRequestResult?.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT && !finishedTestcaseCount && (<>
                      <button
                        className="btn small btn-sm mt-0 display px-2 btn-success abtn"
                        onClick={handleStartTesting}
                      >
                        <PlayCircleOutlined />
                        <span className="m-1">Start</span>
                      </button>
                  </>)}
                </>
              </div>
            )}
          </div>
          <div className="table-responsive mb-5">
            <table className="data-table">
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
              <tbody>
                {!!data &&
                  data.map((component) => [
                    <TestcaseResultRow
                      key={`component-result-${component?.id}`}
                      testcaseResultType={"component"}
                      testResultId={component.id}
                      stompClient={stompClient}
                      toggleClass={component?.class}
                      toggleFunction={toggleComponentRow}
                    ></TestcaseResultRow>,
                    component?.specifications?.map((specification) => [
                      <TestcaseResultRow
                        key={`specification-result-${specification?.id}`}
                        testcaseResultType={"specification"}
                        testResultId={specification.id}
                        stompClient={stompClient}
                        toggleClass={specification?.class}
                        toggleFunction={toggleSpecificationRow}
                      ></TestcaseResultRow>,
                      specification.testCases?.map((testcase) => [
                        <TestcaseResultRow
                          key={`testcase-result-${testcase?.id}`}
                          testcaseResultType={"testcase"}
                          testResultId={testcase.id}
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
  );
}
