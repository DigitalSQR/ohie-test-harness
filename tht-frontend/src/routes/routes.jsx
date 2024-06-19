import { Navigate, createBrowserRouter, useLocation } from "react-router-dom";
import Login from "../components/CommonFiles/Login/Login";
import Dashboard from "../components/CommonFiles/Dashboard/Dashboard";
import { useSelector } from "react-redux";
import WaitingPage from "../components/UserFiles/WaitingPage/WaitingPage.jsx";
import SignUp from "../components/UserFiles/SignUp/SignUp.jsx";
import CongratulationsPage from "../components/UserFiles/CongratulationsPage/CongratulationsPage.jsx";
import ApplicationReport from "../components/AdminFiles/ApplicationReport/ApplicationReport";
import ChooseTest from "../components/AdminFiles/ChooseTest/ChooseTest";
import Landing from "../components/CommonFiles/Landing/Landing.jsx";
import AutomatedTesting from "../components/AdminFiles/AutomatedTesting/AutomatedTesting";
import GoogleAuth from "../components/UserFiles/GoogleAuth/GoogleAuth.jsx";
import ForgotPassword from "../components/CommonFiles/ForgotPassword/ForgotPassword";
import UpdatePassword from "../components/UserFiles/UpdatePassword/UpdatePassword.jsx";
import Assessee from "../components/AdminFiles/Assessee/Assessee";
import Applications from "../components/AdminFiles/Applications/Applications";
import TestingRequests from "../components/CommonFiles/TestingRequests/TestingRequests";
import RegisterApplication from "../components/CommonFiles/RegisterApplication/RegisterApplication";
import EmailVerified from "../components/UserFiles/EmailVerified/EmailVerified.jsx";
import AdminUsers from "../components/AdminFiles/AdminUsers/AdminUsers";
import ManualTesting from "../components/AdminFiles/ManualTesting/ManualTesting";
import ComponentList from "../components/AdminFiles/TestcaseConfig/ComponentList/ComponentList";
import ComponentSpecification from "../components/AdminFiles/TestcaseConfig/ComponentSpecification/ComponentSpecification";
import ManualTestCases from "../components/AdminFiles/TestcaseConfig/SpecQuestions/SpecQuestions.jsx";
import EditQuestion from "../components/AdminFiles/TestcaseConfig/EditQuestion/EditQuestion.jsx";
import UserProfile from "../components/AdminFiles/UserProfile/UserProfile";
import ResetPassword from "../components/CommonFiles/ResetPassword/ResetPassword.jsx";
import AddAdminUser from "../components/AdminFiles/AdminUsers/AddAdminUsers/AddAdminUser";
import UpdateAdminUser from "../components/AdminFiles/AdminUsers/UpdateAdminUser/UpdateAdminUser";
import { USER_ROLES } from "../constants/role_constants.js";
import PageNotFoud from "../components/CommonFiles/PageNotFound/PageNotFound.jsx";
import LogoutComponent from "../components/CommonFiles/LogoutComponent.jsx";
import ValidateConfigFacts from "../components/AdminFiles/TestcaseConfig/ValidateConfigFacts/ValidateConfigFacts.jsx";
import { matchRoutes } from "react-router-dom";
const PrivateDashboardRoute = () => {
  const token = useSelector((state) => state.authSlice.access_token);
  const redirectUri = useLocation();
  const isAuthenticated = !!token;
  if (
    !isAuthenticated &&
    !!redirectUri.pathname &&
    redirectUri.pathname !== "/login" &&
    redirectUri.pathname !== "/"
  ) {
    localStorage.setItem("redirectUri", JSON.stringify(redirectUri));
  }
  return isAuthenticated ? <Landing /> : <Navigate to="/login" />;
};

const PrivateRoute = ({ roles = [], element: Element }) => {
  const loc = useLocation();

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

  if (!isAuthenticated) {
    localStorage.setItem("intendedRoute");
    return <Navigate to="/login" />;
  }

  return hasRequiredRoles ? <Element /> : <Navigate to="/dashboard" />;
};
const routesConfig = [
  { path: "/waiting", element: <WaitingPage />, name: "Waiting" },
  { path: "/login", element: <Login />, name: "Login" },
  { path: "/SignUp", element: <SignUp />, name: "Sign Up" },
  { path: "/CongratulationsPage/:email", element: <CongratulationsPage />, name: "Congratulations", children: [ { path: ":isOauthCreated" }] },
  { path: "application-report/:testRequestId", element: <ApplicationReport />, name: "Application Report" },
  { path: "/google/success", element: <GoogleAuth />, name: "Success" },
  { path: "/forgotpassword", element: <ForgotPassword />, name: "Forgot Password" },
  {
    path: "/reset/cred/:base64UserEmail/:base64TokenId",
    element: <UpdatePassword />,
    name: "Reset Password"
  },
  {
    path: "/email/verify/:base64UserEmail/:base64TokenId",
    element: <EmailVerified />,
    name: "Email Verified"
  },
  {
    path: "/resend/verification/:email",
    element: <Login />,
    name: "Resend Verification Email"
  },
  {
    path: "/",
    element: <PrivateDashboardRoute />,
    name: "",
    children: [
      { path: "", element: <LogoutComponent />, name: "Logout" },
      {
        index: true,
        name: "Dashboard",
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
        name: "Testing Requests",
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
        name: "Applications",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN, USER_ROLES.ROLE_ID_TESTER, USER_ROLES.ROLE_ID_ASSESSEE]}
            element={Applications}
          />
        ),
      },
      {
        path: "choose-test/:testRequestId",
        name: "Choose Test",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN, USER_ROLES.ROLE_ID_TESTER]}
            element={ChooseTest}
          />
        ),
      },
      {
        path: "manual-testing/:testRequestId",
        name: "Manual Testing",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN, USER_ROLES.ROLE_ID_TESTER]}
            element={ManualTesting}
          />
        ),
      },
      {
        path: "automated-testing/:testRequestId",
        name: "Automated Testing",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN, USER_ROLES.ROLE_ID_TESTER]}
            element={AutomatedTesting}
          />
        ),
      },
      {
        path: "assessee",
        name: "Assessees",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN, USER_ROLES.ROLE_ID_TESTER]}
            element={Assessee}
          />
        ),
      },
      {
        path: "register-application/:testRequestId?",
        // name: "Register Application", 
        // It overrides in the register application page where we need dynamic header content.
        element: (
          <PrivateRoute
            roles={[
              USER_ROLES.ROLE_ID_ASSESSEE,
            ]}
            element={RegisterApplication}
          />
        ),
      },
      {
        path: "user-profile",
        name: "Profile",
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
        path: "user-management",
        name: "User Management",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={AdminUsers}
          />
        ),
      },
      {
        path: "user-management/create-user",
        name: "Create User",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={AddAdminUser}
          />
        ),
      },
      {
        path: "user-management/update-admin-user",
        name: "Edit User",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={UpdateAdminUser}
          />
        ),
      },
      {
        path: "testcase-config",
        name: "Component Configuration",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ComponentList}
          />
        ),
      },
      {
        path: "validate-config",
        name: "Validate Testcase Configuartions",
        element: (
          <ValidateConfigFacts/>
        ),
      },
      {
        path: "testcase-config/component-specification/:componentId",
        name: "Specification Configuration",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ComponentSpecification}
          />
        ),
      },
      {
        path: "testcase-config/manual-testcases/:specificationId",
        name: "Testcase Configuration",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={ManualTestCases}
          />
        ),
      },
      {
        path: "testcase-config/edit-question/:testcaseId",
        name: "Edit Testcase",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN]}
            element={EditQuestion}
          />
        ),
      },
      {
        path: "reset-password",
        name: "Reset Password",
        element: (
          <PrivateRoute
            roles={[USER_ROLES.ROLE_ID_ADMIN,
              USER_ROLES.ROLE_ID_ASSESSEE,
              USER_ROLES.ROLE_ID_TESTER]}
            element={ResetPassword}
          />
        ),
      },
    ],
  },
  { path: "*", element: <PageNotFoud /> },
]

export const useCurrentRoute = (callback) => {
  const location = useLocation();
  const matchedRoutes = matchRoutes(routesConfig, location);

  // Find the deepest matching child route
  const exactChildRoute = matchedRoutes.slice(-1)[0].route;

  return callback(exactChildRoute || null); // Handle cases where no route matches
}

const routes = createBrowserRouter(routesConfig);

export default routes;
