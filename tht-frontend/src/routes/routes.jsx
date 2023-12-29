import { Navigate, createBrowserRouter } from "react-router-dom";
import Login from "../components/Login";
import Dashboard from "../components/Dashboard";
import { useSelector } from "react-redux";
import User from "../components/User";
import UploadTestCases from "../components/UploadTestCases";
import WaitingPage from "../components/WaitingPage";
import SignUp from "../components/SignUp";
import CongratulationsPage from "../components/CongratulationsPage";

const PrivateRoute = () => {
  const token = useSelector((state) => state.authSlice.access_token);

  const isAuthenticated = !!token;
  return isAuthenticated ? <Dashboard /> : <Navigate to="/login" />;
};

const routes = createBrowserRouter([
  {path:'/waiting',element:<WaitingPage/>},
  { path: "/login", element: <Login /> },
  { path: "/SignUp", element: <SignUp /> },
  { path: "/CongratulationsPage", element: <CongratulationsPage /> },
  {
    path: "/dashboard",
    element: <PrivateRoute/>,
    children: [
      { path: "user", element: <User/> },
      { path: "testcases", element: <UploadTestCases /> },
    ],
  },
  { path: "/", element: <Navigate to="/login" /> }
]);

export default routes;
//THIS FILE WILL REPLACE THE ROUTES PRESENT IN APP.JS ROUTES
