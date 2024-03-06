import React from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import { Modal } from "antd";
import "../ComponentList/componentList.scss";

const UpsertModal = ({
  isModalOpen,
  handleCancel,
  initialValues,
  validationSchema,
  handleSubmit,
  updateResponse,
}) => {
  return (
    <div>
      <Modal open={isModalOpen} onCancel={handleCancel} footer={null} maskClosable={false}>
        <h4 className="mb-4">
          {!!updateResponse ? "Update Component" : "Create Component"}
        </h4>
        <Formik
          initialValues={initialValues}
          validationSchema={validationSchema}
          onSubmit={handleSubmit}
          enableReinitialize
        >
        {({ errors, touched, values}) => (
          <Form>
            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="name" className="form-label fw-bold">
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
                  <label htmlFor="description" className="form-label fw-bold">
                    Description
                    <span className="text-danger">*</span>
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
                className="btn btn-primary btn-white py-2 mx-2"
                onClick={handleCancel}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="btn btn-primary btn-blue btn-submit py-2 mx-2"
                disabled={(Object.keys(touched).length == 0 && !values.name  && !values.description) || (Object.keys(errors).length > 0)}
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
