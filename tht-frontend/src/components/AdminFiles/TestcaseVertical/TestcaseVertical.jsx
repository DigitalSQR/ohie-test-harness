import { Fragment, useEffect, useRef, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import Options from "../Options/Options";
import { useLoader } from "../../loader/LoaderContext";
import { notification, Modal, Carousel, Image, Empty } from "antd";
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
  const [unsavedNotes,setUnSavedNotes] = useState([]);
  useEffect(() => {
    getCurrentTestcaseResultById(currentTestcase.id);
  }, [currentTestcase]);

  const handleSaveandNext = () => {
    var correctLength = true;

    unsavedNotes.forEach(note=>{
      if(note.value.length > 2000)
      {correctLength = false}
    })
    if(correctLength){
    const testcaseNos = unsavedNotes.map(note => note.key + 1).join(",");
    if(unsavedNotes.length !== 0){
      Modal.confirm({
        title:"Warning!",
        content:`You have unsaved notes in testcases ${testcaseNos} . Do you wish to save and proceed?`,
        okText:"Save and Proceed",
        onOk:async()=>{
          showLoader();
          for (const notes of unsavedNotes) {
            await saveTestcaseResultWithNote(notes.value, notes.testcaseResultInfo);
        }        
          hideLoader();
          setUnSavedNotes([]);
          if(optionsArray.length == 0){
            selectNextSpecification();
          }else{
          TestResultAPI.saveOptions(optionsArray);
          selectNextSpecification();
          }
        }
      })

    }else{
      if(optionsArray.length == 0){
        selectNextSpecification();
      }else{
      TestResultAPI.saveOptions(optionsArray);
      selectNextSpecification();
      }
    }
  }else {
    notification.error({
      className:"notificationError",
      message: "One of your notes exceeds the notes limitation. Please check your notes.",
      placement:"bottomRight"
    })
  }

  }

  const getCurrentTestcaseResultById = (testcaseResultId) => {
    TestResultAPI.getTestCaseResultById(testcaseResultId)
      .then((res) => {
        setTestcaseResult(res);
      })
      .catch((error) => {});
  };


  const saveTestcaseResultWithNote = async (value,testcaseResultInfo) => {    
      var notePatch = [
        { op: "replace", path: "/message", value: value.trim() },
      ];
      const result = await TestResultAPI.patchTestCaseResult(testcaseResultInfo.id, notePatch)
      return result;
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
          } else {
            notification.error({
              className: "notificationError",
              message: "Oops! something wrong ,No question found!",
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
          message: `Attachment added successfully!`,
        });
      })
      .catch((error) => {});
  };


  useEffect(() => {
    if (files.length == 0 && fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  }, [files]);


  useEffect(() => {}, [uploadedFiles]);




  return (
    <div id="testcaseVertical">
      {currentSpecification.childTestcaseResults.map(
        (testcaseResult, index) => {
          return (
            <div className="col-12 non-fuctional-requirement" id={testcaseResult.id}>
              <div>
                {testcaseResult && (
                  <div className=" question-box" key={testcaseResult.id}>
                  

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
                        setUnSavedNotes={setUnSavedNotes}
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
