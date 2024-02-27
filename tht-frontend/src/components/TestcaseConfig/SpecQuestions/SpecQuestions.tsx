  import React, { useEffect, useState } from "react";
  import { useLocation, useNavigate, useParams } from "react-router-dom";
  import { TestCaseAPI } from "../../../api/TestCaseAPI";
  import { useLoader } from "../../loader/LoaderContext";
  import { EditOutlined, LeftOutlined, RightOutlined, LoadingOutlined, FileImageOutlined } from "@ant-design/icons";
  import { TestCaseOptionsAPI } from "../../../api/TestCaseOptionsAPI";
  import "./SpecQuestions.scss";
  import { Question } from "../../../dto/SpecQuestionDTO";
  import { Switch, Tabs, Carousel, notification, Image } from "antd";
  import { useDispatch } from "react-redux";
  import { set_header } from "../../../reducers/homeReducer";
  import { ManualQuestionTypeConstants } from "../../../constants/testcase_constants";
  import TabPane from "antd/es/tabs/TabPane";
  import { DocumentAPI } from "../../../api/DocumentAPI";
  import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
  import {DOCUMENT_STATE_ACTIVE,DOCUMENT_STATE_INACTIVE} from "../../../constants/document_constants";
import { display } from "html2canvas/dist/types/css/property-descriptors/display";

  const ManualTestCases: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const dispatch = useDispatch();
    const { componentId, specificationId, name } = location.state;
    const { showLoader, hideLoader } = useLoader();
    const [questions, setQuestions] = useState<Question[]>();
    const [activeKey, setActiveKey] = useState('1');
    const [questionAndDocument, setQuestionAndDocument] = useState([]);
    const [questionFetched, setQuestionFetched] = useState(true);


    useEffect(() => {
      fetchData();
      dispatch(set_header("Manual Configuration"));
    }, []);

    const handleUpdate = (question: Question, name: string) => {
      console.log(question);
      navigate(`/edit-question/${question.id}`, {
        state: {
          testcase: question,
          name,
          componentId,
        },
      });
    };

    const fetchData = async () => {
      try {
        showLoader();
        const resp = await TestCaseAPI.getTestCasesBySpecificationId(
          specificationId
        );
        console.log(resp);
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
        console.log(sortedQuestions);
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

    const handleClick = (path: string, state: object) => {
      navigate(path, { state });
    };

    const fetchTestCaseOptions = async (testcaseId: string) => {
      const optionsResp = await TestCaseOptionsAPI.getTestCaseOptionsByTestcaseId(
        testcaseId
      );
      return optionsResp.content;
    };

    const handleTabChange = (key) => {
      setActiveKey(key);
    };


    useEffect(() => {
      let key;
      if(activeKey){
        key = activeKey - 1;
      }
      if(questions && (questionAndDocument.filter((questionItem) => questionItem.key === questions[key].id) <= 0)){
        setQuestionFetched(false);
        let question = questions[key]

        DocumentAPI.getDocumentsByRefObjUriAndRefId(
          RefObjUriConstants.TESTCASE_REFOBJURI,
          question.id,
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
          .finally(()=> {
            setQuestionFetched(true);
          });
      }
    }, [activeKey,questions]); // Run this effect whenever activeKey changes


    const handleToggleChange = (testcaseId: string, state:string) => {
      showLoader();
      const newState =
        state === "testcase.status.active"
          ? "testcase.status.inactive"
          : "testcase.status.active";

        TestCaseAPI.changeState(testcaseId, newState)
        .then((res) => {
          hideLoader();

          setQuestions(prevQuestions => {
            const updatedQuestions = prevQuestions?.map((question) =>
              question.id === testcaseId ? { ...question, testcase: res.data } : question
            );
            return updatedQuestions;
          });
    
          console.log(res);
        })
        .catch((error) => {
          throw error;
        });
    };

    return (
      <div id="wrapper">
        <div className="bcca-breadcrumb">
          <div className="bcca-breadcrumb-item">
            {specificationId} - Manual Configuration
          </div>
          <div
            className="bcca-breadcrumb-item"
            onClick={() =>
              handleClick(`/component-specification/${componentId}`, {
                name,
                componentId,
              })
            }
          >
            {" "}
            {name}{" "}
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
        <div className="">
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
                  <div className="col-11 non-fuctional-requirement mt-3">
                    <div className="container-fluid">
                      <div className="row heading">
                        <div className="col-md-7 col-12 p-0">
                          <h2>Question</h2>
                        </div>

                        <div className="col-md-3 col-12 d-md-flex d-none p-0">
                          <h2 className="border-left">Reference</h2>
                        </div>

                        <div className="col-md-2 col-12 d-md-flex d-none p-0">
                          <h2 className="border-left">Action</h2>
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
                              question.options.map((option, optionIndex) => (
                                <div
                                  className="field-box option-item"
                                  key={optionIndex}
                                >
                                  {/* <input
                                  type="checkbox"
                                  id={option.id}
                                  name={option.id}
                                  value={option.id}
                                  checked={option.success}
                                  disabled
                                  readonly={true}
                                  autoComplete="off"
                                  /> */}
                                  {option.success ? <i className="bi bi-check-circle-fill me-2 text-success"></i> : <i className="bi bi-x-circle-fill me-2 text-danger"></i>}
                                  <label className={question.questionType === ManualQuestionTypeConstants.MULTI_SELECT ? "label-before-no-radius" : ""}>{option.name}</label>
                                </div>
                              ))}
                          </div>
                        </div>

                        {/* <div className="col-md-3 col-12 p-3">
                          <>
                           {questionFetched && questionAndDocument.length > 0
                           && (questionAndDocument.find((q) => q.key === question.id)?.files.length > 0)
                           && <Carousel arrows={true} prevArrow={<LeftOutlined />} nextArrow={<RightOutlined />}>
                            {(
                                questionAndDocument.find((q) => q.key === question.id)?.files.map((item) => (
                                    <div key={item.id}>
                                        <h3 className="spec-carousel-background">
                                            <Image width={200}
                                            src={item.url} />
                                        </h3>
                                    </div>
                                ))
                            )}
                            </Carousel>}
                            {questionFetched && (!questionAndDocument.find((q) => q.key === question.id)?.files) &&
                              <div><FileImageOutlined style={{ fontSize: 30 }}/></div>
                            }
                            {!questionFetched && <div><LoadingOutlined style={{ fontSize: 30 }} spin /></div>}
                          </>
                        </div> */}

                            <div className="col-md-3 col-12 p-3 text-center">
                              <>
                                {questionFetched && questionAndDocument.length > 0 && (
                                  questionAndDocument.find((q) => q.key === question.id)?.files.length > 0 ?

                                  (
                                    <Carousel arrows={true} prevArrow={<LeftOutlined />} nextArrow={<RightOutlined />}>
                                      {questionAndDocument.find((q) => q.key === question.id)?.files.map((item) => (
                                        <div key={item.id}>
                                          <h3 className="spec-carousel-background">
                                            <Image width={200} src={item.url} />
                                          </h3>
                                        </div>
                                      ))}
                                    </Carousel>
                                  ) : (
                                    <div>
                                      <span>
                                        <FileImageOutlined style={{ fontSize: 30, display: "block" }}/>
                                        No Image Available
                                        </span>
                                    </div>
                                  )
                                )}
                                {!questionFetched && <div><LoadingOutlined style={{ fontSize: 30 }} spin /></div>}
                              </>
                            </div>


                        <div className="col-md-2 col-12 p-0">
                          <div className="question-actions p-2 pt-5">
                            <span className="edit-icon">
                              <EditOutlined
                                onClick={() => handleUpdate(question, name)}
                              />
                            </span>
                            <Switch
                              defaultChecked={question?.testcase?.state === "testcase.status.active"}
                              onChange={(checked) =>
                                handleToggleChange(question.id, question.testcase?.state)
                              }
                              checkedChildren="ACTIVE"
                              unCheckedChildren="INACTIVE"
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </TabPane>
              ))}
            </Tabs>
          </div>
        </div>
      </div>
    );
  };

  export default ManualTestCases;
