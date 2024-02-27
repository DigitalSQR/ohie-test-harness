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
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
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
	var { stompClient, webSocketConnect, webSocketDisconnect } = WebSocketService();
	const { showLoader, hideLoader } = useLoader();
  const dispatch = useDispatch();
	const { Item } = Tabs;
	const [testcaseName, setTestCaseName] = useState();
	const navigate = useNavigate();

	const fetchTestCaseResultDataAndStartWebSocket = async () => {
		try {
			const res = await TestResultAPI.getTestCases(testRequestId);
			const testcaseResults = [];
			let testcaseRequestResult;
			for (let item of res.content) {
				const testcaseResult = await TestResultAPI.getTestcaseResultStatus(item.id, { manual: true });
				if (testcaseResult.refObjUri === RefObjUriConstants.COMPONENT_REFOBJURI) {
					testcaseResult.childTestcaseResults = [];
					testcaseResults.push(testcaseResult);
				} else if (testcaseResult.refObjUri === RefObjUriConstants.SPECIFICATION_REFOBJURI) {
					testcaseResult.childTestcaseResults = [];
					testcaseResults[testcaseResults.length - 1].childTestcaseResults.push(testcaseResult);
				} else if (testcaseResult.refObjUri === RefObjUriConstants.TESTCASE_REFOBJURI) {
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
			if (testcaseRequestResult?.state !== "testcase.result.status.finished") {
				webSocketConnect();
			}
		} catch (error) {
			console.log(error);
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
			var destination = '/testcase-result/' + testcaseRequestResult.id;
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
				destination = '/testcase-result/' + component.id;
				var componentSubscription = stompClient.subscribe(destination, (msg) => {
					const parsedComponentTestcaseResult = JSON.parse(msg.body);
					parsedComponentTestcaseResult.childTestcaseResults = component.childTestcaseResults;
					testcaseResults[componentIndex] = parsedComponentTestcaseResult;
					setTestcaseResults(testcaseResults);
					if (parsedComponentTestcaseResult?.state === "testcase.result.status.finished") {
						componentSubscription.unsubscribe();
					}
				});
				component.childTestcaseResults.forEach((specification, specificationIndex) => {
					// Listener for the specification
					destination = '/testcase-result/' + specification.id;
					var specificationSubscription = stompClient.subscribe(destination, (msg) => {
						const parsedSpecificationTestcaseResult = JSON.parse(msg.body);
						parsedSpecificationTestcaseResult.childTestcaseResults = specification.childTestcaseResults;
						testcaseResults[componentIndex].childTestcaseResults[specificationIndex] = parsedSpecificationTestcaseResult;
						setTestcaseResults(testcaseResults);
						if (parsedSpecificationTestcaseResult?.state === "testcase.result.status.finished") {
							specificationSubscription.unsubscribe();
						}
					});
				});
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
			.catch(() => {
				notification.error({
					description: "Oops something went wrong!",
					placement: "bottomRight",
				});
			});
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


	return !!testcaseResults && !!currentComponent && (
		<div id="wrapper" className="stepper-wrapper">
			<div class="bcca-breadcrumb">
				<div class="bcca-breadcrumb-item">Manual Testing</div>
				<div class="bcca-breadcrumb-item" onClick={()=>{navigate(`/choose-test/${testRequestId}`)}}>{testcaseName}</div>
				<div class="bcca-breadcrumb-item" onClick={()=>{navigate(`/applications`)}}>Applications</div>
			</div>
			<span>
				<b>Component  </b>
			</span>
			<Select
				onChange={selectComponent}
				className="select"
				value={{ label: (
					<span>
						{currentComponent.name}
						{currentComponent?.state === "testcase.result.status.finished" && (
							<>&nbsp;<i style={{ color: "green" }} className="bi bi-check-circle-fill"></i></>
						)}
					</span>
				) }}
				style={{ width: '200px' }}
			>
				{testcaseResults
					.map((components, index) => {
						return (
							<Select.Option
								key={components.id}
								value={index}
							>
								{
									<span>
										{components.name}
										{components?.state === "testcase.result.status.finished" && (
											<>&nbsp;<i style={{ color: "green" }} className="bi bi-check-circle-fill"></i></>
										)}
									</span>
								}
							</Select.Option>
						);
					})}
			</Select>
			{!!currentSpecification && (
				<Tabs
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
								<span>
									{specification.name}
									{specification?.state === "testcase.result.status.finished" && (
										<>&nbsp;<i style={{ color: "green" }} className="bi bi-check-circle-fill"></i></>
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
					))}
				</Tabs>
			)}
		</div>
	);
}
