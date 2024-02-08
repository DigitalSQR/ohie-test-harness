import React from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import { useNavigate } from "react-router-dom";
import "./addadminuser.scss";
import { notification } from "antd";
import { AdminUserAPI } from "../../../api/AdminUserAPI";
import { useLoader } from "../../loader/LoaderContext";

const AddAdminUser = () => {
  const navigate = useNavigate();
  const { showLoader, hideLoader } = useLoader();

  const initialValues = {
    name: "",
    email: "",
    password: "",
    roleIds: "",
  };

  const validationSchema = Yup.object({
    name: Yup.string().required("Name is required *"),
    email: Yup.string()
      .email("Invalid email address")
      .required("Email is required *"),
    password: Yup.string()
      .required("Password is required *")
      .min(6, "Password must be of minimum 6 characters"),
    roleIds: Yup.string().required("Role is required *"),
  });

  const handleSubmit = async (values, { setSubmitting }) => {
    showLoader();
    const body = { ...values, roleIds: ["role." + values.roleIds] };
    AdminUserAPI.addUser(body)
      .then((response) => {
        hideLoader();
        notification.success({
          placement: "bottomRight",
          description: `User Added Successfully`,
        });
        navigate("/dashboard/admin-users");
      })
      .catch((error) => {
        hideLoader();
        if (
          error.response &&
          error.response.data &&
          error.response.data.length > 0
        ) {
          error.response.data.forEach((element, index) => {
            notification.error({
              placement: "bottomRight",
              description: `${element.message}`,
            });
          });
        } else {
          notification.error({
            placement: "bottomRight",
            description: `${error.message}`,
          });
        }
      })
      .finally(() => {
        setSubmitting(false);
      });
  };

  return (
    <div>
      <div id="wrapper">
        <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div className="form-bg-white">
            <span className="heading-line-up">Admin User Registration</span>
            <Formik
              initialValues={initialValues}
              validationSchema={validationSchema}
              onSubmit={handleSubmit}
            >
              <Form>
                <div className="row">
                  <div className="col-12">
                    <div className="custom-input mb-3">
                      <label htmlFor="name" className="form-label">
                        Name
                      </label>
                      <Field
                        type="text"
                        id="name"
                        name="name"
                        className="form-control"
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
                      </label>
                      <Field
                        type="email"
                        id="email"
                        name="email"
                        className="form-control"
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
                    <div className="custom-input mb-3">
                      <label htmlFor="password" className="form-label">
                        Password
                      </label>
                      <Field
                        type="password"
                        id="password"
                        name="password"
                        className="form-control"
                      />
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
                    <div className="custom-input mb-3">
                      <label htmlFor="role" className="form-label">
                        Role
                      </label>
                      <Field
                        as="select"
                        id="roleIds"
                        name="roleIds"
                        className="form-select"
                      >
                        <option value="">Select Role</option>
                        <option value="tester">Tester</option>
                        <option value="admin">Admin</option>
                      </Field>
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
                    className="btn btn-primary btn-white py-2 font-size-14"
                    onClick={() => {
                      navigate("/dashboard/admin-users");
                    }}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary btn-blue btn-submit py-2 font-size-14"
                  >
                    Submit
                  </button>
                </div>
              </Form>
            </Formik>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddAdminUser;
