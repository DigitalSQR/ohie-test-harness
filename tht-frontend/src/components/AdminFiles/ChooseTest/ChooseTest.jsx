import workflow_logo from "../../../styles/images/workflow-testing.png";
import functional_logo from "../../../styles/images/functional-testing.png";
import { useNavigate, useParams } from "react-router-dom";
import "./choose-test.scss";
import { TestResultAPI } from "../../../api/TestResultAPI";
import { Fragment, useEffect, useState } from "react";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { notification, Progress, Button } from "antd";
import { TestcaseResultStateConstants } from "../../../constants/testcaseResult_constants";
import { handleErrorResponse } from "../../../utils/utils";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";
export default function ChooseTest() {
  const { testRequestId } = useParams();
  const { TESTCASE_REFOBJURI, TESTREQUEST_REFOBJURI } = RefObjUriConstants;
  const [manualEntries, setManualEntries] = useState([]);
  const [testcaseName, setTestCaseName] = useState();
  const [manualProgress, setManualProgress] = useState(0);
  const [automatedProgress, setAutomatedProgress] = useState(0);
  const [totalManualTestcaseResults, setTotalManualTestcaseResults] =
    useState(0);
  const [totalAutomatedTestcaseResults, setTotalAutomatedTestcaseResults] =
    useState(0);
  const [totalFinishedManual, setTotalFinishedManual] = useState(0);
  const [totalFinishedAutomated, setTotalFinishedAutomated] = useState(0);

  const [testcaseResults, setTestCaseResults] = useState([]);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    var totalManual = 0;
    var totalFinishedManual = 0;
    var totalAutomated = 0;
    var totalFinishedAutomated = 0;
    testcaseResults.forEach((testcaseResult) => {
      if (
        testcaseResult.state !==
        TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_DRAFT
      ) {
        if (!!testcaseResult.manual) {
          totalManual++;
          if (
            testcaseResult.state ===
              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_SKIP ||
            testcaseResult.state ===
              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
          ) {
            totalFinishedManual++;
          }
        }
        if (!!testcaseResult.automated) {
          totalAutomated++;
          if (
            testcaseResult.state ===
              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_SKIP ||
            testcaseResult.state ===
              TestcaseResultStateConstants.TESTCASE_RESULT_STATUS_FINISHED
          ) {
            totalFinishedAutomated++;
          }
        }
      }
    });
    setTotalFinishedManual(totalFinishedManual);
    setTotalFinishedAutomated(totalFinishedAutomated);
    setTotalManualTestcaseResults(totalManual);
    setTotalAutomatedTestcaseResults(totalAutomated);

    if (totalManual !== 0) {
      setManualProgress(Math.floor((totalFinishedManual / totalManual) * 100));
    }

    if (totalAutomated !== 0) {
      setAutomatedProgress(
        Math.floor((totalFinishedAutomated / totalAutomated) * 100)
      );
    }
  }, [testcaseResults]);

  const loadProgress = () => {
    const params = {
      testRequestId: testRequestId,
      refObjUri: TESTCASE_REFOBJURI,
    };
    TestResultAPI.fetchCasesForProgressBar(params)
      .then((res) => {
        setTestCaseResults(res.data.content);
      })
      .catch((error) => {
        throw error;
      });
  };

  const handleStartTesting = (manual, automated) => {
    const params = {
      testRequestId,
      refObjUri: TESTREQUEST_REFOBJURI,
      refId: testRequestId,
    };
    if (!!manual) {
      params.manual = true;
    }
    if (!!automated) {
      params.automated = true;
    }
    TestResultAPI.startTests(params)
      .then((response) => {
        notification.success({
          description: "Testing Process has been Started Successfully",
          placement: "bottomRight",
        });
        if (!!automated) {
          navigate(`/dashboard/automated-testing/${testRequestId}`);
        } else {
          navigate(`/dashboard/manual-testing/${testRequestId}`);
        }
        loadProgress();
      })
      .catch((error) => {
        console.log(error);
        notification.info({
          description: handleErrorResponse(error.response.data),
          placement: "bottomRight",
        });
      });
  };
  //finish 2 or skip 3 5 / 50 not draft
  //50 60

  const testCaseInfo = () => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        console.log("testrequestinfo", res);
        setTestCaseName(res.name);
        dispatch(set_header(res.name));
      })
      .catch(() => {
        notification.error({
          description: "Oops something went wrong!",
          placement: "bottomRight",
        });
      });
  };

  useEffect(() => {
    loadProgress();
    testCaseInfo();
  }, []);

  useEffect(() => {
    console.log("manualProgress updated to:", manualProgress);
  }, [manualProgress]);

  return (
    <div id="wrapper">
      <div className="col-12 pt-3">
        <div class="bcca-breadcrumb">
          <div class="bcca-breadcrumb-item">{testcaseName}</div>
          <div
            class="bcca-breadcrumb-item"
            onClick={() => {
              navigate(`/dashboard/applications`);
            }}
          >
            Applications
          </div>
        </div>
        <h5>Choose Testing Type</h5>
        <p className="text-gray">
          Select the type to start testing application with OpenHIE.{" "}
        </p>
        <div className="d-flex flex-wrap">
          <div className="testing-grid">
            <div className="icon-box">
              <img src={functional_logo} />
            </div>
            <div className="text-box">
              <h6 className="">Manual Testing</h6>
              <p className="mb-0">
                If you need more info, please check out{" "}
                <a className="text-blue" href="#">
                  Guideline.
                </a>
              </p>
              {totalManualTestcaseResults == 0 && (
                <button
                  className="btn btn-primary btn-sm mt-4 display"
                  onClick={() => {
                    handleStartTesting(true, null);
                  }}
                >
                  Start Testing
                </button>
              )}
              {totalManualTestcaseResults != 0 && (
                <Fragment>
                  <Progress
                    percent={Math.floor(manualProgress)}
                    format={() => {
                      if (manualProgress === 100) {
                        return <span>Done</span>;
                      } else {
                        return (
                          <span>
                            {totalFinishedManual}/{totalManualTestcaseResults}
                          </span>
                        );
                      }
                    }}
                  />
                  <Button
                    onClick={() =>
                      navigate(`/dashboard/manual-testing/${testRequestId}`)
                    }
                  >
                    Resume
                  </Button>
                </Fragment>
              )}

              {/* <div className="progress-bar-line"> */}
              {/* <div className="progress-fill"></div> */}
              {/* <div className="progress-value">20%</div>  */}
              {/* </div> */}
            </div>
          </div>
          <div className="testing-grid">
            <div className="icon-box">
              <img src={workflow_logo} />
            </div>
            <div className="text-box">
              <h6 className="">Automated Testing</h6>
              <p className="mb-0">
                If you need more info, please check out{" "}
                <a className="text-blue" href="#">
                  Guideline.
                </a>
              </p>
              {totalAutomatedTestcaseResults == 0 && (
                <button
                  className="btn btn-primary small btn-sm mt-4 display"
                  onClick={() => {
                    handleStartTesting(null, true);
                  }}
                >
                  Start Testing
                </button>
              )}
              {totalAutomatedTestcaseResults != 0 && (
                <Fragment>
                  <Progress
                    percent={automatedProgress}
                    format={() => {
                      if (automatedProgress === 100) {
                        return <span>Done</span>;
                      } else {
                        return (
                          <span>
                            {totalFinishedAutomated}/
                            {totalAutomatedTestcaseResults}
                          </span>
                        );
                      }
                    }}
                  />
                  <Button
                    onClick={() =>
                      navigate(`/dashboard/automated-testing/${testRequestId}`)
                    }
                  >
                    Show Result
                  </Button>
                </Fragment>
              )}
              {/* <div className="progress-bar-line"></div> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
