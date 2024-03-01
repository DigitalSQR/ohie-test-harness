import { useNavigate, useParams } from "react-router-dom";
import { useDispatch } from "react-redux";
import { log_out } from "../../../reducers/authReducer";
import { Fragment, useEffect, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import doc_logo from "../../../styles/images/doc.png";
import pdf_logo from "../../../styles/images/pdf.png";
import img_logo from "../../../styles/images/img.png";
import question_img_logo from "../../../styles/images/question-img.png";
import "./functional-testing.scss";
import { Tabs, notification } from "antd";
import { Select } from "antd";
import { Option } from "antd/es/mentions";
import TestCase from "../TestCase/TestCase";
import WebSocketService from "../../../api/WebSocketService";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import { useLoader } from "../../loader/LoaderContext";

import {
  CAccordion,
  CAccordionItem,
  CAccordionHeader,
  CAccordionBody,
} from "@coreui/react";

import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { TestcaseResultStateConstants } from "../../../constants/testcaseResult_constants";
import { set_header } from "../../../reducers/homeReducer";

export default function ManualTesting() {
  const { testRequestId } = useParams();
  const [currentComponentIndex, setCurrentComponentIndex] = useState();
  const [currentSpecificationIndex, setCurrentSpecificationIndex] = useState();
  const [currentTestcaseIndex, setCurrentTestcaseIndex] = useState();
  const [currentComponent, setCurrentComponent] = useState();
  const [currentSpecification, setCurrentSpecification] = useState();
  const [currentTestcase, setCurrentTestcase] = useState();
  const [testcaseResults, setTestcaseResults] = useState();
  const [testcaseRequestResult, setTestcaseRequestResult] = useState();
  const [finishedTestCasesCount, setFinishedTestCasesCount] = useState(0);
  const [totalTestCasesCount, setTotalTestCasesCount] = useState(0);
  var { stompClient, webSocketConnect, webSocketDisconnect } =
    WebSocketService();
  const { showLoader, hideLoader } = useLoader();
  const dispatch = useDispatch();
  const { Item } = Tabs;
  const [testcaseName, setTestCaseName] = useState();
  const navigate = useNavigate();
  const openComponentIndex = -1;

  const fetchTestCaseResultDataAndStartWebSocket = async () => {
    try {
      showLoader();
      const res = await TestResultAPI.getTestCases(testRequestId);
      const testcaseResults = [];
      let testcaseRequestResult;
      let finishedTestcases = 0;
      let totalTestcases = 0;
      for (let item of res.content) {
        const testcaseResult = await TestResultAPI.getTestcaseResultStatus(
          item.id,
          { manual: true }
        );
        if (
          testcaseResult.refObjUri === RefObjUriConstants.COMPONENT_REFOBJURI
        ) {
          testcaseResult.childTestcaseResults = [];
          testcaseResults.push(testcaseResult);
        } else if (
          testcaseResult.refObjUri ===
          RefObjUriConstants.SPECIFICATION_REFOBJURI
        ) {
          testcaseResult.childTestcaseResults = [];
          testcaseResults[testcaseResults.length - 1].childTestcaseResults.push(
            testcaseResult
          );
        } else if (
          testcaseResult.refObjUri === RefObjUriConstants.TESTCASE_REFOBJURI
        ) {
          totalTestcases++;
          if (
            testcaseResult.state ===
            TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
          ) {
            finishedTestcases++;
          }
          testcaseResults[testcaseResults.length - 1].childTestcaseResults[
            testcaseResults[testcaseResults.length - 1].childTestcaseResults
              .length - 1
          ].childTestcaseResults.push(testcaseResult);
        } else if (
          testcaseResult.refObjUri === RefObjUriConstants.TESTREQUEST_REFOBJURI
        ) {
          setTestcaseRequestResult(testcaseResult);
          testcaseRequestResult = testcaseResult;
        }
      }
      setTestcaseResults(testcaseResults);
      setTotalTestCasesCount(totalTestcases);
      setFinishedTestCasesCount(finishedTestcases);
      if (testcaseRequestResult?.state !== "testcase.result.status.finished") {
        webSocketConnect();
      }
    } catch (error) {
      hideLoader();
    }
  };

  useEffect(() => {
    dispatch(set_header("Manual Testing"));
    showLoader();
    fetchTestCaseResultDataAndStartWebSocket();
    testCaseInfo();
    return () => {
      // Disconnect WebSocket when component unmounts
      webSocketDisconnect();
    };
  }, []);

  useEffect(() => {
    if (stompClient && stompClient.connected && !!testcaseResults) {
      // Close the connection once the request is finished
      var destination = "/testcase-result/" + testcaseRequestResult.id;
      stompClient.subscribe(destination, (msg) => {
        const parsedTestcaseResult = JSON.parse(msg.body);
        setTestcaseRequestResult(parsedTestcaseResult);
        if (parsedTestcaseResult?.state === "testcase.result.status.finished") {
          webSocketDisconnect();
        }
      });
      // Recieve message when the manual testcase result item is finished
      testcaseResults.forEach((component, componentIndex) => {
        // Listener for the component
        if (component?.state !== "testcase.result.status.finished") {
          destination = "/testcase-result/" + component.id;
          var componentSubscription = stompClient.subscribe(
            destination,
            (msg) => {
              const parsedComponentTestcaseResult = JSON.parse(msg.body);
              parsedComponentTestcaseResult.childTestcaseResults =
                component.childTestcaseResults;
              testcaseResults[componentIndex] = parsedComponentTestcaseResult;
              setTestcaseResults(testcaseResults);
              if (
                parsedComponentTestcaseResult?.state ===
                "testcase.result.status.finished"
              ) {
                componentSubscription.unsubscribe();
              }
            }
          );
        }
        component.childTestcaseResults.forEach(
          (specification, specificationIndex) => {
            // Listener for the specification
            if (specification?.state !== "testcase.result.status.finished") {
              destination = "/testcase-result/" + specification.id;
              var specificationSubscription = stompClient.subscribe(
                destination,
                (msg) => {
                  const parsedSpecificationTestcaseResult = JSON.parse(
                    msg.body
                  );
                  parsedSpecificationTestcaseResult.childTestcaseResults =
                    specification.childTestcaseResults;
                  testcaseResults[componentIndex].childTestcaseResults[
                    specificationIndex
                  ] = parsedSpecificationTestcaseResult;
                  setTestcaseResults(testcaseResults);
                  if (
                    parsedSpecificationTestcaseResult?.state ===
                    "testcase.result.status.finished"
                  ) {
                    specificationSubscription.unsubscribe();
                  }
                }
              );
            }
            specification.childTestcaseResults.forEach(
              (testcase, testcaseIndex) => {
                // Listener for the test case
                if (testcase?.state !== "testcase.result.status.finished") {
                  destination = "/testcase-result/" + testcase.id;
                  var testcaseSubscription = stompClient.subscribe(
                    destination,
                    (msg) => {
                      const parsedTestcaseResult = JSON.parse(msg.body);
                      testcaseResults[componentIndex].childTestcaseResults[
                        specificationIndex
                      ].childTestcaseResults[testcaseIndex] =
                        parsedTestcaseResult;
                      setTestcaseResults(testcaseResults);
                      if (
                        parsedTestcaseResult?.state ===
                        "testcase.result.status.finished"
                      ) {
                        setFinishedTestCasesCount((prevCount) => prevCount + 1);
                        testcaseSubscription.unsubscribe();
                      }
                    }
                  );
                }
              }
            );
          }
        );
      });
    }
  }, [stompClient]);

  useEffect(() => {
    if (!!testcaseResults && !currentComponentIndex) {
      selectComponent(0);
      hideLoader();
    }
  }, [testcaseResults]);

  const testCaseInfo = () => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        setTestCaseName(res.name);
      })
      .catch(() => {});
  };

  const selectComponent = (componentIndex) => {
    componentIndex = parseInt(componentIndex);
    setCurrentComponent(testcaseResults[componentIndex]);
    setCurrentComponentIndex(componentIndex);
    selectSpecification(0, componentIndex);
  };

  const isLastQuestion = () => {
    return (
      currentComponentIndex === testcaseResults.length - 1 &&
      currentTestcaseIndex ===
        testcaseResults[currentComponentIndex].childTestcaseResults[
          currentSpecificationIndex
        ].childTestcaseResults.length -
          1 &&
      currentSpecificationIndex ===
        testcaseResults[currentComponentIndex].childTestcaseResults.length - 1
    );
  };

  const selectSpecification = (specificationIndex, componentIndex) => {
    specificationIndex = parseInt(specificationIndex);
    if (componentIndex === undefined) {
      componentIndex = currentComponentIndex;
    }
    setCurrentSpecification(
      testcaseResults[componentIndex].childTestcaseResults[specificationIndex]
    );
    setCurrentSpecificationIndex(specificationIndex);
    selectTestcase(0, specificationIndex, componentIndex);
  };

  const selectTestcase = (
    testcaseIndex,
    specificationIndex,
    componentIndex
  ) => {
    if (componentIndex === undefined) {
      componentIndex = currentComponentIndex;
    }
    if (specificationIndex === undefined) {
      specificationIndex = currentSpecificationIndex;
    }
    testcaseIndex = parseInt(testcaseIndex);
    setCurrentTestcase(
      testcaseResults[componentIndex].childTestcaseResults[specificationIndex]
        .childTestcaseResults[testcaseIndex]
    );
    setCurrentTestcaseIndex(testcaseIndex);
  };

  const selectParticularTestCase = (
    componentIndex,
    specificationIndex,
    testcaseIndex
  ) => {
    selectComponent(componentIndex);
    selectSpecification(specificationIndex, componentIndex);
    selectTestcase(testcaseIndex, specificationIndex, componentIndex);
  };

  const selectNextTestcase = () => {
    if (
      currentTestcaseIndex ===
      testcaseResults[currentComponentIndex].childTestcaseResults[
        currentSpecificationIndex
      ].childTestcaseResults.length -
        1
    ) {
      selectNextSpecification();
    } else {
      selectTestcase(
        currentTestcaseIndex + 1,
        currentSpecificationIndex,
        currentComponentIndex
      );
    }
  };

  const selectNextSpecification = () => {
    if (
      currentSpecificationIndex ===
      testcaseResults[currentComponentIndex].childTestcaseResults.length - 1
    ) {
      selectNextComponent();
    } else {
      selectSpecification(currentSpecificationIndex + 1, currentComponentIndex);
    }
  };

  const selectNextComponent = () => {
    if (currentComponentIndex === testcaseResults.length - 1) {
      navigate(`/choose-test/${testRequestId}`);
    } else {
      selectComponent(currentComponentIndex + 1);
    }
  };

  const refreshCurrentTestcase = (testcase) => {
    testcaseResults[currentComponentIndex].childTestcaseResults[
      currentSpecificationIndex
    ].childTestcaseResults[currentTestcaseIndex] = testcase;
  };

  return (
    !!testcaseResults &&
    !!currentComponent && (
      <>
        <div id="wrapper" className="stepper-wrapper">
          <div class="bcca-breadcrumb">
            <div class="bcca-breadcrumb-item">Manual Testing</div>
            <div
              class="bcca-breadcrumb-item"
              onClick={() => {
                navigate(`/choose-test/${testRequestId}`);
              }}
            >
              {testcaseName}
            </div>
            <div
              class="bcca-breadcrumb-item"
              onClick={() => {
                navigate(`/applications`);
              }}
            >
              Applications
            </div>
          </div>
          <span>
            <b>Component </b>
          </span>
          <Select
            onChange={selectComponent}
            className="select"
            value={{
              label: (
                <span>
                  {currentComponent.name}
                  {currentComponent?.state ===
                    "testcase.result.status.finished" && (
                    <>
                      &nbsp;
                      <i
                        style={{ color: "green" }}
                        className="bi bi-check-circle-fill"
                      ></i>
                    </>
                  )}
                </span>
              ),
            }}
            style={{ width: "200px" }}
          >
            {testcaseResults.map((components, index) => {
              return (
                <Select.Option key={components.id} value={index}>
                  {
                    <span>
                      {components.name}
                      {components?.state ===
                        "testcase.result.status.finished" && (
                        <>
                          &nbsp;
                          <i
                            style={{ color: "green" }}
                            className="bi bi-check-circle-fill"
                          ></i>
                        </>
                      )}
                    </span>
                  }
                </Select.Option>
              );
            })}
          </Select>

          <div
            class="offcanvas offcanvas-end"
            tabindex="-1"
            id="manualTesting"
            aria-labelledby="manualTestingLabel"
          >
            <div class="offcanvas-header">
              <div class="offcanvas-title">
                <h5 id="manualTestingLabel">Manual Testing </h5>
                <div class="answeredQuest">
                  <h6>
                    {" "}
                    {finishedTestCasesCount}/{totalTestCasesCount}{" "}
                  </h6>
                </div>
              </div>

              <button
                type="button"
                class="btn-close text-reset"
                data-bs-dismiss="offcanvas"
                aria-label="Close"
              ></button>
            </div>
            <div class="offcanvas-body">
              <CAccordion activeItemKey={openComponentIndex}>
                {testcaseResults.map((component, outerIndex) => (
                  <div key={outerIndex}>
                    <CAccordionItem itemKey={outerIndex} key={outerIndex}>
                      <CAccordionHeader className="component-header">
                        {component.name}
                        {component.state ===
                          "testcase.result.status.finished" && (
                          <>
                            &nbsp;
                            <i
                              style={{ color: "green" }}
                              className="bi bi-check-circle-fill"
                            ></i>
                          </>
                        )}
                      </CAccordionHeader>
                      <CAccordionBody>
                        <div>
                          {component.childTestcaseResults.map(
                            (specification, innerIndex) => (
                              <div key={innerIndex}>
                                <div className="specification-header">
                                  {" "}
                                  {specification.name}
                                </div>
                                {specification.childTestcaseResults.map(
                                  (testcase, index) => (
                                    <span key={index}>
                                      <button
                                        onClick={() =>
                                          selectParticularTestCase(
                                            outerIndex,
                                            innerIndex,
                                            index
                                          )
                                        }
                                        className={`round-span ${
                                          testcase.state ===
                                          "testcase.result.status.finished"
                                            ? "round-span-success"
                                            : ""
                                        }`}
                                      >
                                        {index + 1}
                                      </button>
                                    </span>
                                  )
                                )}
                              </div>
                            )
                          )}
                        </div>
                      </CAccordionBody>
                    </CAccordionItem>
                  </div>
                ))}
              </CAccordion>
            </div>
          </div>
          {!!currentSpecification && (
            <Tabs
              activeKey={currentSpecificationIndex.toString()}
              onChange={(val) => {
                selectSpecification(val, currentComponentIndex);
              }}
            >
              {testcaseResults[currentComponentIndex].childTestcaseResults.map(
                (specification, index) => (
                  <Item
                    key={index}
                    value={specification.id}
                    tab={
                      <span>
                        {specification.name}
                        {specification?.state ===
                          "testcase.result.status.finished" && (
                          <>
                            &nbsp;
                            <i
                              style={{ color: "green" }}
                              className="bi bi-check-circle-fill"
                            ></i>
                          </>
                        )}
                      </span>
                    }
                  >
                    <TestCase
                      currentTestcaseIndex={currentTestcaseIndex}
                      currentTestcase={currentTestcase}
                      currentSpecification={currentSpecification}
                      selectTestcase={selectTestcase}
                      selectNextTestcase={selectNextTestcase}
                      refreshCurrentTestcase={refreshCurrentTestcase}
                      isLastQuestion={isLastQuestion}
                    ></TestCase>
                  </Item>
                )
              )}
            </Tabs>
          )}
        </div>

        <div className="fixed-button">
          <button
            data-bs-toggle="offcanvas"
            href="#manualTesting"
            aria-controls="manualTesting"
          >
            {finishedTestCasesCount}/{totalTestCasesCount}
          </button>
        </div>
      </>
    )
  );
}
