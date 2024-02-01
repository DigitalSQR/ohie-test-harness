import React, { useEffect, useState } from "react";
import { notification } from "antd";
import "./workflow-testing.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import { useLoader } from "../../loader/LoaderContext";
import { useParams } from "react-router-dom";
import passImg from "../../../styles/images/success.svg";
import failImg from "../../../styles/images/failure.svg";
export default function WorkFlowTesting() {
  const { testRequestId } = useParams();
  const { showLoader, hideLoader } = useLoader();
  const [data, setData] = useState({});
  const clickHandler = () => {
    notification.info({
      placement: "bottom-right",
      description: "No actions yet",
    });
  };
  const getResultDisplay = (state, success) => {
    if (state == "testcase.result.status.finished") {
      if (success == true) {
        return <img className="finished" src={passImg} alt="PASS" />;
      } else {
        return <img className="finished" src={failImg} alt="FAIL" />;
      }
    } else if (state == "testcase.result.status.pending") {
      return <div class="spinner-border" role="status"></div>;
    } else if (state == "testcase.result.status.inprogress") {
      return <div class="spinner-border" role="status"></div>;
    }
  };
  const fetchTestCaseResultData = async () => {
    showLoader();
    try {
      const response = await TestResultAPI.getTestCaseResultById(
        testRequestId,
        false
      );
      const grouped = response.content.reduce((acc, item) => {
        if (item.refObjUri.split(".").pop() === "ComponentInfo") {
          if (!acc[item.id]) {
            acc[item.id] = {
              ...item,
              specifications: [],
            };
          }
        } else if (item.refObjUri.split(".").pop() === "SpecificationInfo") {
          const componentId = item.parentTestcaseResultId;
          if (!acc[componentId]) {
            acc[componentId] = {
              specifications: [],
            };
          }
          acc[componentId].specifications.push({
            [item.id]: { ...item, testCases: [] },
          });
        } else if (item.refObjUri.split(".").pop() === "TestcaseInfo") {
          const specificationId = item.parentTestcaseResultId;

          Object.entries(acc).forEach(([cmpId, value]) => {
            const specification = value.specifications.find(
              (spec) => Object.keys(spec)[0] === specificationId
            );
            if (specification) {
              specification[specificationId].testCases.push(item);
            }
          });
        }
        return acc;
      }, {});
      setData(grouped);
      hideLoader();
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    fetchTestCaseResultData();
  }, []);
  return (
    <div className="Workflow-testing-wrapper">
      <div className="container">
        <div className="col-12">
          <div className="table-responsive mb-5">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Component</th>
                  <th>Specification</th>
                  <th>Test Cases</th>
                  <th>Result</th>
                  <th>Duration</th>
                </tr>
              </thead>
              <tbody>
                {Object.entries(data)?.map(([componentId, component]) => [
                  // Component row
                  <tr
                    key={`component-${componentId}`}
                    className="component-row"
                  >
                    <td>{component?.name}</td>
                    <td></td>
                    <td></td>
                    <td>
                      {getResultDisplay(component?.state, component?.success)}
                    </td>
                    <td>{component?.duration} ms</td>
                  </tr>,
                  ...component?.specifications?.map((specification) => [
                    <tr
                      key={`specification-${specification?.id}`}
                      className="specification-row"
                    >
                      <td></td>
                      <td>
                        {specification[Object.keys(specification)[0]].name}
                      </td>
                      <td></td>
                      <td>
                        {getResultDisplay(
                          specification[Object.keys(specification)[0]].state,
                          specification[Object.keys(specification)[0]].success
                        )}
                      </td>
                      <td>{component?.duration} ms</td>
                    </tr>,

                    ...specification[
                      Object.keys(specification)[0]
                    ].testCases?.map((testcase) => [
                      <tr
                        key={`testcase-${testcase?.id}`}
                        className="specification-row"
                      >
                        <td></td>
                        <td></td>
                        <td>{testcase?.name}</td>
                        <td>
                          {getResultDisplay(testcase?.state, testcase?.success)}
                        </td>
                        <td>{component?.duration} ms</td>
                      </tr>,
                    ]),
                  ]),
                ])}
              </tbody>
            </table>
          </div>
          <div className="d-flex justify-content-center">
            <button
              onClick={clickHandler}
              className="btn btn-primary btn-blue py-2 font-size-14"
            >
              Generate report
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
