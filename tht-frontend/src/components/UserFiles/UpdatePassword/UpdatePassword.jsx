import { Fragment, useEffect, useState } from "react";
import openhie_logo from "../../../styles/images/logo.png";
import { useNavigate, useParams } from "react-router-dom";
import { notification } from "antd";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { useLoader } from "../../loader/LoaderContext";
import { useFormik } from "formik";

export default function UpdatePassword() {
  const { base64TokenId } = useParams();
  const { base64UserEmail } = useParams();
  const { showLoader, hideLoader } = useLoader();
  const [confirmPassword, setConfirmPassword] = useState();
  const [passwordMatch,setPasswordMatch] = useState(false);
  const navigate = useNavigate();

  const validate = (values) => {
    const errors = {};
    if (values.newPassword.length == 0) {
      errors.newPassword = "Please enter your new password";
    }
    return errors;
  };

  const formik = useFormik({
    initialValues: {
      newPassword: "",
      base64UserEmail,
      base64TokenId,
    },
    validate: validate,
    onSubmit: () => {
      if (confirmPassword == formik.values.newPassword) {
        showLoader();
        AuthenticationAPI.resetPassword(formik.values)
          .then((response) => {
            var data = response.data;
            notification.success({
              placement: "bottomRight",
              description: `${data?.message}`,
            });
            hideLoader();
            navigate("/login");
          })
          .catch((error) => {           
            hideLoader();
          });
      } else {
        clearPasswords();
        notification.error({
          placement: "bottomRight",
          description: `Confirm password does not match with the password.`,
        });
      }
    },
  });

  const clearPasswords = () => {
    formik.values.newPassword = "";
  };

  const backToLogin = () => {
    navigate("/login");
  };

  useEffect(() => {
  }, []);

  const handleConfirmPassword = (e) => {
    setConfirmPassword(e.target.value);
    if(e.target.value === formik.values.newPassword){
      setPasswordMatch(true);
    }else{
      setPasswordMatch(false);
    }
  }

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
            <div className="login-form-bg pt-5">
              <div className="text-center">
                <img src={openhie_logo} />
              </div>

              <h2 className="my-4 formTitle">Reset Password</h2>

              <div className="custom-input mb-3">
                <label htmlFor="Password" className="form-label">
                  Password<span style={{ color: "red" }}>*</span>
                </label>
                <div className="input-group">
                  <span className="input-group-text">
                    <i className="bi bi-lock"></i>
                  </span>
                  <input
                    type="password"
                    className="form-control border-start-0 ps-0"
                    name="newPassword"
                    id="Password"
                    autoComplete="off"
                    value={formik.values.newPassword}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    placeholder="Password"
                    aria-label="Password"
                  />
                </div>
                {formik.touched.newPassword && formik.errors.newPassword && (
                  <div className="text-danger">{formik.errors.newPassword}</div>
                )}
              </div>

              <div className="custom-input mb-3">
                <label htmlFor="confirmPassword" className="form-label">
                  Confirm New Password<span style={{ color: "red" }}>*</span>
                </label>
                <div className="input-group">
                  <span className="input-group-text">
                    <i className="bi bi-lock"></i>
                  </span>
                  <input
                    type="password"
                    className="form-control border-start-0 ps-0"
                    name="confirmPassword"
                    id="confirmPassword"
                    autoComplete="off"
                    value={confirmPassword}
                    onChange={handleConfirmPassword}
                    placeholder="Confirm New Password"
                    aria-label="Confirm New Password"
                  />
                </div>
              </div>

              <div className="buttonWrapper">
                <button
                disabled={!(formik.isValid && formik.dirty && passwordMatch)}
                  onClick={formik.handleSubmit}
                  className="btn btn-primary"
                >
                  <span>Continue</span>
                  <span id="loader"></span>
                </button>
                <button
                  id="submitButton"
                  onClick={backToLogin}
                  className=" mx-2 btn btn-primary"
                >
                  <span>Back to Login</span>
                  <span id="loader"></span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}
