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
import { TestRequestAPI } from "../../../api/TestRequestAPI";

import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
export default function ManualTesting() {
	const { testRequestId } = useParams();
	const [currentComponentIndex, setCurrentComponentIndex] = useState();
	const [currentSpecificationIndex, setCurrentSpecificationIndex] = useState();
	const [currentTestcaseIndex, setCurrentTestcaseIndex] = useState();
	const [currentComponent, setCurrentComponent] = useState();
	const [currentSpecification, setCurrentSpecification] = useState();
	const [currentTestcase, setCurrentTestcase] = useState();
	const [testcaseResults, setTestcaseResults] = useState();

	const { Item } = Tabs;
	const [testcaseName, setTestCaseName] = useState();
	const navigate = useNavigate();
	
	useEffect(() => {
		TestResultAPI.getTestCases(testRequestId)
			.then((res) => {
				const testcaseResults = [];
				res.content.forEach(testcaseResult => {
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
					}
				});
				setTestcaseResults(testcaseResults);
			})
			.catch((error) => {
				throw error;
			});
		testCaseInfo();
	}, []);

	useEffect(() => {
		if (!!testcaseResults) {
			selectComponent(0);
		}
	}, [testcaseResults]);
	const testCaseInfo = () => {
		TestRequestAPI.getTestRequestsById(testRequestId)
			.then((res) => {
				console.log("testrequestinfo", res);
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
	}

	const isLastQuestion = () => {
		return (currentComponentIndex === testcaseResults.length - 1)
			&& (currentTestcaseIndex === testcaseResults[currentComponentIndex].childTestcaseResults[currentSpecificationIndex].childTestcaseResults.length - 1)
			&& (currentSpecificationIndex === testcaseResults[currentComponentIndex].childTestcaseResults.length - 1);
	}

	const selectSpecification = (specificationIndex, componentIndex) => {
		specificationIndex = parseInt(specificationIndex);
		if (componentIndex === undefined) { componentIndex = currentComponentIndex; }
		setCurrentSpecification(
			testcaseResults[componentIndex]
				.childTestcaseResults[specificationIndex]);
		setCurrentSpecificationIndex(specificationIndex);
		selectTestcase(0, specificationIndex, componentIndex);
	}

	const selectTestcase = (testcaseIndex, specificationIndex, componentIndex) => {
		if (componentIndex === undefined) { componentIndex = currentComponentIndex; }
		if (specificationIndex === undefined) { specificationIndex = currentSpecificationIndex; }
		testcaseIndex = parseInt(testcaseIndex);
		setCurrentTestcase(
			testcaseResults[componentIndex]
				.childTestcaseResults[specificationIndex]
				.childTestcaseResults[testcaseIndex]
		);
		setCurrentTestcaseIndex(testcaseIndex);
	}

	const selectNextTestcase = () => {
		if (currentTestcaseIndex === testcaseResults[currentComponentIndex].childTestcaseResults[currentSpecificationIndex].childTestcaseResults.length - 1) {
			selectNextSpecification();
		} else {
			selectTestcase(currentTestcaseIndex + 1, currentSpecificationIndex, currentComponentIndex);
		}
	}

	const selectNextSpecification = () => {
		if (currentSpecificationIndex === testcaseResults[currentComponentIndex].childTestcaseResults.length - 1) {
			selectNextComponent();
		} else {
			selectSpecification(currentSpecificationIndex + 1, currentComponentIndex);
		}
	}

	const selectNextComponent = () => {
		if (currentComponentIndex === testcaseResults.length - 1) {
			navigate(
				`/dashboard/choose-test/${testRequestId}`
			)
		} else {
			selectComponent(currentComponentIndex + 1);
		}
	}

	const refreshCurrentTestcase = (testcase) => {
		testcaseResults[currentComponentIndex]
			.childTestcaseResults[currentSpecificationIndex]
			.childTestcaseResults[currentTestcaseIndex] = testcase;
	}


	return !!testcaseResults && !!currentComponent && (
		<div id="wrapper" className="stepper-wrapper">
			<div class="bcca-breadcrumb">
				<div class="bcca-breadcrumb-item">Manual Testing</div>
				<div class="bcca-breadcrumb-item" onClick={()=>{navigate(`/dashboard/choose-test/${testRequestId}`)}}>{testcaseName}</div>
				<div class="bcca-breadcrumb-item" onClick={()=>{navigate(`/dashboard/applications`)}}>Applications</div>
			</div>
			<span>
				<b>Component </b>
			</span>
			<Select
				onChange={selectComponent}
				className="select"
				value={currentComponent.name}
			>
				{testcaseResults
					.map((components, index) => {
						return (
							<Select.Option
								key={components.id}
								value={index}
							>
								{components.name}
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
							tab={specification.name}
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


