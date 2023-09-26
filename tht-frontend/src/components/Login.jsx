import React, { useState,Fragment } from 'react';
import { useDispatch } from 'react-redux';
import { loginSuccess, loginFailure } from '../api/authActions';
import axios from 'axios';
import "../styles/Login.css";
import openhie_logo from "../styles/img/openhie-logo.png";
import {useNavigate } from 'react-router-dom';
import api, { setAuthToken } from '../api/auth';
import { useHistory } from 'react-router';
import { login_success } from '../api/authReducer';


export default function Login() {
  
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [error, setError] = useState(''); // Initialize error state
  
    const [formData, setFormData] = useState({
      username: '',
      password: '',
      grant_type: 'password', // Assuming 'password' grant type
    });
    const handleInputChange = (e) => {
      const { name, value } = e.target;
      setFormData({ ...formData, [name]: value });
    };
  const handleLogin = async () => {
    try {

      console.log("formData=",formData);      
      const response = await api.post(
        '/oauth/token',
        new URLSearchParams(formData)
      );
      console.log('API Response:', response);

      const { access_token: accessToken } = response.data;
        console.log("access_token=",accessToken);
      // dispatch(loginSuccess(accessToken));
      dispatch(login_success(accessToken));
      // Redirect to the dashboard if authentication is successful
      navigate('/dashboard');
      
    } catch (error) {
     // dispatch(loginFailure('Invalid username or password'));
      setError('Invalid username or password');
    }
  };

  return (
    <Fragment>
      <div className="full-page-wrapper">
        <div className="openhie-card card">
          <img
            src={openhie_logo}
            alt="logo"
            className="img-fluid mx-auto d-block mb-3"
            hei="true"
          />
          <div className="openhie-form row">
          {error && <p style={{ color: 'red' }}>{error}</p>} {/* Display error message if it exists */}
            <div className="col-12 mb-2">
            {/* {error && <p style={{ color: 'red' }}>{error}</p>} */}
              <div className="mb-3">
                <label
                  htmlFor="exampleFormControlInput1"
                  className="form-label"
                >
                  UserName
                </label>

                <input
                  className="form-control"
                  name="username"
                  id="exampleFormControlInput1"
                  placeholder="username"
                  autoComplete="off"
                  value={formData.username}
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <div className="col-12 mb-2">
              <div className="mb-3">
                <label
                  htmlFor="exampleFormControlInput2"
                  className="form-label"
                >
                  Password
                </label>

                <input
                  type="password"
                  className="form-control"
                  name="password"
                  id="exampleFormControlInput2"
                  placeholder="password"
                  autoComplete="off"
                  value={formData.password}
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <div className="col-md-6 mb-2 pe-0">
              <div className="form-check form-check-inline">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id="inlineCheckbox1"
                  value="option1"
                />
                <label className="form-check-label" htmlFor="inlineCheckbox1">
                  Keep me signed in
                </label>
              </div>
            </div>
            <div className="col-md-6 mb-2 text-end">
              <a className="text-secondary text-decoration-none">
                Forgot Password?
              </a>
            </div>
            <div className="col-12">            
                <button
                  className="btn openhie-primary w-100"
                  id="submit"
                  onClick={handleLogin}
                >
                  Submit
              </button>
            
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}
