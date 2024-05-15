import React, { useEffect, useState } from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import { Modal, notification } from "antd";
import * as Yup from "yup";
import { SpecificationAPI } from "../../../../../api/SpecificationAPI";
import "./ComponentSpecificationUpsertModal.scss";
const UpsertModal = ({
  isModalOpen,
  setIsModalOpen,
  specificationId,
  setSpecificationId,
  componentId,
  refreshAllSpecifications,
}) => {
  const [initialValues, setInitialValues] = useState({
    name: "",
    description: "",
    functional: "true",
    required: "true",
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
    if (Object.values(initialValues).some((value) => !value)) {
      SpecificationAPI.createSpecification({
        ...values,
        componentId,
      })
        .then(() => {
          notification.success({
            className:"notificationSuccess",
            placement: "top",
            message:"Specification created successfully!",
          });

          refreshAllSpecifications();
          setIsModalOpen(false);
        })
        .catch((error) => {});
    } else {
      const data = {
        ...values,
        componentId,
        state: updateResponse.state,
        rank: updateResponse.rank,
        id: updateResponse.id,
        meta: updateResponse.meta,
      };
      SpecificationAPI.updateSpecification(data)
        .then(() => {
          notification.success({
            className:"notificationSuccess",
            placement: "top",
            message:"Specification updated successfully!",
          });
          refreshAllSpecifications();
          setIsModalOpen(false);
        })
        .catch((error) => {});
    }
    resetForm(); 
    setUpdateResponse(null);
    setSubmitting(false);
  };

  const handleCancel = () => {
    setInitialValues({
      name: "",
      description: "",
      functional: "true",
      required: "true",
    });
    setUpdateResponse(null);
    setIsModalOpen(false);
    setSpecificationId(null);
  };

  useEffect(() => {
    if (isModalOpen && !!specificationId) {
      SpecificationAPI.getSpecificationById(specificationId)
        .then((responseData) => {
          console.log(responseData);
          setInitialValues({
            name: responseData.name,
            description: responseData.description,
            functional: responseData.functional.toString(),
            required: responseData.required.toString(),
          });
          setUpdateResponse(responseData);
        })
        .catch((error) => {});
    } else {
      setInitialValues({
        name: "",
        description: "",
        functional: "true",
        required: "true",
      });
      setUpdateResponse(null);
    }
  }, [isModalOpen, specificationId]);
  return (
    <div>
      <Modal open={isModalOpen} onCancel={handleCancel} footer={null}>
        <h4 className="mb-4">
          {!!updateResponse ? "Update Specification" : "Create Specification"}
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

              <div className="d-flex justify-content-between mb-5">
                <div>
                  <div id="functionalRadioGroup" className="form-radio-label">
                    Specification Type
                  </div>
                  <div role="group" aria-labelledby="functionalRadioGroup">
                    <div>
                      <label className="compSpecRadioButton">
                        <Field type="radio" name="functional" value="true" />
                        <span className="ms-2">Functional</span>
                      </label>
                    </div>
                    <div>
                      <label className="compSpecRadioButton">
                        <Field type="radio" name="functional" value="false" />
                        <span className="ms-2">Workflow</span>
                      </label>
                    </div>
                  </div>
                </div>

                <div>
                  <div id="requiredRadioGroup" className="form-radio-label">
                    Recommended / Required
                  </div>
                  <div role="group" aria-labelledby="requiredRadioGroup">
                    <div>
                      <label className="compSpecRadioButton">
                        <Field type="radio" name="required" value="true" />
                        <span className="ms-2">Required</span>
                      </label>
                    </div>
                    <div>
                      <label className="compSpecRadioButton">
                        <Field type="radio" name="required" value="false" />
                        <span className="ms-2">Recommended</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>

              <div className="my-4 text-end">
                <button
                id="compSpecUpsertModal-cancel"
                  type="button"
                  className="btn btn-primary btn-white py-1 font-size-10 mx-3"
                  onClick={handleCancel}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="btn btn-primary btn-blue btn-submit py-1 font-size-10"
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
