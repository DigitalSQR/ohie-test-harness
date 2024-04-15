import { useNavigate, useParams } from "react-router-dom";
import { useDispatch } from "react-redux";
import { log_out } from "../../../reducers/authReducer";
import { Fragment, useEffect, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import doc_logo from "../../../styles/images/doc.png";
import pdf_logo from "../../../styles/images/pdf.png";
import img_logo from "../../../styles/images/img.png";
import question_img_logo from "../../../styles/images/question-img.png";
import "./manual-testing.scss";
import { Breadcrumb, Tabs, notification } from "antd";
import { Select } from "antd";
import { Option } from "antd/es/mentions";
import TestCase from "../TestCase/TestCase";
import TestcaseVertical from "../TestcaseVertical/TestcaseVertical";
import WebSocketService from "../../../api/WebSocketService";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import { useLoader } from "../../loader/LoaderContext";

import { CAccordion, CAccordionItem, CAccordionHeader, CAccordionBody } from '@coreui/react';

import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { TestcaseResultStateConstants } from "../../../constants/testcaseResult_constants";
import { set_header } from "../../../reducers/homeReducer";
import TestCaseVerticalView from "../TestCaseVerticalView/TestCaseVerticalView";

/* 
  Manual Testing Page. 

  This page enables the tester to initiate manual testing for a test-request. 
  Tester can start answering questions, attach files for evidence/proof for any inadequacy she/he 
  encounters. 
*/
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
  const [isTop, setIsTop] = useState(false);
  const [isHorizontal, setIsHorizontal] = useState(false);
  const [test, setTest] = useState(0);
  var { stompClient, webSocketConnect, webSocketDisconnect } = WebSocketService();
  const { showLoader, hideLoader } = useLoader();
  const dispatch = useDispatch();
  const { Item } = Tabs;
  const [testcaseName, setTestCaseName] = useState();
  const navigate = useNavigate();
  const openComponentIndex = -1;

  // This function fetches data for the test-request, sorts data according to the hierarchy, i.e. component,
  // specification  and it's respective testcases.
  // Also updates already finished testcases if any, for a smooth test-case experience. 

  const fetchTestCaseResultDataAndStartWebSocket = async () => {
    try {
      const res = await TestResultAPI.getMultipleTestcaseResultStatus({ manual: true, testRequestId });
      const testcaseResults = [];
      let testcaseRequestResult;
      let finishedTestcases = 0;
      let totalTestcases = 0;
      for (let testcaseResult of res) {
        if (testcaseResult.refObjUri === RefObjUriConstants.COMPONENT_REFOBJURI) {
          testcaseResult.childTestcaseResults = [];
          testcaseResults.push(testcaseResult);
        } else if (testcaseResult.refObjUri === RefObjUriConstants.SPECIFICATION_REFOBJURI) {
          testcaseResult.childTestcaseResults = [];
          testcaseResults[testcaseResults.length - 1].childTestcaseResults.push(testcaseResult);
        } else if (testcaseResult.refObjUri === RefObjUriConstants.TESTCASE_REFOBJURI) {
          totalTestcases++;
          if (testcaseResult.state === TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED) {
            finishedTestcases++;
          }
          testcaseResults[testcaseResults.length - 1]
            .childTestcaseResults[testcaseResults[testcaseResults.length - 1].childTestcaseResults.length - 1]
            .childTestcaseResults
            .push(testcaseResult);
        } else if (testcaseResult.refObjUri === RefObjUriConstants.TESTREQUEST_REFOBJURI) {
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
      console.log(error);
    }
  };

  // UseEffect which is fired on the initial component render, fetches testcase data and disconnects to 
  // web-socket if the component is unmounted.
  useEffect(() => {
    dispatch(set_header("Manual Verification"));
    showLoader();
    fetchTestCaseResultDataAndStartWebSocket();
    testCaseInfo();
    return () => {
      // Disconnect WebSocket when component unmounts
      webSocketDisconnect();
    };
  }, []);

  // This UseEffect is responsible for keeping track of components, specifications and testcases that are
  //  finished. As soon as they are finished, the UI is updated dynamically, which helps the tester, keep track
  //  of his/her progress
  useEffect(() => {
    if (stompClient && stompClient.connected && !!testcaseResults) {
      // Close the connection once the request is finished
      var destination = '/testcase-result/manual/' + testcaseRequestResult.id;
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
          destination = '/testcase-result/manual/' + component.id;
          var componentSubscription = stompClient.subscribe(destination, (msg) => {
            const parsedComponentTestcaseResult = JSON.parse(msg.body);
            parsedComponentTestcaseResult.childTestcaseResults = component.childTestcaseResults;
            testcaseResults[componentIndex] = parsedComponentTestcaseResult;
            setTestcaseResults(testcaseResults);
            if (parsedComponentTestcaseResult?.state === "testcase.result.status.finished") {
              componentSubscription.unsubscribe();
            }
          });
        }
        component.childTestcaseResults.forEach((specification, specificationIndex) => {
          // Listener for the specification
          if (specification?.state !== "testcase.result.status.finished") {
            destination = '/testcase-result/manual/' + specification.id;
            var specificationSubscription = stompClient.subscribe(destination, (msg) => {
              const parsedSpecificationTestcaseResult = JSON.parse(msg.body);
              parsedSpecificationTestcaseResult.childTestcaseResults = specification.childTestcaseResults;
              testcaseResults[componentIndex].childTestcaseResults[specificationIndex] = parsedSpecificationTestcaseResult;
              setTestcaseResults(testcaseResults);
              if (parsedSpecificationTestcaseResult?.state === "testcase.result.status.finished") {
                specificationSubscription.unsubscribe();
              }
            });
          }
          specification.childTestcaseResults.forEach((testcase, testcaseIndex) => {
            // Listener for the test case
            if (testcase?.state !== "testcase.result.status.finished") {
              destination = '/testcase-result/manual/' + testcase.id;
              var testcaseSubscription = stompClient.subscribe(destination, (msg) => {
                const parsedTestcaseResult = JSON.parse(msg.body);
                testcaseResults[componentIndex].childTestcaseResults[specificationIndex].childTestcaseResults[testcaseIndex] = parsedTestcaseResult;
                setTestcaseResults(testcaseResults);
                if (parsedTestcaseResult?.state === "testcase.result.status.finished") {
                  setFinishedTestCasesCount(prevCount => prevCount + 1);
                  testcaseSubscription.unsubscribe();
                }
              });
            }
          });
        });
      });
    }
  }, [stompClient]);

  // The below useEffect ensures that the tester is not brought to a already finished component tests, rather
  // he/she will be routed to the component which is yet to be tested.
  useEffect(() => {
    if (!!testcaseResults && !currentComponentIndex) {
      let componentIndex;
      if (totalTestCasesCount === finishedTestCasesCount) {
        componentIndex = 0;
      }
      else {
        componentIndex = testcaseResults.flat().findIndex(component => component.state !== 'testcase.result.status.finished');
      }
      selectComponent(componentIndex);
      hideLoader();
    }
  }, [testcaseResults]);

  const testCaseInfo = () => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        setTestCaseName(res.name);
      }).catch(() => {

      });
  };

  // This function is used to navigate between components in the select dropdown. The first unfinished component
  // is displayed to the tester. This saves time and testing becomes efficient. This function also 
  // calls the function responsible for the selection of specification.
  const selectComponent = (componentIndex) => {
    componentIndex = parseInt(componentIndex);
    setCurrentComponent(testcaseResults[componentIndex]);
    setCurrentComponentIndex(componentIndex);
    let specificationIndex = 0;
    if (totalTestCasesCount !== finishedTestCasesCount) {
      for (let i = 0; i < testcaseResults[componentIndex].childTestcaseResults.length; i++) {
        const specification = testcaseResults[componentIndex].childTestcaseResults[i];
        if (specification.state !== 'testcase.result.status.finished') {
          specificationIndex = i;
          break;
        }
      }
    }
    selectSpecification(specificationIndex, componentIndex);
  };

  // This function determines when to change the specification tab. Tester is automatically routed to the next 
  // specification as soon as he/she finishes answering all the testcases in that particular specification.
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

  // This function is responsible for selecting the specification tab. The Tester is displayed the first unfinished 
  // specification which saves time and makes the process of testing efficient. 
  const selectSpecification = (specificationIndex, componentIndex) => {
    specificationIndex = parseInt(specificationIndex);
    setTest(specificationIndex);
    if (componentIndex === undefined) {
      componentIndex = currentComponentIndex;
    }
    setCurrentSpecification(
      testcaseResults[componentIndex].childTestcaseResults[specificationIndex]
    );
    setCurrentSpecificationIndex(specificationIndex);
    let testcaseIndex = 0;
    if (totalTestCasesCount !== finishedTestCasesCount) {
      let currentSpecification = testcaseResults[componentIndex].childTestcaseResults[specificationIndex];
      for (let i = 0; i < currentSpecification.childTestcaseResults.length; i++) {
        const testcase = currentSpecification.childTestcaseResults[i];
        if (testcase.state !== 'testcase.result.status.finished') {
          testcaseIndex = i;
          break;
        }
      }
    }
    selectTestcase(testcaseIndex, specificationIndex, componentIndex);
  };

  // Each specifications tab has it's own testcases. The below function is responsible for selecting the testcase.
  // Tester will be taken to the first unfinished testcase. 
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

  // This function is used to dynamically navigate to the desired testcase in the accordion which is made for easy 
  // navigation through all the testcases.
  const selectParticularTestCase = (componentIndex, specificationIndex, testcaseIndex) => {
    selectComponent(componentIndex);
    selectSpecification(specificationIndex, componentIndex);
    if(isHorizontal){
      selectTestcase(testcaseIndex, specificationIndex, componentIndex);
      return;
    }
    if(testcaseIndex>0){
      const idn = testcaseResults[componentIndex].childTestcaseResults[specificationIndex].childTestcaseResults[testcaseIndex-1].id;

      if(test!==specificationIndex){
        setTimeout(() => {
          document.getElementById(idn).scrollIntoView({
            behavior: "smooth",
          });
        }, 800);}
      else{
        document.getElementById(idn).scrollIntoView({
          behavior: "smooth",
        });
       } 
      }
      else{
        if (test !== specificationIndex) {
          setTimeout(() => {
            window.scrollTo({ top: 0, behavior: "smooth" });
          }, 800);
        } else {
          window.scrollTo({ top: 0, behavior: "smooth" });
        }
      }
  }
  
  // This function navigates the tester to the next succeeding testcase, once a testcase has been answered.
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

  // This function navigates the tester to the next succeeding specification, once a testcase finished testcases for a 
  // specification. 
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

  // This function navigates the tester to the next component, once  all the specifications have been finished
  // in a component.
  const selectNextComponent = () => {
    if (currentComponentIndex === testcaseResults.length - 1) {
      navigate(`/choose-test/${testRequestId}`);
    } else {
      selectComponent(currentComponentIndex + 1);
    }
  };

  // This function is used for fetching the latest state of a testcase.
  const refreshCurrentTestcase = (testcase) => {
    testcaseResults[currentComponentIndex].childTestcaseResults[
      currentSpecificationIndex
    ].childTestcaseResults[currentTestcaseIndex] = testcase;
  };

  // Function to handle click on horizontal button
  const handleHorizontalClick = () => {
    setIsHorizontal(true);
    setIsTop(true)
  };

  // Function to handle click on vertical button
  const handleVerticalClick = () => {
    setIsHorizontal(false);
    setIsTop(false)
  };

  return !!testcaseResults && !!currentComponent && (
    <div id="manualQuestions">
      <div id="wrapper" className="stepper-wrapper">
      <Breadcrumb className="mb-3 custom-breadcrumb">
      <Breadcrumb.Item href="" onClick={() => navigate(`/applications`)} className="breadcrumb-item">
        Applications
      </Breadcrumb.Item>
      <Breadcrumb.Item href="" onClick={() => navigate(`/choose-test/${testRequestId}`)} className="breadcrumb-item">
        {testcaseName}
      </Breadcrumb.Item>
      <Breadcrumb.Item className="breadcrumb-item">Manual Verification</Breadcrumb.Item>
    </Breadcrumb>
        <div className="component-container mb-3">
          <div className="row">
            <div className="col-md-6">
              <span className="me-2">
                <b>Component</b>
              </span>
              <Select
                onChange={selectComponent}
                className="select"
                value={{
                  label: (
                    <span>
                      {currentComponent.name}
                      {currentComponent?.state === "testcase.result.status.finished" && (
                        <>
                          &nbsp;
                          <i style={{ color: "green" }} className="bi bi-check-circle-fill"></i>
                        </>
                      )}
                    </span>
                  )
                }}
                style={{ width: '200px' }}
              >
                {testcaseResults.map((components, index) => (
                  <Select.Option key={components.id} value={index}>
                    <span>
                      {components.name}
                      {components?.state === "testcase.result.status.finished" && (
                        <>
                          &nbsp;
                          <i style={{ color: "green" }} className="bi bi-check-circle-fill"></i>
                        </>
                      )}
                    </span>
                  </Select.Option>
                ))}
              </Select>
            </div>
            {/* Below is the button code which is currently commented out */}
            {/* <div className="col-md-6 text-end">
              <div className="layout-buttons">
                <button className={`${isHorizontal ? 'btn btn-outline-secondary   ' : 'btn btn-outline-secondary  '}`} onClick={handleHorizontalClick}>
                  Horizontal
                </button>
                <button className={`${isHorizontal ? 'btn btn-outline-secondary ' : 'btn btn-outline-secondary    '}`} onClick={handleVerticalClick}>
                  Vertical
                </button>
              </div>
            </div> */}
          </div>



        </div>

        <div className="offcanvas offcanvas-end" tabIndex="-1" id="manualTesting" aria-labelledby="manualTestingLabel">
          <div className="offcanvas-header">
            <div className="offcanvas-title">
              <h5 id="manualTestingLabel">Manual Verification </h5>
              <div className="answeredQuest">
                <h6>Status: {finishedTestCasesCount} of {totalTestCasesCount} </h6>
              </div>
            </div>

            <button type="button" className="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
          </div>
          <div className="offcanvas-body manual-testing-sidemenu">
            <CAccordion activeItemKey={openComponentIndex}>

              {testcaseResults.map((component, outerIndex) => (
                <div key={outerIndex}>
                  <CAccordionItem itemKey={outerIndex} key={outerIndex}>
                    <CAccordionHeader className="component-header">
                      {component.name}
                      {component.state === "testcase.result.status.finished" && (
                        <>&nbsp;<i style={{ color: "green" }} className="bi bi-check-circle-fill"></i></>
                      )
                      }

                    </CAccordionHeader>
                    <CAccordionBody>
                      <div>
                        {component.childTestcaseResults.map((specification, innerIndex) => (
                          <div key={innerIndex} className="question-item">
                            <div className="specification-header"> {specification.name}</div>
                            {specification.childTestcaseResults.map((testcase, index) => (
                              <span key={index}>
                                <button
                                  onClick={() => selectParticularTestCase(outerIndex, innerIndex, index)}
                                  className={`round-span ${testcase.state === "testcase.result.status.finished" ? "round-span-success" : ""}`}
                                >
                                  {index + 1}
                                </button>
                              </span>
                            ))}
                          </div>
                        ))}
                      </div>
                    </CAccordionBody>
                  </CAccordionItem>

                </div>
              ))}

            </CAccordion>
          </div>
        </div>
        <div className="vertical-tab-list">
          {!!currentSpecification && (
            <Tabs
              destroyInactiveTabPane={true}
              tabPosition={isTop ? 'top' : 'left'}
              activeKey={currentSpecificationIndex.toString()}
              onChange={(val) => {
                selectSpecification(val, currentComponentIndex);
              }}
            >
              {testcaseResults[currentComponentIndex].childTestcaseResults.map((specification, index) => (
                <Item
                  key={index}
                  value={specification.id}
                  tab={
                    <span style={{ display: "flex", alignItems:"center" }}>
                      {specification.name}
                      {specification?.state === "testcase.result.status.finished" && (
                        <>&nbsp;<i style={{ lineHeight:"16px" }} className="bi bi-check2-all fs-4 completed-questions"></i></>
                      )}
                    </span>
                  }
                >
                  {/* Supplying data to both the question formats, if needed in the future */}
                  {/* {isHorizontal ? (
                    <TestCase
                      currentTestcaseIndex={currentTestcaseIndex}
                      currentTestcase={currentTestcase}
                      currentSpecification={currentSpecification}
                      selectTestcase={selectTestcase}
                      selectNextTestcase={selectNextTestcase}
                      refreshCurrentTestcase={refreshCurrentTestcase}
                      isLastQuestion={isLastQuestion}
                    ></TestCase>
                  ) :
                    <TestCaseVerticalView 
                    currentTestcaseIndex={currentTestcaseIndex}
                    currentTestcase={currentTestcase}
                    currentSpecification={currentSpecification}
                    selectTestcase={selectTestcase}
                    selectNextTestcase={selectNextTestcase}
                    refreshCurrentTestcase={refreshCurrentTestcase}
                    isLastQuestion={isLastQuestion}
                    selectNextSpecification={selectNextSpecification}
                    ></TestCaseVerticalView>
                  } */}
                      <TestCaseVerticalView 
                    currentTestcaseIndex={currentTestcaseIndex}
                    currentTestcase={currentTestcase}
                    currentSpecification={currentSpecification}
                    selectTestcase={selectTestcase}
                    selectNextTestcase={selectNextTestcase}
                    refreshCurrentTestcase={refreshCurrentTestcase}
                    isLastQuestion={isLastQuestion}
                    selectNextSpecification={selectNextSpecification}
                    ></TestCaseVerticalView>
                </Item>
              ))}
            </Tabs>
          )}
        </div>
        {/* {!isHorizontal &&

} */}
      </div>

   
        <div className="fixed-button">
          <button data-bs-toggle="offcanvas" href="#manualTesting" aria-controls="manualTesting">Status: {finishedTestCasesCount} of {totalTestCasesCount}</button>
        </div>
     
    </div>

  );
}

