import React, { useEffect, useState } from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import { useNavigate } from "react-router-dom";
import "./updateadminuser.scss";
import { notification } from "antd";
import { AdminUserAPI } from "../../../../api/AdminUserAPI";
import { useLocation } from "react-router-dom";
import { useLoader } from "../../../loader/LoaderContext";

import CustomSelect from "../CustomSelect";
import { ROLE_ID_ADMIN, ROLE_ID_ASSESSEE, ROLE_ID_TESTER } from "../../../../constants/role_constants";
const UpdateAdminUser = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const userId = new URLSearchParams(location.search).get("userId");
  const [meta, setMeta] = useState();
  const roles = [
    {
      label: "Admin",
      value: ROLE_ID_ADMIN,
    },
    {
      label: "Tester",
      value: ROLE_ID_TESTER
    },
    {
      label: "Assessee",
      value: ROLE_ID_ASSESSEE
    },
  ];
  const { showLoader, hideLoader } = useLoader();
  const [state, setState] = useState();
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    roleIds: [],
  });

  const initialValues = {
    name: formData.name,
    email: formData.email,
    password: formData.password,
    roleIds: formData.roleIds,
  };

  const validationSchema = Yup.object({
    name: Yup.string().required("Name is required *"),
    email: Yup.string()
      .email("Invalid email address")
      .required("Email is required *"),
    roleIds: Yup.array().min(1, "Role is required *"),
  });

  useEffect(() => {
    if (userId) {
      AdminUserAPI.fetchUserDetails(userId)
        .then((userData) => {
          setState(userData.state);
          setFormData({
            name: userData.name,
            email: userData.email,
            password: userData.password,
            roleIds: userData.roleIds,
          });
          setMeta(userData.meta);
        }).catch((response) => {
          
        });
    }
  }, [userId]);

  const handleSubmit = async (values) => {
    showLoader();
    const body = {
      ...values,
      roleIds: values.roleIds.map((role) => role),
      id: userId,
      state: state,
      meta: meta,
    };

    AdminUserAPI.updateUserDetails(body)
      .then((response) => {
        hideLoader();
        notification.success({
          placement: "bottomRight",
          description: `User Updated Successfully`,
        });
        navigate("/admin-users");
      })
      .catch((response) => {
        hideLoader();       
      });
  };

  return (
    <div>
      <div id="wrapper">
        <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div className="form-bg-white">
            <span className="heading-line-up">Update User Details</span>
            <Formik
              initialValues={initialValues}
              validationSchema={validationSchema}
              onSubmit={handleSubmit}
              enableReinitialize={true}
            >
              {(formikProps) => (
                <Form>
                  <div className="row">
                    <div className="col-12">
                      <div className="custom-input mb-3">
                        <label htmlFor="name" className="form-label">
                          Name
                        </label>
                        <Field
                          type="name"
                          className="form-control"
                          id="name"
                          placeholder="Your Email"
                          name="name"
                        />
                        <div className="error-message">
                          <ErrorMessage name="name" />
                        </div>
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
                          className="form-control"
                          id="email"
                          placeholder="Your Email"
                          name="email"
                          disabled={true}
                        />
                        <div className="error-message">
                          <ErrorMessage name="email" />
                        </div>
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
                          className="form-control"
                          id="password"
                          placeholder="Your Password"
                          name="password"
                          disabled={true}
                        />
                        <div className="error-message">
                          <ErrorMessage name="password" />
                        </div>
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
                          className="custom-select"
                          name="roleIds"
                          options={roles}
                          component={CustomSelect}
                          placeholder="Select Roles"
                          isMulti={true}
                        />
                      </div>
                      <div className="error-message">
                        <ErrorMessage name="roleIds" />
                      </div>
                    </div>
                  </div>

                  <div className="my-4 text-end">
                    <button
                      className="btn btn-primary btn-white py-2 font-size-14"
                      onClick={() => {
                        navigate("/admin-users");
                      }}
                    >
                      Cancel
                    </button>
                    <button
                      className="btn btn-primary btn-blue btn-submit py-2 font-size-14"
                      type="submit"
                    >
                      Update
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

export default UpdateAdminUser;
