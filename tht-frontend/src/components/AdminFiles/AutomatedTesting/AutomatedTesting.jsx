import React, { useEffect, useState } from "react";
import { notification } from "antd";
import "./automatedtesting.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import AutomatedResultStateRefresher from "./AutomatedResultStateRefresher/AutomatedResultStateRefresher";
import { useLoader } from "../../loader/LoaderContext";
import { useNavigate, useParams } from "react-router-dom";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import AutomatedToggleButtonRefresher from "./AutomatedToggleButtonRefresher/AutomatedToggleButtonRefresher";
export default function AutomatedTesting() {
  const { testRequestId } = useParams();
  const [testcaseName, setTestCaseName] = useState();
  const { showLoader, hideLoader } = useLoader();
  const [data, setData] = useState([]);
  const navigate = useNavigate();
  const clickHandler = () => {
    notification.info({
      placement: "bottom-right",
      description: "No actions yet",
    });
  };
  const fetchTestCaseResultData = async () => {
    showLoader();
    try {
      const response = await TestResultAPI.getTestCaseResultById(
        testRequestId,
        null,
        true
      );
      const grouped = [];
      for (let item of response.content) {
        item = await TestResultAPI.getTestcaseResultStatus(item.id, {
          automated: true,
        });
        if (item.refObjUri.split(".").pop() === "ComponentInfo") {
          grouped.push({
            ...item,
            specifications: [],
          });
        } else if (item.refObjUri.split(".").pop() === "SpecificationInfo") {
          grouped[grouped.length - 1].specifications.push({
            ...item,
            testCases: [],
          });
        } else if (item.refObjUri.split(".").pop() === "TestcaseInfo") {
          grouped[grouped.length - 1].specifications[
            grouped[grouped.length - 1].specifications.length - 1
          ].testCases.push(item);
        }
      }
      setData(grouped);
      hideLoader();
    } catch (error) {
      console.log(error);
    }
  };

  const toggleTestCaseRow = (testcaseId) => {
    const newData = data.map((component) => ({
      ...component,
      specifications: component.specifications.map((specification) => ({
        ...specification,
        testCases: specification.testCases.map((testcase) => ({
          ...testcase,
          class:
            testcase.id === testcaseId
              ? testcase.class === "show"
                ? "hide"
                : "show"
              : testcase.class,
        })),
      })),
    }));

    setData(newData);
  };

  const toggleSpecificationRow = (specificationId) => {
    const newData = data.map((component) => ({
      ...component,
      specifications: component.specifications.map((specification) => ({
        ...specification,
        class:
          specification.id === specificationId
            ? specification.class === "show"
              ? "hide"
              : "show"
            : specification.class,
      })),
    }));
    setData(newData);
  };

  const toggleComponentRow = (componentId) => {
    const newData = data.map((component) => ({
      ...component,
      class:
        component.id === componentId
          ? component.class === "show"
            ? "hide"
            : "show"
          : component.class,
    }));
    setData(newData);
  };

  const testCaseInfo = () => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        setTestCaseName(res.name);
      })
      .catch(() => {
        notification.error({
          description: "Oops something went wrong!",
          placement: "bottomRight",
        });
      });
  };
  useEffect(() => {
    fetchTestCaseResultData();
    testCaseInfo();
  }, []);
  return (
    <div className="Workflow-testing-wrapper">
      <div className="container">
        <div className="col-12">
          <div className="bcca-breadcrumb">
            <div className="bcca-breadcrumb-item">Automated Testing</div>
            <div
              className="bcca-breadcrumb-item"
              onClick={() => {
                navigate(`/dashboard/choose-test/${testRequestId}`);
              }}
            >
              {testcaseName}
            </div>
            <div
              className="bcca-breadcrumb-item"
              onClick={() => {
                navigate(`/dashboard/applications`);
              }}
            >
              Applications
            </div>
          </div>
          <div className="table-responsive mb-5">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Component</th>
                  <th>Specification</th>
                  <th>Test Cases</th>
                  <th>Result</th>
                  <th>Duration</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {!!data &&
                  data.map((component) => [
                    // Component row
                    <>
                      <tr
                        key={`component-${component.id}`}
                        className="component-row"
                      >
                        <td>{component?.name}</td>
                        <td></td>
                        <td></td>
                        <td>
                          <AutomatedResultStateRefresher
                            key={`component-result-${component?.id}`}
                            testResultId={component.id}
                          />
                        </td>
                        <td>
                          <AutomatedResultStateRefresher
                            key={`component-result-${component?.id}`}
                            testResultId={component.id}
                            isDuration={true}
                          />
                        </td>
                        <td>
                          <AutomatedToggleButtonRefresher
                            testResultId={component?.id}
                            toggleClass={component?.class}
                            toggleFunction={toggleComponentRow}
                          />
                        </td>
                      </tr>
                      <tr>
                        <td
                          colSpan={6}
                          className="text-center hiddenRow m-0 field-box"
                        >
                          <AutomatedToggleButtonRefresher
                            testResultId={component?.id}
                            toggleClass={component?.class}
                            ErrorStatement={true}
                          />
                        </td>
                      </tr>
                    </>,
                    component?.specifications?.map((specification) => [
                      <>
                        <tr
                          key={`specification-${specification?.id}`}
                          className="specification-row"
                        >
                          <td></td>
                          <td>{specification.name}</td>
                          <td></td>
                          <td>
                            <AutomatedResultStateRefresher
                              key={`specification-result-${specification?.id}`}
                              testResultId={specification.id}
                            />
                          </td>
                          <td>
                            <AutomatedResultStateRefresher
                              key={`specification-result-${specification?.id}`}
                              testResultId={specification.id}
                              isDuration={true}
                            />
                          </td>
                          <td>
                            <AutomatedToggleButtonRefresher
                              testResultId={specification?.id}
                              toggleClass={specification?.class}
                              toggleFunction={toggleSpecificationRow}
                            />
                          </td>
                        </tr>
                        <tr>
                          <td
                            colSpan={6}
                            className="text-center hiddenRow m-0 field-box"
                          >
                            <AutomatedToggleButtonRefresher
                              testResultId={specification?.id}
                              toggleClass={specification?.class}
                              ErrorStatement={true}
                            />
                          </td>
                        </tr>
                      </>,

                      specification.testCases?.map((testcase) => [
                        <>
                          <tr
                            key={`testcase-${testcase?.id}`}
                            className="specification-row"
                          >
                            <td></td>
                            <td></td>
                            <td>{testcase?.name}</td>
                            <td>
                              <AutomatedResultStateRefresher
                                key={`testcase-result-${testcase?.id}`}
                                testResultId={testcase.id}
                              />
                            </td>
                            <td>
                              <AutomatedResultStateRefresher
                                key={`testcase-result-${testcase?.id}`}
                                testResultId={testcase.id}
                                isDuration={true}
                              />
                            </td>
                            <td>
                              <AutomatedToggleButtonRefresher
                                testResultId={testcase?.id}
                                toggleClass={testcase?.class}
                                toggleFunction={toggleTestCaseRow}
                              />
                            </td>
                          </tr>
                          <tr>
                            <td
                              colSpan={6}
                              className="text-center hiddenRow m-0 field-box"
                            >
                              <AutomatedToggleButtonRefresher
                                testResultId={testcase?.id}
                                toggleClass={testcase?.class}
                                ErrorStatement={true}
                              />
                            </td>
                          </tr>
                        </>,
                      ]),
                    ]),
                  ])}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
