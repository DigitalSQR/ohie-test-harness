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
import { getHighestPriorityRole } from "../../../utils/utils";

export default function Dashboard() {
  const navigate = useNavigate();
  const [userChartData, setUserChartData] = useState([]);
  const [testRequestChartData, setTestRequestChartData] = useState([]);
  const dispatch = useDispatch();
  const [userInfo, setUserInfo] = useState();
  const [role, setRole] = useState();
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
          </div>
        </div>
      </div>
    </div>
  );
}
