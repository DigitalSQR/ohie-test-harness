import { Fragment, useEffect, useState } from "react";
import { Modal } from "antd";
import { useNavigate } from "react-router-dom";
import { Form, Formik } from "formik";
import * as Yup from "yup";
import { useLoader } from "../../../loader/LoaderContext";
import {
  ROLE_ID_TESTER,
} from "../../../../constants/role_constants";
import { TestcaseVariableAPI } from "../../../../api/TestcaseVariableAPI";
import { TestCaseAPI } from "../../../../api/TestCaseAPI";
import { TestRequestAPI } from "../../../../api/TestRequestAPI";
import { TestRequestValueAPI } from "../../../../api/TestRequestValueAPI";
import { SpecificationAPI } from "../../../../api/SpecificationAPI";
import { ComponentAPI } from "../../../../api/ComponentAPI";
import { Popover } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import "./TesterAutomatedModal.scss";

export default function TesterAutomatedModal(props) {
  const {
    isTesterModalOpen,
    setIsTesterModalOpen,
    currentTestRequestId,
    handleStartTesting
  } = props;

  const [groupedValues, setGroupedValues] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [testRequest, setTestRequest] = useState([]);
  const [defaultTestRequestValues, setDefaultTestRequestValues] = useState([]);
  const [setAsDefault, setSetAsDefault] = useState(true);
  const navigate = useNavigate();

  const [initialValues, setInitialValue] = useState({
    testRequestValues: []
  });

  const { showLoader, hideLoader } = useLoader();

  const handleCancel = () => {
    setInitialValue({ testRequestValues: [] });
    setIsTesterModalOpen(false);
  };


  const handleSubmit = (values) => {
    const data = values.testRequestValues;
    TestRequestValueAPI.updateTestRequestValues(data).then(() => {
      setIsTesterModalOpen(false);
      handleStartTesting(null, true);
    })
      .catch((err) => {
      })
  }


  const validationSchema = Yup.object().shape({
    testRequestValues: Yup.array().of(
      Yup.object().shape({
        value: Yup.string()
          .trim()
          .test(
            'custom-required',
            function (value) {
              const { key } = this.parent;
              if (!value) {
                return this.createError({
                  message: `${key} is required`,
                  path: `testRequestValues[${this.options.index}].value`,
                });
              }
              return true;
            }
          )
          .max(100, "Value must be of maximum 250 characters"),
      })
    )
  });

  const groupByComponent = (testRequestValues) => {
    if (!Array.isArray(testRequestValues)) {
      console.error('testRequestValues is not an array:', testRequestValues);
      return {};
    }
    return testRequestValues.reduce((acc, value, originalIndex) => {
      const { componentName } = value;
      if (!acc[componentName]) {
        acc[componentName] = [];
      }
      acc[componentName].push({ value, originalIndex });
      return acc;
    }, {});
  };

  useEffect(() => {
    if (currentTestRequestId) {
      const newTestRequestValues = [];
      
      TestRequestAPI.getTestRequestsById(currentTestRequestId)
        .then((res) => {
          setTestRequest(res);
          const componentPromises = res.testRequestUrls.map(testRequestUrl => {
            return ComponentAPI.getComponentById(testRequestUrl.componentId);
          });
  
          // Wait for all component promises to resolve
          return Promise.all(componentPromises)
            .then(components => {
              components.sort((a, b) => b.rank - a.rank);
              const selectedComponentsIds = components.map(component => component.id);
              return selectedComponentsIds;
            });
        })
        .then(selectedComponentsIds => {
          return Promise.all(selectedComponentsIds.map(selectedComponentId =>
            TestcaseVariableAPI.getTestcaseVariablesByComponentId(selectedComponentId)
          ));
        })
        .then(allResponses => {
          let promises = [];
          allResponses.forEach((res) => {
            res.forEach((testcaseVariable) => {
              if (testcaseVariable.roleId === ROLE_ID_TESTER) {
                const promise = TestcaseVariableAPI.getTestcaseVariablesById(testcaseVariable.id)
                  .then((testcaseVariableRes) => {
                    return TestCaseAPI.getTestCasesById(testcaseVariableRes.testcaseId)
                      .then(testCaseRes => ({ testcaseVariableRes, testCaseRes }));
                  })
                  .then(({ testcaseVariableRes, testCaseRes }) => {
                    return SpecificationAPI.getSpecificationById(testCaseRes.specificationId)
                      .then(specificationRes => ({ testcaseVariableRes, testCaseRes, specificationRes }));
                  })
                  .then(({ testcaseVariableRes, testCaseRes, specificationRes }) => {
                    return ComponentAPI.getComponentById(specificationRes.componentId)
                      .then(componentRes => ({
                        testcaseVariableRes,
                        testCaseRes,
                        specificationRes,
                        componentRes
                      }));
                  })
                  .then(({ testcaseVariableRes, testCaseRes, specificationRes, componentRes }) => {
                    newTestRequestValues.push({
                      key: testcaseVariableRes.key,
                      testcaseVariableId: testcaseVariableRes.id,
                      value: testcaseVariableRes.defaultValue,
                      testcaseName: testCaseRes.name,
                      specificationName: specificationRes.name,
                      componentName: componentRes.name,
                      testRequestId: currentTestRequestId
                    });
                  })
                  .catch((error) => {
                  });
  
                promises.push(promise);
              }
            });
          });
  
          return Promise.all(promises);
        })
        .then(() => {
          setInitialValue({ testRequestValues: newTestRequestValues });
          if (isTesterModalOpen && newTestRequestValues) {
            setDefaultTestRequestValues(newTestRequestValues);
            setGroupedValues(groupByComponent(newTestRequestValues));
            setShowForm(true);
          }
        })
        .catch((error) => {
        })
        .finally(() => {
          hideLoader();
        });
    }
  }, [isTesterModalOpen, currentTestRequestId]);
  
  return (
    <Fragment>
      <Modal
        open={isTesterModalOpen}
        onCancel={handleCancel}
        destroyOnClose={true}
        footer={null}
      >
        <div id="testerAutomatedModal">
          <h5>Update Testing Request Inputs</h5>
          {showForm === true && (
            <Formik
              enableReinitialize={true}
              initialValues={initialValues}
              onSubmit={handleSubmit}
              validationSchema={validationSchema}
            >
              {({ values, isValid, setFieldValue, setFieldTouched, errors, touched, setTouched }) => (
                <Form>
                  <div className="row scroll-container">
                    <div className="col-12">
                      {
                        Object.entries(groupedValues).map(([componentName, testRequestValues]) => (
                          <div key={componentName}>
                            <label
                              htmlFor={componentName}
                              className="component-header"
                            >
                              {componentName}
                            </label>
                            {testRequestValues.map((testRequestValue, index) => (
                              <div className="row ">
                                <div className="col-12 ">
                                  <div className="custom-input mt-3">
                                    <label
                                      htmlFor={testRequestValue.value.key}
                                      className="form-label"
                                    >
                                      {testRequestValue.value.key}:{" "}
                                      <span style={{ color: "red" }}>*</span>
                                      <Popover
                                        placement="topLeft"
                                        title={
                                          <div
                                            style={{
                                              maxWidth: "450px",
                                              fontWeight: "normal",
                                            }}
                                          >
                                            {" "}
                                            Please provide the value for testcase {testRequestValue.value.testcaseName} of specification {testRequestValue.value.specificationName}
                                          </div>
                                        }
                                      >
                                        <InfoCircleOutlined
                                          style={{
                                            marginLeft: "0.5rem",
                                            marginTop: "0.7rem",
                                          }}
                                        />
                                      </Popover>
                                    </label>
                                    <input
                                      id={
                                        "testRequestValues[" +
                                        testRequestValue.originalIndex +
                                        "].value"
                                      }
                                      name={
                                        "testRequestValues[" +
                                        testRequestValue.originalIndex +
                                        "].value"
                                      }
                                      type="text"
                                      className={`form-control ${touched.testRequestValues && touched.testRequestValues[testRequestValue.originalIndex] && touched.testRequestValues[testRequestValue.originalIndex].value && errors.testRequestValues && errors.testRequestValues[testRequestValue.originalIndex] && errors.testRequestValues[testRequestValue.originalIndex].value
                                        ? "is-invalid"
                                        : ""
                                        }`}
                                      placeholder={testRequestValue.value.key}
                                      value={values.testRequestValues[testRequestValue.originalIndex]?.value || ""}
                                      onChange={(event) => setFieldValue(`testRequestValues[${testRequestValue.originalIndex}].value`, event.target.value)}
                                      onBlur={
                                        () => {
                                          setFieldTouched(`testRequestValues[${testRequestValue.originalIndex}].value`, true)
                                          if (setAsDefault) {
                                            setSetAsDefault(!setAsDefault);
                                          }
                                        }
                                      }
                                      autoComplete="off"
                                    />
                                    {
                                      touched.testRequestValues && touched.testRequestValues[testRequestValue.originalIndex] && touched.testRequestValues[testRequestValue.originalIndex].value && errors.testRequestValues && errors.testRequestValues[testRequestValue.originalIndex] &&
                                      <div className="error-message">
                                        {errors.testRequestValues[testRequestValue.originalIndex].value}
                                      </div>
                                    }
                                  </div>
                                </div>
                              </div>
                            ))
                            }
                          </div>
                        ))
                      }
                      <div className="option-item">
                        <input
                          key="default"
                          type="checkbox"
                          id="default.id"
                          name="default.id"
                          value=""
                          checked={setAsDefault}
                          autoComplete="off"
                          onChange={() => {
                            if (!setAsDefault) {
                              defaultTestRequestValues.forEach((defaultValue, index) => {
                                setFieldValue(`testRequestValues[${index}].value`, defaultValue.value);
                                setTouched({});
                              })
                              setGroupedValues(groupByComponent(values.testRequestValues));
                              setSetAsDefault(!setAsDefault);
                            }
                            else {
                              defaultTestRequestValues.forEach((defaultValue, index) => {
                                setFieldValue(`testRequestValues[${index}].value`, '');
                                setTouched({});
                                setGroupedValues(groupByComponent(values.testRequestValues));
                                setSetAsDefault(!setAsDefault);
                              })
                            }
                          }

                          }
                        />
                        <label className="mt-4 set-default">Reset</label>
                      </div>
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
                          type="submit"
                          className="btn btn-primary btn-blue btn-submit py-1 font-size-10"
                          disabled={
                            !setAsDefault && (!isValid || !Object.keys(touched).length > 0)
                          }
                        >
                          Start Testing
                        </button>
                      </div>
                    </div>
                  </div>
                </Form>
              )
              }
            </Formik>
          )}
        </div>
      </Modal>
    </Fragment>
  );
}
