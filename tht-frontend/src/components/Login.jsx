import React, { useState, Fragment, useEffect } from "react";
import { useDispatch } from "react-redux";
import "../styles/Login.css";
import openhie_logo from "../styles/img/openhie-logo.png";
import { useNavigate } from "react-router-dom";
import { login_success, setIsKeepLoginState } from "../reducers/authReducer";
import { AuthenticationAPI } from "../api/AuthenticationAPI";
import { notification } from "antd";
import { setAuthToken } from "../api/configs/axiosConfigs";
import { setDefaultToken } from "../api/configs/axiosConfigs";
import { useSelector } from "react-redux";
import { useLoader } from "../components/loader/LoaderContext";
import ReCAPTCHA from "react-google-recaptcha";
export default function Login() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const isKeepFromState = useSelector((state) => state.authSlice.isKeepLogin);
  const [isKeepLogin, setIsKeepLogin] = useState(false);
  const [captcha, setCaptcha] = useState(false);

  // Initialize error state
  //const isKeepLogin = useSelector((state) => state.authSlice.isKeepLogin);

  const [formData, setFormData] = useState({
    username: "",
    password: "",
    grant_type: "password", // Assuming 'password' grant type
  });
  const { showLoader, hideLoader } = useLoader();

  useEffect(() => {
    setDefaultToken();
    setKeepMeLoginFromState();
  }, []);
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };
  //server side api call
  // const captchaResponse = async(recaptchaValue) => {
  //   try {
  //     const response = await axios.post('https://www.google.com/recaptcha/api/siteverify', null, {
  //       params: {
  //         secret: 'YOUR_RECAPTCHA_SECRET_KEY',
  //         response: recaptchaValue
  //       }
  //     });

  //     if (response.data.success) {
  //       console.log(response)
  //    setCaptcha(true);
  //     } else {
  //       setCaptcha(false);
  //     }
  //   } catch (error) {
  //     console.log(error);
  //   }
  // };
  //server side api calls 
  const handleLogin = async () => {
    showLoader();
    AuthenticationAPI.doLogin(new URLSearchParams(formData))
      .then((response) => {
        dispatch(login_success(response));
        setAuthToken(response.access_token);

        hideLoader();
        navigate("/dashboard/user");
      })
      .catch((error) => {
        // Handle the error here
        hideLoader();
        notification.error({
          placement: "bottomRight",
          description: "Invalid username or password",
        });
      });
  };

  const setOrUnsetKeepMeLogin = (event) => {
    const { checked } = event.target;
    setIsKeepLogin(checked);
    dispatch(setIsKeepLoginState(checked));
  };

  const setKeepMeLoginFromState = () => {
    if (isKeepFromState && isKeepFromState === true) {
      setIsKeepLogin(true);
    } else {
      setIsKeepLogin(false);
    }
    dispatch(setIsKeepLoginState(isKeepLogin));
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
            {error && <p style={{ color: "red" }}>{error}</p>}{" "}
            {/* Display error message if it exists */}
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
                  checked={isKeepLogin}
                  onChange={setOrUnsetKeepMeLogin}
                />
                <span className="form-check-label" htmlFor="inlineCheckbox1">
                  Keep me signed in
                </span>
              </div>
            </div>
            <div className="col-md-6 mb-2 text-end">
              <span style={{paddingRight:'20px'}}>
                Forgot Password?
              </span>
            </div>
            <div className="col-12">
              <div style={{ marginLeft: "50px", marginBlock: "20px" }}>
                <ReCAPTCHA
                  sitekey="6Lf8OrIoAAAAAAKT2bArym6y1lrkkuoVVpIN0uXf"
                  onChange={()=>{setCaptcha(true)}}
                />
              </div>
              <button
                disabled={!captcha}
                className="btn openhie-primary w-100"
                id="submit"
                onClick={handleLogin}
              >
                Submit
              </button>
              <h6 style={{textAlign:'center'}}>OR</h6>
              <h4  style={{textAlign:'center'}}><a href="/login/oauth2/authorization/google">Login with Google</a></h4>
              <h5 style={{textAlign:'center'}}><a href="/register">New User? Click Here</a></h5>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}
