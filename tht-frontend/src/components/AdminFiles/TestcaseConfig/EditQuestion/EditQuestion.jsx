import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Switch, notification, Modal } from "antd";
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

import {
  DOCUMENT_STATE_ACTIVE,
  DOCUMENT_STATE_INACTIVE,
} from "../../../../constants/document_constants";

export default function EditQuestion() {
  const { testcaseId } = useParams();
  const [currentTestcase, setCurrentTestcase] = useState({});
  const [currentAccordionIndex, setCurrentAccordionIndex] = useState(null);
  const navigate = useNavigate();
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

  const [component, setComponent] = useState();
  const [specification, setSpecification] = useState();

  const [changesMade, setChangesMade] = useState(false);
  const [changesMadeToEditOption, setChangesMadeToEditOption] = useState(false);

  const [initialTestcaseOptions, setInitialTestcaseOptions] = useState([]);
  const [editedTestcaseOptions, setEditedTestcaseOptions] = useState([]);
  const [fileList, setFileList] = useState();

  const handleCancel = () => setPreviewOpen(false);
  const handlePreview = (file) => {
    return file.url;
  };
  const handleChange = ({ fileList: newFileList }) => setFileList(newFileList);

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
        notification.error({
          message: "Error Loading question!",
          placement: "bottomRight",
        });
      });

    TestCaseOptionsAPI.getTestCaseOptionsByTestcaseId(testcaseId)
      .then((res) => {
        if (res.content && res.content.length > 0) {
          console.log("----", res.content);
          setInitialTestcaseOptions(res.content);
          setEditedTestcaseOptions(res.content);
        } else {
          notification.warning({
            message: "Options are not available for this question!",
            placement: "bottomRight",
          });
        }
      })
      .catch((error) => {
        notification.error({
          message: "Error Loading options!",
          placement: "bottomRight",
        });
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
              };
            } catch (error) {
              console.error(error);
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
        console.log(err);
        notification.error({
          message: "Error Loading Files!",
          placement: "bottomRight",
        });
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

  const handleUpload = (file) => {
    const formData = new FormData();
    formData.append(`file`, file);
    formData.append(`fileName`, file.name);
    formData.append("refId", currentTestcase.id);
    formData.append("refObjUri", RefObjUriConstants.TESTCASE_REFOBJURI);
    formData.append(
      "documentType",
      DOCUMENT_TYPE_FOR_TEST_CASES.DOCUMENT_TYPE_QUESTION
    );

    return DocumentAPI.uploadDocument(formData);
  };

  const makeDocumentInactive = (file) => {
    DocumentAPI.changeDocumentState(file.documentId, DOCUMENT_STATE_INACTIVE)
      .then((res) => {
        notification.success({
          message: "Document Removed",
          placement: "bottomRight",
        });
        const index = fileList.indexOf(file);
        const newFileList = fileList.slice();
        newFileList.splice(index, 1);
        setFileList(newFileList);
      })
      .catch((err) => {
        let msg = err.response.data?.message || err.response.data[0].message;
        notification.error({
          message: `${msg}`,
          placement: "bottomRight",
        });
      });
  };

  const confirmDocumentChangeToInactive = (file) => {
    Modal.confirm({
      title: "Delete Image",
      content: "Are you sure about deleting this image ?",
      okText: "Yes",
      cancelText: "Cancel",
      onOk() {
        makeDocumentInactive(file);
      },
    });
  };

  const props = {
    onRemove: (file) => {
      if (file) {
        confirmDocumentChangeToInactive(file);
      }
    },
    beforeUpload: (file) => {
      const isImage =
        file.type === "image/png" ||
        file.type === "image/jpeg" ||
        file.type === "image/jpg";

      if (!isImage) {
        notification.error({
          message: "Only JPEG and PNG Files are allowed.",
          placement: "bottomRight",
        });
        return false;
      }
    },
    customRequest: (options) => {
      if (options.file) {
        handleUpload(options.file).then(async (response) => {
          const base64Image = await DocumentAPI.base64Document(
            response.id,
            response.name
          );
          let imageData = {
            name: response.name,
            status: "done",
            url: base64Image,
            documentId: response.id,
          };
          const updatedFileList = [...fileList];
          updatedFileList.push(imageData);
          setFileList(updatedFileList);
        }).catch((error)=>{

        });
      }
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
            placement: "bottomRight",
            message: "Question updated successfully",
          });
        })
        .catch((error) => {
          console.error("Error saving question:", error);
          notification.error({
            placement: "bottomRight",
            message: "Failed to save question",
          });
        });
    } finally {
      hideLoader();
    }
  };

  const toggleOption = (index) => {
    if (expandedOptionIndex === index) {
      setExpandedOptionIndex(null);
    } else {
      setExpandedOptionIndex(index);
    }
  };

  const handleSelectAccordionItem = (index) => {
    console.log(index + " ");
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
          placement: "bottomRight",
          message: "Option saved successfully",
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
            placement: "bottomRight",
            message: "State Changed successfully",
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
      <div id="wrapper">
        <div className="bcca-breadcrumb">
          <div className="bcca-breadcrumb-item">Question</div>
          <div
            className="bcca-breadcrumb-item"
            onClick={() => navigate(`/testcase-config/manual-testcases/${specification?.id}`)}
          >
            {specification?.name} - Manual Configuration
          </div>
          <div
            className="bcca-breadcrumb-item"
            onClick={() =>
              navigate(`/testcase-config/component-specification/${component?.id}`)
            }
          >
            {" "}
            {component?.name}{" "}
          </div>
          <div
            className="bcca-breadcrumb-item"
            onClick={() => {
              navigate("/testcase-config");
            }}
          >
            Components
          </div>
        </div>
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
                        id="multiSelect"
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
                        name="questionType"
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
                    disabled={!changesMade}
                    onClick={handleSaveQuestion}
                    type="button"
                    className="btn btn-primary mt-3"
                  >
                    Save
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
              <div className="text-center my-4 fs-6">
                No Options yet for the current testcase
              </div>
            ) : (
              <Accordion onSelect={(index) => handleSelectAccordionItem(index)}>
                {editedTestcaseOptions.map((option, index) => (
                  <div key={index}>
                    <Accordion.Item eventKey={index.toString()}>
                      <Accordion.Header>
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
                              className="btn btn-primary mt-3"
                              disabled={!changesMadeToEditOption}
                              onClick={() => handleSaveOption(index)}
                            >
                              Save Option
                            </button>
                          </div>
                          <div className="col-auto">
                            <div className="mt-4">
                              <span className="form-check form-switch">
                                <Switch
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
    )
  );
}