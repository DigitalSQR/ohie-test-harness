import { Fragment } from "react";
import openhie_logo from "../styles/img/openhie-logo.png";
import "../styles/Landing.css";
import api, { setAuthToken } from '../api/auth';
import {useNavigate } from 'react-router-dom';
import { logout } from "../api/authActions";
import { useDispatch } from 'react-redux';
export default function Dashboard() {
 
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const handleLogout = async () => {
    try {

      const response = await api.post(
        '/users/logout'
      );
      console.log('API Response:', response);
      dispatch(logout());
      navigate('/login');
      
    } catch (error) {
      console.log("error=",error);
     // dispatch(loginFailure('Invalid username or password'));
     // setError('Invalid username or password');
    }
  };
  return (
    <Fragment>
      <nav class="navbar navbar-light openhie-navbar">
        <div class="container-fluid">
          <a class="navbar-brand" href="index.html">
            <img src={openhie_logo} alt="" height="50" />
          </a>
        </div>
      </nav>
      <div class="wrapper d-flex">
        <div class="d-inline-flex flex-column flex-shrink-0 px-3 sidebar">
          <div class="menu-toggle text-end">
            <span class="menu-toggle-btn w-30"></span>
          </div>
          <ul class="nav nav-pills flex-column mb-auto">
            <li class="nav-item">
              <a href="#" class="nav-link" aria-current="page">
                <span class="report w-30"></span>
                Reports
              </a>
            </li>
            <li>
              <a href="#" class="nav-link link-dark">
                <span class="history w-30"></span>
                History
              </a>
            </li>
            <li>
              <a href="#" class="nav-link link-dark">
                <span class="profile w-30"></span>
                Profile
              </a>
            </li>
            <li>
              <a href="#" class="nav-link link-dark">
                <span class="settings w-30"></span>
                Settings
              </a>
            </li>
            <li>
              <a  onClick={handleLogout} class="nav-link link-dark">
                <span class="settings w-30"></span>
                Log out
              </a>
            </li>
          </ul>
        </div>
      </div>
    </Fragment>
  );
}