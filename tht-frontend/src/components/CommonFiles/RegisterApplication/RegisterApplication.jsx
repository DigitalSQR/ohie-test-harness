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

	useEffect(() => {
		const userInfo = store.getState().userInfoSlice;
		setUserId(userInfo.id);

		ComponentAPI.getCompoents()
			.then((res) => {
				setComponents(res.content);
				hideLoader();
			})
			.catch((err) => {
				notification.error({
					placement: "bottomRight",
					message: err.data?.message,
				});
				hideLoader();
			});
		ComponentAPI.getCompoents()
			.then((res) => {
				setComponents(res.content);
				hideLoader();
			})
			.catch((err) => {
				notification.error({
					placement: "bottomRight",
					message: err.data?.message,
				});
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
		}

		if (values.productName === "") {
			errors.productName = "Product Name is required";
		}

		if (values.description === "") {
			errors.description = "Application Description is required";
		}

		return errors;
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
							navigate("/dashboard/testing-requests");
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
			);
		},
	});

	const addOrRemoveTestUrls = (component, i) => {
		var turls = formik.getFieldHelpers("testRequestUrls");
		if (component.isSelected) {
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

	const handleMouseDown = () => {
		setShowPassword(true);
	};

	const handleMouseUp = () => {
		setShowPassword(false);
	};

	return (
		<form>
			<div id="wrapper">
				<div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
					<div className="form-bg-white">
						<span className="heading-line-up">
							Application Details
						</span>

						<div className="row">
							<div className="col-sm-6 col-12">
								<div className="custom-input mb-3">
									<label
										htmlFor="name"
										className="form-label"
									>
										Application Name
									</label>
									<input
										id="name"
										name="name"
										type="text"
										className={`form-control ${
											formik.touched.name &&
											formik.errors.name
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
									<div className="text-danger">
										{formik.errors.name}
									</div>
								)}
							</div>

							<div className="col-sm-6 col-12">
								<div className="custom-input mb-3">
									<label
										htmlFor="productName"
										className="form-label"
									>
										Product Name
									</label>
									<input
										id="productName"
										name="productName"
										type="text"
										className={`form-control ${
											formik.touched.productName &&
											formik.errors.productName
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
								{formik.touched.productName &&
									formik.errors.productName && (
										<div className="text-danger">
											{formik.errors.productName}
										</div>
									)}
							</div>
						</div>

						<div className="row">
							<div className="col-12">
								<div className="custom-input mb-3">
									<label
										htmlFor="description"
										className="form-label"
									>
										Application Description
									</label>
									<textarea
										id="description"
										name="description"
										className={`form-control ${
											formik.touched.description &&
											formik.errors.description
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
								{formik.touched.description &&
									formik.errors.description && (
										<div className="text-danger">
											{formik.errors.description}
										</div>
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
													id="component"
													type="checkbox"
													className="field-checkbox component-checkbox"
													name="component"
													onChange={(e) => {
														onComponentSelected(
															index,
															e
														);
													}}
													autoComplete="off"
												/>
												<label
													htmlFor="component"
													className="form-label mx-2 align-middle"
												>
													{component.name}
												</label>
											</div>
										</div>
									</div>
									{formik.values.testRequestUrls.map(
										(url, index) => {
											return (
												<Fragment key={index}>
													{url.componentId ==
													component.id ? (
														<div className="form-bg-white mt-3">
															<span className="heading-line-up">
																{component.name}{" "}
																Details
															</span>
															<div className="row">
																<div className="col-12">
																	{" "}
																	<label
																		htmlFor="username"
																		className="form-label"
																	>
																		Credentials
																	</label>
																</div>
																<div className="col-sm-6 col-12">
																	<div className="custom-input mb-3">
																		<input
																			id="username"
																			name={
																				"testRequestUrls[" +
																				index +
																				"].username"
																			}
																			type="text"
																			className="form-control"
																			placeholder="Username"
																			value={
																				formik
																					.values
																					.testRequestUrls[
																					index
																				]
																					.username
																			}
																			onChange={
																				formik.handleChange
																			}
																			autoComplete="off"
																		/>
																	</div>
																</div>
																<div className="col-sm-6 col-12">
																	<div className="custom-input mb-3" style={{ position: 'relative' }}>
																		<input
																			id="password"
																			name={
																				"testRequestUrls[" +
																				index +
																				"].password"
																			}
                                      type={showPassword ? "text" : "password"}
																			className="form-control"
																			placeholder="Password"
																			value={
																				formik
																					.values
																					.testRequestUrls[
																					index
																				]
																					.password
																			}
																			onChange={
																				formik.handleChange
																			}
																			autoComplete="off"
																		/>
																	<div
																		style={{
																			position:
																				"absolute",
																			top: "50%",
																			right: "10px",
																			transform:
																				"translateY(-50%)",
																			cursor: "pointer",
																		}}
																		onMouseDown={
																			handleMouseDown
																		}
																		onMouseUp={
																			handleMouseUp
																		}
																	>
																		{showPassword ? (
																			<i className="bi bi-eye-slash-fill"></i>
																		) : (
																			<i className="bi bi-eye-fill"></i>
																		)}
																	</div>
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
																			Base
																			Url:{" "}
																			{/* <i className="bi bi-info-circle-fill cursor-pointer"></i> */}
																		</label>
																		<input
																			id="baseUrl"
																			name={
																				"testRequestUrls[" +
																				index +
																				"].baseUrl"
																			}
																			type="text"
																			className="form-control"
																			placeholder="../base-url/"
																			value={
																				formik
																					.values
																					.testRequestUrls[
																					index
																				]
																					.baseUrl
																			}
																			onChange={
																				formik.handleChange
																			}
																			autoComplete="off"
																		/>
																	</div>
																</div>
															</div>
														</div>
													) : null}
												</Fragment>
											);
										}
									)}
								</Fragment>
							);
						})}
						{formik.errors.testRequestUrls ? (
							<div className="text-danger">
								{formik.errors.testRequestUrls}
							</div>
						) : null}
					</div>

					<div className="my-4 text-end">
						<button
							className="btn btn-primary btn-white py-2 font-size-14 mx-2"
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
							className="btn btn-primary btn-blue py-2 font-size-14"
							style={{
								background: !(formik.isValid && formik.dirty)
									? "#6e5f1"
									: "#009fc8",
							}}
						>
							submit
						</button>
					</div>
				</div>
			</div>
		</form>
	);
};

export default RegisterApplication;
