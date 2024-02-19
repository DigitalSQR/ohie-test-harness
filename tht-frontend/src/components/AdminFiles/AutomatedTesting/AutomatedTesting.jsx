import React, { useEffect, useState } from "react";
import { notification } from "antd";
import "./automatedtesting.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import { useLoader } from "../../loader/LoaderContext";
import { useNavigate, useParams } from "react-router-dom";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import WebSocketService from "../../../api/WebSocketService";
import TestcaseResultRow from "./TestcaseResultRow/TestcaseResultRow";
export default function AutomatedTesting() {
  const { testRequestId } = useParams();
  const [testcaseName, setTestCaseName] = useState();
  const [testcaseRequestResult, setTestcaseRequestResult] = useState();
  const { showLoader, hideLoader } = useLoader();
  const [data, setData] = useState([]);
  const navigate = useNavigate();
  const { stompClient, webSocketConnect, webSocketDisconnect } = WebSocketService();
  const clickHandler = () => {
    notification.info({
      placement: "bottom-right",
      description: "No actions yet",
    });
  };
  const fetchTestCaseResultDataAndStartWebSocket = async () => {
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
        } else {
          setTestcaseRequestResult(item);
          // Start the webSocket connection
          if (item?.state !== "testcase.result.status.finished") {
            webSocketConnect();
          }
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
    fetchTestCaseResultDataAndStartWebSocket();
    testCaseInfo();
  }, []);

  useEffect(() => {
    // Close the connection once the request is finished
    if (stompClient && stompClient.connected) {
      const destination = '/testcase-result/' + testcaseRequestResult.id;
      const subscription = stompClient.subscribe(destination, (msg) => {
        const parsedTestcaseResult = JSON.parse(msg.body);
        setTestcaseRequestResult(parsedTestcaseResult);
        if (parsedTestcaseResult?.state === "testcase.result.status.finished") {
          webSocketDisconnect();
        }
      });
    }
  }, [stompClient]);

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
                  <th style={{ width: "40%" }}>Test Cases</th>
                  <th>Result</th>
                  <th>Duration</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {!!data &&
                  data.map((component) => [
                    <TestcaseResultRow key={`component-result-${component?.id}`} testcaseResultType={'component'} testResultId={component.id} stompClient={stompClient} toggleFunction={toggleComponentRow}></TestcaseResultRow>
                    ,component?.specifications?.map((specification) => [
                      <TestcaseResultRow key={`specification-result-${specification?.id}`} testcaseResultType={'specification'} testResultId={specification.id} stompClient={stompClient} toggleClass={specification?.class} toggleFunction={toggleSpecificationRow}></TestcaseResultRow>
                      ,specification.testCases?.map((testcase) => [
                        <TestcaseResultRow key={`testcase-result-${testcase?.id}`} testcaseResultType={'testcase'} testResultId={testcase.id} stompClient={stompClient} toggleClass={testcase?.class} toggleFunction={toggleTestCaseRow}></TestcaseResultRow>
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
