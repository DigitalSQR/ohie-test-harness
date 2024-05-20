import React, { Fragment, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { TestCaseAPI } from "../../../../api/TestCaseAPI";
import { SpecificationAPI } from "../../../../api/SpecificationAPI";
import { ComponentAPI } from "../../../../api/ComponentAPI";
import { useLoader } from "../../../loader/LoaderContext";
import { Breadcrumb, Empty } from "antd";
import sortedUp from "../../../../styles/images/sort-up.png";
import sortedDown from "../../../../styles/images/sort-down.png";
import unsorted from "../../../../styles/images/unsorted.png";

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
import { TestResultAPI } from "../../../../api/TestResultAPI";
import SpecAutomatedUpsertModal from "./SpecAutomatedUpsertModal/SpecAutomatedUpsertModal";
import { fileTypeIcon } from "../../../../utils/utils";
import { Pagination } from "@mui/material";

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
  const [componentDetails, setComponentDetails] = useState();
  const [specificationDetails, setSpecificationDetails] = useState();
  const [activeTab, setActiveTab] = useState("1");
  const [isAutomatedModalOpen, setIsAutomatedModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [currentAutomatedTestcase, setCurrentAutomatedTestcase] = useState();
  const [automatedTestcases, setAutomatedTestCases] = useState();
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);


  const obj = {
    name: "desc",
    rank: "asc",
  };
  const [sortFieldName, setSortFieldName] = useState("rank");
  const [sortDirection, setSortDirection] = useState(obj);

  const renderSortIcon = (fieldName) => {
    if (sortFieldName === fieldName) {
      return (
        <img
          className="cursor-pointer"
          style={{ width: "8px" }}
          src={sortDirection[fieldName] === "asc" ? sortedUp : sortedDown}
        ></img>
      );
    }
    return (
      <img
        className="cursor-pointer"
        style={{ width: "10px" }}
        src={unsorted}
      />
    );
  };
  const handleSort = (sortFieldName) => {
    setSortFieldName(sortFieldName);
    const newSortDirection = { ...obj };
    newSortDirection[sortFieldName] =
      sortDirection[sortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    fetchAutomatedTestCases(sortFieldName, newSortDirection,currentPage);
  };
  const items = [
    {
      key: "1",
      label: "Manual",
    },
    {
      key: "2",
      label: "Automated",
    },
  ];
  const onChange = (key) => {
    setActiveTab(key);
    if (key === "1") {
      fetchData(key === "1");
    }
  };

  const fetchAutomatedTestCases = (newFieldName, newsortDirection,page) => {
    const params = {
      specificationId: specificationId,
      manual: false,
      testcaseRunEnvironment: "testcase.run.environment.eu.testbed",
      sort: `${newFieldName ? newFieldName : sortFieldName},${
        newsortDirection
          ? newsortDirection[newFieldName]
          : sortDirection[sortFieldName]
      }`,
      size : 5
    };
    if(totalPages > 1){
      params.page = page ? page - 1 : currentPage - 1
    }
    TestCaseAPI.getTestCasesBySpecificationId(params)
      .then((res) => {
        setAutomatedTestCases(res.content);
        setTotalPages(res.totalPages);

      })
      .catch(() => {});
  };

  const fetchUploadedZipFiles = () => {
    setUploadedFiles([]);
    if (automatedTestcases) {
      automatedTestcases.forEach((question, index) => {
        DocumentAPI.getDocumentsByRefObjUriAndRefId(
          RefObjUriConstants.TESTCASE_REFOBJURI,
          question.id,
          DOCUMENT_STATE_ACTIVE
        ).then((res) => {
          setUploadedFiles((prev) => {
            if (res.content.length !== 0) {
              return [...prev, res.content[0]];
            }
          });
        });
      });
    }
  };

  useEffect(() => {
    if (!!automatedTestcases) {
      fetchUploadedZipFiles();
    }
  }, [automatedTestcases]);

  useEffect(() => {
    fetchData(true);
    fetchCompSpecDetails();
    dispatch(set_header("Testcase Configuration"));
    fetchAutomatedTestCases();
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

  const fetchData = async (isManual = true) => {
    try {
      showLoader();
      const params = {};
      params.specificationId = specificationId;
      params.manual = isManual;
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

  const handleTabChange = (key) => {
    setActiveKey(key);
  };

  useEffect(() => {
    if(activeTab === '1'){
      if(activeKey === "1"){
        let key;
        if (activeKey) {
          key = activeKey - 1;
        }
        if (
          !!questions &&
          questions.length > 0 &&
          questionAndDocument.filter(
            (questionItem) => questionItem.key === questions[key].id
          ) <= 0
        ) {
          setQuestionFetched(false);
          let question = questions[key];
          if (!!question) {
            DocumentAPI.getDocumentsByRefObjUriAndRefId(
              RefObjUriConstants.TESTCASE_REFOBJURI,
              question.id,
              DOCUMENT_STATE_ACTIVE
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
                      console.log('cuiesgcbivkebrygvsinehb')
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
              .catch((err) => {})
              .finally(() => {
                setQuestionFetched(true);
              });
          }
        }
        }
    } 
  }, [activeKey, questions]); // Run this effect whenever activeKey changes

  const changeTestCaseState = (testcaseId, state, isAutomated = false) => {
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
          handleStateChange(newState, isAutomated);
        },
      });
    };

    const handleStateChange = (newState) => {
      TestCaseAPI.changeState(testcaseId, newState)
        .then((res) => {
          hideLoader();
          if (isAutomated) {
            fetchAutomatedTestCases();
          } else {
            setQuestions((prevQuestions) => {
              const updatedQuestions = prevQuestions?.map((question) =>
                question.id === testcaseId
                  ? { ...question, testcase: res.data }
                  : question
              );
              return updatedQuestions;
            });
          }

          fetchData(!isAutomated);
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

  const editHandler = (testcase) => {
    setCurrentAutomatedTestcase(testcase);
    setIsEditMode(true);
    setIsAutomatedModalOpen(true);
  };

  const downloadFile = (zipFile) => {
    DocumentAPI.downloadDocument(zipFile.id, zipFile.name).catch((err) => {});
  };

  useEffect(() => {
    console.log(uploadedFiles);
  }, [uploadedFiles]);

  const handleChangePage = (event,newPage) => {
    console.log(newPage);
    setCurrentPage(newPage);
    fetchAutomatedTestCases(sortFieldName,sortDirection,newPage)
  };
  return (
    <div id="SpecQuestions">
      <div id="wrapper">
        <Breadcrumb className="custom-breadcrumb">
          <Breadcrumb.Item>
            <Link to="/testcase-config" className="breadcrumb-item" id="specQuestions-navToTestCaseConfig">
              Components
            </Link>
          </Breadcrumb.Item>
          <Breadcrumb.Item>
            <Link
              to={`/testcase-config/component-specification/${componentDetails?.id}`}
              className="breadcrumb-item"
              id={`specQuestions-navToComponent-${componentDetails?.name}`}
            >
              {componentDetails?.name}
            </Link>
          </Breadcrumb.Item>
          <Breadcrumb.Item className="breadcrumb-item">
            Testcase Configuration
          </Breadcrumb.Item>
        </Breadcrumb>

        <div className="d-flex justify-content-between align-items-center">
          <Tabs
            className="mt-3"
            activeKey={activeTab}
            items={items}
            onChange={onChange}
          />

          {activeTab === "1" && (
            <div>
              <button
                type="button"
                id="specQuestions-createManualTestcase"
                className="btn btn-sm btn-outline-secondary menu-like-item"
                onClick={() => setIsModalOpen(true)}
              >
                <i className="bi bi-plus"></i>
                Create Manual Testcase
              </button>
            </div>
          )}
          {activeTab === "2" && (
            <div>
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary menu-like-item"
                id="specQuestions-createAutomatedTestcase"
                onClick={() => {
                  setIsAutomatedModalOpen(true);
                  setCurrentAutomatedTestcase();
                }}
              >
                <i className="bi bi-plus"></i>
                Create Automated Testcase
              </button>
            </div>
          )}
        </div>

        {activeTab === "1" && (
          <div className="">
            {!questions || questions?.length === 0 ? (
              <Empty
                imageStyle={{
                  height: 200, // Adjust the height of the image
                }}
                description="Manual tests haven't been set up for this specification yet."
              />
            ) : (
              <div className="row">
                <div className="col-12 col-md-8 offset-md-2"></div>
                <Tabs
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
                            <div className="col-md-9 col-12 p-0">
                              <h2>Question</h2>
                            </div>

                            <div className="col-md-3 col-12 d-md-flex d-none p-0">
                              <h2 className="border-left">Reference Images</h2>
                            </div>
                          </div>

                          <div className="row question-box">
                            <div
                              className="col-md-9 col-12 p-0 question"
                              style={{
                                display: "flex",
                                flexDirection: "column",
                              }}
                            >
                              <h2>
                                <b>
                                  {index+1}. {question.question}
                                </b>
                              </h2>

                              <div
                                className="custom-multiselect field-checkbox"
                                style={{ flex: 1 }}
                              >
                                {question.options &&
                                  question.options.map(
                                    (option, optionIndex) => (
                                      <div
                                        className="field-box option-item-spec-question"
                                        key={optionIndex}
                                      >
                                        <label
                                          className={
                                            question.questionType ===
                                            ManualQuestionTypeConstants.MULTI_SELECT
                                              ? "label-before-no-radius"
                                              : ""
                                          }
                                        >
                                          {option.success ? (
                                            <i className="bi bi-check-circle-fill me-2 text-success"></i>
                                          ) : (
                                            <i className="bi bi-x-circle-fill me-2 text-danger"></i>
                                          )}
                                          {option.name}
                                        </label>
                                      </div>
                                    )
                                  )}
                                {question.options &&
                                  question.options.length <= 0 && (
                                    <Empty
                                      description="Currently, there are no available options. Please go to the editing question to create some."
                                      image={Empty.PRESENTED_IMAGE_SIMPLE}
                                    />
                                  )}
                              </div>
                              <div
                                className="text-end"
                                style={{ alignSelf: "flex-end" }}
                              >
                                <span className="me-2">
                                  <Switch
                                  id={`specQuestions-manualTestcaseState-toggleButton-${index}`}
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

                                <button
                                id={`specQuestions-editQuestion-${index}`}
                                  onClick={() => handleUpdate(question)}
                                  type="button"
                                  className="cursor-pointer btn btn-outline-success rounded-0"
                                >
                                  <i className="bi bi-pencil-square"></i>&nbsp;
                                  Edit Question
                                </button>
                              </div>
                            </div>

                            <div className="col-md-3 col-12 p-2 text-center">
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
                                  <div
                                    style={{
                                      display: "flex",
                                      justifyContent: "center",
                                      alignItems: "center",
                                      height: "100%",
                                    }}
                                  >
                                    <div style={{ textAlign: "center" }}>
                                      <i
                                        style={{ fontSize: 30 }}
                                        className="bi bi-card-image"
                                      ></i>
                                      <div>No Image Available</div>
                                    </div>
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
        )}

        {activeTab === "2" && (
          <div className="table-responsive">
 {automatedTestcases.length !== 0 ?

            <table className=" data-table capitalize-words">
              <thead>
                <tr>
                  <th style={{ width: "35%" }}>
                    TestCase
                    <span
                    id="specQuestions-sortAutomatedByName"
                      className="ps-1"
                      onClick={() => {
                        handleSort("name");
                      }}
                    >
                      {renderSortIcon("name")}
                    </span>
                  </th>
                  <th style={{ width: "10%" }}>
                    Rank
                    <span
                      className="ps-1"
                      id="specQuestions-sortAutomatedByRank"
                      onClick={() => {
                        handleSort("rank");
                      }}
                    >
                      {renderSortIcon("rank")}
                    </span>
                  </th>
                  <th style={{ width: "30%" }}>Zip File</th>
                  <th style={{ width: "25%" }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                  {automatedTestcases.map((testcase,index) => {
                    return (
                      <tr>
                        <td>{testcase.name}</td>
                        <td>{testcase.rank}</td>
                        {!!uploadedFiles ? (
                          uploadedFiles
                            ?.filter((files) => files.refId === testcase?.id)
                            .map((files) => (
                              <td>
                                <div
                                  className="doc-badge"
                                  type="button"
                                  key={files.id}
                                >
                                  <img src={fileTypeIcon(files.fileType)} />
                                  <span>{files.name}</span>
                                  <span
                                  id={`specQuestions-downloadFile-${index}`}
                                    type="button"
                                    title="Download File"
                                    className="mx-2 font-size-14"
                                    onClick={() => downloadFile(files)}
                                  >
                                    <i className="bi bi-cloud-download"></i>
                                  </span>
                                </div>
                              </td>
                            ))
                        ) : (
                          <td></td>
                        )}

                        <td>
                          <span
                            className="cursor-pointer text-blue  fw-bold"
                            id={`specQuestions-editAutomatedTestCase-${index}`}
                            onClick={() => {
                              editHandler(testcase);
                            }}
                          >
                            <i class="bi bi-pencil-square"></i>
                            EDIT{" "}
                          </span>
                          <span style={{ paddingLeft: "1rem" }}>
                            <Switch
                            id={`specQuestions-automatedTestcaseState-toggleButton-${index}`}
                              onChange={() =>
                                changeTestCaseState(
                                  testcase?.id,
                                  testcase?.state,
                                  true
                                )
                              }
                              checked={
                                testcase?.state === "testcase.status.active"
                              }
                              checkedChildren="ACTIVE"
                              unCheckedChildren="INACTIVE"
                            />
                          </span>
                        </td>
                      </tr>
                    );
                  })}
              </tbody>
            </table>
            :(<Empty description="There are no Automated Testcases for this specification"
            imageStyle={{
              height: 200, // Adjust the height of the image
            }}
            />
            )
}
{ totalPages > 1 && 
      <Pagination 
      className="pagination-ui" 
      count={totalPages}
      page={currentPage}
      variant="outlined"
      shape="rounded"
      onChange={handleChangePage}
      />
  }
          </div>
        )}

        <div>
          <SpecQuestionsUpsertModal
            isModalOpen={isModalOpen}
            setIsModalOpen={setIsModalOpen}
            specificationId={specificationId}
            fetchData={fetchData}
          />
        </div>
        <div>
          <SpecAutomatedUpsertModal
            isAutomatedModalOpen={isAutomatedModalOpen}
            setIsAutomatedModalOpen={setIsAutomatedModalOpen}
            specificationId={specificationId}
            isEditMode={isEditMode}
            setIsEditMode={setIsEditMode}
            currentAutomatedTestcase={currentAutomatedTestcase}
            fetchAutomatedTestCases={fetchAutomatedTestCases}
          ></SpecAutomatedUpsertModal>
        </div>
      </div>
      
    </div>
  );
}
