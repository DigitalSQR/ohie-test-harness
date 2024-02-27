import { Fragment, useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import openhie_logo from "../../styles/images/logo.png";
import capture_logo from "../../styles/images/capture-logo.png";
import { AuthenticationAPI } from "../../api/AuthenticationAPI";
import { useLoader } from "../loader/LoaderContext";
import { notification } from "antd";
import ReCAPTCHA from "react-google-recaptcha";
import { useFormik } from "formik";
import Captcha from "../CommonFiles/Captcha/Captcha";
export default function SignUp() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    name: "",
    password: "",
    companyName: "",
  });
  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      formik.handleSubmit();
    }
  };
  const [confirmPassword, setconfirmPassword] = useState("");
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
    }

    if (values.password.length == 0) {
      errors.password = "Please enter password.";
    }

    if (values.name.length == 0) {
      errors.name = "Please enter name.";
    }

    if (values.companyName.length == 0) {
      errors.companyName = "Please enter your company's name.";
    }

    return errors;
  };

  const formik = useFormik({
    initialValues: {
      email: "",
      name: "",
      password: "",
      companyName: "",
    },
    validate: validate,
    onSubmit: () => {
      if (formik.values.password != confirmPassword) {
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
            },
            (result) => {
              hideLoader();
              const messages = result.response.data.map((res) => res.message);
              const MessageList = () => (
                <div>
                  {messages.map((message, index) => (
                    <li key={index}>{message}</li>
                  ))}
                </div>
              );

              notification.error({
                placement: "bottomRight",
                message: "Error",
                description: <MessageList />,
              });
            }
          )
          .catch((error) => {
            hideLoader();
            notification.error({
              placement: "bottomRight",
              description:
                error.message !== undefined
                  ? `${error.message}`
                  : `Oops something went wrong`,
            });
          });
      }
    },
  });

  const getCaptcha = (code, captcha) => {
    if (captcha) {
      setCaptchaInfo({
        code: code,
        captcha: captcha
      });
    }
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
                      type="password"
                      className="form-control border-start-0 ps-0"
                      placeholder="Confirm Password"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onChange={(e) => setconfirmPassword(e.target.value)}
                      autoComplete="off"
                      onKeyDown={handleKeyPress}
                    />
                  </div>
                </div>
                <Captcha getCaptcha={getCaptcha} />
                <div className="my-3">
                  <button
                    disabled={!(formik.isValid && formik.dirty)}
                    className="btn btn-primary btn-blue w-100 mt-2"
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