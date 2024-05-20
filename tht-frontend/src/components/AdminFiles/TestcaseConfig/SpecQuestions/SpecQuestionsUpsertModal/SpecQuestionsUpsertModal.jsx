import React, { useEffect, useState } from "react";
import { Formik, Field, Form, ErrorMessage } from "formik";
import { TestCaseAPI } from "../../../../../api/TestCaseAPI";
import { Modal, notification } from "antd";
import { ManualQuestionTypeConstants } from "../../../../../constants/testcase_constants";
import * as Yup from "yup";
import "./SpecQuestionsUpsertModal.scss"
import { useNavigate } from "react-router-dom";
const UpsertModal = ({
  isModalOpen,
  setIsModalOpen,
  fetchData,
  specificationId,
}) => {
  const [initialValues, setInitialValues] = useState({
    name: "",
    description: "",
    questionType: ManualQuestionTypeConstants.SINGLE_SELECT
  });
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
  });

  const navigate = useNavigate();
  const handleSubmit = (values, { setSubmitting, resetForm }) => {
    const data = { ...values, manual: true, specificationId };
    TestCaseAPI.createTestCase(data)
      .then((resp) => {
        notification.success({
          className:"notificationSuccess",
          placement: "top",
          message: "Testcase created successfully!",
        });

        fetchData();
        setIsModalOpen(false);
        navigate(`/testcase-config/edit-question/${resp?.id}`);
      })
      .catch((error) => {});
    resetForm();
    setSubmitting(false);
  };

  const handleCancel = () => {
    setInitialValues({ name: "", description: "", questionType: ManualQuestionTypeConstants.SINGLE_SELECT });
    setIsModalOpen(false);
  };

  useEffect(() => {
    setInitialValues({ name: "", description: "", questionType: ManualQuestionTypeConstants.SINGLE_SELECT });
  }, [isModalOpen]);

  return (
    <div>
      <Modal open={isModalOpen} onCancel={handleCancel} footer={null}>
        <h4 className="mb-4">Create Testcase</h4>
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
                      Question
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
              <div>
                <div id="questionTypeRadioGroup" className="form-radio-label">
                  Question Type
                </div>
                <div role="group" aria-labelledby="questionTypeRadioGroup">
                  <div>
                    <label className="questionTypeRadioGroup">
                      <Field
                        type="radio"
                        name="questionType"
                        value={ManualQuestionTypeConstants.SINGLE_SELECT}
                      />
                      <span className="ms-2">Single Select</span>
                    </label>
                  </div>
                  <div>
                    <label className="questionTypeRadioGroup">
                      <Field
                        type="radio"
                        name="questionType"
                        value={ManualQuestionTypeConstants.MULTI_SELECT}
                      />
                      <span className="ms-2">Multi Select</span>
                    </label>
                  </div>
                </div>
              </div>
              <div className="my-4 text-end">
                <button
                  type="button"
                  className="btn btn-primary btn-white py-1 font-size-10 mx-3"
                  onClick={handleCancel}
                  id="#SpecQUestionsUpsertModal-cancel"
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
