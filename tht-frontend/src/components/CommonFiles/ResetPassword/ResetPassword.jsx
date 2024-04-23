import { useFormik } from "formik";
import { useNavigate } from "react-router-dom";
import { UserAPI } from "../../../api/UserAPI";
import { notification } from "antd";
import { useState } from "react";
import "./resetPassword.scss";
export default function ResetPassword() {
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const validate = (values) => {
    const errors = {};
    if (values.newPassword.length === 0) {
      errors.newPassword = "Please enter your new password.";
    }else if(values.newPassword.length < 6) {
      errors.newPassword = "Password must be of minimum 6 characters"
    }
    else if(values.newPassword.length > 255) {
      errors.newPassword = "Password must have less than 255 characters."
    }
    if(values.confirmPassword !== values.newPassword) {
      errors.confirmPassword = "Confirm password does not match"
    }
    return errors;
  };
  const formik = useFormik({
    initialValues: {
      oldPassword: null,
      newPassword: "",
      confirmPassword: "",
    },
    validate: validate,
    onSubmit: (values) => {
      const { confirmPassword, ...reqBody } = values;
      UserAPI.resetPassword(reqBody)
        .then((response) => {
          notification.success({
            className:"notificationSuccess",
            placement: "top",
            message:"Success",
            description: "Password has been reset successfully!",
          });
          navigate("/dashboard");
        })
        .catch((error) => {
          
        });
    },
  });
  const navigate = useNavigate();
  return (
    <div id="resetPassword">
    <div id="wrapper">
      <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
        <div className="form-bg-white">
          <span className="heading-line-up">Reset Password</span>
          <div className="row">
            <div className="col-12">
              <div className="custom-input  mb-3 input-group" style={{}}>
                <label htmlFor="oldPassword" className="form-label input-group ">
                  <b>Old Password (Empty for Google Login)</b>
                </label>
                <input
                  type={showOldPassword ? "text" : "password"}
                  className="form-control input-group"
                  style={{ borderRadius: "6px 0px 0px 6px" }}
                  id="oldPassword"
                  placeholder="Old Password"
                  name="oldPassword"
                  value={formik.values.oldPassword}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                />
                <button
                  className=" btn btn-outline-secondary color"
                  type="button"
                  onClick={() => setShowOldPassword(!showOldPassword)}
                >
                  <i
                    className={`bi ${
                      showOldPassword ? "bi-eye-slash" : "bi-eye"
                    }`}
                  ></i>
                </button>
              </div>
            </div>
          </div>
          <div className="row">
            <div className="col-12 mb-3">
              <div className="custom-input input-group">
                <label htmlFor="password" className="form-label input-group">
                  <b>New Password</b>
                </label>
                <input
                  type={showNewPassword ? "text" : "password"}
                  className="form-control"
                  style={{ borderRadius: "6px 0px 0px 6px" }}
                  id="newPassword"
                  placeholder="New Password"
                  name="newPassword"
                  value={formik.values.newPassword}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                />
                <button
                  className="btn btn-outline-secondary color"
                  type="button"
                  onClick={() => setShowNewPassword(!showNewPassword)}
                >
                  <i
                    className={`bi ${
                      showNewPassword ? "bi-eye-slash" : "bi-eye"
                    }`}
                  ></i>
                </button>
              </div>
              {formik.touched.newPassword && formik.errors.newPassword && (
                <div className="text-danger position-flex">
                  {formik.errors.newPassword}
                </div>
              )}
            </div>
          </div>
          <div className="row">
            <div className="col-12 mb-3">
              <div className="custom-input input-group">
                <label htmlFor="confirmPassword" className="form-label input-group">
                  <b>Confirm New Password</b>
                </label>
                <input
                  type={showConfirmPassword ? "text" : "password"}
                  className="form-control"
                  style={{ borderRadius: "6px 0px 0px 6px" }}
                  id="confirmPassword"
                  placeholder="Confirm New Password"
                  name="confirmPassword"
                  value={formik.values.confirmPassword}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                />
                <button
                  className="btn btn-outline-secondary color"
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                >
                  <i
                    className={`bi ${
                      showConfirmPassword ? "bi-eye-slash" : "bi-eye"
                    }`}
                  ></i>
                </button>
                
              </div>
              {formik.touched.confirmPassword && formik.errors.confirmPassword && (
                <div className="text-danger position-flex">
                  {formik.errors.confirmPassword}
                </div>
              )}
              
            </div>
          </div>
          <div className="my-4 text-end">
            <button
              className="btn btn-primary btn-white font-size-14"
              style={{marginRight:"1rem"}}
              onClick={() => {
                navigate("/dashboard");
                formik.resetForm();
              }}
            >
              Cancel
            </button>
            <button
              disabled={!(formik.isValid && formik.dirty)}
              onClick={formik.handleSubmit}
              className="btn btn-primary btn-blue btn-submit font-size-14"
              type="submit"
            >
              Reset
            </button>
          </div>
        </div>
      </div>
    </div>
    </div>
  );
}
