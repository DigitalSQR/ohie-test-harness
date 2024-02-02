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
import { DOCUMENT_STATE_ACTIVE, DOCUMENT_STATE_INACTIVE } from "../../../constants/document_constants";
export default function TestCase(props) {
	const { specificationId, nextSpecification } = props;
	const [manualQuestions, setManualQuestions] = useState([]);
	const [currentPage, setCurrentPage] = useState(1);
	const [totalPage, setTotalPage] = useState(1);
	const { showLoader, hideLoader } = useLoader();
	const [selectedOption, setSelectedOption] = useState();
	const [files, setFiles] = useState([]);
	const [uploadedFiles, setUploadedFiles] = useState([]);
	const [uploadQuestion, setUploadedQuestion] = useState({});
	const fileInputRef = useRef(null);
	const handlePageChange = (event, page) => {
		showLoader();
		setCurrentPage(page);
		fetchQuestions(page);
		hideLoader();
		setSelectedOption(null);
	};

	const handleSaveandNext = (id, currentPage, rank, index) => {
		console.log(
			currentPage,
			rank,
			index,
			totalPage,
			manualQuestions.length
		);
		if (selectedOption == null) {
			notification.error({
				description: "No answers selected",
				placement: "bottomRight",
			});
		} else {
			TestResultAPI.saveOptions(id, selectedOption)
				.then((res) => {
					console.log(res);
					if (currentPage == totalPage) {
						console.log("arrives in the last page");
						nextSpecification(rank + 1);
					} else if (currentPage < totalPage) {
						setCurrentPage((currentPage) => {
							return currentPage + 1;
						});
						fetchQuestions(currentPage + 1);
					}
				})
				.catch((error) => {
					throw error;
				});
		}
	};

	const fetchQuestions = (currentPage) => {
		TestResultAPI.getQuestions(specificationId, currentPage - 1)
			.then((res) => {
				console.log(res);
				setTotalPage(res.totalPages);
				setManualQuestions(res.content);
			})
			.catch((error) => {
				throw error;
			});
	};
	
	useEffect(() => {
		fetchQuestions(currentPage);
	}, []);

	useEffect(() => {
		if (manualQuestions.length > 0) {
			DocumentAPI.getDocumentsByRefObjUriAndRefId(RefObjUriConstants.TESTCASE_RESULT_REFOBJURI, manualQuestions[0].id, DOCUMENT_STATE_ACTIVE)
			.then((res) => {
				setUploadedFiles(res.content);
			}).catch((err) => {
				notification.error({
					message: "Error Loading Files!",
					placement: "bottomRight"
				});
			});
		}
	}, [manualQuestions])

	const addAttachment = () => {
		var file = document.getElementById("my-file");
		if (file) file.click();
	};

	const addFiles = (event, question, index) => {
		event.preventDefault();
		setFiles([...event.target.files]);
		setUploadedQuestion({
			...question,
			index
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
		formData.append("refObjUri", RefObjUriConstants.TESTCASE_RESULT_REFOBJURI);
		DocumentAPI.uploadDocument(formData)
			.then((res) => {
				console.log(res);
				setUploadedFiles((prevFiles) => [
					...prevFiles,
					{ name: res.name, id: res.id, fileType: res.fileType },
				]);
				const newFiles = [...files];
				newFiles.splice(index, 1);
				setFiles(newFiles);
				notification.success({
					message: `Document Uploaded!`,
					placement: "bottomRight"
				});
			}).catch((err) => {
				let msg = err.response.data?.message || err.response.data[0].message;
				notification.error({
					message: `${msg}`,
					placement: "bottomRight"
				})
			});
	};

	const deleteFile = (file, index) => {
		if (file.id) {
			// delete from db (DocumentAPI)
			DocumentAPI.changeDocumentState(file.id, DOCUMENT_STATE_INACTIVE)
				.then((res) => {
					notification.success({
						message: 'Document Removed',
						placement: "bottomRight"
					});
					setUploadedFiles((prev) => {
						return prev.filter(doc => doc.id !== file.id);
					})
				}).catch((err) => {
					let msg = err.response.data?.message || err.response.data[0].message;
					notification.error({
						message: `${msg}`,
						placement: "bottomRight"
					})
				});
		} else {
			// Remove from files 
			setFiles((prev) => {
				let newFiles = [...prev];
				newFiles = newFiles.splice(index, 1);
				return newFiles;
			})
		}
	};

	useEffect(() => {
		if (files.length == 0 && fileInputRef.current) {
			fileInputRef.current.value = '';
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
					{manualQuestions.map((question, index) => {
						console.log(question.testcaseOptionId);
						const segments = question.refId.split(".");
						const Specification = segments
							.slice(-3)
							.join(".")
							.toUpperCase();
						return (
							<div className="row question-box" key={question.id}>
								{/* <div className="col-md-9 col-12 p-0 question">  for the image space*/}

								<div className="col-md-9 col-12 p-0 question">
									<h2>
										<b>
											{Specification +
												" " +
												question.name +
												" "}
										</b>
									</h2>
									<Options
										refId={question.refId}
										setSelectedOption={setSelectedOption}
										testcaseOptionId={
											question.testcaseOptionId
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
													src={fileTypeIcon(
														file.fileType
													)}
												/>
												<span> {file.name} </span>
												<span
													type="button"
													title="Download File"
													className="mx-2 font-size-14"
													onClick={() => downloadFile(file)}
												>
													<i class="bi bi-cloud-download"></i>
												</span>
												<span
													type="button"
													title="Remove File"
													className="mx-2 font-size-14"
													onClick={() => deleteFile(file)}
												>
													<i class="bi bi-trash3"></i>
												</span>
											</div>
										))}
									</div>
									<div className="text-center mb-3">
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
												onChange={(e) => { addFiles(e, question, index) }}
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
														transform:
															"rotate(-45.975deg)",
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
									{/* <div className="doc-badge-wrapper">
										{files.map((file, index) => (
											<div
												key={file.name}
												className="doc-badge"
											>
												<img
													src={fileTypeIcon(
														file.type
													)}
												/>
												<span> {file.name} </span>
												<span
													onClick={(e) =>
														onUpload(
															e,
															question,
															index
														)
													}
													type="button"
													title="Upload File"
													className="mx-2 font-size-14"
												>
													<i class="bi bi-upload"></i>
												</span>
												<span
													type="button"
													title="Remove File"
													className="mx-2 font-size-14"
													onClick={() => deleteFile(file, index)}
												>
													<i class="bi bi-trash3"></i>
												</span>
											</div>
										))}
									</div> */}
									<div className="text-center mb-3">
										<button
											className=" btn btn-primary"
											onClick={() => {
												handleSaveandNext(
													question.id,
													currentPage,
													question.rank,
													index
												);
											}}
										>
											Save and Next
										</button>
									</div>
									{/* Photos upload code above */}
								</div>
							</div>
						);
					})}
				</div>
			</div>
			<div style={{ display: "flex", justifyContent: "space-between" }}>
				<Pagination
					style={{
						flex: 1,
						display: "flex",
						justifyContent: "center",
					}}
					count={totalPage}
					page={currentPage}
					color="primary"
					onChange={handlePageChange}
				/>
				{/* <button></button> */}
			</div>
		</Fragment>
	);
}
