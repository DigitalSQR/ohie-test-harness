import React, { useState,Fragment } from 'react';
import { useDispatch } from 'react-redux';
import { loginSuccess } from '../api/authActions';
import axios from 'axios';
import "../styles/Login.css";
import openhie_logo from "../styles/img/openhie-logo.png";
import {NavLink} from 'react-router-dom';

// const Login = () => {
  // const [username, setUsername] = useState('');
  // const [password, setPassword] = useState('');
//   const dispatch = useDispatch();

  // const handleLogin = async () => {
  //   try {
  //     // Make a request to your authentication endpoint
  //     const response = await axios.post('/auth/login', {
  //       username,
  //       password,
  //     });

  //     const { accessToken } = response.data;

  //     // Dispatch the loginSuccess action to store the token in Redux
  //     dispatch(loginSuccess(accessToken));

  //     // Redirect or navigate to the secured dashboard
  //   } catch (error) {
  //     console.error('Login failed:', error);
  //   }
  // };

//   return (
//     <div>
//       <h1>Login</h1>
//       <div>
//         <input
//           type="text"
//           placeholder="Username"
//           value={username}
//           onChange={(e) => setUsername(e.target.value)}
//         />
//       </div>
//       <div>
//         <input
//           type="password"
//           placeholder="Password"
//           value={password}
//           onChange={(e) => setPassword(e.target.value)}
//         />
//       </div>
//       <div>
//         <button onClick={handleLogin}>Login</button>
//       </div>
//     </div>
//   );
// };

// export default Login;

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const dispatch = useDispatch();

  const handleLogin = async () => {
    try {
      // Make a request to your authentication endpoint
      const response = await axios.post('/auth/login', {
        username,
        password,
      });

      const { accessToken } = response.data;

      // Dispatch the loginSuccess action to store the token in Redux
      dispatch(loginSuccess(accessToken));

      // Redirect or navigate to the secured dashboard
    } catch (error) {
      console.error('Login failed:', error);
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
            <div className="col-12 mb-2">
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
                  onChange={(e) => setUsername(e.target.value)}
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
                  onChange={(e) => setPassword(e.target.value)}
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
              <NavLink to="/landing">
                <button
                  className="btn openhie-primary w-100"
                  id="submit"
                  onClick={handleLogin}
                >
                  Submit
                </button>
              </NavLink>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}
