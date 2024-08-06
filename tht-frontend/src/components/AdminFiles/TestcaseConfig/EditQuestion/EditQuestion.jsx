import React, { useState, useEffect } from "react";
import { Link, useParams } from "react-router-dom";
import { Switch, notification, Modal, Breadcrumb } from "antd";
import "./EditQuestion.scss";
import { TestCaseOptionsAPI } from "../../../../api/TestCaseOptionsAPI";
import { useLoader } from "../../../loader/LoaderContext";
import { TestCaseAPI } from "../../../../api/TestCaseAPI";
import { SpecificationAPI } from "../../../../api/SpecificationAPI";
import { ComponentAPI } from "../../../../api/ComponentAPI";
import { ManualQuestionTypeConstants } from "../../../../constants/testcase_constants";
import TestCaseOptionUpsertModal from "./TestCaseOptionUpsertModal/TestCaseOptionUpsertModal";
import Accordion from "react-bootstrap/Accordion";
import { RefObjUriConstants } from "../../../../constants/refObjUri_constants";
import { DOCUMENT_TYPE_FOR_TEST_CASES } from "../../../../constants/document_constants";
import { DocumentAPI } from "../../../../api/DocumentAPI";
import { PlusOutlined } from "@ant-design/icons";
import { Upload } from "antd";
import { Empty } from "antd";
import {
  DOCUMENT_STATE_ACTIVE,
  DOCUMENT_STATE_INACTIVE,
} from "../../../../constants/document_constants";

export default function EditQuestion() {
  const { testcaseId } = useParams();
  const [currentTestcase, setCurrentTestcase] = useState({});
  const [currentAccordionIndex, setCurrentAccordionIndex] = useState(null);
  const { showLoader, hideLoader } = useLoader();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editedQuestion, setEditedQuestion] = useState("");
  const [initialQuestion, setInitialQuestion] = useState("");
  const [initialQuestionType, setInitialQuestionType] = useState();
  const [editedQuestionType, setEditedQuestionType] = useState();
  const [expandedOptionIndex, setExpandedOptionIndex] = useState(null);
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [previewTitle, setPreviewTitle] = useState("");
  const [filesTobeDeleted,setFilesTobeDeleted] = useState([]);
  const [component, setComponent] = useState();
  const [specification, setSpecification] = useState();

  const [changesMade, setChangesMade] = useState(false);
  const [changesMadeInPictures,setChangesMadeInPictures] = useState(false);
  const [changesMadeToEditOption, setChangesMadeToEditOption] = useState(false);

  const [initialTestcaseOptions, setInitialTestcaseOptions] = useState([]);
  const [editedTestcaseOptions, setEditedTestcaseOptions] = useState([]);
  const [fileList, setFileList] = useState([]);

  const handleCancel = () => setPreviewOpen(false);

  useEffect(()=>{console.log(fileList);},[fileList])


  const getBase64 = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });

  const fetchData = () => {
    TestCaseAPI.getTestCasesById(testcaseId)
      .then((res) => {
        setCurrentTestcase(res);
        setEditedQuestion(res.name);
        setInitialQuestion(res.name);
        setEditedQuestionType(res.questionType);
        setInitialQuestionType(res.questionType);
      })
      .catch((error) => {
      });

    TestCaseOptionsAPI.getTestCaseOptionsByTestcaseId(testcaseId)
      .then((res) => {
        if (res.content && res.content.length > 0) {
          setInitialTestcaseOptions(res.content);
          setEditedTestcaseOptions(res.content);
        } else {
          notification.warning({
            className:"notificationWarning",
						message: "There are currently no options available for a question. Please add some options.",
            placement: "bottomRight",
          });
        }
      })
      .catch((error) => {
      });

    DocumentAPI.getDocumentsByRefObjUriAndRefId(
      RefObjUriConstants.TESTCASE_REFOBJURI,
      testcaseId,
      DOCUMENT_STATE_ACTIVE,
      DOCUMENT_TYPE_FOR_TEST_CASES.DOCUMENT_TYPE_QUESTION
    )
      .then(async (res) => {
        const updatedFiles = await Promise.all(
          res.content.map(async (relatedDoc) => {
            try {
              const base64Image = await DocumentAPI.base64Document(
                relatedDoc.id,
                relatedDoc.name
              );
              return {
                name: relatedDoc.name,
                status: "done",
                url: base64Image,
                documentId: relatedDoc.id,
                current_state:"saved"
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

        setFileList(updatedFiles);
      })
      .catch((err) => {
      });
  };

  const fetchCompSpecDetails = () => {
    TestCaseAPI.getTestCasesById(testcaseId)
      .then((resp1) => {
        SpecificationAPI.getSpecificationById(resp1.specificationId)
          .then((resp2) => {
            setSpecification(resp2);
            ComponentAPI.getComponentById(resp2.componentId)
              .then((resp3) => {
                setComponent(resp3);
              })
              .catch((error) => {});
          })
          .catch((error) => {});
      })
      .catch((error) => {});
  };

  useEffect(() => {
    fetchData();
    fetchCompSpecDetails();
  }, [testcaseId]);

  const handleUpload = async(file) => {
    const formData = new FormData();
    formData.append(`file`, file.file);
    formData.append(`fileName`, file.name);
    formData.append("refId", currentTestcase.id);
    formData.append("refObjUri", RefObjUriConstants.TESTCASE_REFOBJURI);
    formData.append(
      "documentType",
      DOCUMENT_TYPE_FOR_TEST_CASES.DOCUMENT_TYPE_QUESTION
    );

    return DocumentAPI.uploadDocument(formData);
  };

    

  const confirmDocumentChangeToInactive = (file) => {
    Modal.confirm({
      okButtonProps:{id:`editQuestion-deactivateFile-okButton`},
      cancelButtonProps:{id:`editQuestion-deactivateFile-cancelButton`},
      title: "Delete Image",
      content: "Are you sure about deleting this image ?",
      okText: "Yes",
      cancelText: "Cancel",
      onOk() {
        if(file.current_state === "saved"){
          setFilesTobeDeleted([...filesTobeDeleted,file]);
        }
        const updatedFileList = fileList.filter(prevFiles => prevFiles.documentId !== file.documentId);
        setFileList(updatedFileList);
        setChangesMadeInPictures(true);
      },
    });
  };

  const props = {
    onRemove: (file) => {
        confirmDocumentChangeToInactive(file);
    },
    beforeUpload: async(file) => {
      setChangesMadeInPictures(true);
      const isImage =
        file.type === "image/png" ||
        file.type === "image/jpeg" ||
        file.type === "image/jpg";

      if (!isImage) {
        notification.error({
          className:"notificationError",
          message:"Only JPEG and PNG Files are allowed.",
          placement: "bottomRight",
        });
        return false;
      }

      if(isImage){
        const base64url = await getBase64(file);
        const newImage = {name:file.name,documentId:file.uid,url:base64url,current_state:"unsaved",file:file};
        setFileList([...fileList,newImage]);
      }
      
      
    },
    customRequest: (options) => {
    },
    onDrop(event) {
      console.log(event);
    },
    onPreview(file) {
      setPreviewImage(file.url);
      setPreviewTitle(file.name);
      setPreviewOpen(true);

      return false;
    },
  };

  const handleQuestionChange = (newValue) => {
    setEditedQuestion(newValue);
  };

  const handleQuestionTypeChange = (newValue) => {
    setEditedQuestionType(newValue);
  };

  useEffect(() => {
    setChangesMadeToEditOption(
      JSON.stringify(editedTestcaseOptions[currentAccordionIndex]) !=
        JSON.stringify(initialTestcaseOptions[currentAccordionIndex])
    );
  }, [editedTestcaseOptions]);

  useEffect(() => {
    setChangesMade(
      editedQuestion !== initialQuestion ||
        editedQuestionType !== initialQuestionType
    );
  }, [editedQuestionType, editedQuestion]);

  const handleSaveQuestion = async () => {
    if(changesMadeInPictures){
      for (const [index, options] of fileList.entries()) {
        //uploading unsaved images
        if (options.current_state === "unsaved") {
          try {
            const response = await handleUpload(options);
            notification.success({
              className: "notificationSuccess",
              placement: "top",
              message: `Image ${response.name} uploaded successfully!`,
            });
      
            setFileList((prevFileList) =>
              prevFileList.map((item, idx) =>
                idx === index
                  ? { ...item, current_state: "saved", documentId : response.id }
                  : item
              )
            );
          } catch (error) {
            notification.error({
              className: "notificationError",
              message: `Error while uploading ${options.name}`,
              placement: "bottomRight",
            });
      
            setFileList((prevFiles) =>
              prevFiles.filter((file) => file.documentId !== options.documentId)
            );
          }
        }
      }
      for (const file of filesTobeDeleted) {
        //deleting saved images 
        try {
          const response = await DocumentAPI.changeDocumentState(
            file.documentId,
            DOCUMENT_STATE_INACTIVE
          );
          notification.success({
            className: "notificationSuccess",
            message: `Successfully deleted ${file.name}`,
            placement: "bottomRight",
          });} 
          catch (error) {
          notification.error({
            className: "notificationError",
            message: `Error while deleting ${file.name}`,
            placement: "bottomRight",
          });
          setFileList([...fileList,file]);
        }
      }
      setFilesTobeDeleted([]);
    }
    
    if(changesMade){
      try {
        showLoader();
        var patch = [
          { op: "replace", path: "/name", value: editedQuestion },
          { op: "replace", path: "/questionType", value: editedQuestionType },
        ];
  
        TestCaseAPI.patchTestcase(testcaseId, patch)
          .then((res) => {
            setInitialQuestion(res.name);
            setInitialQuestionType(res.questionType);
            setChangesMade(false);
            notification.success({
              className:"notificationSuccess",
              placement: "top",
              message:"Testcase question updated successfully!",
            });
          })
          .catch((error) => {
          });
      } finally {
        hideLoader();
      }
    }
    setChangesMadeInPictures(false);
  };

  const toggleOption = (index) => {
    if (expandedOptionIndex === index) {
      setExpandedOptionIndex(null);
    } else {
      setExpandedOptionIndex(index);
    }
  };

  const handleSelectAccordionItem = (index) => {
    if (currentAccordionIndex) {
      const updatedOptions = [...initialTestcaseOptions];
      updatedOptions[currentAccordionIndex] =
        initialTestcaseOptions[currentAccordionIndex];
      setEditedTestcaseOptions(updatedOptions);
    }
    setCurrentAccordionIndex(index);
  };

  const handleOptionNameChange = (index, e) => {
    const updatedOptions = [...editedTestcaseOptions];
    updatedOptions[index] = { ...updatedOptions[index], name: e.target.value };
    setEditedTestcaseOptions(updatedOptions);
  };

  const handleIsCorrectChange = (index, e) => {
    const updatedOptions = [...editedTestcaseOptions];
    updatedOptions[index] = {
      ...updatedOptions[index],
      success: e.target.checked,
    };
    setEditedTestcaseOptions(updatedOptions);
  };

  const handleSaveOption = (index) => {
    let optionToUpdate = editedTestcaseOptions[index];
    var patch = [
      { op: "replace", path: "/name", value: optionToUpdate.name },
      { op: "replace", path: "/success", value: optionToUpdate.success },
    ];

    TestCaseOptionsAPI.patchTestcaseOption(optionToUpdate.id, patch)
      .then((res) => {
        const updatedOptions = [...initialTestcaseOptions];
        updatedOptions[index] = res;
        setInitialTestcaseOptions(updatedOptions);
        setEditedTestcaseOptions(updatedOptions);
        notification.success({
          className:"notificationSuccess",
          placement: "top",
          message:"Testcase option updated successfully!",
        });
      })
      .catch((error) => {});
  };

  const changeOptionState = (index) => {
    const newState =
      initialTestcaseOptions[index].state === "testcase.option.status.active"
        ? "testcase.option.status.inactive"
        : "testcase.option.status.active";

    const confirmStateChange = () => {
      Modal.confirm({
        okButtonProps:{id:`editQuestion-deactivateOption-okButton`},
        cancelButtonProps:{id:`editQuestion-deactivateOption-cancelButton`},
        title: "State Change",
        content: "Are you sure about changing state to Inactive ?",
        okText: "Save",
        cancelText: "Cancel",
        onOk() {
          handleStateChange(newState);
        },
      });
    };

    const handleStateChange = (newState) => {
      TestCaseOptionsAPI.changeState(initialTestcaseOptions[index].id, newState)
        .then((res) => {
          const updatedOptions = [...initialTestcaseOptions];
          updatedOptions[index] = res;
          setInitialTestcaseOptions(updatedOptions);
          setEditedTestcaseOptions(updatedOptions);
          notification.success({
            className:"notificationSuccess",
            placement: "top",
            message:`Option has been marked as ${newState==='testcase.option.status.active' ? 'active' : 'inactive'} successfully!`,
          });
        })
        .catch((error) => {});
    };

    if (
      initialTestcaseOptions[index].state === "testcase.option.status.active"
    ) {
      confirmStateChange();
    } else {
      handleStateChange(newState);
    }
  };

  return (
    currentTestcase && (
      <div id="editQuestion">
      <div id="wrapper">
        <Breadcrumb className="custom-breadcrumb">
          <Breadcrumb.Item><Link id="editQuestion-navToTestcaseConfig" to="/testcase-config" className="breadcrumb-item">Components</Link></Breadcrumb.Item>
          <Breadcrumb.Item>
            <Link id={`editQuestion-navTo-component-${component?.name}`} to={`/testcase-config/component-specification/${component?.id}`} className="breadcrumb-item">
              {component?.name}
            </Link>
          </Breadcrumb.Item>

          
          <Breadcrumb.Item>
          <Link id={`editQuestion-navToTestCases-for-${specification?.name}`} to={`/testcase-config/manual-testcases/${specification?.id}`} className="breadcrumb-item">
          {specification?.name} - Testcase Configuration
          </Link>
          </Breadcrumb.Item>
          <Breadcrumb.Item className="breadcrumb-item">Question</Breadcrumb.Item>
        </Breadcrumb>
        <div className="col-12 my-4">
          <div className="card">
            <div className="card-header">Manage Question</div>
            <div className="card-body row">
              <div className="col-md-8 col-12">
                <form>
                  <div className="form-group mb-3">
                    <label htmlFor="question" className="mb-2">
                      Question
                    </label>
                    <textarea
                      className="form-control"
                      id="question"
                      rows={3}
                      value={editedQuestion}
                      onChange={(e) => handleQuestionChange(e.target.value)}
                      placeholder="Enter your question"
                    ></textarea>
                  </div>
                  <div className="form-group mb-3">
                    <label>Question Type</label>
                    <div className="form-check">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="questionType"
                        id="editQuestion-multiSelect"
                        value={ManualQuestionTypeConstants.MULTI_SELECT}
                        checked={editedQuestionType === "MULTI_SELECT"}
                        onChange={(e) =>
                          handleQuestionTypeChange(e.target.value)
                        }
                      />
                      <label className="form-check-label" htmlFor="multiSelect">
                        Multi Select
                      </label>
                    </div>
                    <div className="form-check">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="editQuestion-questionType"
                        id="singleSelect"
                        value={ManualQuestionTypeConstants.SINGLE_SELECT}
                        checked={editedQuestionType === "SINGLE_SELECT" || !editedQuestionType}
                        onChange={(e) =>
                          handleQuestionTypeChange(e.target.value)
                        }
                      />
                      <label
                        className="form-check-label"  
                        htmlFor="singleSelect"
                      >
                        Single Select
                      </label>
                    </div>
                  </div>
                  <button
                  id={`editQuestion-updateQuestion`}
                    disabled={(!changesMade && !changesMadeInPictures)}
                    onClick={handleSaveQuestion}
                    type="button"
                    className="btn btn-sm btn-primary mt-3"
                  >
                    Update Question
                  </button>
                </form>
              </div>
              <div className="col-md-4 image-border-left">
                <div>
                  <div className="p-2 pt-2 q-img">
                    {/* <label>Reference Images</label> */}
                    <>
                      <div className="d-flex justify-content-between">
                        <Upload
                          {...props}
                          listType="picture-card"
                          accept=".png,.jpg,.jpeg,image/png,image/jpeg"
                          fileList={fileList}
                        >
                          <button
                            id="editQuestion-uploadImageForOption"
                            style={{
                              border: 0,
                              background: "none",
                            }}
                            type="button"
                          >
                            <div>
                              <PlusOutlined />
                              <div style={{ marginTop: 8 }}>Upload</div>
                            </div>
                          </button>
                        </Upload>
                      </div>
                      <Modal
                      cancelButtonProps={{id:"editQuestion-previewImage-cancelButton"}}
                        open={previewOpen}
                        title={previewTitle}
                        footer={null}
                        onCancel={handleCancel}
                      >
                        <img
                          alt="example"
                          style={{
                            width: "100%",
                          }}
                          src={previewImage}
                        />
                      </Modal>
                    </>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className="d-flex justify-content-end my-3">
            <div>
              <button
                type="button"
                id={`editQuestion-addOption`}
                className="btn btn-sm btn-outline-secondary menu-like-item"
                onClick={() => setIsModalOpen(true)}
              >
                <i className="bi bi-plus"></i>
                Add Option
              </button>
            </div>
          </div>

          <div className="card mt-3">
            <div className="card-header">Manage Options</div>

            {initialTestcaseOptions.length < 1 ? (
              <Empty
                className="py-3"
                description=" No Record Found"
              />
            ) : (
              <Accordion onSelect={(index) => handleSelectAccordionItem(index)}>
                {editedTestcaseOptions.map((option, index) => (
                  <div key={index}>
                    <Accordion.Item eventKey={index.toString()}>
                      <Accordion.Header id={`editQuestion-detailsAboutOption-${index}`}>
                        {initialTestcaseOptions[index].success ? (
                          <i className="bi bi-check-circle-fill me-2 text-success"></i>
                        ) : (
                          <i className="bi bi-x-circle-fill me-2 text-danger"></i>
                        )}

                        {initialTestcaseOptions[index].name}
                      </Accordion.Header>
                      <Accordion.Body>
                        <div className="form-group">
                          <label htmlFor={`option${index}`}>Option Label</label>
                          <textarea
                            className="form-control"
                            id={`option${index}`}
                            value={option.name || ""}
                            onChange={(e) => handleOptionNameChange(index, e)}
                          />
                        </div>

                        <div className="form-group mt-2">
                          <span className="form-check">
                            <input
                              className="form-check-input"
                              type="checkbox"
                              id={`isCorrect${index}`}
                              checked={option.success}
                              onChange={(e) => handleIsCorrectChange(index, e)}
                            />
                            <label
                              className="form-check-label"
                              htmlFor={`isCorrect${index}`}
                            >
                              Is Correct
                            </label>
                          </span>
                        </div>

                        <div className="row">
                          <div className="col-auto">
                            <button
                            id={`editQuestion-updateOption-${index}`}
                              className="btn btn-sm btn-primary mt-3"
                              disabled={!changesMadeToEditOption}
                              onClick={() => handleSaveOption(index)}
                            >
                              Update Option
                            </button>
                          </div>
                          <div className="col-auto">
                            <div className="mt-3">
                              <span >
                                <Switch
                                  id={`editQuestion-switchOption-${index}`}
                                  onChange={() => changeOptionState(index)}
                                  checked={
                                    option?.state ===
                                    "testcase.option.status.active"
                                  }
                                  checkedChildren="ACTIVE"
                                  unCheckedChildren="INACTIVE"
                                />
                              </span>
                            </div>
                          </div>
                        </div>
                      </Accordion.Body>
                    </Accordion.Item>
                  </div>
                ))}
              </Accordion>
            )}
          </div>
        </div>

        <TestCaseOptionUpsertModal
          isModalOpen={isModalOpen}
          setIsModalOpen={setIsModalOpen}
          testcaseId={testcaseId}
          fetchData={fetchData}
        />
      </div>
      </div>
    )
  );
}
