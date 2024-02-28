import { Fragment, useEffect, useRef, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import Options from "../Options/Options";
import { Pagination, PaginationItem } from "@mui/material";
import { useLoader } from "../../loader/LoaderContext";
import { Button, notification, Modal ,Carousel, Image } from "antd";
import { EditOutlined, LeftOutlined, RightOutlined } from "@ant-design/icons";
import "./testcase.scss";
import { DocumentAPI } from "../../../api/DocumentAPI";
import { TestResultRelationAPI } from "../../../api/TestResultRelationAPI";
import { fileTypeIcon } from "../../../utils/utils";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import {
	DOCUMENT_STATE_ACTIVE,
	DOCUMENT_STATE_INACTIVE,
	DOCUMENT_TYPE_FOR_USER,
	DOCUMENT_TYPE_FOR_TEST_CASE_RESULTS
} from "../../../constants/document_constants";
import question_img_logo from "../../../styles/images/question-img.png";
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';

export default function TestCase(props) {
	const {
		isLastQuestion,
		currentTestcase,
		currentSpecification,
		selectTestcase,
		selectNextTestcase,
		currentTestcaseIndex,
		refreshCurrentTestcase
	} = props;
	const { showLoader, hideLoader } = useLoader();
	const [selectedOptions, setSelectedOptions] = useState([]);
	const [files, setFiles] = useState([]);
	const [uploadedFiles, setUploadedFiles] = useState([]);
	const [uploadQuestion, setUploadedQuestion] = useState({});
	const fileInputRef = useRef(null);
	const [saveButton, setSaveButton] = useState("");
	const [currentQuestion, setCurrentQuestion] = useState({});
	const [questionAndDocument, setQuestionAndDocument] = useState([]);
	const [noteMessage, setNoteMessage] = useState();
	const [testcaseResult, setTestcaseResult] = useState();
	const [editMode, setEditMode] = useState(false);
	const [initialNoteMessage, setInitialNoteMessage] = useState();

	const [showNote, setShowNote] = useState(false);
	const handlePageChange = (event, page) => {
		let isSame = isMessageSame();
		 // Ask for confirmation
		 if (!isSame && !window.confirm("Continuing will discard unsaved note changes. Are you sure?")) {
			// If user cancels, do nothing
			return;
		}

		showLoader();
		console.log(page);
		selectTestcase(page - 1);
		setSelectedOptions([]);
		setNoteMessage();
		setInitialNoteMessage();
		setEditMode(false);
		hideLoader();
	};

	useEffect(() => {
		getCurrentTestcaseResultById(currentTestcase.id);
	}, [currentTestcase]);

	const submitOptions = () => {
		TestResultAPI.saveOptions(testcaseResult.id, selectedOptions)
		.then((res) => {
			refreshCurrentTestcase(res);
			selectNextTestcase();
		})
		.catch((error) => {
			throw error;
		});
	}

	const handleSaveandNext = () => {
		if (!selectedOptions || selectedOptions.length == 0) {
			notification.error({
				description: "No answers selected",
				placement: "bottomRight",
			});
		} else {
			if(isMessageSame()){
				submitOptions();
			}
			else{
				Modal.confirm({
					title: 'Note Saving Confirmation',
					content: 'Would you like to save the notes before proceeding?',
					okText: 'Save',
					cancelText: 'Discard',
					onOk() {
						saveTestcaseResultWithNote()
						.then(() => {
							submitOptions();
						});
					},
					onCancel(){
						submitOptions();
					}

				});
			}
		}
	};

	const getCurrentTestcaseResultById = (testcaseResultId) => {
		TestResultAPI.getTestCaseResultById(testcaseResultId)
		.then((res) => {
			setTestcaseResult(res)
		})
		.catch((error) => {
			console.error(error);
			notification.error({
				message: "Error Fetching Test Case!",
				placement: "bottomRight",
			});
		})
	}


	const showNoteDiv = () => {
		setShowNote(!showNote);
	};

	const handleSaveNote = async () => {
		await saveTestcaseResultWithNote();
	}

	const saveTestcaseResultWithNote = (showNotification = true) => {
		return new Promise((resolve, reject) => {
			if (!isMessageSame()) {
				var notePatch = [
					{ op: "replace", path: "/message", value: noteMessage.trim() },
				];
				TestResultAPI.patchTestCaseResult(testcaseResult.id, notePatch)
					.then((res) => {
						setInitialNoteMessage(res.message);
						setEditMode(false);
						notification.success({
							message: `Note Updated Successfully!`,
							placement: "bottomRight",
						});
						resolve(); // Resolve the promise when the operation is successful
					})
					.catch((error) => {
						notification.error({
							message: "Error Updating Note Successfully!",
							placement: "bottomRight",
						});
						reject(error); // Reject the promise if there is an error
					});
			} else {
				if (showNotification) {
					notification.warning({
						message: "No Changes detected to save in note!",
						placement: "bottomRight",
					});
				}
				resolve(); // Resolve the promise if no changes are detected
			}
		});
	};


	const handleOnChangeForNote = (e) => {
		setNoteMessage(e.target.value);
	}

	useEffect(() => {
		if(testcaseResult){
			DocumentAPI.getDocumentsByRefObjUriAndRefId(
				RefObjUriConstants.TESTCASE_RESULT_REFOBJURI,
				testcaseResult.id,
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
		}
	}, [testcaseResult]);

	useEffect(() => {
		setShowNote(false);
		setCurrentQuestion({});
		showLoader();
		if(testcaseResult){

			// set note message to show message
			setNoteMessage(testcaseResult.message);
			setInitialNoteMessage(testcaseResult.message);


			TestResultRelationAPI.getTestcaseResultRelatedObject(
				testcaseResult.id,
				RefObjUriConstants.TESTCASE_REFOBJURI
			).then((res) => {
				if (res && res.length > 0) {
					hideLoader();
					setCurrentQuestion(res[0]);
				} else {
					notification.error({
						message: "Oops! something wrong ,No question found!",
						placement: "bottomRight",
					});
				}
			})
			.catch((err) => {
				notification.error({
					message: "Error Loading question!",
					placement: "bottomRight",
				});
			});
		}
	}, [testcaseResult]);

	const handleEditNoteButtonClick = () => {
		setEditMode(true);
	};

	const handleCancelNoteButtonClick = () => {
		let isSame = isMessageSame();
		 // Ask for confirmation
		 if (!isSame && !window.confirm("Are you sure you want to cancel? Any unsaved changes will be lost.")) {
			// If user cancels, do nothing
			return;
		}
		setNoteMessage(initialNoteMessage);
		setEditMode(false);
	};

	const isMessageSame = () => {
		return noteMessage?.trim() === initialNoteMessage?.trim();
	}

	useEffect(() => {
		getQuestionImagesIfNotExists();
	},[currentQuestion]);

	const getQuestionImagesIfNotExists = () => {
		if(currentQuestion && currentQuestion.id && (questionAndDocument.filter((questionItem) => questionItem.key === currentQuestion.id) <= 0)){

        DocumentAPI.getDocumentsByRefObjUriAndRefId(
          RefObjUriConstants.TESTCASE_REFOBJURI,
          currentQuestion.id,
          DOCUMENT_STATE_ACTIVE
        ).then(async (res) => {
            const updatedFiles = await Promise.all(res.content.map(async (relatedDoc) => {
              try {
                  const base64Image = await DocumentAPI.base64Document(relatedDoc.id,relatedDoc.name);
                  return {
                      name: relatedDoc.name,
                      status: 'done',
                      url: base64Image,
                      documentId : relatedDoc.id
                  };
              } catch (error) {
                  console.error(error);
                  return {
                      name: relatedDoc.name,
                      status: 'error',
                      url: null
                  };
              }
          }));

          let item = {};
          item.key = currentQuestion.id
          item.files = updatedFiles;

          const updatedQuestions = [...questionAndDocument];
          updatedQuestions.push(item);
          setQuestionAndDocument(updatedQuestions);

          })
          .catch((err) => {
            console.log(err);
            notification.error({
              message: "Error Loading Files!",
              placement: "bottomRight",
            });
          });
		}
	}

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
		if(file){
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

	const renderTooltip = (props) => (
		<Tooltip id="button-tooltip" {...props}>
			Accepted file types: PDFs and images only.
		</Tooltip>
	);
	function getStatusColor(page, type) {
		var style = {};
		if (page != null && (type !== "previous" && type !== "next")) {
			if (page === currentTestcaseIndex) {
				style.border = "2px solid #1976d2";
			} if (currentSpecification.childTestcaseResults[page]?.state === "testcase.result.status.finished") {
				style.backgroundColor = "#a7ffa7";
			}
		}
		return style;
	}

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
					{
						testcaseResult &&
						<div className="row question-box" key={testcaseResult.id}>
							{/* <div className="col-md-9 col-12 p-0 question">  for the image space*/}

							<div className="col-md-9 col-12 p-0 question">
								<h2>
									<b>
										{testcaseResult.refId
											.split(".")
											.slice(-3)
											.join(".")
											.toUpperCase() +
											" "}
										{currentQuestion ? currentQuestion.name : " "}
									</b>
								</h2>
								<Options
									refId={testcaseResult.refId}
									testcaseResultInfo={testcaseResult}
									setSelectedOptions={setSelectedOptions}
									currentQuestion={currentQuestion}
									testcaseOptionId={
										testcaseResult.testcaseOptionId
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
								{showNote && <div className="text-end m-3 position-relative" id="note-textarea">
									<textarea className="form-control note-text-area" rows="3" disabled={!editMode} value={noteMessage} onChange={handleOnChangeForNote}></textarea>
									<div className="note-text-area-button-group">
										{editMode && <span role="button" className="save-btn-for-now fw-bold mx-1" title="Cancel" onClick={handleCancelNoteButtonClick}><i class="bi bi-x-lg"></i></span>}
										{editMode && <span role="button" className="save-btn-for-now fw-bold mx-1" title="Save Note" onClick={handleSaveNote}><i class="bi bi-floppy"></i></span>}
										{!editMode && <span role="button" className="save-btn-for-now fw-bold mx-1" title="Edit Note"  onClick={handleEditNoteButtonClick}><i class="bi bi-pencil-square"></i></span>}
									</div>
								</div>}
								<div className="text-end mb-3">
									<div
										className="cst-btn-group btn-group margin"
										role="group"
										aria-label="Basic example"
									>
										<input
											type="file"
											ref={fileInputRef}
											name="my_file"
											id="my-file"
											onChange={(e) => {
												addFiles(e, testcaseResult, 0);
											}}
											className="visibility"
										></input>
										<button
											variant="success"
											type="button"
											className="btn cst-btn-default"
											onClick={addAttachment}
										>
											<i
												className="bi bi-paperclip rotate"
											></i>
											Add Attachments
											<OverlayTrigger
												placement="right"
												delay={{ show: 250, hide: 400 }}
												overlay={renderTooltip}
											>
												<i className="bi bi-info-circle-fill document-tooltip-info"></i>
											</OverlayTrigger>
										</button>
										<button
											type="button"
											className="btn cst-btn-default"
											onClick={showNoteDiv}
										>
											<i className="bi bi-chat-right-text"></i>
											{showNote ? 'Hide Note' : 'Show Note'}
										</button>
									</div>
								</div>

								<div className="text-end mb-3">
									<button
										disabled={!testcaseResult.testcaseOptionId && !selectedOptions.length}
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
                                    <>
                                        <Carousel arrows={true} prevArrow={<LeftOutlined />} nextArrow={<RightOutlined />}>
                                            {questionAndDocument.length > 0 && (
                                                questionAndDocument.find((q) => q.key === currentQuestion.id)?.files.map((item) => (
                                                    <div key={item.id}>
                                                        <h3 className="testcase-carousel-background">
                                                            <Image width={200}
                                                                   src={item.url} />
                                                        </h3>
                                                    </div>
                                                ))
                                            )}
                                        </Carousel>
                                    </>
								</div>
							</div>
						</div>
					}
				</div>
			</div>
			<div className="display">
				<Pagination
					className="pagination"
					count={currentSpecification.childTestcaseResults.length}
					page={currentTestcaseIndex + 1}
					onChange={handlePageChange}
					renderItem={(item) => (
						<PaginationItem
							{...item}
							style={getStatusColor(item.page-1,item.type)}
						/>
					)}
				/>
			</div>
		</Fragment>
	);
}