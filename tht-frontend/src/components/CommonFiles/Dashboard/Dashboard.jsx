import { useNavigate } from "react-router-dom";
import tool_icon from "../../../styles/images/tool-icon.png";
import { createContext, useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import "./dashboard.scss";
import { USER_ROLES } from "../../../constants/role_constants";
import { store } from "../../../store/store";
import { Empty, Tabs } from "antd";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";
import { getHighestPriorityRole } from "../../../utils/utils";
import ComplianceByComponent from "./Graphs/ComplianceByComponent";
import StackedBarGraph from "./Graphs/StackedBarGraph";
import BarGraph from "./Graphs/BarGraph";
import PieChart from "./Graphs/PieChart";
import Statistics from "./Graphs/Statistics";
import TabPane from "antd/es/tabs/TabPane";
import { DashboardAPI } from "../../../api/DashboardAPI";
import { TestRequestStateConstantNames } from "../../../constants/test_requests_constants";
export default function Dashboard() {
  const navigate = useNavigate();
  const [userChartData, setUserChartData] = useState([]);
  const dispatch = useDispatch();
  const [userInfo, setUserInfo] = useState();
  const [role, setRole] = useState();

  //use States for graph
  const [statistics, setStatistics] = useState({});
  const [piechartData, setPiechartData] = useState({ series: [], labels: [] });
  const [percentageCumulativeBarGraph, setPercentageCumulativeBarGraph] =
    useState({ series: [], categories: [] });
  const [topCompliantApplications, setTopCompliantApplications] = useState({
    componentsData: [],
    categories: [],
  });
  const [componentComplianceData, setComponentComplianceData] = useState([]);

  //use state and handleYearChange function for "Application Requests By year" bar graph
  const [year, setYear] = useState();
  const handleYearChange = (event) => {
    setYear(event.target.value);
  };
  const [applicationRequestsByMonth, setApplicationRequestsByMonth] = useState(
    {}
  );

  useEffect(() => {
    dispatch(set_header(""));
    const userInfo = store.getState().userInfoSlice;
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

  //For statistics
  const statsGraph = (
    totalApplications,
    assesseeRegistered,
    complianceRate,
    testingRate
  ) => {
    setStatistics({
      Applications: totalApplications,
      "Assessees Registered": assesseeRegistered,
      "Compliance Rate": complianceRate,
      "Testing Rate": testingRate,
    });
  };

  //Function to display the Pie Chart for the test requests
  const pieChart = (data) => {
    const pieChartObj = { labels: [], series: [] };
    Object.keys(data).forEach((key) => {
      if (data[key] !== 0) {
        pieChartObj.labels.push(TestRequestStateConstantNames[key]);
        pieChartObj.series.push(data[key]);
      }
    });
    setPiechartData(pieChartObj);
  };

  //Function to handle the Percentage of Compliant Requests By Component using a horizontal bar graph
  const percentageOfCompliantRequestsByComponent = (data) => {
    const percentageCumulativeObj = { series: [], categories: [] };
    data.forEach((value) => {
      if (value.compliantTestRequests == 0) {
        return;
      }
      percentageCumulativeObj.series.push(
        ((value.compliantTestRequests / value.totalTestRequests) * 100).toFixed(
          2
        )
      );
      percentageCumulativeObj.categories.push(value.componentName);
    });
    setPercentageCumulativeBarGraph(percentageCumulativeObj);
  };

  //Function to display the top 5 compliant Applications using a stacked bar graph
  const topFiveCompliantApplications = (data) => {
    // {
    //   applicationName: "test4",
    //   rank: 1,
    //   testcasesPassed: 7,
    //   totalTestcases: 22,
    //   components: [
    //     {
    //       componentName: "Facility Registry (FR)",
    //       componentRank: 2,
    //       testcasesPassed: 7,
    //       totalTestcases: 22,
    //     },
    //   ],
    // },

    const topCompliantAppsObj = { componentsData: [], categories: [] };
    data.forEach((value, index) => {
      topCompliantAppsObj.categories.push(value.applicationName);
      value.components.forEach((component) => {
        const idx = topCompliantAppsObj.componentsData.findIndex(
          (item) => item.name == component.componentName
        );
        const compliantPercentage = (
          (component.testcasesPassed / component.totalTestcases) *
          100
        ).toFixed(2);
        if (idx < 0) {
          topCompliantAppsObj.componentsData.push({
            name: component.componentName,
            data: Array(5).fill(0).map((value, i)=>(i===index ? compliantPercentage :value)),
          });
          
        } else {
          topCompliantAppsObj.componentsData[idx].data[index] =
            compliantPercentage;
        }
      });
    });
    setTopCompliantApplications(topCompliantAppsObj);
  };

  //Function to display the Application Requests By Year using a stacked bar graph
  const appRequestsByYear = (data) => {
    const applicationRequestsObj = {};
    data.forEach((yearValues) => {
      const dataArray = [
        { name: "Non-Compliant", data: [] },
        { name: "Compliant", data: [] },
      ];
      yearValues.applicationRequestDataByMonthList.forEach((monthValues) => {
        dataArray
          .find((item) => item.name == "Non-Compliant")
          .data.push(monthValues.nonCompliant);
        dataArray
          .find((item) => item.name == "Compliant")
          .data.push(monthValues.compliant);
      });
      applicationRequestsObj[yearValues.year] = { dataArray };
      setApplicationRequestsByMonth(applicationRequestsObj);
      setYear(Object.keys(applicationRequestsObj)[0]);
    });
  };

  //Function to display the top compliance by component
  const topComplianceByComponent = (data) => {
    const topComplianceArray = [];
    data.forEach((value) => {
      const topComplianceObj = {
        component: "",
        data: { appName: [], compliancePercentage: [] },
      };
      topComplianceObj.component = value.componentName;
      value.awardApplicationList.forEach((x) => {
        if (x.totalTestcases == 0) return;
        topComplianceObj.data.appName.push(x.appName);
        topComplianceObj.data.compliancePercentage.push(
          ((x.passedTestcases / x.totalTestcases) * 100).toFixed(2)
        );
      });
  
      const combinedData = topComplianceObj.data.appName.map((appName, index) => ({
        appName,
        compliancePercentage: topComplianceObj.data.compliancePercentage[index],
      }));
  
      combinedData.sort(
        (a, b) => b.compliancePercentage - a.compliancePercentage
      );
  
      const sortedAppName = combinedData.map((item) => item.appName);
      const sortedCompliancePercentage = combinedData.map(
        (item) => item.compliancePercentage
      );
  
      topComplianceObj.data.appName = sortedAppName;
      topComplianceObj.data.compliancePercentage = sortedCompliancePercentage;
  
      if (topComplianceObj.data.appName.length > 0)
        topComplianceArray.push(topComplianceObj);
     
    });
    setComponentComplianceData(topComplianceArray);
  };

  useEffect(() => {
    DashboardAPI.getDashBoard()
      .then((responseData) => {
        //Function being called to display the statistics
        statsGraph(
          responseData.totalApplications,
          responseData.assesseeRegistered,
          Math.round(responseData.complianceRate),
          Math.round(responseData.testingRate)
        );

        //Function being called to display the pieChart
        pieChart(responseData.pieChart);

        //Function being called to display the horizontal bar graph to show the percentage of Compliant Requests By Component
        percentageOfCompliantRequestsByComponent(
          responseData.percentageCumulativeGraph
        );

        //Function being called to display the stacked bar graph to show the top five compliant applications
        topFiveCompliantApplications(responseData.compliantApplications);

        //Application requests by year
        appRequestsByYear(responseData.applicationRequestsByMonth);

        //Function being called to display the top compliance by component
        topComplianceByComponent(responseData.awardGraph);
      })
      .catch((error) => {});
  }, []);

  return (
    <div id="dashboard">
      <div id="wrapper">
        <div className="row">
          <div className="col-xxl-10 mx-auto">
            <div class="alert alert-success p-2 mb-4" role="alert">
              <div className="row align-items-center">
                <div className="col-md-3 align-items-center text-center mt-3">
                  <img
                    src={tool_icon}
                    alt="Tool Icon"
                    style={{ maxHeight: "42px" }}
                    className="me-2"
                  />
                  <h5 className="mt-2">Testing Harness Tool</h5>
                </div>
                <div className="col">
                  <div className="font-size-16 mt-4">
                    {role === "ADMIN" && (
                      <>
                        <p>
                          As a THT Admin, you hold the key to managing the application testing process. Your responsibilities include configuring and modifying test cases, evaluating new assessee registration request, evaluating new application testing requests, and monitoring progress to ensure alignment with OpenHIE Architecture.
                        </p>
                        <p>
                          Please note that your dashboard will initially appear empty as there is no data available for analysis. As you configure test cases and evaluate testing requests, data will populate the dashboard, enabling you to monitor progress and get insights regarding application testing.
                        </p>
                      </>
                    )}
                    {role === "TESTER" &&
                      "Oversee verification requests and execute manual/automatic tests. Responsibilities include reviewing and approving verification requests, conducting various tests to determine alignment with OpenHEI Architecture specification and health and data content, as specified by  WHO SMART Guidelines."}
                    {role === "ASSESSEE" && (
                      <>
                        <p>
                          As a THT Assessee, you can register your application
                          for testing. Our platform facilitates the verification
                          process to ensure your application aligns with the
                          OpenHIE Architecture specification and WHO SMART
                          Guidelines.
                        </p>
                        <p>
                          Register your application now to begin the journey
                          towards compliance and excellence in healthcare
                          technology by clicking on the button below.
                        </p>
                      </>
                    )}
                  </div>
                  <div className="col">
                    <p className="mt-2">
                      <a
                        className="text-blue font-weight-500"
                        target="_blank"
                        href="https://guides.ohie.org/arch-spec/openhie-component-specifications-1"
                      >
                        View OpenHIE Component Specifications
                      </a>
                    </p>
                    {userInfo?.roleIds?.includes(
                      USER_ROLES.ROLE_ID_ASSESSEE
                    ) && (
                      <div className="my-4">
                        <button
                          className="btn btn-primary mt-2 theme-blue-color"
                          id="#Dashboard-1"
                          onClick={() => navigate("/register-application")}
                        >
                          <i className="bi bi-pencil-square"></i> Register Test
                          Request
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {(role === "ADMIN" || role === "TESTER") && (
          <div className="pt-0">
            <div className="text-center">
              <div className="d-flex mb-3">
                {Object.keys(statistics).map((key, index, array) => (
                  <Statistics
                    key={key}
                    parameter={key}
                    value={statistics[key]}
                    index={index}
                    array={array}
                  />
                ))}
              </div>
              <div className="row">
                <div className="col-md-6">
                  <div className="card p-3" style={{ height: "100%" }}>
                    {applicationRequestsByMonth[year]?.dataArray.length > 0 ? (
                      <div className="dropdown-container">
                        <select
                          value={year}
                          onChange={handleYearChange}
                          className="form-select"
                          style={{ appearance: "none", paddingRight: "40px" }}
                        >
                          {Object.keys(applicationRequestsByMonth).map(
                            (key) => (
                              <option key={key} value={key}>
                                {key}
                              </option>
                            )
                          )}
                        </select>
                      </div>
                    ) : (
                      <></>
                    )}

                    <StackedBarGraph
                      dropdown={true}
                      series={applicationRequestsByMonth[year]?.dataArray || []}
                      title="Application Requests by Year"
                      categories={[
                        "Jan",
                        "Feb",
                        "Mar",
                        "Apr",
                        "May",
                        "Jun",
                        "Jul",
                        "Aug",
                        "Sep",
                        "Oct",
                        "Nov",
                        "Dec",
                      ]}
                    />
                  </div>
                </div>

                <div className="col-md-6">
                  <div className="card p-3" style={{ height: "100%" }}>
                    <StackedBarGraph
                      series={topCompliantApplications?.componentsData}
                      title="Top 5 Compliant Applications"
                      categories={topCompliantApplications?.categories}
                      yAxisSymbol="%"
                    />
                  </div>
                </div>
              </div>
              {
                <div className="row mt-3">
                  <div className="col-12 mx-auto">
                    <div className="card p-3">
                      <BarGraph
                        series={[
                          {
                            data: percentageCumulativeBarGraph
                              ? percentageCumulativeBarGraph.series
                              : [],
                          },
                        ]}
                        title="Percentage of Compliant Requests By Component"
                        categories={percentageCumulativeBarGraph?.categories}
                      />
                    </div>
                  </div>
                </div>
              }
              <div className="row mt-3">
                <div className="col-xxl-4 ">
                  <div className="card p-3">
                    <PieChart
                      title={"Application Requests By Status"}
                      series={piechartData?.series}
                      labels={piechartData?.labels}
                    />
                  </div>
                </div>
              <div className="col-xxl-8 mt-sm-3 mt-xxl-0">
                <div className="card p-3" style={{ height: "100%" }}>
                  <div
                    className="d-flex justify-content-left"
                    style={{ fontWeight: 600, fontSize: "14px" }}
                  >
                    {" "}
                    <p>Top Compliance By Component</p>
                  </div>
                  <Tabs defaultActiveKey="1">
                    {componentComplianceData?.map((x, index) => (
                      <TabPane
                        tab={
                          <div
                          style={{
                            width: "max-content",
                            maxWidth: "130px", 
                            whiteSpace: "pre-wrap", 
                            lineHeight: "1.2",
                            color: "#009fc8"
                          }}
                          >
                            {x.component}
                          </div>
                        }
                        key={index + 1}
                      >
                        <div>
                          <ComplianceByComponent
                            data={x?.data}
                            component={x.component}
                          />
                        </div>
                      </TabPane>
                    ))}
                    {componentComplianceData?.length === 0 && (
                      <TabPane tab="No Record Found" key="empty">
                        <Empty
                          description="No Record Found."
                          imageStyle={{
                            height: 200,
                          }}
                        />
                      </TabPane>
                    )}
                  </Tabs>
                </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
