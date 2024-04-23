import React, { Fragment, useEffect, useState } from "react";
import "./_registrationApplication.scss";
import { useNavigate, useParams } from "react-router-dom";
import { useFormik } from "formik";
import { UserAPI } from "../../../api/UserAPI.js";
import { ComponentAPI } from "../../../api/ComponentAPI.js";
import { useLoader } from "../../loader/LoaderContext.js";
import { TestRequestAPI } from "../../../api/TestRequestAPI.js";
import { notification } from "antd";
import { TestRequestStateConstants } from "../../../constants/test_requests_constants.js";
import {
  CREATE_VALIDATION,
  UPDATE_VALIDATION,
} from "../../../constants/validation_constants.js";
import { store } from "../../../store/store.js";
import { Popover } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer.jsx";
const RegisterApplication = () => {
  const navigate = useNavigate();
  const { showLoader, hideLoader } = useLoader();
  const [components, setComponents] = useState([]);
  const [userId, setUserId] = useState();
  const [showPassword, setShowPassword] = useState(false);
  const [touched, setTouched] = useState({});
  const [meta, setMeta] = useState(); //only used when updating test request.
  const { testRequestId } = useParams();
  const dispatch = useDispatch();

  // A custom validation function. This must return an object
  // which keys are symmetrical to our values/initialValues
  const validate = (values) => {
    const errors = {};

    if (values.testRequestUrls.length === 0) {
      errors.testRequestUrls = "Please select atleast one component";
    }

    if (values.name === "") {
      errors.name = "Application Name is required";
    } else if (values.name.length < 3) {
      errors.name = "Application name must be of minimum 3 characters";
    } else if (values.name.length > 1000) {
      errors.name = "Application name must have less than 1000 characters.";
    }

    // if (values.description === "") {
    //   errors.description = "Application Description is required";
    // } else if (values.description.length > 1000) {
    //   errors.description =
    //     "Application description must have less than 1000 characters.";
    // }

    values.testRequestUrls.forEach((url, index) => {
      if (url.username === "") {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].username`
        ] = "Username is required";
      } else if (url.username.length > 255) {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].username`
        ] = "Username must have less than 255 characters";
      }
      if (url.password === "") {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].password`
        ] = "Password is required";
      } else if (url.password.length > 255) {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].password`
        ] = "Password must have less than 255 characters";
      }
      if (url.fhirApiBaseUrl === "") {
        errors[
          `testRequestUrls[${modifiedComponentId(
            url.componentId
          )}].fhirApiBaseUrl`
        ] = "fhirApiBaseUrl is required";
      } else if (url.fhirApiBaseUrl.length > 255) {
        errors[
          `testRequestUrls[${modifiedComponentId(
            url.componentId
          )}].fhirApiBaseUrl`
        ] = "fhirApiBaseUrl must have less than 255 characters";
      }
      if (url.websiteUIBaseUrl.length > 255) {
        errors[
          `testRequestUrls[${modifiedComponentId(
            url.componentId
          )}].websiteUIBaseUrl`
        ] = "websiteUIBaseUrl must have less than 255 characters";
      }
    });

    return errors;
  };

  const modifiedComponentId = (componentId) => {
    return componentId.replace(/\./g, "");
  };

  const formik = useFormik({
    initialValues: {
      name: "",
      state: TestRequestStateConstants.TEST_REQUEST_STATUS_PENDING,
      description: "",
      assesseeId: "",
      testRequestUrls: [],
    },
    validate,
    onSubmit: (values) => {
      formik.values.assesseeId = userId;

      if (testRequestId) {
        const data = { ...values, id: testRequestId,meta:meta };
        showLoader();
        TestRequestAPI.validateTestRequest(UPDATE_VALIDATION, data)
          .then((res) => {
            if (res.length == 0) {
              TestRequestAPI.updateTestRequest(data)
                .then((res) => {
                  notification.success({
                    className: "notificationSuccess",
                    placement: "top",
                    message: "Success",
                    description: "Test Request Updated Successfully.",
                  });
                  hideLoader();
                  navigate("/testing-requests");
                })
                .catch(() => {});
            } else {
              res.forEach((err) => {
                notification.error({
                  className: "notificationError",
                  message: "Error",
                  placement: "bottomRight",
                  description: err.message,
                });
              });
              hideLoader();
            }
          })
          .catch(() => {});
      } else {
        showLoader();
        TestRequestAPI.validateTestRequest(CREATE_VALIDATION, values)
          .then((res) => {
            if (res.length == 0) {
              TestRequestAPI.createTestRequest(values)
                .then((res) => {
                  notification.success({
                    className: "notificationSuccess",
                    placement: "top",
                    message: "Success",
                    description: `Your application testing request has been successfully created and sent to admin for approval.`,
                  });
                  hideLoader();
                  navigate("/testing-requests");
                })
                .catch((error) => {
                  hideLoader();
                });
            } else {
              res.forEach((err) => {
                notification.error({
                  className: "notificationError",
                  message: "Error",
                  placement: "bottomRight",
                  description: err.message,
                });
              });
              hideLoader();
            }
          })
          .catch((error) => {});
      }
    },
  });

  const addOrRemoveTestUrls = (component, i) => {
    var turls = formik.getFieldHelpers("testRequestUrls");
    const key = modifiedComponentId(component.id);

    if (component.isSelected) {
      setTouched({
        ...touched,
        [key]: {
          username: false,
          password: false,
          fhirApiBaseUrl: false,
          websiteUIBaseUrl: false,
        },
      });

      turls.setValue([
        ...formik.values.testRequestUrls,
        {
          username: "",
          password: "",
          fhirApiBaseUrl: "",
          websiteUIBaseUrl: "",
          componentId: component.id,
        },
      ]);
    } else {
      const { [key]: removedKey, ...remainingTouched } = touched;

      setTouched(remainingTouched);

      turls.setValue(
        formik.values.testRequestUrls.filter(
          (url) => url.componentId !== component.id
        )
      );
    }
  };

  const onComponentSelected = (index, e) => {
    components[index].isSelected = e.target.checked;
    setComponents(components);
    addOrRemoveTestUrls(components[index], index);
  };

  const handleBlur = (key, componentId) => {
    setTouched((prevTouched) => {
      const updatedTouched = {
        ...prevTouched,
        [modifiedComponentId(componentId)]: {
          ...prevTouched[modifiedComponentId(componentId)],
          [key]: true,
        },
      };
      return updatedTouched;
    });
  };

  useEffect(() => {
    if (testRequestId) {
      dispatch(set_header("Update Application"));
      TestRequestAPI.getTestRequestsById(testRequestId).then((res) => {
        formik.values.name = res.name;
        formik.values.description = res.description;
        formik.values.testRequestUrls = res.testRequestUrls;
        setMeta(res.meta);
      });
    } else {
      dispatch(set_header("Register Application"));
    }
    console.log(formik.values);

    const userInfo = store.getState().userInfoSlice;
    setUserId(userInfo.id);
    const params = {};
    params.state = "component.status.active";
    params.sort = "rank,asc";
    ComponentAPI.getComponents(params)
      .then((res) => {
        setComponents(res.content);
        hideLoader();
      })
      .catch((err) => {
        hideLoader();
      });
  }, []);

  return (
    <div id="registerApplication">
      <div id="wrapper">
        <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div className="form-bg-white">
            <span className="heading-line-up">Test Request Details</span>

            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="name" className="form-label">
                    Application Name<span style={{ color: "red" }}>*</span>
                  </label>
                  <input
                    id="name"
                    name="name"
                    type="text"
                    className={`form-control ${
                      formik.touched.name && formik.errors.name
                        ? "is-invalid"
                        : ""
                    }`}
                    placeholder="Application Name"
                    value={formik.values.name}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    autoComplete="off"
                    required
                  />
                  {formik.touched.name && formik.errors.name && (
                    <div className="error-message">{formik.errors.name}</div>
                  )}
                </div>
              </div>
            </div>
          {/*  <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="description" className="form-label">
                    Application Description
                    <span style={{ color: "red" }}>*</span>
                  </label>
                  <textarea
                    id="description"
                    name="description"
                    className={`form-control ${
                      formik.touched.description && formik.errors.description
                        ? "is-invalid"
                        : ""
                    }`}
                    rows="3"
                    placeholder="Application Description"
                    value={formik.values.description}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    autoComplete="off"
                    required
                  ></textarea>
                  {formik.touched.description && formik.errors.description && (
                    <div className="error-message">
                      {formik.errors.description}
                    </div>
                  )}
                </div>
              </div>
            </div>
           */}
           {formik.errors.testRequestUrls ? (
            <div className="error-message">
              {formik.errors.testRequestUrls}
            </div>
          ) : null}
            {components.map((component, index) => {
              const isChecked = formik.values.testRequestUrls.some(
                (selectedComponent) =>
                  selectedComponent.componentId === component.id
              );
              return (
                <Fragment key={index}>
                  <div className="row mt-2">
                    <div className="col-12">
                      <div className="field-box d-flex align-items-center">
                        <input
                          id={index}
                          checked={isChecked}
                          type="checkbox"
                          className="field-checkbox component-checkbox"
                          name="component"
                          onChange={(e) => {
                            onComponentSelected(index, e);
                          }}
                          autoComplete="off"
                        />
                        <label
                          htmlFor={index}
                          className="form-label m-0 ms-2 align-middle d-block w-100 cursor-pointer"
                        >
                          {component.name}
                        </label>
                      </div>
                    </div>
                  </div>
                  {formik.values.testRequestUrls.map((url, index) => {
                    return (
                      <Fragment key={index}>
                        {url.componentId == component.id ? (
                          <div className="form-bg-white mt-3">
                            <span className="heading-line-up font-size-16 bg-white">
                              {component.name} Details
                            </span>
                            <div className="row">
                              <div className="col-12">
                                {" "}
                                <label
                                  htmlFor="username"
                                  className="form-label"
                                >
                                  {" "}
                                  Credentials
                                  <span style={{ color: "red" }}>*</span>
                                  <Popover
                                    placement="topLeft"
                                    title={
                                      <div style={{ maxWidth: '450px', fontWeight: 'normal' }}>
                                        <p>
                                          Please provide the username and
                                          password that testers will use to log
                                          in to your application/website.
                                        </p>  
                                        <p>
                                          {" "}
                                          These credentials will also be
                                          utilized by the system to execute test
                                          scripts for testing purposes.
                                        </p>
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
                              </div>
                              <div className="col-sm-6 col-12">
                                <div className="custom-input mb-3">
                                  <input
                                    id={
                                      "testRequestUrls[" + index + "].username"
                                    }
                                    name={
                                      "testRequestUrls[" + index + "].username"
                                    }
                                    type="text"
                                    className={`form-control ${
                                      touched?.[
                                        modifiedComponentId(url.componentId)
                                      ]?.username &&
                                      formik.errors[
                                        "testRequestUrls[" +
                                          modifiedComponentId(url.componentId) +
                                          "].username"
                                      ]
                                        ? "is-invalid"
                                        : ""
                                    }`}
                                    placeholder="Username"
                                    value={
                                      formik.values.testRequestUrls[index]
                                        .username
                                    }
                                    onChange={formik.handleChange}
                                    onBlur={() =>
                                      handleBlur("username", url.componentId)
                                    }
                                    autoComplete="off"
                                  />
                                  {touched?.[
                                    modifiedComponentId(url.componentId)
                                  ]?.username &&
                                    formik.errors[
                                      "testRequestUrls[" +
                                        modifiedComponentId(url.componentId) +
                                        "].username"
                                    ] && (
                                      <div className="error-message">
                                        {
                                          formik.errors[
                                            "testRequestUrls[" +
                                              modifiedComponentId(
                                                url.componentId
                                              ) +
                                              "].username"
                                          ]
                                        }
                                      </div>
                                    )}
                                </div>
                              </div>
                              <div className=" custom-input col-sm-6 col-12">
                                <div className=" input-group position-relative">
                                  <input
                                    id={
                                      "testRequestUrls[" + index + "].password"
                                    }
                                    name={
                                      "testRequestUrls[" + index + "].password"
                                    }
                                    type={showPassword ? "text" : "password"}
                                    className={`form-control ${
                                      touched?.[
                                        modifiedComponentId(url.componentId)
                                      ]?.password &&
                                      formik.errors[
                                        "testRequestUrls[" +
                                          modifiedComponentId(url.componentId) +
                                          "].password"
                                      ]
                                        ? "is-invalid"
                                        : ""
                                    }`}
                                    placeholder="Password"
                                    value={
                                      formik.values.testRequestUrls[index]
                                        .password
                                    }
                                    onChange={formik.handleChange}
                                    onBlur={() =>
                                      handleBlur("password", url.componentId)
                                    }
                                    autoComplete="off"
                                  />
                                  {!(
                                    touched?.[
                                      modifiedComponentId(url.componentId)
                                    ]?.password &&
                                    formik.errors[
                                      "testRequestUrls[" +
                                        modifiedComponentId(url.componentId) +
                                        "].password"
                                    ]
                                  ) && (
                                    <button
                                      className="btn btn-outline-secondary color"
                                      type="button"
                                      onClick={() => {
                                        setShowPassword(!showPassword);
                                      }}
                                    >
                                      <i
                                        className={`bi ${
                                          showPassword
                                            ? "bi-eye-slash"
                                            : "bi-eye"
                                        }`}
                                      ></i>
                                    </button>
                                  )}
                                </div>
                                <div>
                                  {touched?.[
                                    modifiedComponentId(url.componentId)
                                  ]?.password &&
                                    formik.errors[
                                      "testRequestUrls[" +
                                        modifiedComponentId(url.componentId) +
                                        "].password"
                                    ] && (
                                      <div className="error-message">
                                        {
                                          formik.errors[
                                            "testRequestUrls[" +
                                              url.componentId.replace(
                                                /\./g,
                                                ""
                                              ) +
                                              "].password"
                                          ]
                                        }
                                      </div>
                                    )}
                                </div>
                              </div>
                            </div>

                            <div className="row">
                              <div className="col-12 ">
                                <div className="custom-input">
                                  <label
                                    htmlFor="fhirApiBaseUrl"
                                    className="form-label"
                                  >
                                    Website/UI URL:{" "}
                                    <Popover
                                      placement="topLeft"
                                      title={
                                        <div style={{ maxWidth: '450px', fontWeight: 'normal' }}>
                                          {" "}
                                          Please provide link to your
                                          application/ website. Make sure the
                                          URL is accurate and includes the
                                          correct protocol (e.g., http:// or
                                          https://).
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
                                      "testRequestUrls[" +
                                      index +
                                      "].websiteUIBaseUrl"
                                    }
                                    name={
                                      "testRequestUrls[" +
                                      index +
                                      "].websiteUIBaseUrl"
                                    }
                                    type="text"
                                    className={`form-control ${
                                      touched?.[
                                        modifiedComponentId(url.componentId)
                                      ]?.websiteUIBaseUrl &&
                                      formik.errors[
                                        "testRequestUrls[" +
                                          modifiedComponentId(url.componentId) +
                                          "].websiteUIBaseUrl"
                                      ]
                                        ? "is-invalid"
                                        : ""
                                    }`}
                                    placeholder="../website-ui-base-url/"
                                    value={
                                      formik.values.testRequestUrls[index]
                                        .websiteUIBaseUrl
                                    }
                                    onChange={formik.handleChange}
                                    onBlur={() =>
                                      handleBlur(
                                        "websiteUIBaseUrl",
                                        url.componentId
                                      )
                                    }
                                    autoComplete="off"
                                  />
                                </div>
                                {touched?.[modifiedComponentId(url.componentId)]
                                  ?.fhirApiBaseUrl &&
                                  formik.errors[
                                    "testRequestUrls[" +
                                      modifiedComponentId(url.componentId) +
                                      "].websiteUIBaseUrl"
                                  ] && (
                                    <div className="error-message">
                                      {
                                        formik.errors[
                                          "testRequestUrls[" +
                                            modifiedComponentId(
                                              url.componentId
                                            ) +
                                            "].websiteUIBaseUrl"
                                        ]
                                      }
                                    </div>
                                  )}
                              </div>
                              <div className="col-12 mt-3">
                                <div className="custom-input">
                                  <label
                                    htmlFor="fhirApiBaseUrl"
                                    className="form-label"
                                  >
                                    FHIR API Base URL:{" "}
                                    <span style={{ color: "red" }}>*</span>
                                    <Popover
                                      placement="topLeft"
                                      title={
                                        <div style={{ maxWidth: '450px', fontWeight: 'normal' }}>
                                          {" "}
                                          Please provide the base URL of the
                                          FHIR API endpoint. This URL will be
                                          used to execute testing on the
                                          specified API. Make sure to include
                                          the correct protocol (e.g., http:// or
                                          https://) and endpoint path.
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
                                      "testRequestUrls[" +
                                      index +
                                      "].fhirApiBaseUrl"
                                    }
                                    name={
                                      "testRequestUrls[" +
                                      index +
                                      "].fhirApiBaseUrl"
                                    }
                                    type="text"
                                    className={`form-control ${
                                      touched?.[
                                        modifiedComponentId(url.componentId)
                                      ]?.fhirApiBaseUrl &&
                                      formik.errors[
                                        "testRequestUrls[" +
                                          modifiedComponentId(url.componentId) +
                                          "].fhirApiBaseUrl"
                                      ]
                                        ? "is-invalid"
                                        : ""
                                    }`}
                                    placeholder="../base-url/"
                                    value={
                                      formik.values.testRequestUrls[index]
                                        .fhirApiBaseUrl
                                    }
                                    onChange={formik.handleChange}
                                    onBlur={() =>
                                      handleBlur(
                                        "fhirApiBaseUrl",
                                        url.componentId
                                      )
                                    }
                                    autoComplete="off"
                                  />
                                </div>
                                {touched?.[modifiedComponentId(url.componentId)]
                                  ?.fhirApiBaseUrl &&
                                  formik.errors[
                                    "testRequestUrls[" +
                                      modifiedComponentId(url.componentId) +
                                      "].fhirApiBaseUrl"
                                  ] && (
                                    <div className="error-message">
                                      {
                                        formik.errors[
                                          "testRequestUrls[" +
                                            modifiedComponentId(
                                              url.componentId
                                            ) +
                                            "].fhirApiBaseUrl"
                                        ]
                                      }
                                    </div>
                                  )}
                              </div>
                            </div>
                          </div>
                        ) : null}
                      </Fragment>
                    );
                  })}
                </Fragment>
              );
            })}
           
          </div>

          <div className="text-end">
            <button
              className="btn btn-primary btn-white mx-2"
              onClick={() => {
                navigate("/dashboard");
              }}
            >
              Cancel
            </button>
            <button
              disabled={!(formik.isValid && formik.dirty)}
              type="button"
              onClick={formik.handleSubmit}
              className="btn btn-primary btn-blue"
            >
              {testRequestId ? "Update" : "Submit"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterApplication;
