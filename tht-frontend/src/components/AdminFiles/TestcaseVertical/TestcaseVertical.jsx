import { Fragment, useEffect, useRef, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import Options from "../Options/Options";
import { useLoader } from "../../loader/LoaderContext";
import { notification, Modal, Carousel, Image } from "antd";
import { LeftOutlined, RightOutlined } from "@ant-design/icons";
import "./testcaseVertical.scss";
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
import OverlayTrigger from "react-bootstrap/OverlayTrigger";
import Tooltip from "react-bootstrap/Tooltip";
import VerticalOptions from "../VerticalOptions/VerticalOptions";

export default function TestcaseVertical(props) {
  const {
    isLastQuestion,
    currentTestcase,
    currentSpecification,
    selectTestcase,
    selectNextTestcase,
    currentTestcaseIndex,
    refreshCurrentTestcase,
    selectNextSpecification

  } = props;
  // console.log(currentSpecification);
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
  const [optionsArray,setOptionsArray] = useState([]);
  const handlePageChange = (event, page) => {
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
  }, [currentTestcase]);

  const submitOptions = () => {
    TestResultAPI.saveOptions()
      .then((res) => {
        refreshCurrentTestcase(res);
        selectNextTestcase();
      })
      .catch((error) => {});
  };

  // const handleSaveandNext = () => {
  //   if (!selectedOptions || selectedOptions.length == 0) {
  //     notification.error({
  //       className: "notificationError",
  //       message: "Error",
  //       description: "No answers selected",
  //       placement: "bottomRight",
  //     });
  //   } else {
  //     if (!isModified) {
  //       selectNextTestcase();
  //     } else {
  //       if (isMessageSame()) {
  //         submitOptions();
  //       } else {
  //         Modal.confirm({
  //           title: "Note Saving Confirmation",
  //           content: "Would you like to save the notes before proceeding?",
  //           okText: "Save",
  //           cancelText: "Discard",
  //           onOk() {
  //             saveTestcaseResultWithNote().then(() => {
  //               submitOptions();
  //             });
  //           },
  //           onCancel() {
  //             submitOptions();
  //           },
  //         });
  //       }
  //     }
  //   }
  // };

  const handleSaveandNext = () => {
    if(optionsArray.length == 0){
      selectNextSpecification();
    }else{
    TestResultAPI.saveOptions(optionsArray);
    selectNextSpecification();
    }
  }

  const getCurrentTestcaseResultById = (testcaseResultId) => {
    TestResultAPI.getTestCaseResultById(testcaseResultId)
      .then((res) => {
        setTestcaseResult(res);
      })
      .catch((error) => {});
  };

  const showNoteDiv = () => {
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
              message: "Success",
              description: `Notes saved successfully!`,
            });
            resolve(); // Resolve the promise when the operation is successful
          })
          .catch((error) => {});
      } else {
        if (showNotification) {
          notification.warning({
            className: "notificationWarning",
            message: "Warning",
            description: "No changes detected in notes. Click the close button if you don't wish to make any changes.",
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
      )
        .then((res) => {
          if (res && res.length > 0) {
            setCurrentQuestion(res[0]);
            console.log("current question ",res[0]);
          } else {
            notification.error({
              className: "notificationError",
              message: "Error",
              description: "Oops! something wrong ,No question found!",
              placement: "bottomRight",
            });
          }
        })
        .catch((error) => {})
        .finally(() => {
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
    if (
      !isSame &&
      !window.confirm(
        "Are you sure you want to cancel? Any unsaved changes will be lost."
      )
    ) {
      // If user cancels, do nothing
      return;
    }
    setNoteMessage(initialNoteMessage);
    setEditMode(false);
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
    formData.append("refObjUri", RefObjUriConstants.TESTCASE_RESULT_REFOBJURI);
    formData.append(
      "documentType",
      DOCUMENT_TYPE_FOR_TEST_CASE_RESULTS.DOCUMENT_TYPE_EVIDENCE
    );
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
          message: "Success",
          description: `Attachment added successfully!`,
        });
      })
      .catch((error) => {});
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
              message: "Success",
              description: "Attachment removed successfully!",
            });
            setUploadedFiles((prev) => {
              return prev.filter((doc) => doc.id !== file.id);
            });
          })
          .catch((error) => {});
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
      Accepted file types: PDFs and images only.
      <br /> Maximum file size: 2MB.
    </Tooltip>
  );
  function getStatusColor(page, type) {
    var style = {};
    if (page != null && type !== "previous" && type !== "next") {
      if (page === currentTestcaseIndex) {
        style.border = "4px solid #b6cce1";
        style.color = "black";
        style.width = "40px";
        style.height = "40px";
        style.borderRadius = "50%";
        style.backgroundColor = "white";
      }
      if (
        currentSpecification.childTestcaseResults[page]?.state ===
        "testcase.result.status.finished"
      ) {
        style.backgroundColor = "#078707";
        style.color = "white";
      }
    }
    return style;
  }



  return (
    <div id="testcaseVertical">
      {currentSpecification.childTestcaseResults.map(
        (testcaseResult, index) => {
          return (
            <div className="col-12 non-fuctional-requirement">
              <div>
                {testcaseResult && (
                  <div className="row question-box" key={testcaseResult.id}>
                  

                      <VerticalOptions
					            	setOptionsArray={setOptionsArray}
                        refId={testcaseResult.refId}
                        testcaseResultInfo={testcaseResult}
                        setSelectedOptions={setSelectedOptions}
                        currentQuestion={currentQuestion}
                        setIsModified={setIsModified}
                        testcaseOptionId={testcaseResult.testcaseOptionId}
                        fileId={testcaseResult.id}
                        index={index}
                      ></VerticalOptions>
                  </div>
                )}
              </div>
            </div>
          );
        }
      )}
      <div className="row">
        <div className="col-12 pe-0 text-end">
          <button className="cst-btn-group btn btn-blue save-and-next" onClick={handleSaveandNext}>

          {!!isLastQuestion()
                      ? "Save"
                      : optionsArray.length == 0 
                      ? "Next" : "Save and Next"
          }
          </button>
        </div>
      </div>
    </div>
  );
}
