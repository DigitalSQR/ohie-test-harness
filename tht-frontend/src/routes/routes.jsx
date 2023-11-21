import { Navigate, createBrowserRouter } from "react-router-dom";
import Login from "../components/Login";
import Dashboard from "../components/Dashboard";
import { useSelector } from "react-redux";
import User from "../components/User";
import UploadTestCases from "../components/UploadTestCases";
import WaitingPage from "../components/WaitingPage";
import Register from "../components/Register";

const PrivateRoute = () => {
  const token = useSelector((state) => state.authSlice.access_token);

  const isAuthenticated = !!token;
  return isAuthenticated ? <WaitingPage /> : <Navigate to="/login" />;
};

const routes = createBrowserRouter([
  {path:'/waiting',element:<PrivateRoute/>},
  { path: "/login", element: <Login /> },
  {
    path: "/dashboard",
    element: <Dashboard/>,
    children: [
      { path: "user", element: <User/> },
      { path: "testcases", element: <UploadTestCases /> },
    ],
  },
  { path: "/", element: <Navigate to="/login" /> },
  {path:'/register',element:<Register/>}
]);

export default routes;
//THIS FILE WILL REPLACE THE ROUTES PRESENT IN APP.JS ROUTES
