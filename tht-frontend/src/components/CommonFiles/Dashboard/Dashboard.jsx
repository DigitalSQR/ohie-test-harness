import { useNavigate } from "react-router-dom";
import tool_icon from "../../../styles/images/tool-icon.png";
import { createContext, useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import ApexChart from "react-apexcharts";
import "./dashboard.scss";
import { USER_ROLES } from "../../../constants/role_constants";
import { store } from "../../../store/store";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";
import { userStateConstantNames } from "../../../constants/user_constants";
import { TestRequestStateConstantNames } from "../../../constants/test_requests_constants";

export default function Dashboard() {
  const navigate = useNavigate();
  const [userChartData, setUserChartData] = useState([]);
  const [testRequestChartData, setTestRequestChartData] = useState([]);
  const dispatch = useDispatch();
  const [userInfo, setUserInfo] = useState();

  useEffect(() => {
    dispatch(set_header(""));
    const userInfo = store.getState().userInfoSlice;
    console.log(userInfo);
    setUserInfo(userInfo);
    if(userInfo.roleIds.includes('role.tester') || userInfo.roleIds.includes('role.admin'))
    {
    const fetchUserData = async () => {

      const response = await UserAPI.getUsers();
      setUserChartData(response.content);
    };
    fetchUserData();
  }
  }, []);

  // const userContext = createContext(userInfo);

  useEffect(() => {

    const fetchTestRequestData = async () => {
      const response = await TestRequestAPI.getTestRequestsByState("");
      setTestRequestChartData(response.content);
    };
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
      return userStateConstantNames[stateKey] || stateKey;
    }),
  };

  const userSeries = Object.values(userStateCounts);

  const testRequestOptions = {
    labels: Object.keys(testRequestStateCounts).map((stateKey) => {
      return TestRequestStateConstantNames[stateKey] || stateKey;
    }),
  };

  const testRequestSeries = Object.values(testRequestStateCounts);

  return (
    <div id="dashboard">
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
          {userInfo?.roleIds?.includes(USER_ROLES.ROLE_ID_ASSESSEE) && (
            <div className="my-4">
              <button
                className="btn btn-primary mt-2"
                onClick={() => navigate("/register-application")}
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
              <div className="chart-container">
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
              <h5 className="mb-3 text-center mt-1">
                Test Request Status Pie Chart
              </h5>
              <div className="chart-container">
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
    </div>
  );
}
