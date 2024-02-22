  import React, { useEffect, useState } from "react";
  import { useLocation, useNavigate } from "react-router-dom";
  import { TestCaseAPI } from "../../../api/TestCaseAPI";
  import { useLoader } from "../../loader/LoaderContext";
  import { EditOutlined } from "@ant-design/icons";
  import { TestCaseOptionsAPI } from "../../../api/TestCaseOptionsAPI";
  import "./SpecQuestions.scss";
  import { Question } from "../../../dto/SpecQuestionDTO";
  import { Switch, Tabs } from "antd";
  import { useDispatch } from "react-redux";
  import { set_header } from "../../../reducers/homeReducer";
  import TabPane from "antd/es/tabs/TabPane";

  const ManualTestCases: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const dispatch = useDispatch();
    const { componentId, specificationId, name } = location.state;
    const { showLoader, hideLoader } = useLoader();
    const [questions, setQuestions] = useState<Question[]>();

    useEffect(() => {
      fetchData();
      dispatch(set_header("Manual Configuration"));
    }, []);

    const handleUpdate = (question: Question, name: string) => {
      console.log(question);
      navigate(`/dashboard/edit-question`, {
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
              handleClick(`/dashboard/component-specification/${componentId}`, {
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
              navigate("/dashboard/testcase-config");
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
                                  <label>{option.name}</label>
                                </div>
                              ))}
                          </div>
                        </div>

                        <div className="col-md-3 col-12 p-0">
                          <div className="p-2 pt-5 q-img">
                            <img
                              src="../../../styles/images/question-img.png"
                              alt="No Image Found"
                            />
                          </div>
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
