import { useNavigate } from "react-router-dom";
import tool_icon from "../../../styles/images/tool-icon.png";
import { createContext, useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import "./dashboard.scss";
import { USER_ROLES } from "../../../constants/role_constants";
import { store } from "../../../store/store";
import { TestRequestAPI } from "../../../api/TestRequestAPI";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";
import { getHighestPriorityRole } from "../../../utils/utils";
import ComplianceByComponent from "./Graphs/ComplianceByComponent";

import StackedBarGraph from "./Graphs/StackedBarGraph";
import BarGraph from "./Graphs/BarGraph";
import PieChart from "./Graphs/PieChart";
import Statistics from "./Graphs/Statistics";
export default function Dashboard() {
  const navigate = useNavigate();
  const [userChartData, setUserChartData] = useState([]);
  const [testRequestChartData, setTestRequestChartData] = useState([]);
  const dispatch = useDispatch();
  const [userInfo, setUserInfo] = useState();
  const [role, setRole] = useState();

  const statistics = {
    Applications: 89,
    "Assessees Registered": 43,
    "Compliance Rate": 40,
    "Testing Rate": 70,
  };

  const ApplicationRequestsByMonth = [
    {
      name: "Non-Compliant",
      data: [44, 55, 41, 37, 22, 43, 21],
    },
    {
      name: "Compliant",
      data: [53, 32, 33, 52, 13, 43, 32],
    },
  ];

  const TopCompliantApplications = [
    {
      name: "Health Registry",
      data: [30, 40, 12, 49, 120, 49],
    },
    {
      name: "Facility Registry",
      data: [23, 53, 12, 53, 10],
    },
    {
      name: "Health Worker Registry",
      data: [30],
    },
  ];

  useEffect(() => {
    dispatch(set_header(""));
    const userInfo = store.getState().userInfoSlice;
    console.log(userInfo);
    setUserInfo(userInfo);
    setRole(getHighestPriorityRole(userInfo));
    if (
      userInfo.roleIds.includes("role.tester") ||
      userInfo.roleIds.includes("role.admin")
    ) {
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

  return (
    <div id="dashboard">
      <div id="wrapper">
        <div className="pt-3">
          <div className="text-center row">
            <div className="col-6 offset-3 text-center">
              <img src={tool_icon} alt="Tool Icon" />
              <h4 className="mt-2">Testing Harness Tool</h4>
              <div className="font-size-16 mt-4">
                {role === "ADMIN" &&
                  "Manage the verification process by configuring test cases, evaluating registration requests, and monitoring progress to ensure alignment with OpenHIE Architecture and WHO SMART Guidelines."}
                {role === "TESTER" &&
                  "Oversee verification requests and execute manual/automatic tests. Responsibilities include reviewing and approving verification requests, conducting various tests to determine alignment with OpenHEI Architecture specification and health and data content, as specified by WHO SMART Guidelines."}
                {role === "ASSESSEE" && "Register your application to the open-source verification harness and complete test framework that will facilitate verifying how well technologies align to the OpenHIE Architecture specification and health and data content, as specified by WHO SMART Guidelines. "}
              </div>
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
                    className="btn btn-primary mt-2 theme-blue-color"
                    onClick={() => navigate("/register-application")}
                  >
                    <i className="bi bi-pencil-square"></i> Register Test Request
                  </button>
                </div>
              )}
            </div>

            <div className="d-flex my-5">
              {Object.keys(statistics).map((key) => (
                <Statistics key={key} parameter={key} value={statistics[key]} />
              ))}
            </div>

            <div className="d-flex justify-content-between px-5 my-5">
              <div style={{ minWidth: "40%" }}>
                <StackedBarGraph
                  series={ApplicationRequestsByMonth}
                  title="Application Requests by Month"
                  categories={[
                    "Jan",
                    "Feb",
                    "Mar",
                    "Apr",
                    "May",
                    "June",
                    "July",
                  ]}
                />
              </div>

              <div style={{ minWidth: "40%" }}>
                <StackedBarGraph
                  series={TopCompliantApplications}
                  title="Top 5 Compiant Applications"
                  categories={[
                    "Medplat",
                    "app1",
                    "app2",
                    "app3",
                    "app4",
                    "app5",
                  ]}
                  yAxisSymbol="%"
                />
              </div>
            </div>

            <div className="my-5 d-flex justify-content-between align-items-center">
              <div className="d-flex justify-content-center">
                <PieChart
                  series={[50, 30, 20]}
                  labels={["In-progress", "Pending", "Completed"]}
                />
              </div>
              <div className="d-flex justify-content-center align-items-center flex-column">
                <ComplianceByComponent
                  component="Client Registry"
                  appName="MedPlat"
                  compliancePercentage="80"
                />

                <ComplianceByComponent
                  component="Facility Registry"
                  appName="Health App"
                  compliancePercentage="50"
                />
              </div>
            </div>

            <div className="d-flex justify-content-center my-5">
              <div style={{ width: "70%" }}>
                <BarGraph
                  series={[
                    {
                      data: [80, 60],
                    },
                  ]}
                  title="Percentage of Compliant Requests By Component"
                  categories={["Client Registry", "Facility Registry"]}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
