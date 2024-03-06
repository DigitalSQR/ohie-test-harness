import { Fragment, useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import openhie_logo from "../../../styles/images/logo.png";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { notification } from "antd";
import ReCAPTCHA from "react-google-recaptcha";
import { useFormik } from "formik";
import Captcha from "../../CommonFiles/Captcha/Captcha";
import { useLoader } from "../../loader/LoaderContext";
import "./SignUp.scss"
export default function SignUp() {
  const navigate = useNavigate();
  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      formik.handleSubmit();
    }
  };
  const [captchaInfo, setCaptchaInfo] = useState({
    code: "",
    captcha: ""
  });
  const validate = (values) => {
    const errors = {};

    if (values.email.length == 0) {
      errors.email = "Please enter email.";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email.trim())) {
      errors.email = "Please enter a valid email address";
    }else if(values.email.length > 255){
      errors.email = "Email must have less than 255 characters."
    }

    if (values.password.length == 0) {
      errors.password = "Please enter password.";
    }else if(values.password.length < 6) {
      errors.password = "Password must be of minimum 6 characters"
    }else if(values.password.length > 255) {
      errors.password = "Password must have less than 255 characters."
    }

    if (values.confirmPassword.length == 0) {
      errors.confirmPassword = "Please enter Confirm password.";
    }else if(values.confirmPassword.length < 6) {
      errors.confirmPassword = "Confirm Password must be of minimum 6 characters"
    }else if(values.confirmPassword.length > 255) {
      errors.confirmPassword = "Confirm must have less than 255 characters."
    }

    if (values.name.length == 0) {
      errors.name = "Please enter name.";
    } else if(values.name.length > 1000) {
      errors.password = "Password must have less than 1000 characters."
    }

    if (values.companyName.length == 0) {
      errors.companyName = "Please enter your company's name.";
    }else if(values.companyName.length > 255) {
      errors.companyName = "Company name must have less than 255 characters."
    }

    return errors;
  };

  const formik = useFormik({
    initialValues: {
      email: "",
      name: "",
      password: "",
      confirmPassword: "",
      companyName: "",
    },
    validate: validate,
    onSubmit: () => {
      if (formik.values.password != formik.values.confirmPassword) {
        notification.error({
          placement: "bottomRight",
          description: "Passwords do not match.",
        });
      } else {
        if (!captchaInfo.code && captchaInfo.captcha) {
          notification.error({
            placement: "bottomRight",
            description: "Invalid captcha",
          });
          return;
        }
        showLoader();
        AuthenticationAPI.signup(formik.values, captchaInfo)
          .then(
            (result) => {
              hideLoader();
              navigate(`/CongratulationsPage/${result.email}
              `);
            }
          )
          .catch((error) => {
            hideLoader();
          });
      }
    },
  });

  const getCaptcha = (code, captcha) => {
      setCaptchaInfo({
        code: code,
        captcha: captcha
      });
  }
  const { showLoader, hideLoader } = useLoader();

  const ClickHandler = () => {
    navigate("/login");
  };

  return (
    <Fragment>
      <div className="container-fluid ps-0">
        <div className="row">
          <div className="col-md-6 col-12 col-sm-12 p-0">
            <div
              className="login-bg"
              style={{ height: "max(100vh,100%)", overflowY: "hidden" }}
            >
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
              <h4 className="my-4">Sign Up</h4>
              <div>
                <div className="custom-input mb-3">
                  <label
                    htmlFor="exampleFormControlInput1"
                    className="form-label"
                  >
                    Name<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-person"></i>
                    </span>
                    <input
                      name="name"
                      value={formik.values.name}
                      type="text"
                      className="form-control border-start-0 ps-0"
                      placeholder="Full name"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      autoComplete="off"
                      autoFocus={true}
                      onKeyDown={handleKeyPress}
                    />
                  </div>
                  {formik.touched.name && formik.errors.name && (
                    <div className="text-danger">{formik.errors.name}</div>
                  )}
                </div>
                <div className="custom-input mb-3">
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
                      name="email"
                      value={formik.values.email}
                      type="text"
                      className="form-control border-start-0 ps-0"
                      placeholder="Email"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      //   className="email"
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      autoComplete="off"
                      onKeyDown={handleKeyPress}

                    />
                  </div>
                  {formik.touched.email && formik.errors.email && (
                    <div className="text-danger">{formik.errors.email}</div>
                  )}
                </div>
                <div className="custom-input mb-3">
                  <label
                    htmlFor="exampleFormControlInput1"
                    className="form-label"
                  >
                    Company<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-building"></i>
                    </span>
                    <input
                      name="companyName"
                      value={formik.values.companyName}
                      type="text"
                      className="form-control border-start-0 ps-0"
                      placeholder="Company name"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onBlur={formik.handleBlur}
                      onChange={formik.handleChange}
                      autoComplete="off"
                      onKeyDown={handleKeyPress}
                    />
                  </div>
                  {formik.touched.companyName && formik.errors.companyName && (
                    <div className="text-danger">
                      {formik.errors.companyName}
                    </div>
                  )}
                </div>
                <div className="custom-input mb-3">
                  <label
                    htmlFor="exampleFormControlInput1"
                    className="form-label"
                  >
                    Password<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-lock"></i>
                    </span>
                    <input
                      name="password"
                      value={formik.values.password}
                      type="password"
                      className="form-control border-start-0 ps-0"
                      placeholder="Password"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onBlur={formik.handleBlur}
                      onChange={formik.handleChange}
                      autoComplete="off"
                      onKeyDown={handleKeyPress}
                    />
                  </div>
                  {formik.touched.password && formik.errors.password && (
                    <div className="text-danger">{formik.errors.password}</div>
                  )}
                </div>
                <div className="custom-input mb-3">
                  <label
                    htmlFor="exampleFormControlInput1"
                    className="form-label"
                  >
                    Confirm Password<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-lock"></i>
                    </span>
                    <input
                      name="confirmPassword"
                      type="password"
                      className="form-control border-start-0 ps-0"
                      placeholder="Confirm Password"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onBlur={formik.handleBlur}
                      onChange={formik.handleChange}
                      value={formik.values.confirmPassword}    
                      autoComplete="off"
                      onKeyDown={handleKeyPress}
                    />
                  </div>
                  {formik.touched.confirmPassword && formik.errors.confirmPassword && (
                    <div className="text-danger">{formik.errors.confirmPassword}</div>
                   )} 
                </div>
                <Captcha getCaptcha={getCaptcha} />
                <div className="my-3">
                  <button
                    disabled={!(formik.isValid && formik.dirty)}
                    className="btn btn-primary btn-blue w-100 mt-2 signup-button"
                    onClick={formik.handleSubmit}
                  >
                    Sign Up
                  </button>
                </div>

                <div className="text-center mb-5">
                  Already have an account?{" "}
                  <a
                    onClick={ClickHandler}
                    className="font-weight-500 ps-2 text-blue"
                    href="#"
                  >
                    SIGN IN
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