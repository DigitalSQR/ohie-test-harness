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

export default function ManualTesting() {
	const { testRequestId } = useParams();
	const [testId, setTestId] = useState();
	const [manualQuestions, setManualQuestions] = useState([]);
	const [componentId, setComponentId] = useState();
	const [activeSpecification, setActiveSpecification] = useState();
	const [defaultValue, setDefaultValue] = useState();
	const { Item } = Tabs;
	const [testcaseName, setTestCaseName] = useState();
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
	useEffect(() => {
		TestResultAPI.getTestCases(testRequestId)
			.then((res) => {
				setTestId(res.content[0].id);
				console.log(res.content);
				setManualQuestions(res.content);
				setComponentId(res.content[1].id);
				setDefaultValue(res.content[1].name);
			})
			.catch((error) => {
				throw error;
			});
		testCaseInfo();
	}, []);

	const nextSpecification = (rank, nextRefId) => {
		console.log(manualQuestions);
		const nextSpecification = manualQuestions.filter((entry) => {
			return entry.rank == rank;
		});
		console.log("the next specification ", nextSpecification);
		// console.log(nextSpecification[0].refId)
		if (
			nextSpecification[0] != undefined &&
			nextSpecification[0]?.refId.includes(nextRefId)
		) {
			setActiveSpecification(nextSpecification[0].id);
		} else {
			notification.info({
				description: "This was the last question",
				placement: "bottomRight",
			});
		}
	};

	const getSpecifications = () => {
		const items = [];

		manualQuestions
			.filter(
				(specification) =>
					specification.parentTestcaseResultId == componentId
			)
			.map((specification) => {
				items.push({
					title: specification.name,
					id: specification.id, // i changed it from specification to specification.id
				});
			});

		return items;
	};

	const onComponentChange = (val) => {
		setComponentId(val);
		const index = manualQuestions.findIndex(
			(specification) => specification.parentTestcaseResultId == val
		);
		setActiveSpecification(manualQuestions[index].id);
	};

	return (
		<div id="wrapper" className="stepper-wrapper">

			<nav aria-label="breadcrumb">
				<ol class="breadcrumb">
					<li class="breadcrumb-item">
						<a href="/dashboard/applications">Applications</a>
					</li>
					<li class="breadcrumb-item" aria-current="page">
						<a href={`/dashboard/choose-test/${testRequestId}`}>
							{testcaseName}
						</a>
					</li>
					<li class="breadcrumb-item active">Manual Testing</li>
				</ol>
			</nav>
			<span>
				<b>Component </b>
			</span>
			<Select
				onChange={onComponentChange}
				style={{ width: "145px", marginBottom: "10px" }}
				defaultValue={"Client Registry"}
			>
				{manualQuestions
					.filter((components) => {
						return components.parentTestcaseResultId == testId;
					})
					.map((components) => {
						return (
							<Select.Option
								key={components.id}
								value={components.id}
							>
								{components.name}
							</Select.Option>
						);
					})}
			</Select>
			{!!componentId && (
				<Tabs
					type="card"
					activeKey={activeSpecification}
					onChange={(val) => {
						setActiveSpecification(val);
					}}
				>
					{getSpecifications().map((specification) => (
						<Item key={specification.id} tab={specification.title}>
							<TestCase
								specificationId={specification.id}
								nextSpecification={nextSpecification}
							></TestCase>
						</Item>
					))}
				</Tabs>
			)}
		</div>
	);
}

// import Item from "antd/es/list/Item";

// export default function ManualTesting() {
// 	const { Item } = Tabs;
// 	const { Option } = Select;
// 	const navigate = useNavigate();
// 	const dispatch = useDispatch();
// 	const { testRequestId } = useParams();
// 	const [manualQuestions, setManualQuestions] = useState([]);
// 	const [options, setOptions] = useState([]);
// 	const [testId, setTestId] = useState("");
// 	const [componentId, setComponentId] = useState();
// 	const [specificationId, setSpecificationId] = useState();
// 	const [selectedQuestionIndex, setSelectedQuestionIndex] = useState(0);
// 	// const [testCaseId,setTestCaseId] = useState("");
// 	useEffect(() => {
// 		TestResultAPI.getTestCases(testRequestId, true)
// 			.then((res) => {
// 				setManualQuestions(res.content);
// 				setTestId(res.content[0].id);
// 				console.log(res);
// 			})
// 			.catch((error) => {
// 				throw error;
// 			});
// 		// TestResultAPI.getTestCaseOptions();
// 	}, []);

// 	const fetchOptions = (testcaseId) => {
// 		// const data = { testcaseId: id };
// 		TestResultAPI.getTestCaseOptions(testcaseId)
// 			.then((res) => {
// 				setOptions(res.content);
// 				console.log(options);
// 				return res.content;
// 			})
// 			.catch((err) => {
// 				throw err;
// 			});
// 	};

// 	const handleTabChange = (e) => {
// 		setSpecificationId(e)
// 	}

// 	return (
// 		<div id="wrapper" className="stepper-wrapper">
// 			<span>
// 				<b>Select Component : </b>
// 			</span>
// 			<Select
// 				style={{ width: "145px", marginBottom: "10px" }}
// 				defaultActiveFirstOption={manualQuestions[1]}
// 				onChange={(val) => {
// 					setComponentId(val);
// 				}}
// 			>
// 				{manualQuestions
// 					.filter((question) => {
// 						// console.log(question);
// 						return question.parentTestcaseResultId == testId;
// 					})
// 					.map((question) => {
// 						return (
// 							<Option value={question.id}>
// 								<div
// 									key={question.id}
// 								>
// 									{question.name}
// 								</div>
// 							</Option>
// 						);
// 					})}
// 			</Select>
// 			{
// 				!!componentId ?
// 				<Tabs key={componentId} defaultActiveKey="" activeKey={specificationId} onChange={handleTabChange}>
// 					{manualQuestions
// 						.filter(
// 							(specification) =>
// 								specification.parentTestcaseResultId == componentId
// 						)
// 						.map((specification, index) => {
// 							console.log("HERE index: " + index);
// 							return (
// 								<Item
// 									key={specification.id}
// 									tab={specification.name}
// 								>
// 									{/* {question.parentTestcaseResultId == question.id && (<h>{question.name}</h>)} */}
// 									{manualQuestions
// 										.filter(
// 											(testcase) =>
// 												testcase.parentTestcaseResultId ==
// 												specification.id
// 										)
// 										.map((questions) => {
// 											console.log("HERE 2 index: " + index);
// 											// Specifications!

// 											fetchOptions(specification.id);
// 											return (
// 												<div className="col-12 non-fuctional-requirement">
// 													<div
// 														className="container"
// 														key={questions.id}
// 													>
// 														<div className="row question-box">
// 															<div className="col-md-9 col-12 p-0 question">
// 																<p>
// 																	{questions.name}
// 																</p>
// 																<ul>
// 																	{/* {options.map(
// 																			(
// 																				option
// 																			) => {
// 																				<li>
// 																					{
// 																						option
// 																					}
// 																				</li>;
// 																			}
// 																		)} */}
// 																</ul>
// 															</div>
// 														</div>
// 													</div>
// 												</div>
// 											);
// 										})}
// 								</Item>
// 							);
// 						})}
// 				</Tabs>
// 				: null
// 			}
// 			{/* Tab code */}
// 			<div className="col-12 non-fuctional-requirement">
// 				{/* <div className="container">
// 					<h5 className="text-blue pb-3">
// 						Non-Functional Requirements
// 					</h5>
// 					<div className="row heading">
// 						<div className="col-md-9 col-12 p-0">
// 							<h2>Question</h2>
// 						</div>
// 						<div className="col-md-3 col-12 d-md-flex d-none p-0">
// 							<h2 className="border-left">Reference</h2>
// 						</div>
// 					</div>
// 					<div className="row question-box">
// 						<div className="col-md-9 col-12 p-0 question">
// 							<h2>
// 								CR.Q1. Are you able to find the user interface
// 								from where you can set up rules to find patients
// 								who might be the same? To test this try
// 								accessing the system and check if there is a
// 								dedicated feature or section for patiennt
// 								matching rule.
// 							</h2>
// 							<div className="custom_radio">
// 								<input
// 									type="radio"
// 									id="featured-1"
// 									name="featured"
// 									checked
// 								/>
// 								<label htmlFor="featured-1">
// 									Yes, I easily found the User Interface to
// 									set up patient matching rules.
// 								</label>
// 								<input
// 									type="radio"
// 									id="featured-2"
// 									name="featured"
// 								/>
// 								<label htmlFor="featured-2">
// 									No, I couldn't locate the User Interface for
// 									configuring patient matching rules.
// 								</label>
// 								<input
// 									type="radio"
// 									id="featured-3"
// 									name="featured"
// 								/>
// 								<label htmlFor="featured-3">
// 									I found it but I encountered difficulties,
// 									and it wasn't clear where to find the User
// 									Interface for configuring matching rules.
// 								</label>
// 							</div>
// 							<div className="text-end mb-3">
// 								<div
// 									className="cst-btn-group btn-group"
// 									role="group"
// 									aria-label="Basic example"
// 									style={{ margin: "0 15px" }}
// 								>
// 									<button
// 										type="button"
// 										className="btn cst-btn-default"
// 									>
// 										<i
// 											style={{
// 												transform: "rotate(-45.975deg)",
// 											}}
// 											className="bi bi-paperclip"
// 										></i>
// 										Add Attachments
// 									</button>
// 									<button
// 										type="button"
// 										className="btn cst-btn-default"
// 									>
// 										<i className="bi bi-chat-right-text"></i>
// 										Add Notes
// 									</button>
// 								</div>
// 							</div>
// 							<div className="doc-badge-wrapper">
// 								<div className="doc-badge">
// 									<img src={doc_logo} />
// 									<p>A_125 Documents of req</p>
// 								</div>
// 								<div className="doc-badge">
// 									<img src={pdf_logo} />
// 									<p> A_125 Documents of req sadf </p>
// 								</div>
// 								<div className="doc-badge">
// 									<img src={img_logo} />
// 									<p> A_125 Documents of req </p>
// 								</div>
// 							</div>
// 						</div>
// 						<div className="col-md-3 col-12 p-0">
// 							<div className=" p-2 pt-5 q-img">
// 								<img src={question_img_logo} />
// 								<a>
// 									<i className="bi bi-zoom-in"></i> Click to
// 									enlarge
// 								</a>
// 							</div>
// 						</div>
// 					</div>
// 					<div className="row question-box">
// 						<div className="col-md-9 col-12 p-0 question">
// 							<h2>
// 								CR.Q2. In ‘Potential Matches’ page, select the
// 								actions that you are able to see.
// 							</h2>
// 							<div className="custom-multiselect field-checkbox">
// 								<div className="field-box">
// 									<input
// 										type="checkbox"
// 										name="checkbox-choice"
// 										id="checkbox-choice-1"
// 										value="choice-1"
// 									/>
// 									<label htmlFor="checkbox-choice-1">
// 										Able to see ‘link’ records action
// 									</label>
// 								</div>
// 								<div className="field-box">
// 									<input
// 										type="checkbox"
// 										name="checkbox-choice"
// 										id="checkbox-choice-2"
// 										value="choice-2"
// 									/>
// 									<label htmlFor="checkbox-choice-2">
// 										Able to see ’merge’ records action
// 									</label>
// 								</div>
// 								<div className="field-box">
// 									<input
// 										type="checkbox"
// 										name="checkbox-choice"
// 										id="checkbox-choice-3"
// 										value="choice-3"
// 									/>
// 									<label htmlFor="checkbox-choice-3">
// 										Able to see ‘mark incorrect matches’
// 										action
// 									</label>
// 								</div>
// 								<div className="field-box">
// 									<input
// 										type="checkbox"
// 										name="checkbox-choice"
// 										id="checkbox-choice-4"
// 										value="choice-4"
// 									/>
// 									<label htmlFor="checkbox-choice-4">
// 										Not able to see any of the mentioned
// 										actions
// 									</label>
// 								</div>
// 							</div>
// 							<div className="text-end mb-3">
// 								<div
// 									className="cst-btn-group btn-group"
// 									role="group"
// 									aria-label="Basic example"
// 									style={{ margin: "0 15px" }}
// 								>
// 									<button
// 										type="button"
// 										className="btn cst-btn-default"
// 									>
// 										<i
// 											style={{
// 												transform: "rotate(-45.975deg)",
// 											}}
// 											className="bi bi-paperclip"
// 										></i>
// 										Add Attachments
// 									</button>
// 									<button
// 										type="button"
// 										className="btn cst-btn-default"
// 									>
// 										<i className="bi bi-chat-right-text"></i>
// 										Add Notes
// 									</button>
// 								</div>
// 							</div>
// 						</div>
// 						<div className="col-md-3 col-12 p-0">
// 							<div className=" p-2 pt-5 q-img">
// 								<img src={question_img_logo} />
// 								<a>
// 									<i className="bi bi-zoom-in"></i> Click to
// 									enlarge
// 								</a>
// 							</div>
// 						</div>
// 					</div>
// 					<div className="d-flex justify-content-between">
// 						<button
// 							className="btn btn-primary btn-white py-2 font-size-14"
// 							onClick={() => {
// 								navigate("/dashboard");
// 							}}
// 						>
// 							Save & Exit
// 						</button>
// 						<button className="btn btn-primary btn-blue py-2 font-size-14">
// 							Next
// 						</button>
// 					</div>
// 				</div> */}
// 			</div>
// 		</div>
// 	);

// }
