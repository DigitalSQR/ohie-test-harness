import { Fragment, useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import argusoft_logo from "../../../styles/images/argusoft-logo-outer.png";
import openhie_logo from "../../../styles/images/logo.png";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { notification } from "antd";
import ReCAPTCHA from "react-google-recaptcha";
import { useFormik } from "formik";
import * as Yup from "yup";
import Captcha from "../../CommonFiles/Captcha/Captcha";
import { useLoader } from "../../loader/LoaderContext";
import "./SignUp.scss"
export default function SignUp() {
  const navigate = useNavigate();
  const [captchaInfo, setCaptchaInfo] = useState({
    code: "",
    captcha: ""
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [enterPressed,setEnterPressed]=useState(false);
  const validationSchema = Yup.object({
    name: Yup.string()
      .trim()
      .required("Name is required")
      .max(1000, "Name must have less than 1000 characters"),
    email: Yup.string()
      .trim()
      .email("Invalid email address")
      .required("Email is required")
      .max(255, "Email must have less than 255 characters"),
    password: Yup.string()
      .trim()
      .required("Password is required")
      .min(6, "Password must be at least 6 characters")
      .max(255, "Password must have less than 255 characters"),
    confirmPassword: Yup.string()
      .trim()
      .required("Confirm password is required")
      .oneOf(
        [Yup.ref("password"), null],
        "Confirm password does not match with the password."
      ),
    companyName: Yup.string()
      .trim()
      .required("Please enter your company's name.")
      .max(255, "Company name must have less than 255 characters"),
  });

  const formik = useFormik({
    initialValues: {
      email: "",
      name: "",
      password: "",
      confirmPassword: "",
      companyName: "",
    },
    validationSchema: validationSchema,
    onSubmit: () => {
      const trimmedValues = {
        ...formik.values,
        email: formik.values.email.trim(),
        name: formik.values.name.trim(),
        password: formik.values.password.trim(),
        confirmPassword: formik.values.confirmPassword.trim(),
        companyName: formik.values.companyName.trim(),
      };
  
      if (trimmedValues.password !== trimmedValues.confirmPassword) {
        notification.error({
          className: "notificationError",
          message: "Confirm password does not match with the password.",
          placement: "bottomRight",
        });
      } else {
        if (!captchaInfo.code && captchaInfo.captcha) {
          notification.error({
            className: "notificationError",
            message: "Invalid captcha",
            placement: "bottomRight",
          });
          return;
        }
        showLoader();
        AuthenticationAPI.signup(trimmedValues, captchaInfo)
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

  const handleConfirmPasswordKeyDown = (event) => {
    if (event.key === "Enter") {
      setEnterPressed(true); // Set state to indicate Enter key was pressed
      formik.validateField("confirmPassword"); // Trigger validation for confirmPassword field
    }
  };

  const { showLoader, hideLoader } = useLoader();

  const ClickHandler = () => {
    navigate("/login");
  };

  return (
    <Fragment>
      <div id="signUp" className="container-fluid ps-0">
        <div className="row">
          <div className="col-md-6 col-12 col-sm-12 p-0">
            <div
              className="login-bg"
              style={{ height: "max(100vh,100%)", overflowY: "hidden" }}
            >
              <div className="col-10 col-md-11 col-lg-10 col-xl-8 col-xxl-6">
                <h1>Testing Harness Test Automation</h1>
                <p className="font-size-16 mt-3">
                  Experience streamlined OpenHIE standards compliance verification
                  for healthcare websites. Our tool ensures precision,
                  simplifies complexities, and empowers your projects.
                </p>
              </div>
            </div>
          </div>

          <div className="col-md-6 col-12 col-sm-12 d-flex align-items-center position-relative">
            <div className="login-form-bg pt-5" style={{ minHeight: "100vh" }}>
              <div className="text-center">
                <img src={openhie_logo} />
              </div>
              <h4 className="my-4">Sign Up</h4>
                <form onSubmit={formik.handleSubmit}>
                <div className="custom-input mb-3">
                  <label
                    htmlFor="name"
                    className="form-label"
                    id="#signUp-name"
                  >
                    Name<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-person"></i>
                    </span>
                    <input
                      name="name"
                      id="signup-name"
                      value={formik.values.name}
                      type="text"
                      className={`form-control ${
                        formik.touched.name && formik.errors.name
                          ? "is-invalid"
                          : ""
                      }`}
                      placeholder="Full name"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      autoComplete="off"
                      autoFocus={true}
                    />
                  </div>
                  {formik.touched.name && formik.errors.name && (
                    <div className="text-danger">{formik.errors.name}</div>
                  )}
                </div>
                <div className="custom-input mb-3">
                  <label
                    htmlFor="email"
                    className="form-label"
                    id="#signup-email"
                  >
                    Email<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-envelope"></i>
                    </span>
                    <input
                      name="email"
                      id="signup-email"
                      value={formik.values.email}
                      type="email"
                      className={`form-control ${
                        formik.touched.email && formik.errors.email
                          ? "is-invalid"
                          : ""
                      }`}
                      placeholder="Email"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      //   className="email"
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      autoComplete="off"

                    />
                  </div>
                  {formik.touched.email && formik.errors.email && (
                    <div className="text-danger">{formik.errors.email}</div>
                  )}
                </div>
                <div className="custom-input mb-3">
                  <label
                    htmlFor="companyName"
                    className="form-label"
                    id="signup-company"
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
                      id="signUp-companyName"
                      type="text"
                      className={`
                      form-control ${
                        formik.touched.companyName && formik.errors.companyName
                          ? "is-invalid"
                          : ""
                      }`}
                      placeholder="Company name"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onBlur={formik.handleBlur}
                      onChange={formik.handleChange}
                      autoComplete="off"
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
                    htmlFor="password"
                    className="form-label"
                    id="signup-password"
                  >
                    Password<span style={{ color: "red" }}>*</span>
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-lock"></i>
                    </span>
                    <input
                      name="password"
                      id="signup-password"
                      value={formik.values.password}
                      type={showPassword ? "text" : "password"}
                      className={`form-control ${
                        formik.touched.password && formik.errors.password
                          ? "is-invalid"
                          : ""
                      }`}
                      placeholder="Password"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onBlur={formik.handleBlur}
                      onChange={formik.handleChange}
                      autoComplete="off"
                    />
                    <button
                      className="btn btn-outline-secondary signup-password"
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      id="#signup-showPassword"
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
                <div className="custom-input mb-3">
                  <label
                    htmlFor="confirmPassword"
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
                      id="signup-confirmPassword"
                      type={showConfirmPassword ? "text" : "password"}
                      className={`form-control ${
                        formik.touched.confirmPassword &&
                        formik.errors.confirmPassword
                          ? "is-invalid"
                          : ""
                      }`}
                      placeholder="Confirm Password"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onBlur={formik.handleBlur}
                      onChange={formik.handleChange}
                      value={formik.values.confirmPassword}    
                      onKeyDown={handleConfirmPasswordKeyDown}
                      autoComplete="off"
                    />
                    <button
                      className="btn btn-outline-secondary signup-password"
                      type="button"
                      id="#signup-confirmPassword"
                      onClick={() =>
                        setShowConfirmPassword(!showConfirmPassword)
                      }
                    >
                      <i
                        className={`bi ${
                          showConfirmPassword ? "bi-eye-slash" : "bi-eye"
                        }`}
                      ></i>
                    </button>
                    </div>
                    {(formik.touched.confirmPassword || enterPressed ) && formik.errors.confirmPassword && (
                      <div className="text-danger">
                        {formik.errors.confirmPassword}
                      </div>
                    )}
                </div>
                <Captcha getCaptcha={getCaptcha} />
                <div className="my-3">
                  <button
                    id="signUp-btn"
                    disabled={!(formik.isValid && formik.dirty)}
                    className="btn btn-primary btn-blue w-100 mt-2 signup-button"
                  >
                    Sign Up
                  </button>
                </div>
                </form>
                <div className="text-center mb-5 bottom-margin-btn" >
                  Already have an account?{" "}
                  <a
                    id="signIn-btn"
                    onMouseDown={ClickHandler}
                    className="font-weight-500 ps-2 text-blue"
                    href="#"
                  >
                    SIGN IN
                  </a>
                </div>
                  <div className="text-center mt-auto pb-2 position-absolute start-0 end-0 bottom-0">
                    <a href="https://www.argusoft.com/" target="_blank">
                      <img src={argusoft_logo} alt="Argusoft Logo" />
                    </a>
                  </div>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}