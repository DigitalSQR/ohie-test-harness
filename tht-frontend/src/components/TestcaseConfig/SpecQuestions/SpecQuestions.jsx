import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { TestCaseAPI } from "../../../api/TestCaseAPI";
import { useLoader } from "../../loader/LoaderContext";
import { EditOutlined } from "@ant-design/icons";
import { TestCaseOptionsAPI } from "../../../api/TestCaseOptionsAPI";
import "./SpecQuestions.scss";
const ManualTestCases = () => {
  const { specificationId } = useParams();
  const { showLoader, hideLoader } = useLoader();
  const [questions, setQuestions] = useState();
  useEffect(() => {
    fetchData();
  }, []);
  const fetchData = async () => {
    try {
      showLoader();
      const resp = await TestCaseAPI.getTestCasesBySpecificationId(
        specificationId
      );
      const questionPromises = resp.content.map(async (testcase) => {
        try {
          const optionsResp = await fetchTestCaseOptions(testcase.id);
          const options = optionsResp
            .map((option) => {
              return { name: option.name, rank: option.rank };
            })
            .sort((a, b) => a.rank - b.rank);
          return {
            question: testcase.name,
            rank: testcase.rank,
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
  const fetchTestCaseOptions = async (testcaseId) => {
    const optionsResp = await TestCaseOptionsAPI.getTestCaseOptionsByTestcaseId(
      testcaseId
    );
    return optionsResp.content;
  };
  return (
    <div id="wrapper">
      <h1 className="specification-id">{`${specificationId} - Manual Configuration`}</h1>
      {questions?.map((question, index) => (
        <div key={index} className="question-container">
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
              <EditOutlined />
            </span>
            <span className="badges-green-dark">ACTIVE</span>
          </div>
        </div>
      ))}
    </div>
  );
};

export default ManualTestCases;
