import { React, useState } from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import { useNavigate } from "react-router-dom";
import "./addadminuser.scss";
import { notification } from "antd";
import { AdminUserAPI } from "../../../../api/AdminUserAPI";
import { useLoader } from "../../../loader/LoaderContext";
import CustomSelect from "../CustomSelect";
import {
  ROLE_ID_ADMIN,
  ROLE_ID_TESTER,
} from "../../../../constants/role_constants";
const AddAdminUser = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const { showLoader, hideLoader } = useLoader();
  const roles = [
    {
      label: "Admin",
      value: ROLE_ID_ADMIN,
    },
    {
      label: "Tester",
      value: ROLE_ID_TESTER,
    }
  ];
  const initialValues = {
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    roleIds: [],
  };
  const validationSchema = Yup.object({
    name: Yup.string()
      .required("Name is required")
      .max(1000, "Name must have less than 1000 characters"),
    email: Yup.string()
      .email("Invalid email address")
      .required("Email is required")
      .max(255, "Email must have less than 255 characters"),
    password: Yup.string()
      .required("Password is required")
      .min(6, "Password must be of minimum 6 characters")
      .max(15, "Password must have less than 15 characters"),
    roleIds: Yup.array().min(1, "Role is required"),
    confirmPassword: Yup.string().oneOf(
      [Yup.ref("password"), null],
      "Confirm password does not match with the password."
    ),
  });

  const handleSubmit = async (values, { setSubmitting }) => {
    showLoader();
    const { confirmPassword, ...body } = values;
    body.roleIds = body.roleIds.map((role) => role);
    AdminUserAPI.addUser(body)
      .then((response) => {
        hideLoader();
        notification.success({
          placement: "bottomRight",
          description: `User Added Successfully`,
        });
        navigate("/user-management");
      })
      .catch((error) => {
        hideLoader();
      })
      .finally(() => {
        setSubmitting(false);
      });
  };

  const hasValues = (errors) => {
    return Object.keys(errors).length > 0;
  };

  return (
    <div id="addAdminUser">
      <div id="wrapper">
        <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div className="form-bg-white">
            <span className="heading-line-up">User Registration</span>
            <Formik
              initialValues={initialValues}
              validationSchema={validationSchema}
              onSubmit={handleSubmit}
            >
              {({ errors, touched }) => (
                <Form>
                  <div className="row">
                    <div className="col-12">
                      <div className="custom-input mb-3">
                        <label htmlFor="name" className="form-label">
                          Name
                          <span className="text-danger">*</span>
                        </label>
                        <Field
                          type="text"
                          id="name"
                          name="name"
                          className={`form-control ${
                            touched.name && errors.name ? "is-invalid" : ""
                          }`}
                          placeholder="Name"
                        />
                        <ErrorMessage
                          name="name"
                          component="div"
                          className="error-message"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-12">
                      <div className="custom-input mb-3">
                        <label htmlFor="email" className="form-label">
                          Email
                          <span className="text-danger">*</span>
                        </label>
                        <Field
                          type="email"
                          id="email"
                          name="email"
                          className={`form-control ${
                            touched.email && errors.email ? "is-invalid" : ""
                          }`}
                          placeholder="Email"
                        />
                        <ErrorMessage
                          name="email"
                          component="div"
                          className="error-message"
                        />
                      </div>
                    </div>
                  </div>
                  <div className="row">
                    <div className="col-12">
                      <div className="custom-input mb-3 position-relative">
                        <label htmlFor="password" className="form-label">
                          Password
                          <span className="text-danger">*</span>
                        </label>
                        <div className="input-group">
                          <Field
                            type={showPassword ? "text" : "password"}
                            id="password"
                            name="password"
                            className={`form-control ${
                              touched.password && errors.password
                                ? "is-invalid"
                                : ""
                            }`}
                            placeholder="Password"
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
                        <ErrorMessage
                          name="password"
                          component="div"
                          className="error-message"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-12">
                      <div className="custom-input mb-3 position-relative">
                        <label htmlFor="confirmPassword" className="form-label">
                          Confirm Password
                          <span className="text-danger">*</span>
                        </label>
                        <div className="input-group">
                          <Field
                            type={showConfirmPassword ? "text" : "password"}
                            id="confirmPassword"
                            name="confirmPassword"
                            className={`form-control ${
                              touched.confirmPassword && errors.confirmPassword
                                ? "is-invalid"
                                : ""
                            }`}
                            placeholder="Confirm Password"
                          />
                          <button
                            className="btn btn-outline-secondary login"
                            type="button"
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
                        <ErrorMessage
                          name="confirmPassword"
                          component="div"
                          className="error-message"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-12">
                      <div className="custom-input mb-3">
                        <label htmlFor="role" className="form-label">
                          Role
                          <span className="text-danger">*</span>
                        </label>
                        <Field
                          className={`custom-select ${
                            touched.roleIds && errors.roleIds
                              ? "is-invalid"
                              : ""
                          }`}
                          name="roleIds"
                          options={roles}
                          component={CustomSelect}
                          placeholder="Select Roles"
                          isMulti={true}
                        />
                        <ErrorMessage
                          name="roleIds"
                          component="div"
                          className="error-message"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="my-4 text-end">
                    <button
                      className="btn btn-primary btn-white py-2"
                      onClick={() => {
                        navigate("/user-management");
                      }}
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="btn btn-primary btn-blue py-2"
                      disabled={hasValues(errors) || !hasValues(touched)}
                      style={{ marginLeft: "1rem" }}
                    >
                      Submit
                    </button>
                  </div>
                </Form>
              )}
            </Formik>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddAdminUser;
