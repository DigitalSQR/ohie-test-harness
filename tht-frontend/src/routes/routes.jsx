import { Navigate, createBrowserRouter } from "react-router-dom";
import Login from "../components/Login";
import Dashboard from "../components/Dashboard";
import { useSelector } from "react-redux";
import User from "../components/User";
import TestCases from "../components/TestCases";

const PrivateRoute = () => {
  const token = useSelector((state) => state.authSlice.access_token);

  const isAuthenticated = !!token;
  return isAuthenticated ? <Dashboard /> : <Navigate to="/login" />;
};

const routes = createBrowserRouter([
  { path: "/login", element: <Login /> },
  {
    path: "/dashboard",
    element: <PrivateRoute />,
    children: [
      { path: "user", element: <User/> },
      { path: "testcases", element: <TestCases /> },
    ],
  },
  { path: "/", element: <Navigate to="/login" /> },
]);

export default routes;
//THIS FILE WILL REPLACE THE ROUTES PRESENT IN APP.JS ROUTES
