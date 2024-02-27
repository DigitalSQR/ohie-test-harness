import { Navigate, createBrowserRouter } from "react-router-dom";
import Login from "../components/CommonFiles/Login/Login";
import Dashboard from "../components/CommonFiles/Dashboard";
import { useSelector } from "react-redux";
import WaitingPage from "../components/UserFiles/WaitingPage";
import SignUp from "../components/UserFiles/SignUp";
import CongratulationsPage from "../components/UserFiles/CongratulationsPage";
import ApplicationReport from "../components/AdminFiles/ApplicationReport/ApplicationReport";
import ChooseTest from "../components/AdminFiles/ChooseTest/ChooseTest";
import Landing from "../components/CommonFiles/Landing";
import FunctionalTesting from "../components/AdminFiles/ManualTesting/ManualTesting";
import AutomatedTesting from "../components/AdminFiles/AutomatedTesting/AutomatedTesting";
import GoogleAuth from "../components/UserFiles/GoogleAuth";
import ForgotPassword from "../components/CommonFiles/ForgotPassword";
import UpdatePassword from "../components/UserFiles/UpdatePassword";
import Assessee from "../components/AdminFiles/Assessee/Assessee";
import Applications from "../components/AdminFiles/Applications/Applications";
import TestingRequests from "../components/CommonFiles/TestingRequests/TestingRequests";
import RegisterApplication from "../components/CommonFiles/RegisterApplication/RegisterApplication";
import EmailVerified from "../components/UserFiles/EmailVerified";
import AdminUsers from "../components/AdminFiles/AdminUsers/AdminUsers";
import ManualTesting from "../components/AdminFiles/ManualTesting/ManualTesting";
import ComponentList from "../components/TestcaseConfig/ComponentList/ComponentList.tsx";
import ComponentSpecification from "../components/TestcaseConfig/ComponentSpecification/ComponentSpecification.tsx";
import ManualTestCases from "../components/TestcaseConfig/SpecQuestions/SpecQuestions.tsx";
import EditQuestion from "../components/TestcaseConfig/EditQuestion/EditQuestion.tsx";
import UserProfile from "../components/AdminFiles/UserProfile/UserProfile";
import ResetPassword from "../components/CommonFiles/ResetPassword/ResetPassword.jsx";
import AddAdminUser from "../components/AdminFiles/AdminUsers/AddAdminUsers/AddAdminUser";
import UpdateAdminUser from "../components/AdminFiles/AdminUsers/UpdateAdminUser/UpdateAdminUser";
const PrivateRoute = () => {
  const token = useSelector((state) => state.authSlice.access_token);

  const isAuthenticated = !!token;
  return isAuthenticated ? <Landing /> : <Navigate to="/login" />;
};

const routes = createBrowserRouter([
  { path: "/waiting", element: <WaitingPage /> },
  { path: "/login", element: <Login /> },
  { path: "/SignUp", element: <SignUp /> },
  { path: "/CongratulationsPage/:email", element: <CongratulationsPage /> },
  { path: "application-report/:testRequestId", element: <ApplicationReport /> },
  { path: "/google/success", element: <GoogleAuth /> },
  { path: "/forgotpassword", element: <ForgotPassword /> },
  {
    path: "/reset/cred/:base64UserEmail/:base64TokenId",
    element: <UpdatePassword />,
  },
  {
    path: "/email/verify/:base64UserEmail/:base64TokenId",
    element: <EmailVerified />,
  },
  { 
    path: "/resend/verification/:email", 
    element: <Login/> 
  },
  {
    path: "/",
    element: <PrivateRoute />,
    children: [
      { path: "dashboard", element: <Dashboard /> },
      { path: "testing-requests", element: <TestingRequests /> },
      { path: "applications", element: <Applications /> },
      { path: "choose-test/:testRequestId", element: <ChooseTest /> },
      { path: "manual-testing/:testRequestId", element: <ManualTesting /> },
      {
        path: "automated-testing/:testRequestId",
        element: <AutomatedTesting />,
      },
      { path: "assessee", element: <Assessee /> },
      { path: "register-application", element: <RegisterApplication /> },
      { path: "user-profile", element: <UserProfile /> },
      { path: "admin-users", element: <AdminUsers /> },
      { path: "admin-users/add-admin-user", element: <AddAdminUser/> },
      { path: "admin-users/update-admin-user", element: <UpdateAdminUser/> },
      // { path: "admin-users/update-admin-user", element: <AdminUsers /> },
      { path: "testcase-config", element: <ComponentList /> },
      {
        path: "component-specification/:componentId",
        element: <ComponentSpecification />,
      },
      {
        path: "manual-testcases/:specificationId",
        element: <ManualTestCases />,
      },
      { path: "edit-question/:testcaseId", element: <EditQuestion /> },
      {path:"reset-password",element:<ResetPassword/>}
    ],
  },
  { path: "", element: <Navigate to="/login" /> },
]);

export default routes;
