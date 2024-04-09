import React, { useEffect, useState } from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import "./updateadminuser.scss";
import { Modal, notification } from "antd";
import { AdminUserAPI } from "../../../../api/AdminUserAPI";
import { useLoader } from "../../../loader/LoaderContext";

import CustomSelect from "../CustomSelect";
import { ROLE_ID_ADMIN, ROLE_ID_TESTER } from "../../../../constants/role_constants";

//Component that provides the functionality to update the user details
const UpdateAdminUser = ({
  isModalOpen,
  setIsModalOpen,
  userId,
  setUserId,
  refreshAllComponents
}) => {
  const [meta, setMeta] = useState();
  const roles = [
    {
      label: "Admin",
      value: ROLE_ID_ADMIN,
    },
    {
      label: "Tester",
      value: ROLE_ID_TESTER
    }
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
  
  //Function to handle cancelling a admin user modal
  const handleCancel = () => {
    setFormData({
      name: "",
      email: "",
      password: "",
      roleIds: [],
    });
    setIsModalOpen(false);
    setUserId(null);
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

  //UseEffect to fetch user Details when the component mounts to prefill the form with current user details
  useEffect(() => {
    if (isModalOpen && userId) {
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
  }, [isModalOpen, userId]);

  //Function to Submit the form with updated user details
  const handleSubmit = async (values, {resetForm}) => {
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
          className:"notificationSuccess",
          placement: "top",
          message:"Success",
          description: `Profile has been updated successfully!`,
        });
        setIsModalOpen(false);
        resetForm(); 
        refreshAllComponents();
      })
      .catch((response) => {
        hideLoader();       
      });
  };

  return (
    <Modal open={isModalOpen} closable={false} keyboard={false} footer={null}>
      <div id="updateAdminUser">
            <h4 className="mb-4">
             Update User Detail
            </h4>
            <Formik
              initialValues={initialValues}
              validationSchema={validationSchema}
              onSubmit={handleSubmit}
              enableReinitialize={true}
            >
              {({ errors, touched, isValid, dirty }) => {
                return (
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
                            className={`form-control ${
                              touched.name && errors.name ? "is-invalid" : ""
                            }`}
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
                            className={`form-control disable-field ${
                              touched.email && errors.email ? "is-invalid" : ""
                            }`}
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
                            className={`custom-select user-role-select ${
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
                          <div className="error-message">
                            <ErrorMessage name="roleIds" />
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="my-4 text-end">
                      <button
                        type="button"
                        className="btn btn-primary btn-white"
                        onClick={() => {
                          handleCancel();
                        }}
                      >
                        Cancel
                      </button>
                      <button
                        style={{ marginLeft: "1rem" }}
                        className="btn btn-primary btn-blue btn-submit py-1"
                        type="submit"
                        disabled={!isValid || !dirty}
                      >
                        Update
                      </button>
                    </div>
                  </Form>
                );
              }}
            </Formik>
          </div>
        </Modal>
  );
};

export default UpdateAdminUser;
