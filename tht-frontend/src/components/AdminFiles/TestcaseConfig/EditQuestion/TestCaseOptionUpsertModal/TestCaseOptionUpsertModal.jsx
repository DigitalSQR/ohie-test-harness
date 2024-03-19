import React, {  useState } from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import { TestCaseOptionsAPI } from "../../../../../api/TestCaseOptionsAPI";
import "./TestCaseOptionUpsertModal.scss";
import { Modal, notification } from "antd";
import Select from "react-select";
import * as Yup from "yup";
const UpsertModal = ({
  isModalOpen,
  setIsModalOpen,
  fetchData,
  testcaseId,
}) => {
  const [initialValues, setInitialValues] = useState({
    name: "",
    description: "",
    success: true,
  });

  const options = [
    { value: true, label: "True" },
    { value: false, label: "False" },
  ];

  const validationSchema = Yup.object({
    name: Yup.string()
      .trim()
      .required("Name is required *")
      .min(3, "Name must be of minimum 3 characters")
      .max(1000, "Name must be of maximum 1000 characters"),
    // description: Yup.string().max(
    //   1000,
    //   "Description can be of maximum 1000 characters"
    // ),
    success: Yup.boolean().required("Success is required *"),
  });

  const handleSubmit = (values, { setSubmitting, resetForm }) => {
    const data = { ...values, testcaseId };
    TestCaseOptionsAPI.createTestCaseOption(data)
      .then(() => {
        notification.success({
          className:"notificationSuccess",
          placement: "top",
          message:"Success",
          description: "Testcase Option Created Successfully",
        });

        fetchData();
        setIsModalOpen(false);
      })
      .catch((error) => {});
    resetForm();
    setSubmitting(false);
  };

  const handleCancel = () => {
    setInitialValues({
      name: "",
      description: "",
      success: true,
    });
    setIsModalOpen(false);
  };

  return (
    <div>
      <Modal open={isModalOpen} onCancel={handleCancel} footer={null}>
        <h4 className="mb-4">Add Option</h4>
        <Formik
          initialValues={initialValues}
          validationSchema={validationSchema}
          onSubmit={handleSubmit}
          enableReinitialize
        >
          {({ setFieldValue, isValid, dirty }) => (
            <Form>
              <div className="row">
                <div className="col-12">
                  <div className="custom-input mb-3">
                    <label htmlFor="name" className="form-label">
                      Option Name
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
              {/* <div className="row">
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
              </div> */}
              <div className="form-group">
                <label htmlFor="success" className="form-label">
                  Success
                </label>
                <Field name="success">
                  {({ field }) => (
                    <Select
                      {...field}
                      options={options}
                      onChange={(option) =>
                        setFieldValue("success", option.value)
                      }
                      value={options.find(
                        (option) => option.value === field.value
                      )}
                    />
                  )}
                </Field>

                <ErrorMessage
                  name="success"
                  component="div"
                  className="error-message"
                />
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
