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
import ComponentList from "../components/TestcaseConfig/ComponentList/ComponentList.jsx";
import ComponentSpecification from "../components/TestcaseConfig/ComponentSpecification/ComponentSpecification.tsx";
import ManualTestCases from "../components/TestcaseConfig/SpecQuestions/SpecQuestions.tsx";
import EditQuestion from "../components/TestcaseConfig/EditQuestion/EditQuestion.tsx";
import UserProfile from "../components/AdminFiles/UserProfile/UserProfile";
import ResetPassword from "../components/CommonFiles/ResetPassword/ResetPassword.jsx";
import AddAdminUser from "../components/AdminFiles/AdminUsers/AddAdminUsers/AddAdminUser";
import UpdateAdminUser from "../components/AdminFiles/AdminUsers/UpdateAdminUser/UpdateAdminUser";
import { USER_ROLES } from "../constants/role_constants.js";
import PageNotFoud from "../components/CommonFiles/PageNotFound/PageNotFound.jsx";
import LogoutComponent from "../components/CommonFiles/LogoutComponent.jsx";
const PrivateDashboardRoute = () => {
  const token = useSelector((state) => state.authSlice.access_token);

  const isAuthenticated = !!token;
  return isAuthenticated ? <Landing /> : <Navigate to="/login" />;
};

const PrivateRoute = ({ roles = [], element: Element, ...rest }) => {
  const token = useSelector((state) => state.authSlice.access_token);
  const userRoles = useSelector((state) => state.userInfoSlice.roleIds);

  const isAuthenticated = !!token;

  let userRole = USER_ROLES.ROLE_ID_ASSESSEE;

  if (userRoles.includes(USER_ROLES.ROLE_ID_ADMIN)) {
    userRole = USER_ROLES.ROLE_ID_ADMIN;
  } else if (userRoles.includes(USER_ROLES.ROLE_ID_TESTER)) {
    userRole = USER_ROLES.ROLE_ID_TESTER;
  } else {
    userRole = USER_ROLES.ROLE_ID_ASSESSEE;
  }
  const hasRequiredRoles = roles.includes(userRole);

  if (!isAuthenticated) return <Navigate to="/login" />;

  return hasRequiredRoles ? (
    <Element {...rest} />
  ) : (
    <Navigate to="/dashboard" />
  );
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
    element: <Login />,
  },
  {
    path: "/",
    element: <PrivateDashboardRoute />,

    children: [
      { path: "", element: <LogoutComponent/> },
      {
        index: true,
        path: "dashboard",
        element: (
          <PrivateRoute
            roles={[
              USER_ROLES.ROLE_ID_ADMIN,
              USER_ROLES.ROLE_ID_ASSESSEE,
              USER_ROLES.ROLE_ID_TESTER,
            ]}
            element={Dashboard}
          />
        ),
      },
      {
        path: "testing-requests",
        element: (
          <PrivateRoute
            roles={[
              USER_ROLES.ROLE_ID_ADMIN,
              USER_ROLES.ROLE_ID_TESTER,
              USER_ROLES.ROLE_ID_ASSESSEE,
            ]}
            element={TestingRequests}
          />
        ),
      },
      {
        path: "applications",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN, USER_ROLES.ROLE_ID_TESTER]}
            element={Applications}
          />
        ),
      },
      {
        path: "choose-test/:testRequestId",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ChooseTest}
          />
        ),
      },
      {
        path: "manual-testing/:testRequestId",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ManualTesting}
          />
        ),
      },
      {
        path: "automated-testing/:testRequestId",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={AutomatedTesting}
          />
        ),
      },
      {
        path: "assessee",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN, USER_ROLES.ROLE_ID_TESTER]}
            element={Assessee}
          />
        ),
      },
      {
        path: "register-application",
        element: (
          <PrivateRoute
            roles={[
              USER_ROLES.ROLE_ID_ADMIN,
              USER_ROLES.ROLE_ID_ASSESSEE,
              USER_ROLES.ROLE_ID_TESTER,
            ]}
            element={RegisterApplication}
          />
        ),
      },
      {
        path: "user-profile",
        element: (
          <PrivateRoute
            roles={[
              USER_ROLES.ROLE_ID_ADMIN,
              USER_ROLES.ROLE_ID_ASSESSEE,
              USER_ROLES.ROLE_ID_TESTER,
            ]}
            element={UserProfile}
          />
        ),
      },
      {
        path: "admin-users",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={AdminUsers}
          />
        ),
      },
      {
        path: "admin-users/add-admin-user",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={AddAdminUser}
          />
        ),
      },
      {
        path: "admin-users/update-admin-user",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={UpdateAdminUser}
          />
        ),
      },
      {
        path: "testcase-config",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ComponentList}
          />
        ),
      },
      {
        path: "component-specification/:componentId",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ComponentSpecification}
          />
        ),
      },
      {
        path: "manual-testcases/:specificationId",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ManualTestCases}
          />
        ),
      },
      {
        path: "edit-question/:testcaseId",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={EditQuestion}
          />
        ),
      },
      {
        path: "reset-password",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ResetPassword}
          />
        ),
      },
    ],
  },
  { path: "*", element: <PageNotFoud /> },
]);

export default routes;
