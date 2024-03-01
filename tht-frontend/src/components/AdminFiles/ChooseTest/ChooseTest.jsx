import workflow_logo from "../../../styles/images/workflow-testing.png";
import functional_logo from "../../../styles/images/functional-testing.png";
import { useNavigate, useParams } from "react-router-dom";
import "./choose-test.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import { Fragment, useEffect, useState } from "react";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { notification, Progress, Button } from "antd";
import { CheckCircleFilled, SyncOutlined } from "@ant-design/icons";

import { TestcaseResultStateConstants } from "../../../constants/testcaseResult_constants";
import { handleErrorResponse } from "../../../utils/utils";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";
import WebSocketService from "../../../api/WebSocketService";

export default function ChooseTest() {
  const { testRequestId } = useParams();
  const { TESTCASE_REFOBJURI, TESTREQUEST_REFOBJURI } = RefObjUriConstants;
  const [testcaseName, setTestCaseName] = useState();
  const [totalManualTestcaseResults, setTotalManualTestcaseResults] = useState(0);
  const [totalAutomatedTestcaseResults, setTotalAutomatedTestcaseResults] = useState(0);
  const [totalFinishedManual, setTotalFinishedManual] = useState(0);
  const [totalFinishedAutomated, setTotalFinishedAutomated] = useState(0);
  const [totalAllManual, setTotalAllManual] = useState(0);
  const [totalAllAutomated, setTotalAllAutomated] = useState(0);
  const [totalInprogressAutomated, setTotalInprogressAutomated] = useState(0);
  const [testcaseResults, setTestCaseResults] = useState([]);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { stompClient, webSocketConnect, webSocketDisconnect } = WebSocketService();

  useEffect(() => {
    var totalManual = 0;
    var totalFinishedManual = 0;
    var totalInprogressAutomated = 0;
    var totalAutomated = 0;
    var totalFinishedAutomated = 0;
    var totalAllAutomated = 0;
    var totalAllMenual = 0;
    testcaseResults.forEach((testcaseResult) => {
      if (
        testcaseResult.state !==
        TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT
      ) {
        if (!!testcaseResult.manual) {
          totalManual++;
          if (
            testcaseResult.state ===
              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_SKIP ||
            testcaseResult.state ===
              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
          ) {
            totalFinishedManual++;
          }
        }
        if (!!testcaseResult.automated) {
          totalAutomated++;
          if (
            testcaseResult.state ===
              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_SKIP ||
            testcaseResult.state ===
              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
          ) {
            totalFinishedAutomated++;
          }
        }
      }
      if (
        !!testcaseResult.automated &&
        testcaseResult.state ===
          TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_INPROGRESS
      ) {
        totalInprogressAutomated++;
      }
      if (!!testcaseResult.manual) {
        totalAllMenual++;
      }
      if (!!testcaseResult.automated) {
        totalAllAutomated++;
      }
    });
    setTotalAllManual(totalAllMenual);
    setTotalAllAutomated(totalAllAutomated);
    setTotalFinishedManual(totalFinishedManual);
    setTotalFinishedAutomated(totalFinishedAutomated);
    setTotalManualTestcaseResults(totalManual);
    setTotalAutomatedTestcaseResults(totalAutomated);
    setTotalInprogressAutomated(totalInprogressAutomated);

    // Start the WebSocket Connection
    if (
      (totalFinishedManual < totalManual ||
        totalFinishedAutomated < totalAutomated ||
        totalAutomated === 0 ||
        totalManual === 0) &&
      stompClient === null &&
      testcaseResults.length > 0
    ) {
      webSocketConnect();
    }

    // Disconnect the WebSocket onnce all of the testcase are finished
    if (
      totalFinishedManual === totalManual &&
      totalFinishedAutomated === totalAutomated &&
      totalAutomated > 0 &&
      totalManual > 0
    ) {
      webSocketDisconnect();
    }
  }, [testcaseResults]);

  const loadProgress = () => {
    const params = {
      testRequestId: testRequestId,
      refObjUri: TESTCASE_REFOBJURI,
    };
    TestResultAPI.fetchCasesForProgressBar(params)
      .then((res) => {
        setTestCaseResults(res.data.content);
      })
      .catch((error) => {
       
      });
  };

  const handleStartTesting = (manual, automated) => {
    const params = {
      testRequestId,
      refObjUri: TESTREQUEST_REFOBJURI,
      refId: testRequestId,
    };
    if (!!manual) {
      params.manual = true;
    }
    if (!!automated) {
      params.automated = true;
    }
    TestResultAPI.startTests(params)
      .then((response) => {
        notification.success({
          description: "Testing Process has been Started Successfully",
          placement: "bottomRight",
        });
        if (!!automated) {
          navigate(`/automated-testing/${testRequestId}`);
        } else {
          navigate(`/manual-testing/${testRequestId}`);
        }
        loadProgress();
      }).catch((error) => {
       
      });
  };

  const testCaseInfo = () => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        setTestCaseName(res.name);
        dispatch(set_header(res.name));
      }).catch((error) => {
       
      });
  };

  useEffect(() => {
    if (stompClient && stompClient.connected) {
      testcaseResults.forEach((testcaseResult, index) => {
        // Listener for the testcase if in pending
        if (
          testcaseResult.state !==
          TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
        ) {
          const destination = "/testcase-result/" + testcaseResult.id;
          var subscription = stompClient.subscribe(destination, (msg) => {
            const parsedTestcaseResult = JSON.parse(msg.body);
            setTestCaseResults((prevTestcaseResults) => {
              const updatedTestcaseResults = [...prevTestcaseResults];
              updatedTestcaseResults[index] = parsedTestcaseResult;
              return updatedTestcaseResults;
            });
            if (
              parsedTestcaseResult?.state === "testcase.result.status.finished"
            ) {
              subscription.unsubscribe();
            }
          });
        }
      });
    }
  }, [stompClient]);

  useEffect(() => {
    loadProgress();
    testCaseInfo();
    return () => {
      // Disconnect WebSocket when component unmounts
      webSocketDisconnect();
    };
  }, []);

  return (
    <div id="wrapper">
      <div className="col-12 pt-3">
        <div class="bcca-breadcrumb">
          <div class="bcca-breadcrumb-item">{testcaseName}</div>
          <div
            class="bcca-breadcrumb-item"
            onClick={() => {
              navigate(`/applications`);
            }}
          >
            Applications
          </div>
        </div>
        <h5>Choose Testing Type</h5>
        <p className="text-gray">
          Select the type to start testing application with OpenHIE.{" "}
        </p>
        <div className="d-flex flex-wrap">
          <div className="testing-grid">
            <div className="icon-box">
              <img src={functional_logo} />
            </div>
            <div className="text-box">
              <h6 className="">Manual Testing</h6>
              <p className="mb-0">
                If you need more info, please check out{" "}
                <a className="text-blue" href="#">
                  Guideline.
                </a>
              </p>
              {totalManualTestcaseResults == 0 && (
                <button
                  className="btn btn-primary btn-sm mt-4 display"
                  onClick={() => {
                    handleStartTesting(true, null);
                  }}
                >
                  Start Testing
                </button>
              )}
              {totalManualTestcaseResults != 0 && (
                <Fragment>
                  <Progress
                    percent={Math.floor(
                      (totalFinishedManual /
                        (!!totalFinishedManual
                          ? totalAllManual
                          : totalManualTestcaseResults)) *
                        100
                    )}
                    format={() => {
                      if (
                        Math.floor(
                          (totalFinishedManual /
                            (!!totalFinishedManual
                              ? totalAllManual
                              : totalManualTestcaseResults)) *
                            100
                        ) === 100
                      ) {
                        return <span>Done</span>;
                      } else {
                        return (
                          <span>
                            {totalFinishedManual}/
                            {!!totalFinishedManual
                              ? totalAllManual
                              : totalManualTestcaseResults}
                          </span>
                        );
                      }
                    }}
                  />
                  <Button
                    onClick={() => navigate(`/manual-testing/${testRequestId}`)}
                  >
                    Resume
                  </Button>
                </Fragment>
              )}

              {/* <div className="progress-bar-line"> */}
              {/* <div className="progress-fill"></div> */}
              {/* <div className="progress-value">20%</div>  */}
              {/* </div> */}
            </div>
          </div>
          <div className="testing-grid">
            <div className="icon-box">
              <img src={workflow_logo} />
            </div>
            <div className="text-box">
              <h6 className="">Automated Testing</h6>
              <p className="mb-0">
                If you need more info, please check out{" "}
                <a className="text-blue" href="#">
                  Guideline.
                </a>
              </p>
              {totalAutomatedTestcaseResults == 0 && (
                <button
                  className="btn btn-primary small btn-sm mt-4 display"
                  onClick={() => {
                    handleStartTesting(null, true);
                  }}
                >
                  Start Testing
                </button>
              )}

              {totalAutomatedTestcaseResults != 0 && (
                <Fragment>
                  <Progress
                    percent={Math.floor(
                      (totalFinishedAutomated /
                        (!!totalFinishedAutomated
                          ? totalAllAutomated
                          : totalAutomatedTestcaseResults)) *
                        100
                    )}
                    format={() => {
                      if (
                        Math.floor(
                          (totalFinishedAutomated /
                            (!!totalFinishedAutomated
                              ? totalAllAutomated
                              : totalAutomatedTestcaseResults)) *
                            100
                        ) === 100
                      ) {
                        return <CheckCircleFilled className="text-success" />;
                      } else {
                        return (
                          <div>
                            {" "}
                            {totalInprogressAutomated != 1 ? (
                              <div></div>
                            ) : (
                              <SyncOutlined spin/>
                            )}
                            <span className="ml-2">
                              {" "}
                              {totalFinishedAutomated}/
                              {!!totalFinishedAutomated
                                ? totalAllAutomated
                                : totalAutomatedTestcaseResults}
                               
                            </span>
                          </div>
                        );
                      }
                    }}
                    strokeColor={
                      totalInprogressAutomated != 1
                        ? Math.floor(
                            (totalFinishedAutomated /
                              (!!totalFinishedAutomated
                                ? totalAllAutomated
                                : totalAutomatedTestcaseResults)) *
                              100
                          ) === 100
                          ? "green"
                          : "red"
                        : "blue"
                    }
                    status={
                      Math.floor(
                        (totalFinishedAutomated /
                          (!!totalFinishedAutomated
                            ? totalAllAutomated
                            : totalAutomatedTestcaseResults)) *
                          100
                      ) === 100
                        ? "inactive"
                        : "active"
                    }
                  />
                  <Button
                    onClick={() =>
                      navigate(`/automated-testing/${testRequestId}`)
                    }
                  >
                    Show Result
                  </Button>
                </Fragment>
              )}
              {/* <div className="progress-bar-line"></div> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
