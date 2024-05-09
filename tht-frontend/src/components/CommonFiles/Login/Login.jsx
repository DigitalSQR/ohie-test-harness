import React, { useState, Fragment, useEffect } from "react";
import { useDispatch } from "react-redux";
import "./login.scss";
// import "./_buttons.scss";
import openhie_logo from "../../../styles/images/logo.png";
import { useNavigate } from "react-router-dom";
import {
  login_success,
  setIsKeepLoginState,
} from "../../../reducers/authReducer";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { notification } from "antd";
import { setAuthToken } from "../../../api/configs/axiosConfigs";
import { setDefaultToken } from "../../../api/configs/axiosConfigs";
import { useSelector } from "react-redux";
import { useLoader } from "../../loader/LoaderContext";
import ReCAPTCHA from "react-google-recaptcha";
import GoogleLoginIcon from "../../../styles/images/GoogleLoginIcon.png";
import { UserAPI } from "../../../api/UserAPI";
import { userinfo_success } from "../../../reducers/UserInfoReducer";
import { useFormik } from "formik";
import Captcha from "../Captcha/Captcha";
export default function Login() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const isKeepFromState = useSelector((state) => state.authSlice.isKeepLogin);
  const [isKeepLogin, setIsKeepLogin] = useState(false);
  const { showLoader, hideLoader } = useLoader();
  const [showPassword, setShowPassword] = useState(false);
  const [captchaInfo, setCaptchaInfo] = useState({
    code: "",
    captcha: ""
  });

  useEffect(() => {
    setDefaultToken();
    setKeepMeLoginFromState();
  }, []);

  const redirectToSignUp = async () => {
    navigate("/SignUp");
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

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      formik.handleSubmit();
    }
  };

  const validate = (values) => {
    const errors = {};
  
    const trimmedUsername = values.username.trim();
    const trimmedPassword = values.password.trim();
  
    if (!trimmedUsername) {
      errors.username = "Please enter your email.";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmedUsername)) {
      errors.username = "Please enter a valid email address";
    } else if (trimmedUsername.length > 255) {
      errors.username = "Email must have less than 255 characters.";
    }
  
    if (!trimmedPassword) {
      errors.password = "Please enter password.";
    } else if (trimmedPassword.length < 6) {
      errors.password = "Password must be at least 6 characters";
    } else if (trimmedPassword.length > 255) {
      errors.password = "Password must have less than 255 characters.";
    }
  
    return errors;
  };
  

  const getCaptcha=(code,captcha)=>{
      setCaptchaInfo({
        code: code,
        captcha: captcha,
      });
    }

  const formik = useFormik({
    initialValues: {
      username: "",
      password: "",
      grant_type: "password",
    },
    validate: validate,
    onSubmit: async () => {
      if (!captchaInfo.code && captchaInfo.captcha) {
        notification.error({
          className:"notificationError",
          message: "Invalid captcha",
          placement: "bottomRight",
        });
        return;
      }
      showLoader();
      AuthenticationAPI.doLogin(new URLSearchParams(formik.values), captchaInfo)
        .then(
          (response) => {
            dispatch(login_success(response));
            setAuthToken(response.access_token);
            hideLoader();
            UserAPI.viewUser().then((user) => {
              dispatch(userinfo_success(user));
              const redirectUri = localStorage.getItem("redirectUri");
              if (redirectUri) {
                navigate(JSON.parse(redirectUri));
                localStorage.removeItem("redirectUri");
              } else {
                navigate("/dashboard");
              }
            });
          }
        )
        .catch((error) => {
          // Handle the error here
          hideLoader();
        });
    },
  });

  return (
    <div id="login">
      <div className="container-fluid ps-0">
        <div className="row">
          <div className="col-md-6 col-12 col-sm-12 p-0">
            <div className="login-bg">
              <div className="col-10 col-md-11 col-lg-10 col-xl-8 col-xxl-6">
                <h1>Testing Harness Tool</h1>
                <p className="font-size-16 mt-4 text-align">
                  Testing Harness Tool (THT) is your gateway to ensuring the quality and compliance of your healthcare applications against OpenHIE standards.Upon successful testing, THT issues compliance certificates, validating your application's adherence to established benchmarks.
                </p>
                <p className="font-size-16 mt-3 text-align">
                  Log in now to access THT's comprehensive testing capabilities and contribute to the advancement of healthcare application quality and compliance.
                </p>
              </div>
            </div>
          </div>

          <div className="col-md-6 col-12 col-sm-12 d-flex align-items-center">
            <div className="login-form-bg pt-5">
              <div className="text-center">
                <img src={openhie_logo} />
              </div>
              <div className="sm-cut">
                <div
                  className="custom-input mb-4"
                  style={{ position: "relative" }}
                >
                  <label
                    htmlFor="exampleFormControlInput1"
                    className="form-label"
                  >
                    Email<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-envelope"></i>
                    </span>
                    <input
                      type="text"
                      className={"form-control border-start-rounded-0 rounded-end"}
                      name="username"
                      id="exampleFormControlInput1"
                      autoComplete="on"
                      value={formik.values.username}
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      placeholder="Email"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onKeyDown={handleKeyPress}
                      autoFocus={true}
                    />
                    {formik.touched.username && formik.errors.username && (
                      <div className="text-danger position">
                        {formik.errors.username}
                      </div>
                    )}
                  </div>
                </div>
                <div className="custom-input mb-3 password">
                  <label
                    htmlFor="exampleFormControlInput2"
                    className="form-label"
                  >
                    Password<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-lock"></i>
                    </span>
                    <input
                      type={showPassword ? "text" : "password"}
                      className="form-control "
                      name="password"
                      id="exampleFormControlInput2"
                      autoComplete="off"
                      value={formik.values.password}
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      placeholder="Password"
                      aria-label="Password"
                      aria-describedby="basic-addon1"
                      onKeyDown={handleKeyPress}
                    />
                    <button
                      className="btn btn-outline-secondary login"
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      <i
                        className={`bi ${
                          showPassword ? "bi-eye-slash" : "bi-eye"
                        }`}
                      ></i>
                    </button>
                  </div>
                  {formik.touched.password && formik.errors.password && (
                    <div className="text-danger">{formik.errors.password}</div>
                  )}
                </div>
                <Captcha getCaptcha={getCaptcha} />
                <div className="d-flex justify-content-between">
                  <label className="custom-checkbox">
                    Remember me
                    <input
                      className="form-check-input"
                      type="checkbox"
                      id="inlineCheckbox1"
                      checked={isKeepLogin}
                      onChange={setOrUnsetKeepMeLogin}
                    />
                    <span className="checkmark"></span>
                  </label>
                  <a
                    href=""
                    className="text-blue"
                    onClick={() => {
                      navigate("/forgotpassword");
                    }}
                  >
                    Forgot Password?
                  </a>
                </div>

                <div className="my-4">
                  <button
                    disabled={!(formik.isValid && formik.dirty)}
                    type="submit"
                    onClick={formik.handleSubmit}
                    className="btn btn-primary btn-blue w-100 login-button"
                  >
                    Login
                  </button>
                  <h6 className="m-2 align">OR</h6>
                  <h4 className="align">
                    <a
                      href="/api/oauth2/authorization/google"
                      className="login-google"
                    >
                      <img src={GoogleLoginIcon} />
                      Login with Google
                    </a>
                  </h4>
                </div>
                <div className="text-center mb-3">
                  <a
                    href=""
                    onClick={redirectToSignUp}
                    className="font-weight-500 ps-2 pt-4 d-block text-blue"
                  >
                    Click Here to Register{" "}
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
