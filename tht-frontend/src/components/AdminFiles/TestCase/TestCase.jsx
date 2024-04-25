import { Fragment, useEffect, useRef, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import Options from "../Options/Options";
import { Pagination, PaginationItem } from "@mui/material";
import { useLoader } from "../../loader/LoaderContext";
import { Button, notification, Modal, Carousel, Image, Popconfirm } from "antd";
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
  DOCUMENT_TYPE_FOR_TEST_CASE_RESULTS,
  DOCUMENT_TYPE_FOR_TEST_CASES,
} from "../../../constants/document_constants";
import question_img_logo from "../../../styles/images/question-img.png";
import OverlayTrigger from "react-bootstrap/OverlayTrigger";
import Tooltip from "react-bootstrap/Tooltip";

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
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [files, setFiles] = useState([]);
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const [uploadQuestion, setUploadedQuestion] = useState({});
  const fileInputRef = useRef(null);
  const [currentQuestion, setCurrentQuestion] = useState({});
  const [questionAndDocument, setQuestionAndDocument] = useState([]);
  const [noteMessage, setNoteMessage] = useState();
  const [testcaseResult, setTestcaseResult] = useState();
  const [editMode, setEditMode] = useState(false);
  const [initialNoteMessage, setInitialNoteMessage] = useState();
  const [showNote, setShowNote] = useState(false);
  const [isModified, setIsModified] = useState(true);
  const handlePageChange = (event, page) => {
	console.log(currentSpecification);
    let isSame = isMessageSame();
    // Ask for confirmation
    if (
      !isSame &&
      !window.confirm(
        "Continuing will discard unsaved note changes. Are you sure?"
      )
    ) {
      // If user cancels, do nothing
      return;
    }

    showLoader();
    selectTestcase(page - 1);
    setSelectedOptions([]);
    setNoteMessage();
    setInitialNoteMessage();
    setEditMode(false);
    hideLoader();
  };

  useEffect(() => {
    getCurrentTestcaseResultById(currentTestcase.id);
  }, [currentTestcase.id]);

  const submitOptions = () => {
	const data=[{testcaseResultId:testcaseResult.id,selectedTestcaseOptionIds:selectedOptions}]
    TestResultAPI.saveOptions(data)
      .then((res) => {
        refreshCurrentTestcase(res[0]);
        selectNextTestcase();
      })
      .catch((error) => {});
  };

	const handleSaveandNext = () => {
		if (!selectedOptions || selectedOptions.length == 0) {
			notification.error({
				className: "notificationError",
				message: "No answers selected",
				placement: "bottomRight",
			});
		} else {
			if (!isModified) {
				selectNextTestcase();
			}
			else {
				if (isMessageSame()) {
					submitOptions();
				}
				else {
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
						onCancel() {
							submitOptions();
						}

					});
				}
			}
		}
	};

  const getCurrentTestcaseResultById = (testcaseResultId) => {
    TestResultAPI.getTestCaseResultById(testcaseResultId)
      .then((res) => {
        setTestcaseResult(res);
      })
      .catch((error) => {});
  };

  const showNoteDiv = () => {
    if (!noteMessage) {
      setEditMode(true);
    }
    setShowNote(!showNote);
  };

  const handleSaveNote = async () => {
    await saveTestcaseResultWithNote();
  };

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
							className: "notificationSuccess",
							placement: "top",
							message: "Notes saved successfully!",
						});
						resolve(); // Resolve the promise when the operation is successful
					})
					.catch((error) => {
					});
			} else {
				if (showNotification) {
					notification.warning({
						className: "notificationWarning",
						message: "No changes detected in notes. Click the close button if you don't wish to make any changes.",
						placement: "bottomRight",
					});
				}
				resolve(); // Resolve the promise if no changes are detected
			}
		});
	};


  const handleOnChangeForNote = (e) => {
    setNoteMessage(e.target.value);
  };

  useEffect(() => {
    if (testcaseResult) {
      DocumentAPI.getDocumentsByRefObjUriAndRefId(
        RefObjUriConstants.TESTCASE_RESULT_REFOBJURI,
        testcaseResult.id,
        DOCUMENT_STATE_ACTIVE
      )
        .then((res) => {
          setUploadedFiles(res.content);
		  console.log(res);
        })
        .catch((error) => {});
    }
  }, [testcaseResult]);

	useEffect(() => {
		setShowNote(false);
		setCurrentQuestion({});
		showLoader();
		if (testcaseResult) {

			// set note message to show message
			setNoteMessage(testcaseResult.message);
			setInitialNoteMessage(testcaseResult.message);


			TestResultRelationAPI.getTestcaseResultRelatedObject(
				testcaseResult.id,
				RefObjUriConstants.TESTCASE_REFOBJURI
			).then((res) => {
				if (res && res.length > 0) {

					setCurrentQuestion(res[0]);
				} else {
					notification.error({
						className: "notificationError",
						message: "Oops! something wrong ,No question found!",
						placement: "bottomRight",
					});
				}
			}).catch((error) => {

			}).finally(() => {
				hideLoader();
			});
		}
	}, [testcaseResult]);

  const handleEditNoteButtonClick = () => {
    setEditMode(true);
  };

  const handleCancelNoteButtonClick = () => {
    let isSame = isMessageSame();
    // Ask for confirmation
    if (!isSame) {
      Modal.confirm({
        content:
          "Are you sure you want to cancel? Any unsaved changes will be lost.",
        onCancel() {
          return;
        },
        onOk() {
          setNoteMessage(initialNoteMessage);
          setEditMode(false);
        }
      });
    }else{
		setNoteMessage(initialNoteMessage);
		setEditMode(false);
	}

  };

  const isMessageSame = () => {
    return noteMessage?.trim() === initialNoteMessage?.trim();
  };

  useEffect(() => {
    getQuestionImagesIfNotExists();
  }, [currentQuestion]);

  const getQuestionImagesIfNotExists = () => {
    if (
      currentQuestion &&
      currentQuestion.id &&
      questionAndDocument.filter(
        (questionItem) => questionItem.key === currentQuestion.id
      ) <= 0
    ) {
      TestResultRelationAPI.getTestcaseResultRelatedObject(
        testcaseResult.id,
        RefObjUriConstants.DOCUMENT_REFOBJURI
      )
        .then(async (res) => {
          if (res && res.length > 0) {
            const updatedFiles = await Promise.all(
              res
                .filter(
                  (item) =>
                    DOCUMENT_TYPE_FOR_TEST_CASES.DOCUMENT_TYPE_QUESTION ===
                    item?.documentType
                )
                .map(async (relatedDoc) => {
                  try {
                    const base64Image = await DocumentAPI.base64Document(
                      relatedDoc.id
                    );
                    return {
                      name: relatedDoc.name,
                      status: "done",
                      url: base64Image,
                      documentId: relatedDoc.id,
                    };
                  } catch (error) {
                    return {
                      name: relatedDoc.name,
                      status: "error",
                      url: null,
                    };
                  }
                })
            );

            let item = {};
            item.key = currentQuestion.id;
            item.files = updatedFiles;

            const updatedQuestions = [...questionAndDocument];
            updatedQuestions.push(item);
            setQuestionAndDocument(updatedQuestions);
          }
        })
        .catch((error) => {});
    }
  };

  const addAttachment = () => {
    var file = document.getElementById("my-file");
    if (file) file.click();
  };

  const addFiles = (event, question, index) => {
	console.log(question);
    event.preventDefault();
	console.log("uploaded files ",event.target.value);
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
		formData.append("documentType", DOCUMENT_TYPE_FOR_TEST_CASE_RESULTS.DOCUMENT_TYPE_EVIDENCE);
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
					className: "notificationSuccess",
					placement: "top",
					message: "Attachment added successfully!",
				});
			}).catch((error) => {

			});
	};

	const deleteFile = (file, index) => {
		if (file) {
			if (file.id) {
				// delete from db (DocumentAPI)
				DocumentAPI.changeDocumentState(file.id, DOCUMENT_STATE_INACTIVE)
					.then((res) => {
						notification.success({
							className: "notificationSuccess",
							placement: "top",
							message: "Attachment removed successfully!",
						});
						setUploadedFiles((prev) => {
							return prev.filter((doc) => doc.id !== file.id);
						});
					}).catch((error) => {

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
    DocumentAPI.downloadDocument(file.id, file.name).catch((err) => {});
  };

  useEffect(() => {}, [uploadedFiles]);

	const renderTooltip = (props) => (
		<Tooltip id="button-tooltip" {...props}>
			Accepted file types: PDFs and images only.<br /> Maximum file size: 2MB.
		</Tooltip>
	);
	function getStatusColor(page, type) {
		var style = {};
		if (page != null && (type !== "previous" && type !== "next")) {
			if (page === currentTestcaseIndex) {
				style.border = "4px solid #b6cce1";
				style.color = "black";
				style.width = "40px";
				style.height = "40px";
				style.borderRadius = "50%";
				style.backgroundColor = "white";
			} if (currentSpecification.childTestcaseResults[page]?.state === "testcase.result.status.finished") {
				style.backgroundColor = "#078707";
				style.color = "white";
			}
		}
		return style;
	}

  return (
    <div id="testCase">
      <div className="col-12 non-fuctional-requirement">
        <div className="container-fluid">
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
							<div className="col-12 question-list">
								<div className="col-9">

									<h2>
										<b>
											{(currentTestcaseIndex + 1) +
												". "}
											{currentQuestion ? currentQuestion.name : " "}
										</b>
									</h2>
								</div>
							</div>
							<div className="col-md-9 col-12 p-0 question">

								<Options

									refId={testcaseResult.refId}
									testcaseResultInfo={testcaseResult}
									setSelectedOptions={setSelectedOptions}
									currentQuestion={currentQuestion}
									setIsModified={setIsModified}
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
									<textarea className="form-control note-text-area" rows="3" disabled={!editMode} value={noteMessage || ""} onChange={handleOnChangeForNote}></textarea>
									<div className="note-text-area-button-group">
										{editMode && <span role="button" className="save-btn-for-now fw-bold mx-1" title="Cancel" onClick={handleCancelNoteButtonClick}><i className="bi bi-x-lg"></i></span>}
										{editMode && <span role="button" className="save-btn-for-now fw-bold mx-1" title="Save Note" onClick={handleSaveNote}><i className="bi bi-floppy"></i></span>}
										{!editMode && <span role="button" className="save-btn-for-now fw-bold mx-1" title="Edit Note" onClick={handleEditNoteButtonClick}><i className="bi bi-pencil-square"></i></span>}
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
											{/* {showNote ? 'Hide Note' : 'Show Note'} */}
											{
												noteMessage ? showNote ? "Hide Note" : "Show Note" : showNote ? "Hide Note" : "Add Note"
											}
										</button>
									</div>
								</div>
					

                <div className="text-end mb-3">
                  <button
                    disabled={
                      !testcaseResult.testcaseOptionId &&
                      !selectedOptions.length
                    }
                    className="cst-btn-group btn btn-blue save-and-next"
                    onClick={() => {
                      handleSaveandNext();
                    }}
                  >
                    {!!isLastQuestion()
                      ? "Save"
                      : selectedOptions.length
                      ? !isModified
                        ? "Next"
                        : "Save and Next"
                      : "Save and Next"}
                  </button>
                </div>
                {/* Photos upload code above */}
              </div>
              <div className="col-md-3 col-12 p-0">
                <div className=" p-2 pt-5 q-img">
                  <>
                    <Image.PreviewGroup>
                      <Carousel
                        infinite={false}
                        arrows={true}
                        prevArrow={<LeftOutlined />}
                        nextArrow={<RightOutlined />}
                      >
                        {questionAndDocument.length > 0 &&
                          questionAndDocument
                            .find((q) => q.key === currentQuestion.id)
                            ?.files.map((item) => (
                              <div key={item.id}>
                                <h3 className="testcase-carousel-background">
                                  <Image width={200} src={item.url} />
                                </h3>
                              </div>
                            ))}
                      </Carousel>
                    </Image.PreviewGroup>
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
              style={getStatusColor(item.page - 1, item.type)}
            />
          )}
        />
      </div>
    </div>
  );
}
