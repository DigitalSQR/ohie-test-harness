import React from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import { Modal } from "antd";
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
                className="btn btn-primary btn-white py-1 font-size-10"
                onClick={handleCancel}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="btn btn-primary btn-blue btn-submit py-1 font-size-10"
              >
                Submit
              </button>
            </div>
          </Form>
        </Formik>
      </Modal>
    </div>
  );
};

export default UpsertModal;
