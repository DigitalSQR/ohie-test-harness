import { Fragment } from "react";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { useLoader } from "../../loader/LoaderContext";
import openhie_logo from "../../../styles/images/logo.png";
import { notification } from "antd";
import { useNavigate } from "react-router-dom";
import { useFormik } from "formik";
import "./forgotPassword.scss";

export default function ForgotPassword() {
  const { showLoader, hideLoader } = useLoader();
  const navigate = useNavigate();

  const validate = (values) => {
    const errors = {};
    if (values.enteredEmail.length === 0) {
      errors.enteredEmail = "Please enter your registered email";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.enteredEmail.trim())) {
      errors.enteredEmail = "Please enter a valid email address";
    }
    return errors;
  };

  const formik = useFormik({
    initialValues: {
      enteredEmail: "",
    },
    validate: validate,
  });

  const VerifyEmail = () => {
    showLoader();
    AuthenticationAPI.forgotpassword(formik.values.enteredEmail)
      .then((response) => {
        var data = response.data;
        notification.success({
          className:"notificationSuccess",
          placement: "top",
          message:"Success",
          description: `${data?.message}`,
        });
        hideLoader();
        navigate("/login");
      })
      .catch((error) => {       
        hideLoader();
      });
  };

  const backToLogin = () => {
    navigate("/login");
  };

  return (
    <Fragment>
      <div className="container-fluid ps-0">
        <div className="row">
          <div className="col-md-6 col-12 col-sm-12 p-0">
            <div className="login-bg">
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

          <div className="col-md-6 col-12 col-sm-12">
            <div className="login-form-bg pt-5 mt-5">
              <div className="text-center">
                <img src={openhie_logo} />
              </div>
              <h3 className="my-4 formTitle">Forgot Password</h3>
              <div className="custom-input mb-3">
                <label htmlFor="Password" className="form-label">
                  Registered Email
                </label>
                <div className="input-group">
                  <span className="input-group-text">
                  <i className="bi bi-envelope"></i>
                  </span>
                  <input
                    type="email"
                    className="form-control border-start-0 ps-0"
                    name="enteredEmail"
                    id="email"
                    autoComplete="off"
                    value={formik.values.enteredEmail}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    placeholder="Email"
                    aria-label="Email"
                  />
                </div>
                {formik.touched.enteredEmail && formik.errors.enteredEmail && (
                  <div className="text-danger">
                    {formik.errors.enteredEmail}
                  </div>
                )}
              </div>

              <div className="buttonWrapper">
                <button
                  disabled={!(formik.isValid && formik.dirty)}
                  id="submitButton"
                  onClick={VerifyEmail}
                  className="btn btn-blue forgot-button"
                >
                  <span>Continue</span>
                  <span id="loader"></span>
                </button>
              </div>
              <div className="text-center mt-4">
                <a
                    onClick={backToLogin}
                    className=" mx-2 back-button"
                  >
                    <span>Back to Login</span>
                    <span id="loader"></span>
                  </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}
