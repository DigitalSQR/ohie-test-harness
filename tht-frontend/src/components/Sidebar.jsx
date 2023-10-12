import { Fragment, useState } from "react";
import "../styles/Landing.css";
import {Router, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { log_out } from "../reducers/authReducer";
import { AuthenticationAPI } from "../api/AuthenticationAPI";
import { clearAuthInfo } from '../api/configs/axiosConfigs'


export default function Sidebar({onComponentClick}){
    const [addUser,setAddUser]= useState(false);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const handleLogout = async () => {
      
      AuthenticationAPI.doLogout()
        .then((response) => {
          dispatch(log_out());
          navigate("/login");
          clearAuthInfo();        
        })
        .catch((error) => {
          
        })   
  
    };
    const viewUser = async () => {
        AuthenticationAPI.viewUser("argus")
        .then((response) => {
        
        })
        .catch((error) => {
          // Handle the error here
  
        /*  notification.error({
            placement: "bottomRight",
            description: "Invalid username or password",
          });*/
        })   
    };
    return(
        <div className="wrapper d-flex">
        <div className="d-inline-flex flex-column flex-shrink-0 px-3 sidebar">
          <div className="menu-toggle text-end">
            <span className="menu-toggle-btn w-30"></span>
          </div>
          <ul className="nav nav-pills flex-column mb-auto">
            <li className="nav-item">
              <a href="#" className="nav-link" aria-current="page">
                <span className="report w-30"></span>
                Reports
              </a>
            </li>
            <li>
              <a href="#" className="nav-link link-dark">
                <span className="history w-30"></span>
                History
              </a>
            </li>
            <li>
              <a href="#" className="nav-link link-dark">
                <span className="profile w-30"></span>
                Profile
              </a>
            </li>
            <li>
              <a href="#" className="nav-link link-dark">
                <span className="settings w-30"></span>
                Settings
              </a>
            </li>
            <li>
              <a  onClick={viewUser} className="nav-link link-dark">
                <span className="settings w-30"></span>
                View User
              </a>
            </li>
            <li>
              <a  onClick={handleLogout} className="nav-link link-dark">
                <span className="settings w-30"></span>
                Log out
              </a>
            </li>
            <li>
              <a className="nav-link link-dark" onClick={()=>{onComponentClick('User');
               navigate('/dashboard/user')}} >
                <span className="settings w-30"></span>
                Users
              </a>
            </li>
            <li>
              <a className="nav-link link-dark" onClick={()=>{onComponentClick('TestCases');
            navigate('/dashboard/testcases')}}>
                <span className="settings w-30"></span>
                TestCases
              </a>
            </li>
          </ul>
        </div>
        </div>
    )
}