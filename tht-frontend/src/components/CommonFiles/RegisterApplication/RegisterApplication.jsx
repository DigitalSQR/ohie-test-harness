import React, { Fragment, useEffect, useState } from "react";
import "./_registrationApplication.scss";
import { useNavigate } from "react-router-dom";
import { useFormik } from "formik";
import { UserAPI } from "../../../api/UserAPI.js";
import { ComponentAPI } from "../../../api/ComponentAPI.js";
import { useLoader } from "../../loader/LoaderContext.js";
import { TestRequestAPI } from "../../../api/TestRequestAPI.js";
import { notification } from "antd";
import { TestRequestStateConstants } from "../../../constants/test_requests_constants.js";
import { CREATE_VALIDATION } from "../../../constants/validation_constants.js";
import { store } from "../../../store/store.js";

const RegisterApplication = () => {
  const navigate = useNavigate();
  const { showLoader, hideLoader } = useLoader();
  const [components, setComponents] = useState([]);
  const [userId, setUserId] = useState();
  const [showPassword, setShowPassword] = useState(false);
  const [touched, setTouched] = useState({});

  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserId(userInfo.id);
    const params={};
    params.state="component.status.active"
    ComponentAPI.getComponents(params)
      .then((res) => {
        setComponents(res.content);
        hideLoader();
      })
      .catch((err) => {       
        hideLoader();
      });
  }, []);

  // A custom validation function. This must return an object
  // which keys are symmetrical to our values/initialValues
  const validate = (values) => {
    const errors = {};

    if (values.testRequestUrls.length === 0) {
      errors.testRequestUrls = "Please select atleast one component";
    }

    if (values.name === "") {
      errors.name = "Application Name is required";
    }else if(values.name.length < 3){
      errors.name = "Application name must be of minimum 3 characters"
    }else if (values.name.length > 1000) {
      errors.name = "Application name must have less than 1000 characters.";
    }

    if (values.productName === "") {
      errors.productName = "Product Name is required";
    }else if(values.productName.length < 3){
      errors.productName = "Product name must be of minimum 3 characters"
    }else if (values.productName.length > 1000) {
      errors.productName = "Product name must have less than 1000 characters.";
    }

    if (values.description === "") {
      errors.description = "Application Description is required";
    }else if (values.description.length > 1000) {
      errors.description = "Application description must have less than 1000 characters.";
    }

    values.testRequestUrls.forEach((url, index) => {
      if (url.username === "") {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].username`
        ] = "Username is required";
      }
      else if (url.username.length > 255) {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].username`
        ] = "Username must have less than 255 characters";
      }
      if (url.password === "") {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].password`
        ] = "Password is required";
      }
      else if (url.password.length > 255) {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].password`
        ] = "Password must have less than 255 characters";
      }
      if (url.baseUrl === "") {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].baseUrl`
        ] = "BaseUrl is required";
      }
      else if (url.baseUrl.length > 255) {
        errors[
          `testRequestUrls[${modifiedComponentId(url.componentId)}].baseUrl`
        ] = "BaseUrl must have less than 255 characters";
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
      productName: "",
      description: "",
      assesseeId: "",
      testRequestUrls: [],
    },
    validate,
    onSubmit: (values) => {
      showLoader();
      formik.values.assesseeId = userId;
      TestRequestAPI.validateTestRequest(CREATE_VALIDATION, values).then(
        (res) => {
          if (res.length == 0) {
            TestRequestAPI.createTestRequest(values).then((res) => {
              notification.success({
                placement: "bottomRight",
                message: `Successfully Created! Waiting for Approval`,
              });
              hideLoader();
              navigate("/testing-requests");
            }).catch((error) => {
              hideLoader();
            });
          } else {
            res.forEach((err) => {
              notification.error({
                placement: "bottomRight",
                message: err.message,
              });
            });
            hideLoader();
          }
        }
      ).catch((error) => {
       
      });
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
          baseUrl: false,
        },
      });

      turls.setValue([
        ...formik.values.testRequestUrls,
        {
          username: "",
          password: "",
          baseUrl: "",
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
      };
      updatedTouched[modifiedComponentId(componentId)][key] = true;
      return updatedTouched;
    });
  };

  return (
    <div id="registerApplication">
      <div id="wrapper">
        <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div className="form-bg-white">
            <span className="heading-line-up">Test Request Details</span>

            <div className="row">
              <div className="col-sm-6 col-12">
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
                </div>
                {formik.touched.name && formik.errors.name && (
                  <div className="text-danger">{formik.errors.name}</div>
                )}
              </div>

              <div className="col-sm-6 col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="productName" className="form-label">
                    Product Name<span style={{ color: "red" }}>*</span>
                  </label>
                  <input
                    id="productName"
                    name="productName"
                    type="text"
                    className={`form-control ${
                      formik.touched.productName && formik.errors.productName
                        ? "is-invalid"
                        : ""
                    }`}
                    placeholder="Product Name"
                    value={formik.values.productName}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    autoComplete="off"
                    required
                  />
                </div>
                {formik.touched.productName && formik.errors.productName && (
                  <div className="text-danger">{formik.errors.productName}</div>
                )}
              </div>
            </div>
            <div className="row">
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
                </div>
                {formik.touched.description && formik.errors.description && (
                  <div className="text-danger">{formik.errors.description}</div>
                )}
              </div>
            </div>

            {components.map((component, index) => {
              return (
                <Fragment key={index}>
                  <div className="row mt-2">
                    <div className="col-12">
                      <div className="field-box">
                        <input
                          id={index}
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
                          className="form-label mx-2 align-middle"
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
                            <span className="heading-line-up">
                              {component.name} Details
                            </span>
                            <div className="row">
                              <div className="col-12">
                                {" "}
                                <label
                                  htmlFor="username"
                                  className="form-label"
                                >
                                  Credentials
                                  <span style={{ color: "red" }}>*</span>
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
                                </div>
                                {touched?.[modifiedComponentId(url.componentId)]
                                  ?.username &&
                                  formik.errors[
                                    "testRequestUrls[" +
                                      modifiedComponentId(url.componentId) +
                                      "].username"
                                  ] && (
                                    <div className="text-danger">
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
                              <div className=" custom-input col-sm-6 col-12">
                                <div className=" input-group mb-3 position-relative">
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
                                      <div className="text-danger">
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
                              <div className="col-12">
                                <div className="custom-input mb-3">
                                  <label
                                    htmlFor="baseUrl"
                                    className="form-label"
                                  >
                                    Base Url:{" "}
                                    <span style={{ color: "red" }}>*</span>
                                    {/* <i className="bi bi-info-circle-fill cursor-pointer"></i> */}
                                  </label>
                                  <input
                                    id={
                                      "testRequestUrls[" + index + "].baseUrl"
                                    }
                                    name={
                                      "testRequestUrls[" + index + "].baseUrl"
                                    }
                                    type="text"
                                    className={`form-control ${
                                      touched?.[
                                        modifiedComponentId(url.componentId)
                                      ]?.baseUrl &&
                                      formik.errors[
                                        "testRequestUrls[" +
                                          modifiedComponentId(url.componentId) +
                                          "].baseUrl"
                                      ]
                                        ? "is-invalid"
                                        : ""
                                    }`}
                                    placeholder="../base-url/"
                                    value={
                                      formik.values.testRequestUrls[index]
                                        .baseUrl
                                    }
                                    onChange={formik.handleChange}
                                    onBlur={() =>
                                      handleBlur("baseUrl", url.componentId)
                                    }
                                    autoComplete="off"
                                  />
                                </div>
                                {touched?.[modifiedComponentId(url.componentId)]
                                  ?.baseUrl &&
                                  formik.errors[
                                    "testRequestUrls[" +
                                      modifiedComponentId(url.componentId) +
                                      "].baseUrl"
                                  ] && (
                                    <div className="text-danger">
                                      {
                                        formik.errors[
                                          "testRequestUrls[" +
                                            modifiedComponentId(
                                              url.componentId
                                            ) +
                                            "].baseUrl"
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
            {formik.errors.testRequestUrls ? (
              <div className="text-danger">{formik.errors.testRequestUrls}</div>
            ) : null}
          </div>

          <div className="text-end">
            <button
              className="btn btn-primary btn-white py-2 mx-2"
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
              className="btn btn-primary btn-blue py-2"
            >
              submit
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterApplication;
