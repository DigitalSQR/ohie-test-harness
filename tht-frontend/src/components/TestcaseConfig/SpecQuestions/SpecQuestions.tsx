import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { TestCaseAPI } from "../../../api/TestCaseAPI";
import { useLoader } from "../../loader/LoaderContext";
import { EditOutlined } from "@ant-design/icons";
import { TestCaseOptionsAPI } from "../../../api/TestCaseOptionsAPI";
import "./SpecQuestions.scss";
import { SpecificationDTO } from "../../../dto/SpecificationDTO";
import { Question, Option } from "../../../dto/SpecQuestionDTO";

const ManualTestCases: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { componentId, specificationId, name } = location.state;
  const { showLoader, hideLoader } = useLoader();
  const [questions, setQuestions] = useState<Question[]>();

  useEffect(() => {
    fetchData();
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

  return (
    <div id="wrapper">
      <div className="bcca-breadcrumb">
        <div className="bcca-breadcrumb-item">{specificationId} - Manual Configuration</div>
        <div className="bcca-breadcrumb-item" onClick={() => handleClick(`/dashboard/component-specification/${componentId}`, {name, componentId})}> {name} </div>
        <div className="bcca-breadcrumb-item" onClick={()=>{navigate("/dashboard/testcase-config")}}>Components</div>
      </div>
      {questions?.map((question, index) => (
        <div key={index} className="question-container my-4">
          <div className="question-content">
            <h5 className="question">
              {question.rank}. {question.question}
            </h5>
            <ol className="options-list" type="a">
              {question.options.map((option, optionIndex) => (
                <li key={optionIndex} className="option">
                  {option.name}
                </li>
              ))}
            </ol>
          </div>
          <div className="question-actions">
            <span className="edit-icon">
              <EditOutlined onClick={() => handleUpdate(question, name)} />
            </span>
            <span className="badges-green-dark">ACTIVE</span>
          </div>
        </div>
      ))}
    </div>
  );
};

export default ManualTestCases;
