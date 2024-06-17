import { Fragment, useEffect, useState } from "react";
import { TestCaseAPI } from "../../../../../api/TestCaseAPI";
import { Modal, notification } from "antd";
import { Field, Form, Formik, FieldArray, ErrorMessage } from "formik";
import * as Yup from "yup";
import "./SpecAutomatedUpsertModal.scss";
import { useLoader } from "../../../../loader/LoaderContext";
import CustomSelect from "../../../../AdminFiles/AdminUsers/CustomSelect";
import {
  ROLE_ID_ASSESSEE,
  ROLE_ID_TESTER,
} from "../../../../../constants/role_constants";

export default function SpecAutomatedUpsertModal(props) {
  const {
    isAutomatedModalOpen,
    setIsAutomatedModalOpen,
    specificationId,
    isEditMode,
    setIsEditMode,
    currentAutomatedTestcase,
    fetchAutomatedTestCases,
  } = props;

  const [testcaseVariables, setTestcaseVariables] = useState([]);

  const [initialValues, setInitialValue] = useState({
    name: "",
    description: "",
    testcaseVariables: []
  });

  const roles = [
    {
      label: "Assessee",
      value: ROLE_ID_ASSESSEE,
    },
    {
      label: "Tester",
      value: ROLE_ID_TESTER,
    }
  ];

  const { showLoader, hideLoader } = useLoader();


  const [uploadedZipFile, setUploadedZipFile] = useState();
  const [currentTestCaseId, setCurrentTestCaseId] = useState();
  const handleCancel = () => {
    setInitialValue({ name: "", description: "", testcaseVariables: [] });
    setUploadedZipFile();
    setIsAutomatedModalOpen(false);
    setIsEditMode(false);
    setCurrentTestCaseId();
  };

  const handleSubmit = (values) => {

    const data = {
      name: values.name,
      specificationId: specificationId,
      description: "",
      testcaseVariables: values.testcaseVariables,
      manual: false,
      testcaseRunEnvironment: "testcase.run.environment.eu.testbed",
      zipFile: uploadedZipFile
    };


    if (isEditMode) {
      const data = currentAutomatedTestcase;
      data.name = values.name;
      data.zipFile = uploadedZipFile;

      const valuesIds = new Set(values.testcaseVariables.map(variable => variable.id));

      const inactiveTestcaseVariables = currentAutomatedTestcase.testcaseVariables
        .filter(testcaseVariable => !valuesIds.has(testcaseVariable.id))
        .map(testcaseVariable => ({
          ...testcaseVariable,
          state: "testcase.variable.status.inactive",
        }));

      data.testcaseVariables = [...values.testcaseVariables, ...inactiveTestcaseVariables];

      showLoader();
      TestCaseAPI.updateTestCase(data).then((res) => {
        notification.success({
          className: "notificationSuccess",
          message: "Automated TestCase Updated successfully",
          placement: "top",
        });
        setIsAutomatedModalOpen(false);
        setIsEditMode(false);
        setInitialValue({
          name: "",
          description: "",
          testcaseVariables: []
        });
        fetchAutomatedTestCases();
        setCurrentTestCaseId();
      }).catch(() => { })
      hideLoader();
    } else {
      console.log("reached here");
      showLoader();
      TestCaseAPI.createTestCase(data)
        .then((res) => {
          notification.success({
            className: "notificationSuccess",
            message: "Automated TestCase Sucessfully created",
            placement: "top",
          });
          setIsAutomatedModalOpen(false);
          setIsEditMode(false);
          setInitialValue({
            name: "",
            description: "",
            testcaseVariables: []
          });
          fetchAutomatedTestCases();
        })
        .catch(() => { });
      hideLoader();

    }
  };

  const validationSchema = Yup.object().shape({
    name: Yup.string()
      .trim()
      .required("Name is required *")
      .min(3, "Name must be of minimum 3 characters")
      .max(100, "Name must be of maximum 1000 characters"),
    testcaseVariables: Yup.array().of(
      Yup.object().shape({
        testcaseVariableKey: Yup.string()
          .trim()
          .required("Key is required *"),
        roleId: Yup.string()
          .trim()
          .required("Role is required *"),
        defaultValue: Yup.string()
          .trim()
          .required("Default value is required *")
      })
    )
      .test('unique-key', 'Keys must be unique.', function (testcaseVariables) {
        if (!Array.isArray(testcaseVariables)) return true;
        const keys = testcaseVariables.map(item => item.testcaseVariableKey);
        const uniqueKeys = new Set(keys);
        return keys.length === uniqueKeys.size;
      })

  });

  useEffect(() => {
    if (currentAutomatedTestcase) {
      setCurrentTestCaseId(currentAutomatedTestcase.id);
      setInitialValue({
        name: currentAutomatedTestcase.name,
        description: currentAutomatedTestcase.description,
        testcaseVariables: currentAutomatedTestcase.testcaseVariables.filter(testcaseVariable =>
          testcaseVariable.state === "testcase.variable.status.active"
        )
      });
      setUploadedZipFile();
    } else {
      setInitialValue({
        name: "",
        description: "",
        testcaseVariables: []
      });
      setUploadedZipFile();
      setCurrentTestCaseId();
    }


  }, [isAutomatedModalOpen, currentAutomatedTestcase]);

  return (
    <Fragment>
      <Modal
        cancelButtonProps={{ id: "SpecAutomatedUpsertModal-cancelButton" }}
        okButtonProps={{ id: "SpecAutomatedUpsertModal-okButton" }}
        open={isAutomatedModalOpen}
        onCancel={handleCancel}
        destroyOnClose={true}
        footer={null}
      >
        <h5>{isEditMode ? "Update Automated TestCase" : "Create Automated TestCase"}</h5>
        <Formik
          enableReinitialize={true}
          initialValues={initialValues}
          onSubmit={handleSubmit}
          validationSchema={validationSchema}
        >
          {({ values, isValid, dirty, setFieldValue, setFieldTouched, errors, touched }) => (
            <Form>
              <div className="row scroll-container">
                <div className="col-12">
                  <div className="custom-input mb-3">
                    <label htmlFor="name" className="form-label">
                      Name
                    </label>
                    <Field
                      type="text"
                      id="name"
                      name="name"
                      className={`form-control ${touched.name && errors.name
                        ? "is-invalid"
                        : ""
                        }`}
                      placeholder="Name"
                    />
                    <ErrorMessage
                      name="name"
                      component="div"
                      className="error-message"
                    />
                  </div>
                  {!!uploadedZipFile && (
                    <div>
                      {uploadedZipFile.name}
                      <span
                        id="specAutomatedUpserModal-removeZipFile"
                        type="button"
                        title="Remove File"
                        className="mx-2 font-size-14 mb-3 "
                        onClick={() => setUploadedZipFile()}
                      >
                        <i className="bi bi-trash3"></i>
                      </span>
                    </div>
                  )}
                  {!uploadedZipFile && (
                    <div
                      className="cst-btn-group margin mb-3"
                      role="group"
                      aria-label="Basic example"
                    >
                      <input
                        type="file"
                        style={{ display: "none" }}
                        id="fileInput"
                        onChange={(e) => {
                          console.log(e.target.files);
                          setUploadedZipFile(e.target.files[0]);
                        }}
                      ></input>
                      <label htmlFor="fileInput">
                        <button
                          id="specAutomatedUpserModal-update/uploadZipFile"
                          variant="success"
                          type="button"
                          className="btn cst-btn-default"
                          onClick={() =>
                            document.getElementById("fileInput").click()
                          }
                        >
                          <i
                            class="bi bi-file-zip-fill"
                            style={{ paddingRight: "3px" }}
                          ></i>{" "}
                          {isEditMode ? "Update Zip Fie" : "Upload Zip File"}
                        </button>
                      </label>
                    </div>
                  )}

                  <FieldArray name="testcaseVariables">
                    {({ push, remove }) => (
                      <>
                        {values.testcaseVariables.length > 0 && (
                          <span className="info-msg">Note : All fields are required and please ensure keys are unique.</span>
                        )}
                        {values.testcaseVariables
                          .map((_, index) => (

                            <div className="row mt-2" key={index}>
                              <div className="custom-input  col-lg-3">
                                <Field
                                  type="text"
                                  id={`key-${index}`}
                                  name={`testcaseVariables[${index}].testcaseVariableKey`}
                                  className={`form-control input-class ${touched.testcaseVariables?.[index]?.testcaseVariableKey && errors.testcaseVariables?.[index]?.testcaseVariableKey
                                    ? "is-invalid"
                                    : ""
                                    }`}
                                  placeholder="Key"
                                  onChange={(event) => setFieldValue(`testcaseVariables[${index}].testcaseVariableKey`, event.target.value)}
                                />
                              </div>
                              <div className="custom-input input-field mb-3 col-lg-4">
                                {/* <Field
                                  className={`custom-select user-role-select dropdown-class ${touched.testcaseVariables?.[index]?.roleId && errors.testcaseVariables?.[index]?.roleId
                                    ? "is-invalid"
                                    : ""
                                    }`}

                                  name={`testcaseVariables[${index}].roleId`}
                                  options={roles}
                                  component={CustomSelect}
                                  placeholder="Select Role"
                                  id={`roleId-${index}`}
                                  isMulti={false}
                                  onChange={(event) => {
                                    setFieldValue(`testcaseVariables[${index}].roleId`, event.target.value);
                                  }}
                                /> */}
                                <select
                                  id={`roleId-${index}`}
                                  className={`form-select custom-select custom-select-sm select-role 
                                  ${!values.testcaseVariables[index]?.roleId ? "text-muted-select" : ""}
                                  ${touched.testcaseVariables?.[index]?.roleId && errors.testcaseVariables?.[index]?.roleId
                                      ? "is-invalid"
                                      : ""
                                    }`}
                                  name={`testcaseVariables[${index}].roleId`}
                                  placeholder="Select role"
                                  onBlur={() => {
                                    setFieldTouched(`testcaseVariables[${index}].roleId`, true)
                                  }}
                                  onChange={(event) => {
                                    setFieldValue(`testcaseVariables[${index}].roleId`, event.target.value);
                                  }}
                                  value={values.testcaseVariables[index]?.roleId || ''}
                                >
                                  <option className="select-default" value="" >Select role</option>
                                  {roles.map(
                                    (role) => (
                                      <option
                                        style={{ fontSize: '16px' }}
                                        value={role.value}
                                        key={role.label}
                                      >
                                        {role.label}
                                      </option>
                                    )
                                  )}
                                </select>
                              </div>
                              {
                                <div className="custom-input default-value col-lg-3">
                                  <Field
                                    type="text"
                                    id={`defaultValue-${index}`}
                                    name={`testcaseVariables[${index}].defaultValue`}
                                    className={`form-control input-class ${touched.testcaseVariables?.[index]?.defaultValue && errors.testcaseVariables?.[index]?.defaultValue
                                      ? "is-invalid"
                                      : ""
                                      }`}
                                    placeholder="Default Value"
                                    onChange={(event) => setFieldValue(`testcaseVariables[${index}].defaultValue`, event.target.value)}
                                  />
                                </div>
                              }

                              <div className="col-lg-1 cancel-icon" onClick={() => {
                                remove(index);
                                const newFields = testcaseVariables.filter((field, i) => i !== index);
                                setTestcaseVariables(newFields);
                              }
                              }
                              >
                                <i className="bi bi-x-circle-fill" style={{ cursor: "pointer" }}></i>
                              </div>
                            </div>
                          ))
                        }
                        <div className="my-2 cst-btn-group margin mb-3">
                          <button
                            id="addFields"
                            variant="success"
                            type="button"
                            className="btn cst-btn-default"
                            onClick={() => {
                              push({ testcaseVariableKey: null, roleId: null });
                              setTestcaseVariables(prevVariables => [...prevVariables, { testcaseVariableKey: null, roleId: null }]);
                            }}
                          >
                            <i className="bi bi-plus" style={{ paddingRight: "3px", fontSize: "16px" }}></i>{" "} Add Fields
                          </button>
                        </div>
                      </>
                    )}
                  </FieldArray>

                  <div className="my-4 text-end">
                    <button
                      id="specAutomatedUpserModal-cancel"
                      type="button"
                      className="btn btn-primary btn-white py-1 font-size-10 mx-3"
                      onClick={handleCancel}
                    >
                      Cancel
                    </button>

                    <button
                      id="specAutomatedUpserModal-submit"
                      type="submit"
                      className="btn btn-primary btn-blue btn-submit py-1 font-size-10"
                      disabled={(isEditMode && ((!isValid || !dirty) && !uploadedZipFile)) || (!isEditMode && (!isValid || !dirty || !uploadedZipFile))}
                    >
                      {isEditMode ? "Update" : "Submit"}
                    </button>
                  </div>
                </div>
              </div>
            </Form>
          )}
        </Formik>
      </Modal>
    </Fragment>
  );
}
