import React, { useEffect, useState } from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import { ComponentAPI } from "../../../../../api/ComponentAPI";
import { Modal, notification } from "antd";
import "./ComponentUpsertModal.scss";
import * as Yup from "yup";
const UpsertModal = ({
  isModalOpen,
  setIsModalOpen,
  componentId,
  setComponentId,
  refreshAllComponents,
}) => {
  const [initialValues, setInitialValues] = useState({
    name: "",
    description: "",
  });
  const [updateResponse, setUpdateResponse] = useState();
  const validationSchema = Yup.object({
    name: Yup.string()
      .trim()
      .required("Name is required *")
      .min(3, "Name must be of minimum 3 characters")
      .max(1000, "Name must be of maximum 1000 characters"),
    description: Yup.string().max(
      1000,
      "Description can be of maximum 1000 characters"
    ),
  });

  const handleSubmit = (values, { setSubmitting, resetForm }) => {
    console.log(values);
    if (Object.values(initialValues).some((value) => !value.trim())) {
      //create component
      ComponentAPI.createComponent(values)
        .then(() => {
          notification.success({
            className:"notificationSuccess",
            placement: "top",
            message:"Component created successfully!",
          });

          refreshAllComponents();
          setIsModalOpen(false);
        })
        .catch((error) => {});
    } else {
      //update component
      const data = {
        ...values,
        state: updateResponse.state,
        rank: updateResponse.rank,
        id: updateResponse.id,
        meta: updateResponse.meta,
      };
      ComponentAPI.updateComponent(data)
        .then(() => {
          notification.success({
            className:"notificationSuccess",
            placement: "top",
            message:"Component updated successfully!",
          });
          refreshAllComponents();
          setIsModalOpen(false);
        })
        .catch((error) => {});
    }
    resetForm(); 
    setUpdateResponse(null);
    setSubmitting(false);
  };

  const handleCancel = () => {
    setInitialValues({ name: "", description: "" });
    setUpdateResponse(null);
    setIsModalOpen(false);
    setComponentId(null);
  };

  useEffect(() => {
    if (isModalOpen && !!componentId) {
      // Fetch component details when modal is opened and componentId is valid
      ComponentAPI.getComponentById(componentId)
        .then((responseData) => {
          setInitialValues({
            name: responseData.name,
            description: responseData.description,
          });
          setUpdateResponse(responseData);
        })
        .catch((error) => {
          // Handle error
        });
    } else {
      // Clear component details when modal is closed or componentId is not valid
      setInitialValues({ name: "", description: "" });
      setUpdateResponse(null);
    }
  }, [isModalOpen, componentId]);
  return (
    <div>
      <Modal open={isModalOpen} onCancel={handleCancel} footer={null}>
        <h4 className="mb-4">
          {!!updateResponse ? "Update Component" : "Create Component"}
        </h4>
        <Formik
          initialValues={initialValues}
          validationSchema={validationSchema}
          onSubmit={handleSubmit}
          enableReinitialize
        >
          {({ isValid, dirty }) => (
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
                      className="form-control"
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
                      Description
                    </label>
                    <Field
                      as="textarea"
                      type="description"
                      id="description"
                      name="description"
                      className="form-control"
                      placeholder="Description"
                      rows="5"
                    />
                    <ErrorMessage
                      name="description"
                      component="div"
                      className="error-message"
                    />
                  </div>
                </div>
              </div>
              <div className="my-4 text-end">
                <button
                  type="button"
                  className="btn btn-primary btn-white py-1 font-size-10 mx-3"
                  onClick={handleCancel}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className={`btn btn-primary btn-blue btn-submit py-1 mx-2 font-size-10`}
                  disabled={!isValid || !dirty}
                >
                  Submit
                </button>
              </div>
            </Form>
          )}
        </Formik>
      </Modal>
    </div>
  );
};

export default UpsertModal;
