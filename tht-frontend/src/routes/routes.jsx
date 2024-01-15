import { Navigate, createBrowserRouter } from "react-router-dom";
import Login from "../components/Login";
import Dashboard from "../components/Dashboard";
import { useSelector } from "react-redux";
import User from "../components/User";
import UploadTestCases from "../components/UploadTestCases";
import WaitingPage from "../components/WaitingPage";
import SignUp from "../components/SignUp";
import CongratulationsPage from "../components/CongratulationsPage";
import ApplicationReport from "../components/ApplicationReport";
import ChooseTest from "../components/ChooseTest";
import Landing from "../components/Landing";
import FunctionalTesting from "../components/FunctionalTesting";
import WorkFlowTesting from "../components/workflow-testing";
import GoogleAuth from "../components/GoogleAuth";
import ForgotPassword from "../components/ForgotPassword";
import UpdatePassword from "../components/UpdatePassword";
import UserRegistration from "../components/UserRegistration";
import Applications from "../components/Applications";
import TestingRequests from "../components/TestingRequests";
import RegisterApplication from "../components/RegisterApplication";

const PrivateRoute = () => {
	const token = useSelector((state) => state.authSlice.access_token);

	const isAuthenticated = !!token;
	return isAuthenticated ? <Dashboard /> : <Navigate to="/login" />;
};

const routes = createBrowserRouter([
	{ path: "/waiting", element: <WaitingPage /> },
	{ path: "/login", element: <Login /> },
	{ path: "/SignUp", element: <SignUp /> },
	{ path: "/CongratulationsPage", element: <CongratulationsPage /> },
	{ path: "application-report", element: <ApplicationReport /> },
	{ path: "/google/success", element: <GoogleAuth /> },
	{ path: "/forgotpassword", element: <ForgotPassword /> },
	{ path: "/user/verify/:base64UserEmail/:base64TokenId", element: <UpdatePassword /> },
	{
		path: "/dashboard",
		element: <PrivateRoute />,
		children: [
			{ path: "", element: <Landing /> },
			{ path: "user", element: <User /> },
			{ path: "testcases", element: <UploadTestCases /> },
			{ path: "testing-requests", element: <TestingRequests /> },
			{ path: "applications", element: <Applications /> },
			{ path: "choose-test", element: <ChooseTest /> },
			{ path: "functional-testing", element: <FunctionalTesting /> },
			{ path: "workflow-testing", element: <WorkFlowTesting /> },
			{ path: "user-registration", element: <UserRegistration /> },
			{ path: "register-application", element: <RegisterApplication />}
		],
	},
	{ path: "/", element: <Navigate to="/login" /> },
]);

export default routes;
//THIS FILE WILL REPLACE THE ROUTES PRESENT IN APP.JS ROUTES
