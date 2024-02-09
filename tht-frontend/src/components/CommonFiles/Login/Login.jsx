import React, { useState, Fragment, useEffect } from "react";
import { useDispatch } from "react-redux";
import "./Login.css";
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
export default function Login() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const isKeepFromState = useSelector((state) => state.authSlice.isKeepLogin);
  const [isKeepLogin, setIsKeepLogin] = useState(false);
  const { showLoader, hideLoader } = useLoader();
  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    setDefaultToken();
    setKeepMeLoginFromState();
  }, []);

  const handleLogin = async () => {};

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

    if (values.username.length == 0) {
      errors.username = "Please enter Username.";
    }

    if (values.password.length == 0) {
      errors.password = "Password can't be empty.";
    }

    return errors;
  };

  const formik = useFormik({
    initialValues: {
      username: "",
      password: "",
      grant_type: "password",
    },
    validate,
    onSubmit: async () => {
      showLoader();
      AuthenticationAPI.doLogin(new URLSearchParams(formik.values))
        .then(
          (response) => {
            dispatch(login_success(response));
            setAuthToken(response.access_token);
            hideLoader();
            UserAPI.viewUser().then((user) => {
              dispatch(userinfo_success(user));
              navigate("/dashboard");
            });
          },
          (response) => {
            hideLoader();
            console.log(response);
            notification.error({
              placement: "bottomRight",
              description: `${response.response.data.error_description}`,
            });
          }
        )
        .catch((error) => {
          // Handle the error here
          hideLoader();
          console.log(error);
          notification.error({
            placement: "bottomRight",
            description: "Invalid username or password",
          });
        });
    },
  });

  return (
    <Fragment>
      <div className="container-fluid ps-0">
        <div className="row">
          <div className="col-md-6 col-12 col-sm-12 p-0">
            <div className="login-bg">
              <div className="col-10 col-md-11 col-lg-10 col-xl-8 col-xxl-6">
                <h1>Testing Harness Test Automation</h1>
                <p className="font-size-16 mt-3">
                  Experience streamlined OpenHIE standards compliance testing
                  for healthcare websites. Our tool ensures precision,
                  simplifies complexities, and empowers your projects.
                </p>
              </div>
            </div>
          </div>

          <div className="col-md-6 col-12 col-sm-12">
            <div className="login-form-bg pt-5">
              <div className="text-center">
                <img src={openhie_logo} />
              </div>
              <div className="custom-scrollbar sm-cut">
                <div className="custom-input mb-3">
                  <label
                    htmlFor="exampleFormControlInput1"
                    className="form-label"
                  >
                    Email
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-envelope"></i>
                    </span>
                    <input
                      type="text"
                      className="form-control border-start-0 ps-0"
                      name="username"
                      id="exampleFormControlInput1"
                      autoComplete="off"
                      value={formik.values.username}
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      placeholder="Email"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onKeyDown={handleKeyPress}
                    />
                  </div>
                </div>
                <div className="custom-input mb-3">
                  <label
                    htmlFor="exampleFormControlInput2"
                    className="form-label"
                  >
                    Password
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-lock"></i>
                    </span>
                    <input
                      type={showPassword ? "text" : "password"}
                      className="form-control border-start-0 ps-0"
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
                      className="btn btn-outline-secondary"
                      type="button"
                      style={{ borderColor: "#ccdade" }}
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      <i
                        className={`bi ${
                          showPassword ? "bi-eye-slash" : "bi-eye"
                        }`}
                      ></i>
                    </button>
                  </div>
                </div>

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
                    onClick={() => {
                      navigate("/forgotpassword");
                    }}
                  >
                    Reset Password
                  </a>
                </div>

                <div className="my-4">
                  <button
                  type="submit"
                    onClick={formik.handleSubmit}
                    className="btn btn-primary btn-blue w-100"
                  >
                    Login
                  </button>
                  <h6 style={{ textAlign: "center" }} className="m-2">
                    OR
                  </h6>
                  <h4 style={{ textAlign: "center" }}>
                    <a href="/api/oauth2/authorization/google">
                      <img src={GoogleLoginIcon} />
                      {/* Login with Google */}
                    </a>
                  </h4>
                </div>
                <div className="text-center">
                  <a
                    href=""
                    onClick={redirectToSignUp}
                    className="font-weight-500 ps-2 text-blue"
                  >
                    Click Here to Register{" "}
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}
