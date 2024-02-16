import { useNavigate } from "react-router-dom";
import tool_icon from "../../styles/images/tool-icon.png";
import { createContext, useEffect, useState } from "react";
import { UserAPI } from "../../api/UserAPI";
import ApexChart from "react-apexcharts";
import { USER_ROLES } from "../../constants/role_constants";
import { store } from "../../store/store";
import { TestRequestAPI } from "../../api/TestRequestAPI";
export default function Landing() {
  const navigate = useNavigate();
  const [user, setUser] = useState();
  const [userChartData, setUserChartData] = useState([]);
  const [testRequestChartData, setTestRequestChartData] = useState([]);

  const [userInfo, setUserInfo] = useState();

  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserInfo(userInfo);
  }, []);

  // const userContext = createContext(userInfo);

  useEffect(() => {
    const fetchUserData = async () => {
      const response = await UserAPI.getUsers();
      setUserChartData(response.content);
    };

    const fetchTestRequestData = async () => {
      const response = await TestRequestAPI.getTestRequestsByState("");
      console.log(response.content);
      setTestRequestChartData(response.content);
    };

    fetchUserData();
    fetchTestRequestData();
  }, []);

  const userStateCounts = userChartData.reduce((counts, user) => {
    const state = user.state;
    counts[state] = (counts[state] || 0) + 1;
    return counts;
  }, {});

  const testRequestStateCounts = testRequestChartData.reduce(
    (counts, testRequest) => {
      const state = testRequest.state;
      counts[state] = (counts[state] || 0) + 1;
      return counts;
    },
    {}
  );

  const userOptions = {
    labels: Object.keys(userStateCounts).map((stateKey) => {
      const labelMap = {
        "user.status.active": "Active",
        "user.status.inactive": "Inactive",
        "user.status.rejected": "Rejected",
        "user.status.verification.pending": "Not verified",
        "user.status.approval.pending": "Pending",
      };

      return labelMap[stateKey] || stateKey;
    }),
  };

  const userSeries = Object.values(userStateCounts);

  const testRequestOptions = {
    labels: Object.keys(testRequestStateCounts).map((stateKey) => {
      const labelMap = {
        "test.request.status.pending": "Pending",
        "test.request.status.accepted": "Accepted",
        "test.request.status.rejected": "Rejected",
        "test.request.status.inprogress": "In Progress",
        "test.request.status.finished": "Finished",
        "test.request.status.skipped": "Skipped",
      };

      return labelMap[stateKey] || stateKey;
    }),
  };

  const testRequestSeries = Object.values(testRequestStateCounts);

  return (
    <div id="wrapper">
      <div className="pt-3">
        <div className="text-center">
           <img src={tool_icon} alt="Tool Icon" />
          <h4 className="mt-2">Testing Harness Tool</h4>
          <p className="font-size-16 mt-4">
            Register your application to the open-source testing harness and
            complete test framework that will facilitate testing how well
            technologies align to the OpenHIE Architecture specification and
            health and data content, as specified by WHO SMART Guidelines.
          </p>
          <p className="my-4">
            <a
              className="text-blue font-weight-500"
              target="_blank"
              href="https://guides.ohie.org/arch-spec/openhie-component-specifications-1"
            >
              View OpenHIE Component Specifications
            </a>
          </p> 
          {user?.roleIds?.includes(USER_ROLES.ROLE_ID_ASSESSEE) && (
            <div className="my-4">
              <button
                className="btn btn-primary btn-blue mt-2"
                onClick={() => navigate("/dashboard/register-application")}
              >
                <i className="bi bi-pencil-square"></i> Register application
              </button>
            </div>
          )}
        </div>
        <div className="row mt-5 justify-content-around">
        {!userInfo?.roleIds?.includes(USER_ROLES.ROLE_ID_ASSESSEE) && (
          <div className="col-md-4 border rounded">
            <h5 className="mb-3 text-center mt-1">User Status Pie Chart</h5>
            <div
              style={{
                maxHeight: "50vh", // Set the maximum height in viewport height units
                maxWidth: "50vw", // Set the maximum width in viewport width units
                margin: "0 auto",
                padding: "10px",
                width: "100%",
              }}
            >
              <ApexChart
                options={userOptions}
                series={userSeries}
                type="pie"
                width="100%"
              />
            </div>
          </div>
        )}
        {testRequestSeries.length > 0 && (
          <div className="col-md-4 border rounded">
            <h5 className="mb-3 text-center mt-1">Test Request Status Pie Chart</h5>
            <div
              style={{
                maxHeight: "50vh", // Set the maximum height in viewport height units
                maxWidth: "50vw", // Set the maximum width in viewport width units
                margin: "0 auto",
                padding: "10px",
                width: "100%",
              }}
            >
              <ApexChart
                options={testRequestOptions}
                series={testRequestSeries}
                type="pie"
                width="100%"
              />
            </div>
          </div>
        )}
      </div>
      
      </div>
    </div>
  );
}
