import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Button, Select, notification } from "antd";
import "./EditQuestion.scss";
import { TestCaseOptionsAPI } from "../../../api/TestCaseOptionsAPI";
import { useLoader } from "../../loader/LoaderContext";
import { TestCaseAPI } from "../../../api/TestCaseAPI";

const { Option } = Select;

const EditQuestion = () => {
  const location = useLocation();
  const navigate = useNavigate();
  let { testcase, name, componentId } = location.state;
  const { showLoader, hideLoader } = useLoader();
  const [editedQuestion, setEditedQuestion] = useState(testcase.testcase.name);
  const [editedOptions, setEditedOptions] = useState(
    testcase.options.map((option) => ({
      label: option.name,
      metaVersion: option.meta.version,
      checked: false,
      status: "Active",
      changesMade: false,
    }))
  );

  const [changesMade, setChangesMade] = useState(false);

  const handleQuestionChange = (newValue) => {
    setEditedQuestion(newValue);

    // Enable/disable Save button based on the questionChanged state
    setChangesMade(newValue !== testcase.testcase.name);
  };

  const handleOptionChange = (index, newValue) => {
    const updatedOptions = [...editedOptions];
    updatedOptions[index].label = newValue;
    updatedOptions[index].changesMade =
      updatedOptions[index].label !== testcase.options[index].name;

    setEditedOptions(updatedOptions);
  };

  const handleStatusChange = (index, newStatus) => {
    const updatedOptions = [...editedOptions];
    updatedOptions[index].status = newStatus;
    setEditedOptions(updatedOptions);
  };

  const handleRadioChange = (index) => {
    const updatedOptions = [...editedOptions];
    updatedOptions[index].checked = !editedOptions[index].checked;
    setEditedOptions(updatedOptions);
  };

  const handleSaveQuestion = async () => {
    try {
      showLoader();

      const body = {
        ...testcase.testcase,
        meta: {
          version: testcase.testcase.meta?.version,
        },
        name: editedQuestion,
      };
      await TestCaseAPI.editTestCaseName(body);

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

      await TestCaseOptionsAPI.editTestCaseOptions(body);

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

      setEditedQuestion(resp.name);
      setChangesMade(false);
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

  const handleClick = (path, state) => {
    navigate(path, { state });
  };

  const handleSave = (path, state) => {
    navigate(path, { state });
  };

  return (
    <div id="wrapper">
      <div class="bcca-breadcrumb">
        <div class="bcca-breadcrumb-item">Question</div>
        <div class="bcca-breadcrumb-item" onClick={() => handleSave(`/dashboard/manual-testcases/${testcase.testcase.id}`, {
            name,
            componentId,
            specificationId: testcase.testcase.specificationId})}>
          {testcase.testcase.specificationId} - Manual Configuration
        </div>
        <div class="bcca-breadcrumb-item" onClick={() => handleClick(`/dashboard/component-specification/${componentId}`, {
              name,
              componentId})}>
          {" "}
          {name}{" "}
        </div>
        <div class="bcca-breadcrumb-item" onClick={() => {navigate("/dashboard/testcase-config")}}>
          Components
        </div>
      </div>
      <div className="col-12 my-4">
        <div className="row mb-2 justify-content-between">
          <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
            <b style={{ fontSize: "1.3em" }} className="bolder-text my-3">
              {`Edit: ${testcase.description.replace("Question for the ", "")}`}
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
              disabled={!changesMade}
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
