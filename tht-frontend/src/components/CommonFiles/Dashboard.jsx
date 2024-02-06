import { Fragment, useEffect, useState } from "react";
import Header from "./Header/Header";
import { useParams, useLocation } from "react-router-dom";
import Sidebar from "./Sidebar/Sidebar";
import { Outlet } from "react-router-dom";
import Footer from "./Footer/Footer";
import { TestRequestAPI } from "../../api/TestRequestAPI";

export default function Dashboard() {
  const location = useLocation();
  const currentPath = location.pathname;
  const { testRequestId } = useParams();
  const [dynamicHeader, setDynamicHeader] = useState("");

  useEffect(() => {
    if (currentPath.includes("/dashboard/choose-test") && testRequestId) {
      fetchTestCaseInfo(testRequestId);
    } else if (currentPath.includes("/dashboard/manual-testing")) {
      setDynamicHeader("Manual Testing");
    } else if (currentPath.includes("/dashboard/automated-testing")) {
      setDynamicHeader("Automation Testing");
    } else {
      setDynamicHeader(getHeaderContent());
    }
  }, [currentPath]);

  const getHeaderContent = () => {
    switch (currentPath) {
      case "/dashboard/applications":
        return "Applications";
      case "/dashboard/testing-requests":
        return "Testing Requests";
      case "/dashboard/assessee":
        return "Assessees";
      default:
        return "";
    }
  };

  const fetchTestCaseInfo = (testRequestId) => {
    TestRequestAPI.getTestRequestsById(testRequestId)
      .then((res) => {
        setDynamicHeader(res.name);
      })
      .catch(() => {
        console.error("Error fetching test case information");
      });
  };

  return (
    <Fragment>
      <Header headerContent={dynamicHeader} />
      <div>
        <Sidebar />
        <main>{<Outlet />}</main>
      </div>
      <Footer />
    </Fragment>
  );
}
