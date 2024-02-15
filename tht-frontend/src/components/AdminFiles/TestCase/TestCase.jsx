import { Fragment, useEffect, useRef, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import Options from "../Options/Options";
import { Pagination } from "@mui/material";
import { useLoader } from "../../loader/LoaderContext";
import { Button, notification } from "antd";
import "./testcase.scss";
import { DocumentAPI } from "../../../api/DocumentAPI";
import { fileTypeIcon } from "../../../utils/utils";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import {
	DOCUMENT_STATE_ACTIVE,
	DOCUMENT_STATE_INACTIVE,
	DOCUMENT_TYPE_FOR_USER,
	DOCUMENT_TYPE_FOR_TEST_CASE_RESULTS
} from "../../../constants/document_constants";
import question_img_logo from "../../../styles/images/question-img.png";

export default function TestCase(props) {
	const {
		isLastQuestion,
		currentTestcase,
		currentSpecification,
		selectTestcase,
		selectNextTestcase,
		currentTestcaseIndex,
		refreshCurrentTestcase,
	} = props;
	const { showLoader, hideLoader } = useLoader();
	const [selectedOption, setSelectedOption] = useState();
	const [files, setFiles] = useState([]);
	const [uploadedFiles, setUploadedFiles] = useState([]);
	const [uploadQuestion, setUploadedQuestion] = useState({});
	const fileInputRef = useRef(null);
	const [saveButton, setSaveButton] = useState("");
	const handlePageChange = (event, page) => {
		showLoader();
		selectTestcase(page - 1);
		setSelectedOption(null);
		hideLoader();
	};

	const handleSaveandNext = () => {
		if (selectedOption == null) {
			notification.error({
				description: "No answers selected",
				placement: "bottomRight",
			});
		} else {
			TestResultAPI.saveOptions(currentTestcase.id, selectedOption)
				.then((res) => {
					refreshCurrentTestcase(res);
					selectNextTestcase();
				})
				.catch((error) => {
					throw error;
				});
		}
	};

	useEffect(() => {
		DocumentAPI.getDocumentsByRefObjUriAndRefId(
			RefObjUriConstants.TESTCASE_RESULT_REFOBJURI,
			currentTestcase.id,
			DOCUMENT_STATE_ACTIVE
		)
			.then((res) => {
				setUploadedFiles(res.content);
			})
			.catch((err) => {
				notification.error({
					message: "Error Loading Files!",
					placement: "bottomRight",
				});
			});
	}, [currentTestcase]);

	const addAttachment = () => {
		var file = document.getElementById("my-file");
		if (file) file.click();
	};

	const addFiles = (event, question, index) => {
		event.preventDefault();
		setFiles([...event.target.files]);
		setUploadedQuestion({
			...question,
			index,
		});
	};

	useEffect(() => {
		if (files.length > 0) {
			files.forEach((file) => {
				uploadDocuments(file, uploadQuestion, uploadQuestion.index);
			});
		}
	}, [uploadQuestion]);

	const uploadDocuments = (file, question, index) => {
		const formData = new FormData();
		formData.append(`file`, file);
		formData.append(`fileName`, file.name);
		formData.append("refId", question?.id);
		formData.append(
			"refObjUri",
			RefObjUriConstants.TESTCASE_RESULT_REFOBJURI
		);
		formData.append("documentType",DOCUMENT_TYPE_FOR_TEST_CASE_RESULTS.DOCUMENT_TYPE_EVIDENCE);
		DocumentAPI.uploadDocument(formData)
			.then((res) => {
				setUploadedFiles((prevFiles) => [
					...prevFiles,
					{ name: res.name, id: res.id, fileType: res.fileType },
				]);
				const newFiles = [...files];
				newFiles.splice(index, 1);
				setFiles(newFiles);
				notification.success({
					message: `Document Uploaded!`,
					placement: "bottomRight",
				});
			})
			.catch((err) => {
				let msg =
					err.response.data?.message || err.response.data[0].message;
				notification.error({
					message: `${msg}`,
					placement: "bottomRight",
				});
			});
	};

	const deleteFile = (file, index) => {
		if (file.id) {
			// delete from db (DocumentAPI)
			DocumentAPI.changeDocumentState(file.id, DOCUMENT_STATE_INACTIVE)
				.then((res) => {
					notification.success({
						message: "Document Removed",
						placement: "bottomRight",
					});
					setUploadedFiles((prev) => {
						return prev.filter((doc) => doc.id !== file.id);
					});
				})
				.catch((err) => {
					let msg =
						err.response.data?.message ||
						err.response.data[0].message;
					notification.error({
						message: `${msg}`,
						placement: "bottomRight",
					});
				});
		} else {
			// Remove from files
			setFiles((prev) => {
				let newFiles = [...prev];
				newFiles = newFiles.splice(index, 1);
				return newFiles;
			});
		}
	};

	useEffect(() => {
		if (files.length == 0 && fileInputRef.current) {
			fileInputRef.current.value = "";
		}
	}, [files]);

	const downloadFile = (file) => {
		DocumentAPI.downloadDocument(file.id, file.name).catch((err) => {
			console.error(err.data.message);
		});
	};

	useEffect(() => {
		console.log("the uploaded docs include ", uploadedFiles);
	}, [uploadedFiles]);

	return (
		<Fragment>
			<div className="col-12 non-fuctional-requirement">
				<div className="container">
					<div className="row heading">
						<div className="col-md-9 col-12 p-0">
							<h2>Question</h2>
						</div>

						<div className="col-md-3 col-12 d-md-flex d-none p-0">
							<h2 className="border-left">Reference</h2>
						</div>
					</div>
					<div className="row question-box" key={currentTestcase.id}>
						{/* <div className="col-md-9 col-12 p-0 question">  for the image space*/}

						<div className="col-md-9 col-12 p-0 question">
							<h2>
								<b>
									{currentTestcase.refId
										.split(".")
										.slice(-3)
										.join(".")
										.toUpperCase() +
										" " +
										currentTestcase.name +
										" "}
								</b>
							</h2>
							<Options
								refId={currentTestcase.refId}
								setSelectedOption={setSelectedOption}
								testcaseOptionId={
									currentTestcase.testcaseOptionId
								}
							></Options>
							{/* Photos upload code below */}
							<div className="doc-badge-wrapper">
								{uploadedFiles.map((file) => (
									<div
										type="button"
										key={file.id}
										className="doc-badge"
									>
										<img
											src={fileTypeIcon(file.fileType)}
										/>
										<span> {file.name} </span>
										<span
											type="button"
											title="Download File"
											className="mx-2 font-size-14"
											onClick={() => downloadFile(file)}
										>
											<i className="bi bi-cloud-download"></i>
										</span>
										<span
											type="button"
											title="Remove File"
											className="mx-2 font-size-14"
											onClick={() => deleteFile(file)}
										>
											<i className="bi bi-trash3"></i>
										</span>
									</div>
								))}
							</div>
							<div className="text-end mb-3">
								<div
									className="cst-btn-group btn-group"
									role="group"
									aria-label="Basic example"
									style={{ margin: "0 15px" }}
								>
									<input
										type="file"
										ref={fileInputRef}
										name="my_file"
										id="my-file"
										onChange={(e) => {
											addFiles(e, currentTestcase, 0);
										}}
										style={{
											visibility: "hidden",
											width: "0",
										}}
									></input>
									<button
										type="button"
										className="btn cst-btn-default"
										onClick={addAttachment}
									>
										<i
											style={{
												transform: "rotate(-45.975deg)",
											}}
											className="bi bi-paperclip"
										></i>
										Add Attachments
									</button>
									<button
										type="button"
										className="btn cst-btn-default"
									>
										<i className="bi bi-chat-right-text"></i>
										Add Notes
									</button>
								</div>
							</div>

							<div className="text-end mb-3">
								<button
									disabled={!currentTestcase.testcaseOptionId && !selectedOption}
									className="cst-btn-group btn btn-primary"
									onClick={() => {
										handleSaveandNext();
									}}
								>
									{!!isLastQuestion()
										? "Save"
										: "Save and Next"}
								</button>
							</div>
							{/* Photos upload code above */}
						</div>
						<div class="col-md-3 col-12 p-0">
							<div class=" p-2 pt-5 q-img">
								<img src={question_img_logo} />
							</div>
						</div>
					</div>
				</div>
			</div>
			<div style={{ display: "flex", justifyContent: "space-between" }}>
				<Pagination
					style={{
						flex: 1,
						display: "flex",
						justifyContent: "center",
					}}
					count={currentSpecification.childTestcaseResults.length}
					page={currentTestcaseIndex + 1}
					color="primary"
					onChange={handlePageChange}
				/>
				{/* <button></button> */}
			</div>
		</Fragment>
	);
}
