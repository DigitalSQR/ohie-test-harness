import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { Button, Select, notification } from "antd";
import "./EditQuestion.scss";
import { TestCaseOptionsAPI } from "../../../api/TestCaseOptionsAPI";
import { useLoader } from "../../loader/LoaderContext";
import { TestCaseAPI } from "../../../api/TestCaseAPI";

const { Option } = Select;

const EditQuestion = () => {
  const location = useLocation();
  let { testcase } = location.state;
  const { showLoader, hideLoader } = useLoader();
  const [loading, setLoading] = useState(false);
  const [editedQuestion, setEditedQuestion] = useState(testcase.question);
  const [editedOptions, setEditedOptions] = useState(
    testcase.options.map((option) => ({
      label: option.name,
      metaVersion: option.meta.version,
      checked: false,
      status: "Active",
      changesMade: false,
    }))
  );
  const [questionChanged, setQuestionChanged] = useState(false);

  const [changesMade, setChangesMade] = useState(false);

  const handleChange = () => {};

  const handleQuestionChange = (newValue) => {
    setEditedQuestion(newValue);

    // Check if the new value is different from the original question
    setQuestionChanged(newValue !== testcase.question);

    // Enable/disable Save button based on the questionChanged state
    setChangesMade(newValue !== testcase.question);
  };

  const handleOptionChange = (index, newValue) => {
    const updatedOptions = [...editedOptions];
    updatedOptions[index].label = newValue;
    updatedOptions[index].changesMade =
      updatedOptions[index].label !== testcase.options[index].name;

    setEditedOptions(updatedOptions);
    setChangesMade(true);
    handleChange();
  };

  const handleStatusChange = (index, newStatus) => {
    const updatedOptions = [...editedOptions];
    updatedOptions[index].status = newStatus;
    setEditedOptions(updatedOptions);
    setChangesMade(true);
  };

  const handleRadioChange = (index) => {
    const updatedOptions = [...editedOptions];
    updatedOptions[index].checked = !editedOptions[index].checked;
    setEditedOptions(updatedOptions);
    setChangesMade(true);
  };

  const handleSaveQuestion = async () => {
    try {
      setLoading(true);
      showLoader();

      const body = {
        ...testcase.testcase,
        meta: {
          version: testcase.testcase.meta?.version,
        },
        name: editedQuestion,
      };
      const resp = await TestCaseAPI.editTestCaseName(body);
      console.log(resp);

      notification.success({
        placement: "bottomRight",
        message: "Question saved successfully",
      });

      await fetchTestCaseQuestion();
    } catch (error) {
      console.error("Error saving question:", error);

      notification.error({
        placement: "bottomRight",
        message: "Failed to save question",
      });
    } finally {
      setLoading(false);
      hideLoader();
    }
  };

  const handleSaveOption = async (index) => {
    try {
      showLoader();

      const body = {
        ...testcase.options[index],
        meta: {
          version: editedOptions[index].metaVersion,
        },
        name: editedOptions[index].label,
        testcaseId: testcase.id,
      };

      const resp = await TestCaseOptionsAPI.editTestCaseOptions(body);
      console.log(resp);

      notification.success({
        placement: "bottomRight",
        message: "Option saved successfully",
      });

      await fetchTestCaseOptions();
    } catch (error) {
      console.error("Error saving option:", error);

      notification.error({
        placement: "bottomRight",
        message: "Failed to save option",
      });
    } finally {
      hideLoader();
    }
  };

  const fetchTestCaseOptions = async () => {
    try {
      showLoader();

      const testcaseId = testcase.id;

      const resp = await TestCaseOptionsAPI.getTestCaseOptionsByTestcaseId(
        testcaseId
      );

      const options = resp.content.sort((a, b) => a.rank - b.rank);

      testcase.options = options;

      setEditedOptions(
        options.map((option) => ({
          label: option.name,
          metaVersion: option.meta.version,
          checked: false,
          status: "Active",
          changesMade: false,
        }))
      );
    } catch (error) {
      console.error("Error loading options:", error);

      notification.error({
        placement: "bottomRight",
        message: "Failed to load options",
      });
    } finally {
      hideLoader();
    }
  };

  const fetchTestCaseQuestion = async () => {
    try {
      showLoader();

      const testcaseId = testcase.id;

      const resp = await TestCaseAPI.getTestCasesById(testcaseId);

      testcase.testcase = {
        ...testcase.testcase,
        ...resp,
      };

      console.log(testcase.testcase);
      setEditedQuestion(resp.name);
    } catch (error) {
      console.error("Error loading options:", error);

      notification.error({
        placement: "bottomRight",
        message: "Failed to load options",
      });
    } finally {
      hideLoader();
    }
  };

  return (
    <div id="wrapper">
      <div className="col-12">
        <div className="row mb-2 justify-content-between">
          <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
            <b style={{ fontSize: "1.5em" }} className="bolder-text">
              Edit: {testcase.description}
            </b>
          </div>
        </div>
        <div className="row mb-2 justify-content-between align-items-center my-5">
          <label className="col-auto">Question:</label>
          <textarea
            className="col mx-3 ml-5 form-control non-resizable"
            rows="4"
            value={editedQuestion}
            onChange={(e) => handleQuestionChange(e.target.value)}
          />
          <div className="col-md-2 text-center">
            <Button
              className="smaller-button"
              type="primary"
              onClick={handleSaveQuestion}
              disabled={!changesMade || loading}
            >
              Save
            </Button>
          </div>
        </div>
        <div className="my-5">
          {editedOptions.map((option, index) => (
            <div
              key={index}
              className="row mb-2 justify-content-between align-items-center"
            >
              <label className="col-md-1 align-items-center">{`Option ${String.fromCharCode(
                65 + index
              )}:`}</label>
              <div className="col-md-5 align-items-start">
                <input
                  className="form-control"
                  type="text"
                  value={option.label}
                  onChange={(e) => handleOptionChange(index, e.target.value)}
                />
              </div>
              <div className="col-md-2 text-center">
                <input
                  type="radio"
                  checked={option.checked || false}
                  onChange={() => handleRadioChange(index)}
                />
              </div>
              <div className="col-md-2 text-center">
                <Select
                  value={option.status}
                  onChange={(value) => handleStatusChange(index, value)}
                  dropdownStyle={{
                    backgroundColor:
                      option.status === "Active" ? "#4CAF50" : "#FF8A65",
                  }}
                >
                  <Option value="Active">Active</Option>
                  <Option value="Inactive">Inactive</Option>
                </Select>
              </div>
              <div className="col-md-2 text-center">
                <Button
                  className="smaller-button"
                  type="primary"
                  onClick={() => handleSaveOption(index)}
                  disabled={!option.changesMade}
                >
                  Save
                </Button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default EditQuestion;
