import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { TestCaseAPI } from "../../../../api/TestCaseAPI";
import { SpecificationAPI } from "../../../../api/SpecificationAPI";
import { ComponentAPI } from "../../../../api/ComponentAPI";
import { useLoader } from "../../../loader/LoaderContext";
import { Empty } from "antd";
import {
  EditFilled,
  LeftOutlined,
  RightOutlined,
  LoadingOutlined,
  FileImageOutlined,
} from "@ant-design/icons";
import { TestCaseOptionsAPI } from "../../../../api/TestCaseOptionsAPI";
import "./SpecQuestions.scss";
import { Switch, Tabs, Carousel, notification, Image, Modal } from "antd";
import { useDispatch } from "react-redux";
import { set_header } from "../../../../reducers/homeReducer";
import { ManualQuestionTypeConstants } from "../../../../constants/testcase_constants";
import SpecQuestionsUpsertModal from "./SpecQuestionsUpsertModal/SpecQuestionsUpsertModal";
import TabPane from "antd/es/tabs/TabPane";
import { DocumentAPI } from "../../../../api/DocumentAPI";
import { RefObjUriConstants } from "../../../../constants/refObjUri_constants";
import {
  DOCUMENT_STATE_ACTIVE,
  DOCUMENT_STATE_INACTIVE,
  DOCUMENT_TYPE_FOR_TEST_CASES,
} from "../../../../constants/document_constants";

export default function ManualTestCases() {
  const navigate = useNavigate();
  const { specificationId } = useParams();
  const dispatch = useDispatch();
  const { showLoader, hideLoader } = useLoader();
  const [questions, setQuestions] = useState();
  const [activeKey, setActiveKey] = useState("1");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [questionAndDocument, setQuestionAndDocument] = useState([]);
  const [questionFetched, setQuestionFetched] = useState(true);
  const [componetDetails, setComponentDetails] = useState();
  const [specificationDetails, setSpecificationDetails] = useState();
  const [activeTab, setActiveTab] = useState("1");
  const items = [
    {
      key: "1",
      label: "Automation",
    },
    {
      key: "2",
      label: "Manual",
    },
  ];
  const onChange = (key) => {
    setActiveTab(key);
    fetchData(key === "2");
  };

  useEffect(() => {
    fetchData(false);
    fetchCompSpecDetails();
    dispatch(set_header("Manual Configuration"));
  }, []);

  const fetchCompSpecDetails = () => {
    SpecificationAPI.getSpecificationById(specificationId)
      .then((resp1) => {
        setSpecificationDetails(resp1);
        ComponentAPI.getComponentById(resp1.componentId)
          .then((resp2) => {
            setComponentDetails(resp2);
          })
          .catch((error) => {});
      })
      .catch((error) => {});
  };

  const handleUpdate = (question) => {
    navigate(`/testcase-config/edit-question/${question.id}`);
  };

  const fetchData = async (isManual) => {
    try {
      showLoader();
      const params = {};
      params.specificationId = specificationId;
      params.isManual = isManual;
      const resp = await TestCaseAPI.getTestCasesBySpecificationId(params);
      if (resp.length < 1) {
        hideLoader();
        return;
      }
      const questionPromises = resp.content.map(async (testcase) => {
        try {
          const optionsResp = await fetchTestCaseOptions(testcase.id);
          const options = optionsResp.sort((a, b) => a.rank - b.rank);
          return {
            id: testcase.id,
            metaVersion: testcase.meta.version,
            description: testcase.description,
            questionType: testcase.questionType,
            question: testcase.name,
            rank: testcase.rank,
            testcase,
            options,
          };
        } catch (optionsError) {
          console.error(
            `Error fetching options for testCaseId ${testcase.id}:`,
            optionsError
          );
          return null;
        }
      });
      const questionResults = await Promise.all(questionPromises);
      const sortedQuestions = questionResults.sort((a, b) => a.rank - b.rank);
      setQuestions(sortedQuestions);
      const filteredQuestions = questionResults.filter(
        (question) => question !== null
      );
      setQuestions(filteredQuestions);
    } catch (error) {
      console.error("Error fetching test cases:", error);
    } finally {
      hideLoader();
    }
  };

  const handleClick = (path) => {
    navigate(path);
  };

  const fetchTestCaseOptions = async (testcaseId) => {
    const optionsResp = await TestCaseOptionsAPI.getTestCaseOptionsByTestcaseId(
      testcaseId
    );
    return optionsResp.content;
  };

  const handleTabChange = (activeKey) => {
    setActiveKey(activeKey);
    let key;
    if (activeKey) {
      key = activeKey - 1;
    }
    if (
      questions &&
      questionAndDocument.filter(
        (questionItem) => questionItem.key === questions[key].id
      ) <= 0
    ) {
      setQuestionFetched(false);
      let question = questions[key];

      DocumentAPI.getDocumentsByRefObjUriAndRefId(
        RefObjUriConstants.TESTCASE_REFOBJURI,
        question.id,
        DOCUMENT_STATE_ACTIVE,
        DOCUMENT_TYPE_FOR_TEST_CASES.DOCUMENT_TYPE_QUESTION
      )
        .then(async (res) => {
          const updatedFiles = await Promise.all(
            res.content.map(async (relatedDoc) => {
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
                console.error(error);
                return {
                  name: relatedDoc.name,
                  status: "error",
                  url: null,
                };
              }
            })
          );

          let item = {};
          item.key = question.id;
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
        })
        .finally(() => {
          setQuestionFetched(true);
        });
    }
  };

  const changeTestCaseState = (testcaseId, state) => {
    const newState =
      state === "testcase.status.active"
        ? "testcase.status.inactive"
        : "testcase.status.active";

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
      TestCaseAPI.changeState(testcaseId, newState)
        .then((res) => {
          hideLoader();

          setQuestions((prevQuestions) => {
            const updatedQuestions = prevQuestions?.map((question) =>
              question.id === testcaseId
                ? { ...question, testcase: res.data }
                : question
            );
            return updatedQuestions;
          });

          fetchData();
        })
        .catch((error) => {
          throw error;
        });
    };

    if (state === "testcase.status.active") {
      confirmStateChange();
    } else {
      handleStateChange(newState);
    }
  };

  return (
    <div id="SpecQuestions">
      <div id="wrapper">
        <div className="bcca-breadcrumb">
          <div className="bcca-breadcrumb-item">
            {specificationDetails?.name} - Manual Configuration
          </div>
          <div
            className="bcca-breadcrumb-item"
            onClick={() =>
              handleClick(
                `/testcase-config/component-specification/${componetDetails?.id}`
              )
            }
          >
            {" "}
            {componetDetails?.name}{" "}
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

        <div className="d-flex justify-content-between align-items-center">
          <Tabs
            type="card"
            className="mt-5"
            activeKey={activeTab}
            items={items}
            onChange={onChange}
          />

          {activeTab === "2" && (
            <div className="mt-5 create-testcase-button">
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary menu-like-item"
                onClick={() => setIsModalOpen(true)}
              >
                <i className="bi bi-plus"></i>
                Create Testcase
              </button>
            </div>
          )}
        </div>

        {activeTab === "2" ? (
          <div className="">
            {!questions || questions?.length === 0 ? (
              <Empty
                imageStyle={{
                  height: 200, // Adjust the height of the image
                }}
                description={<span>No Data</span>} // Custom description message
              />
            ) : (
              <div className="row">
                <div className="col-12 col-md-8 offset-md-2"></div>
                <Tabs
                  type="card"
                  defaultActiveKey="1"
                  tabPosition="top"
                  className="questions-tabs mt-3"
                  activeKey={activeKey}
                  onChange={handleTabChange}
                >
                  {questions?.map((question, index) => (
                    <TabPane tab={`Question ${index + 1}`} key={index + 1}>
                      <div className="col-12 non-fuctional-requirement mt-3">
                        <div className="container-fluid">
                          <div className="row heading">
                            <div className="col-md-7 col-12 p-0">
                              <h2>Question</h2>
                            </div>

                            <div className="col-md-5 col-12 d-md-flex d-none p-0">
                              <h2 className="border-left">Reference</h2>
                            </div>

                          </div>

                          <div className="row question-box">
                            <div className="col-md-7 col-12 p-0 question">
                              <h2>
                                <b>
                                  {question.rank}. {question.question}
                                </b>
                              </h2>

                              <div className="custom-multiselect field-checkbox">
                                {question.options &&
                                  question.options.map(
                                    (option, optionIndex) => (
                                      <div
                                        className="field-box option-item"
                                        key={optionIndex}
                                      >
                                        {option.success ? (
                                          <i className="bi bi-check-circle-fill me-2 text-success"></i>
                                        ) : (
                                          <i className="bi bi-x-circle-fill me-2 text-danger"></i>
                                        )}
                                        <label
                                          className={
                                            question.questionType ===
                                            ManualQuestionTypeConstants.MULTI_SELECT
                                              ? "label-before-no-radius"
                                              : ""
                                          }
                                        >
                                          {option.name}
                                        </label>
                                      </div>
                                    )
                                  )}
                              </div>
                              <div className="text-end">
                                <span className="me-2">
                                  <Switch
                                    checked={
                                      question?.testcase?.state ===
                                      "testcase.status.active"
                                    }
                                    onChange={() =>
                                      changeTestCaseState(
                                        question.id,
                                        question.testcase?.state
                                      )
                                    }
                                    checkedChildren="ACTIVE"
                                    unCheckedChildren="INACTIVE"
                                  />
                                </span>
                                <button onClick={() => handleUpdate(question)} className="btn btn-outline-success rounded-0">
                                <i class="bi bi-pencil-square"></i>&nbsp;
                                  Edit Question
                                </button>
                              </div>
                            </div>

                            <div className="col-md-5 col-12 p-3 text-center">
                              <>
                                {questionFetched &&
                                questionAndDocument.length > 0 &&
                                questionAndDocument.find(
                                  (q) => q.key === question.id
                                )?.files.length > 0 ? (
                                  <Image.PreviewGroup>
                                    <Carousel
                                      infinite={false}
                                      arrows={true}
                                      prevArrow={<LeftOutlined />}
                                      nextArrow={<RightOutlined />}
                                    >
                                      {questionAndDocument
                                        .find((q) => q.key === question.id)
                                        ?.files.map((item) => (
                                          <div key={item.id}>
                                            <h3 className="spec-carousel-background">
                                              <Image
                                                width={200}
                                                src={item.url}
                                                preview
                                              />
                                            </h3>
                                          </div>
                                        ))}
                                    </Carousel>
                                  </Image.PreviewGroup>
                                ) : (
                                  <div>
                                    <span>
                                      <FileImageOutlined
                                        style={{
                                          fontSize: 30,
                                          display: "block",
                                        }}
                                      />
                                      No Image Available
                                    </span>
                                  </div>
                                )}
                                {!questionFetched && (
                                  <div>
                                    <LoadingOutlined
                                      style={{ fontSize: 30 }}
                                      spin
                                    />
                                  </div>
                                )}
                              </>
                            </div>


                          </div>
                        </div>
                      </div>
                    </TabPane>
                  ))}
                </Tabs>
              </div>
            )}
          </div>
        ) : (
          <Empty
            imageStyle={{
              height: 200, // Adjust the height of the image
            }}
            description={<span>No Data</span>} // Custom description message
          />
        )}

        <div>
          <SpecQuestionsUpsertModal
            isModalOpen={isModalOpen}
            setIsModalOpen={setIsModalOpen}
            specificationId={specificationId}
            fetchData={fetchData}
          />
        </div>
      </div>
    </div>
  );
}
