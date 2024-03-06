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
    name: Yup.string().required("Name is required")
      .max(1000, "Name must have less than 1000 characters"),
    email: Yup.string()
      .email("Invalid email address")
      .required("Email is required")
      .max(255, "Email must have less than 255 characters"),
    roleIds: Yup.array().min(1, "Role is required"),
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
    <div id="updateAdminUser">
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
              {({ errors}) => (
                <Form>
                  <div className="row">
                    <div className="col-12">
                      <div className="custom-input mb-3">
                        <label htmlFor="name" className="form-label">
                          Name
                          <span className="text-danger">*</span>
                        </label>
                        <Field
                          type="name"
                          className="form-control"
                          id="name"
                          placeholder="Your Name"
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
                          <span className="text-danger">*</span>
                        </label>
                        <Field
                          type="email"
                          className="form-control disable-field"
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
                        <label htmlFor="role" className="form-label">
                          Role
                          <span className="text-danger">*</span>
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
                      className="btn btn-primary btn-white py-2"
                      onClick={() => {
                        navigate("/admin-users");
                      }}
                    >
                      Cancel
                    </button>
                    <button
                      style={{marginLeft:"1rem"}}
                      className="btn btn-primary btn-blue btn-submit py-2"
                      type="submit"
                      disabled={Object.keys(errors).length > 0}
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
