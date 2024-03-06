import { useFormik } from "formik";
import { useNavigate } from "react-router-dom";
import { UserAPI } from "../../../api/UserAPI";
import { notification } from "antd";
import { useState } from "react";

export default function ResetPassword() {
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);

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

    return errors;
  };
  const formik = useFormik({
    initialValues: {
      oldPassword: "",
      newPassword: "",
    },
    validate: validate,
    onSubmit: (values) => {
      UserAPI.resetPassword(values)
        .then((response) => {
          notification.success({
            description: "Password changed successfully",
            placement: "bottomRight",
          });
          navigate("/dashboard");
        })
        .catch((error) => {
          
        });
    },
  });
  const navigate = useNavigate();
  return (
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
                  placeholder="Old password"
                  name="oldPassword"
                  value={formik.values.oldPassword}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                />
                <button
                  className=" btn btn-outline-secondary login"
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
            <div className="col-12">
              <div className="custom-input input-group mb-3">
                <label htmlFor="password" className="form-label input-group">
                  <b>New Password</b>
                </label>
                <input
                  type={showNewPassword ? "text" : "password"}
                  className="form-control"
                  style={{ borderRadius: "6px 0px 0px 6px" }}
                  id="newPassword"
                  placeholder="New password"
                  name="newPassword"
                  value={formik.values.newPassword}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                />
                <button
                  className=" btn btn-outline-secondary login"
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
          <div className="my-4 text-end">
            <button
              className="btn btn-primary btn-white py-2 font-size-14"
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
              className="btn btn-primary btn-blue btn-submit py-2 font-size-14"
              type="submit"
            >
              Reset
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
