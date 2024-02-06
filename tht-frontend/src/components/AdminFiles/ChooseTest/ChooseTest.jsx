import workflow_logo from "../../../styles/images/workflow-testing.png";
import functional_logo from "../../../styles/images/functional-testing.png";
import { useNavigate, useParams } from "react-router-dom";
import "./choose-test.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import { Fragment, useEffect, useState } from "react";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { notification, Progress, Button } from "antd";
import { TestcaseResultStateConstants } from "../../../constants/testcaseResult_constants";
import { handleErrorResponse } from "../../../utils/utils";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
export default function ChooseTest() {
	const { testRequestId } = useParams();
	const { TESTCASE_REFOBJURI, TESTREQUEST_REFOBJURI } = RefObjUriConstants;
	const [manualEntries, setManualEntries] = useState([]);
	const [testcaseName, setTestCaseName] = useState();
	const [manualProgress, setManualProgress] = useState(0);
	const [automatedProgress, setAutomatedProgress] = useState(0);
	const [totalManualTestcaseResults, setTotalManualTestcaseResults] =
		useState(0);
	const [totalAutomatedTestcaseResults, setTotalAutomatedTestcaseResults] =
		useState(0);

	const [testcaseResults, setTestCaseResults] = useState([]);
	const navigate = useNavigate();

	useEffect(() => {
		// console.log("In use Effect => ", totalManualTestcaseResults);
		const completedManualTestcaseResults = testcaseResults.filter(
			(tescaseResults) =>
				tescaseResults.manual == true &&
				(tescaseResults.state ==
					TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_SKIP ||
					tescaseResults.state ==
						TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED)
		).length;

		if (totalManualTestcaseResults !== 0) {
			setManualProgress(
				(completedManualTestcaseResults / totalManualTestcaseResults) *
					100
			);
		}
	}, [totalManualTestcaseResults]);

	useEffect(() => {
		var total = testcaseResults.filter(
			(totalTestCaseResults) =>
				totalTestCaseResults.manual == true &&
				totalTestCaseResults.state !==
					TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT
		).length;
		setTotalManualTestcaseResults(total);
		// console.log("manutotal ", total);

		// console.log(totalManualTestcaseResults);
		var automatedTotal = testcaseResults.filter(
			(totalTestCaseResults) =>
				totalTestCaseResults.manual == false &&
				totalTestCaseResults.state !==
					TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT
		).length;

		setTotalAutomatedTestcaseResults(automatedTotal);
		console.log("auto total ", automatedTotal);

		// console.log(completedManualTestcaseResults);
	}, [testcaseResults]);

	useEffect(() => {
		const completedAutomatedTestcaseResults = testcaseResults.filter(
			(tescaseResults) =>
				tescaseResults.manual == false &&
				(tescaseResults.state ==
					TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_SKIP ||
					tescaseResults.state ==
						TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED)
		).length;
		// console.log(completedManualTestcaseResults);
		console.log("completed automate", completedAutomatedTestcaseResults);

		if (totalAutomatedTestcaseResults !== 0) {
			setAutomatedProgress(
				(completedAutomatedTestcaseResults /
					totalAutomatedTestcaseResults) *
					100
			);
		}
	}, [totalAutomatedTestcaseResults]);

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
				throw error;
			});
	};
	const handleStartTesting = (manual, link) => {
		const params = { testRequestId, TESTREQUEST_REFOBJURI, manual };
		TestResultAPI.startTests(params)
			.then((response) => {
				console.log(response);
				notification.success({
					description: "Test Started Successfully",
					placement: "bottomRight",
				});
				loadProgress();
			})
			.catch((error) => {
				console.log(error);
				notification.info({
					description: handleErrorResponse(error.response.data),
					placement: "bottomRight",
				});
			});
	};
	//finish 2 or skip 3 5 / 50 not draft
	//50 60
	const testCaseResultInfo = () => {
		TestRequestAPI.getTestRequestsById(testRequestId)
			.then((res) => {
				console.log("testrequestinfo", res);
				setTestCaseName(res.productname);
			})
			.catch(() => {
				notification.error({
					description: "Oops something went wrong!",
					placement: "bottomRight",
				});
			});
	};
	const testCaseInfo = () => {
		TestRequestAPI.getTestRequestsById(testRequestId)
			.then((res) => {
				console.log("testrequestinfo", res);
				setTestCaseName(res.productName);
			})
			.catch(() => {
				notification.error({
					description: "Oops something went wrong!",
					placement: "bottomRight",
				});
			});
	};
	useEffect(() => {
		loadProgress();
		testCaseInfo();
	}, []);

	useEffect(() => {
		console.log("manualProgress updated to:", manualProgress);
	}, [manualProgress]);

	return (
		<div id="wrapper">
			<div className="col-12 pt-3">
				<nav aria-label="breadcrumb">
					<ol class="breadcrumb">
						<li class="breadcrumb-item">
							<a href="/dashboard/applications">Applications</a>
						</li>
						<li class="breadcrumb-item active" aria-current="page">
							{testcaseName}
						</li>
					</ol>
				</nav>
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
									className="btn btn-primary  btn-sm mt-4"
									style={{ alignItems: "flex-end" }}
									onClick={() => {
										handleStartTesting(
											true,
											"manual-testing"
										);
									}}
								>
									Start Testing
								</button>
							)}
							{totalManualTestcaseResults != 0 && (
								<Fragment>
									<Progress
										percent={Math.floor(manualProgress)}
									/>
									<Button
										onClick={() =>
											navigate(
												`/dashboard/manual-testing/${testRequestId}`
											)
										}
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
					<div
						className="testing-grid"
					>
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
									className="btn btn-primary small btn-sm mt-4"
									style={{ alignItems: "flex-end" }}
									onClick={() => {
										handleStartTesting(false);
									}}
								>
									Start Testing
								</button>
							)}
							{totalAutomatedTestcaseResults != 0 && (
								<Fragment>
									<Progress percent={automatedProgress} />
									<Button
										onClick={() =>
											navigate(
												`/dashboard/automated-testing/${testRequestId}`
											)
										}
									>
										Resume
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
