import { useEffect, useRef, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import "./verticalOptions.scss";
import { useLoader } from "../../loader/LoaderContext";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { TestResultRelationAPI } from "../../../api/TestResultRelationAPI";
import { Button, notification, Carousel, Image, Modal } from "antd";
import { LeftOutlined, RightOutlined } from "@ant-design/icons";
import { ManualQuestionTypeConstants } from "../../../constants/testcase_constants";
import { DocumentAPI } from "../../../api/DocumentAPI";
import {
  DOCUMENT_STATE_ACTIVE,
  DOCUMENT_STATE_INACTIVE,
  DOCUMENT_TYPE_FOR_TEST_CASES,
  DOCUMENT_TYPE_FOR_TEST_CASE_RESULTS,
} from "../../../constants/document_constants";
import { fileTypeIcon } from "../../../utils/utils";
import OverlayTrigger from "react-bootstrap/OverlayTrigger";
import Tooltip from "react-bootstrap/Tooltip";


/* 
	Vertical Options Component.

	This component displays all the options present for a particular testcase.

	Props:-
		1. testcaseResultInfo:- Details of the concerned parent Testcase question.
		2. setSelectedOption:- Function responsible for defining the selected option.
		3. currentQuestion:- Details regarding current question, which defines the question being single/multi select. 
		4. testcaseOptionId:- The unique identifier for the particular question.
		5. setIsModified:- The flag which indicates if any changes have been made in an question.
*/
export default function VerticalOptions(props) {
  const {
    setOptionsArray,
    refId,
    testcaseResultInfo,
    setSelectedOptions,
    currentQuestion,
    testcaseOptionId,
    setIsModified,
    fileId,
    index,
    setUnSavedNotes
  } = props;
  const [options, setOptions] = useState([]);
  const [currentOptions, setCurrentOptions] = useState([]);
  const [testResultRelationInfos, setTestResultRelations] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const [previousOption, setPreviousOption] = useState();
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const [uploadQuestion, setUploadedQuestion] = useState({});
  const [showNote, setShowNote] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [noteMessage, setNoteMessage] = useState("");
  const [questionAndDocument, setQuestionAndDocument] = useState([]);

  const handleOnChangeForNote = (e,testcaseResultInfo,index) => {
    const value = e.target.value;
    if(value.length <= 2001){
    setNoteMessage(value);
    }
    setUnSavedNotes((prev)=>{
      const intentedIndex = prev.findIndex(notes=>notes?.key === index);
      if (intentedIndex !== -1) {
        const updatedNotes = [...prev]; 
        updatedNotes[intentedIndex] = {
            ...updatedNotes[intentedIndex],
            value: value,
            testcaseResultInfo: testcaseResultInfo,
            key: index
        }

        return updatedNotes; //if an object is found,update that particular object
        
    } else {
        return [...prev, { value, testcaseResultInfo, key: index }];//create a new object if not found
    }  
    
      
    })
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
          setUnSavedNotes(prev=>{
            const intendedIndex = prev.findIndex(note => note.key === index);
            const updatedNotes = [...prev];
            updatedNotes.splice(intendedIndex,1);
            return updatedNotes;
          })
        },
      });
    } else {
      setNoteMessage(initialNoteMessage);
      setEditMode(false);
    }
  };

  const isMessageSame = () => {
    return noteMessage?.trim() === initialNoteMessage?.trim();
  };

  const [initialNoteMessage, setInitialNoteMessage] = useState();

  const showNoteDiv = () => {
    if (!noteMessage) {
      setEditMode(true);
    }
    setShowNote(!showNote);
  };

  const saveTestcaseResultWithNote = (index,showNotification = true) => {
    return new Promise((resolve, reject) => {
      if (!isMessageSame()) {
        var notePatch = [
          { op: "replace", path: "/message", value: noteMessage.trim() },
        ];
        TestResultAPI.patchTestCaseResult(testcaseResultInfo.id, notePatch)
          .then((res) => {
            setInitialNoteMessage(res.message);
            setEditMode(false);
            if(!!noteMessage){
            setUnSavedNotes(prev=>{
              const updatedUnsavedNotes = [...prev];
              const intendedIndex = updatedUnsavedNotes.findIndex(note=>note.key === index);
              updatedUnsavedNotes.splice(intendedIndex,1);
              return updatedUnsavedNotes;
            })}
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

  const fileInputRef = useRef(null);

  const [files, setFiles] = useState([]);

  const inputRef = useRef(new Array());

  let uploadingId = fileId;

  // This useEffect fetches all the concerned options for a specific testcase.
  useEffect(() => {
    getReferenceImages();
    showLoader();
    TestResultRelationAPI.getTestcaseResultRelatedObject(
      testcaseResultInfo.id,
      RefObjUriConstants.TESTCASE_OPTION_REFOBJURI
    )
      .then((res) => {
        if (res && res.length > 0) {
          hideLoader();
          res.sort(
            (a, b) =>
              (a.rank ?? Number.MAX_SAFE_INTEGER) -
              (b.rank ?? Number.MAX_SAFE_INTEGER)
          );
          setOptions(res);
        } else {
          hideLoader();
          notification.error({
            className: "notificationError",
            message: "Error",
            description: "Oops! something went wrong ,No answer found!",
            placement: "bottomRight",
          });
        }
        fetchNoteMessage();
      })
      .catch((error) => {
        hideLoader();
      });
  }, []);

  // This useEffect updates the selected option and checks whether there have been any change in the previous
  // answers.
  useEffect(() => {
    const selectedOption = testResultRelationInfos
      .filter((item) => item.selected)
      .map((item) => item.refId);
    setIsModified(
      JSON.stringify(selectedOption) !== JSON.stringify(currentOptions)
    );
  }, [currentOptions]);

  useEffect(() => {
    showLoader();
    TestResultRelationAPI.getTestcaseResultRelationInfosByTestcaseResultIdAndRefObjUri(
      testcaseResultInfo.id,
      RefObjUriConstants.TESTCASE_OPTION_REFOBJURI
    )
      .then((res) => {
        if (res && res.content.length > 0) {
          hideLoader();
          setTestResultRelations(res.content);
        } else {
          hideLoader();
          notification.error({
            className: "notificationError",
            message: "Error",
            description:
              "Oops! something went wrong ,No Result Related Data found!",
            placement: "bottomRight",
          });
        }
      })
      .catch((error) => {
        hideLoader();
      });
  }, []);

  const getReferenceImages = () => {
    if (
      testcaseResultInfo &&
      testcaseResultInfo.id &&
      questionAndDocument.filter(
        (questionItem) => questionItem.key === testcaseResultInfo.id
      ) <= 0
    ) {
      TestResultRelationAPI.getTestcaseResultRelatedObject(
        testcaseResultInfo.id,
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
            item.key = testcaseResultInfo.id;
            item.files = updatedFiles;
            const updatedQuestions = [...questionAndDocument];
            updatedQuestions.push(item);
            setQuestionAndDocument(updatedQuestions);
          }
        })
        .catch((error) => {});
    }
  };

  useEffect(() => {
    if (options.length > 0 && testResultRelationInfos.length > 0) {
      options.forEach((option) => {
        let isSelected = isOptionSelected(option.id);
        if (isSelected) {
          setCurrentOptions((prevOptions) => [...prevOptions, option.id]);
          setPreviousOption([option.id]);
        }
      });
    }
  }, [options, testResultRelationInfos]);

  useEffect(() => {
    setSelectedOptions(currentOptions);
  }, [currentOptions]);

  const handleChange = (e) => {
    if (
      currentQuestion.questionType === ManualQuestionTypeConstants.SINGLE_SELECT
    ) {
      setCurrentOptions([e.target.value]);
    } else if (
      currentQuestion.questionType === ManualQuestionTypeConstants.MULTI_SELECT
    ) {
      let selectedVals = getSelectedCheckboxValuesByName();
      setCurrentOptions(selectedVals);
    }
  };

  const isOptionSelected = (testcaseOptionId) => {
    var testResultRelationInfo = testResultRelationInfos.filter(
      (resultRelationInfo) => resultRelationInfo.refId == testcaseOptionId
    );
    if (!!testResultRelationInfo[0]) {
      return testResultRelationInfo[0].selected;
    } else {
      notification.error({
        className: "notificationError",
        message: "Error",
        description:
          "There is something wrong in filtering result relation info for testOptionId",
        placement: "bottomRight",
      });
    }
  };

  const getSelectedCheckboxValuesByName = () => {
    // Find all input elements with the given name using refs
    const inputs = inputRef.current.filter((ref) => !!ref && ref.checked);
    // Map the selected inputs to get their values
    const selectedValues = inputs.map((input) => input.value);
    return selectedValues;
  };

  const onLabelClick = (e, index) => {
    e.preventDefault(); // Stop event propagation
    if (inputRef.current[index]) {
      inputRef.current[index].click();
    }
  };

  useEffect(() => {
    if (currentOptions.length !== 0) {
      if (JSON.stringify(currentOptions) !== JSON.stringify(previousOption)) {
        setOptionsArray((prev) => {
          const index = prev.findIndex(
            (item) => item.testcaseResultId == testcaseResultInfo.id
          );
          if (index !== -1) {
            const newArray = [...prev];
            newArray[index] = {
              ...newArray[index],
              selectedTestcaseOptionIds: currentOptions,
            };
            return newArray;
          } else {

            return [
              ...prev,
              {
                testcaseResultId: testcaseResultInfo.id,
                selectedTestcaseOptionIds: currentOptions,
              },
            ];
          }
        });
      } else if (
        JSON.stringify(currentOptions) == JSON.stringify(previousOption)
      ) {
        setOptionsArray((prev) => {

          return prev.filter(
            (item) => item.testcaseResultId !== testcaseResultInfo.id
          );
        });
      }
    }
  }, [currentOptions]);

  const downloadFile = (file) => {
    DocumentAPI.downloadDocument(file.id, file.name).catch((err) => {});
  };

  useEffect(() => {}, [uploadedFiles]);

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

  const addFiles = (event, id, index) => {
    // const uploadId = testcaseResultInfo.id
    event.preventDefault();
    setFiles([...event.target.files]);
    setUploadedQuestion({
      id,
      index,
    });
  };

  const addAttachment = () => {
    var file = document.getElementById(`my-file-${testcaseResultInfo.id}`);
    if (file) file.click();
  };

  const fetchNoteMessage = () => {
    TestResultAPI.getTestCaseResultById(testcaseResultInfo.id)
      .then((res) => {
        setNoteMessage(res.message);
      })
      .catch((error) => {});
  };

  const renderTooltip = (props) => (
    <Tooltip id={testcaseResultInfo.id} {...props}>
      Accepted file types: PDFs and images only.
      <br /> Maximum file size: 2MB.
    </Tooltip>
  );

  useEffect(() => {
    if (files.length > 0) {
      files.forEach((file) => {
        uploadDocuments(file, uploadQuestion.id, uploadQuestion.index);
      });
    }
  }, [files]);

  const uploadDocuments = (file, id, index) => {
    const refId = testcaseResultInfo.id;
    let formData = new FormData();
    formData.append(`file`, file);
    formData.append(`fileName`, file.name);
    formData.append("refId", uploadingId);
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

  useEffect(() => {
    if (files.length == 0 && fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  }, [files]);

  const handleSaveNote = async (index) => {
    await saveTestcaseResultWithNote(index);
  };

  const handleEditNoteButtonClick = () => {
    setEditMode(true);
  };

  useEffect(() => {
    const id = testcaseResultInfo.refId;
    if (testcaseResultInfo) {
      DocumentAPI.getDocumentsByRefObjUriAndRefId(
        RefObjUriConstants.TESTCASE_RESULT_REFOBJURI,
        fileId,
        DOCUMENT_STATE_ACTIVE
      )
        .then((res) => {
          setUploadedFiles(res.content);
        })
        .catch((error) => {});
    }
  }, [testcaseResultInfo]);
  return (
    <div id="options">
      <div className="col-12 px-0 question-list" style={{ display: "flex" }}>
        <div className="col-9" style={{ flexGrow: 1 }}>
          <h2>
            <b>
              {" "}
              {index + 1 + ". "}
              {testcaseResultInfo ? testcaseResultInfo.name : " "}
            </b>
          </h2>
        </div>
      </div>
      <div className="row">
        
        <div className={questionAndDocument.length !== 0 ? "col-md-9 col-12 question" : "col-md-12 question"}>
          <div className="custom-multiselect field-checkbox">
            {options &&
              options.map((option, index) => (
                <div
                  className={
                    currentOptions.includes(option.id)
                      ? "field-box option-selected"
                      : "field-box"
                  }
                  key={option.id}
                >
                  <div className="option-item">
                    <input
                      key={option.id}
                      ref={(element) =>
                        element &&
                        inputRef.current.indexOf(element) == -1 &&
                        inputRef.current.push(element)
                      }
                      type="checkbox"
                      id={option.id}
                      name={option.id}
                      value={option.id}
                      checked={currentOptions.includes(option.id)}
                      onChange={(e) => handleChange(e)}
                      autoComplete="off"
                    />
                    <label
                      onClick={(e) => onLabelClick(e, index)}
                      className={
                        currentQuestion.questionType ===
                        ManualQuestionTypeConstants.MULTI_SELECT
                          ? "label-before-no-radius cursor-pointer"
                          : "cursor-pointer"
                      }
                      htmlFor={option.id}
                    >
                      {option.name}
                    </label>
                  </div>
                </div>
              ))}
          </div>
          <div className="doc-badge-wrapper">
            {uploadedFiles.map((file) => (
              <div type="button" key={file.id} className="doc-badge">
                <img src={fileTypeIcon(file.fileType)} />
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
          {showNote && (
            <div className="text-end m-3 position-relative" id="note-textarea">
              <textarea
                style={{borderColor : noteMessage?.length > 2000 ? "red" : ""}}
                className="form-control note-text-area"
                rows="2"
                disabled={!editMode}
                value={noteMessage || ""}
                onChange={(e)=>{handleOnChangeForNote(e,testcaseResultInfo,index)
                }}
              ></textarea>
              <div className="note-text-area-button-group">
                {editMode && (
                  <span
                    role="button"
                    className="save-btn-for-now fw-bold mx-1"
                    title="Cancel"
                    onClick={()=>{handleCancelNoteButtonClick(index)}}
                  >
                    <i className="bi bi-x-lg"></i>
                  </span>
                )}
                {editMode && noteMessage?.length <= 2000 && (
                  <span
                    role="button"
                    className="save-btn-for-now fw-bold mx-1"
                    title="Save Note"
                    onClick={()=>{handleSaveNote(index)}}
                  >
                    <i className="bi bi-floppy"></i>
                  </span>
                )}
                {!editMode && (
                  <span
                    role="button"
                    className="save-btn-for-now fw-bold mx-1"
                    title="Edit Note"
                    onClick={handleEditNoteButtonClick}
                  >
                    <i className="bi bi-pencil-square"></i>
                  </span>
                )}
              </div>
              {noteMessage?.length > 2000 && <p style={{color:"red",textAlign:"left"}}>The note can't be longer than 2000 characters.</p>}
            </div>
          )}
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
                id={`my-file-${testcaseResultInfo.id}`}
                onChange={(e) => {
                  addFiles(e, testcaseResultInfo.id, 0);
                }}
                className="visibility"
              ></input>
              <button
                variant="success"
                type="button"
                className="btn cst-btn-default"
                onClick={addAttachment}
              >
                <i className="bi bi-paperclip rotate"></i>
                Add Attachments
                <OverlayTrigger
                              placement="top"
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
                {noteMessage
                  ? showNote
                    ? "Hide Note"
                    : "Show Note"
                  : showNote
                  ? "Hide Note"
                  : "Add Note"}
              </button>
            </div>
          </div>
        </div>

        { !!questionAndDocument &&
        <div className="col-md-3 col-12">
          <div className=" p-3  q-img">
            <>
              <Image.PreviewGroup>
                <Carousel
                  infinite={false}
                  arrows={true}
                  prevArrow={<LeftOutlined />}
                  nextArrow={<RightOutlined />}
                >
                  {questionAndDocument.length &&
                    questionAndDocument
                      .find((q) => q.key === testcaseResultInfo.id)
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
}
      </div>
    </div>
  );
}
