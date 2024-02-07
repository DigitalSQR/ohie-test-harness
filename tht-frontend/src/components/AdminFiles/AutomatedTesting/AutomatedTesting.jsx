import React, { useEffect, useState } from "react";
import { notification } from "antd";
import "./automatedtesting.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import AutomatedResultStateRefresher from "./AutomatedResultStateRefresher/AutomatedResultStateRefresher";
import { useLoader } from "../../loader/LoaderContext";
import { useNavigate, useParams } from "react-router-dom";
import passImg from "../../../styles/images/success.svg";
import failImg from "../../../styles/images/failure.svg";
import skipImg from "../../../styles/images/skip.svg";
import stopImg from "../../../styles/images/stop.svg";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
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
    const getResultDisplay = (state, success) => {
        if (state == "testcase.result.status.finished") {
            if (!!success) {
                return <img className="finished" src={passImg} alt="PASS" />;
            } else {
                return <img className="finished" src={failImg} alt="FAIL" />;
            }
        } else if (state == "testcase.result.status.pending") {
            return <img className="finished" src={stopImg} alt="PENDING" />;
        } else if (state == "testcase.result.status.skip") {
            return <img className="finished" src={skipImg} alt="SKIP" />;
        } else if (state == "testcase.result.status.inprogress") {
            return <div class="spinner-border" role="status"></div>;
        }
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
                item = await TestResultAPI.getTestcaseResultStatus(item.id, { automated: true });
                if (item.refObjUri.split(".").pop() === "ComponentInfo") {
                    grouped.push({
                        ...item,
                        specifications: [],
                    });
                } else if (item.refObjUri.split(".").pop() === "SpecificationInfo") {
                    grouped[grouped.length - 1].specifications.push({ ...item, testCases: [] });
                } else if (item.refObjUri.split(".").pop() === "TestcaseInfo") {
                    grouped[grouped.length - 1].specifications[grouped[grouped.length - 1].specifications.length - 1].testCases.push(item);
                }
            }
            setData(grouped);
            hideLoader();
        } catch (error) {
            console.log(error);
        }
    };
    const testCaseInfo = () => {
        TestRequestAPI.getTestRequestsById(testRequestId)
            .then((res) => {
                console.log("testrequestinfo", res);
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
                    <div class="bcca-breadcrumb">
                        <div class="bcca-breadcrumb-item">
                            Automated Testing
                        </div>
                        <div
                            class="bcca-breadcrumb-item"
                            onClick={() => {
                                console.log("helllo")
                                navigate(
                                    `/dashboard/choose-test/${testRequestId}`
                                );
                            }}
                        >
                            {testcaseName}
                        </div>
                        <div
                            class="bcca-breadcrumb-item"
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
                                </tr>
                            </thead>
                            <tbody>
                                {!!data && data.map((component) => [
                                    // Component row
                                    <tr
                                        key={`component-${component.id}`}
                                        className="component-row"
                                    >
                                        <td>{component?.name}</td>
                                        <td></td>
                                        <td></td>
                                        <td>
                                            <AutomatedResultStateRefresher key={`component-result-${component?.id}`} testResultId={component.id} />
                                        </td>
                                        <td>
                                            <AutomatedResultStateRefresher key={`component-result-${component?.id}`} testResultId={component.id} isDuration={true} />
                                        </td>
                                    </tr>,
                                    component?.specifications?.map((specification) => [
                                        <tr
                                            key={`specification-${specification?.id}`}
                                            className="specification-row"
                                        >
                                            <td></td>
                                            <td>
                                                {specification.name}
                                            </td>
                                            <td></td>
                                            <td>
                                                <AutomatedResultStateRefresher key={`specification-result-${specification?.id}`} testResultId={specification.id} />
                                            </td>
                                            <td>
                                                <AutomatedResultStateRefresher key={`specification-result-${specification?.id}`} testResultId={specification.id} isDuration={true} />
                                            </td>
                                        </tr>,

                                        specification.testCases?.map((testcase) => [
                                            <tr
                                                key={`testcase-${testcase?.id}`}
                                                className="specification-row"
                                            >
                                                <td></td>
                                                <td></td>
                                                <td>{testcase?.name}</td>
                                                <td>
                                                    <AutomatedResultStateRefresher key={`testcase-result-${testcase?.id}`} testResultId={testcase.id} />
                                                </td>
                                                <td>
                                                    <AutomatedResultStateRefresher key={`testcase-result-${testcase?.id}`} testResultId={testcase.id} isDuration={true} />
                                                </td>
                                            </tr>,
                                        ]),
                                    ]),
                                ])}
                            </tbody>
                        </table>
                    </div>
                    {/* <div className="d-flex justify-content-center">
                        <button
                            onClick={clickHandler}
                            className="btn btn-primary btn-blue py-2 font-size-14"
                        >
                            Generate report
                        </button>
                    </div> */}
                </div>
            </div>
        </div>
    );
}