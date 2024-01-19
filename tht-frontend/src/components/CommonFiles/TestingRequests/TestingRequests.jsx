import React, { useEffect, useState } from "react";
import "./application-status.scss";
import "./_table.scss";
import { TestRequestStateLabels, TestRequestStateConstants, TestRequestStateConstantNames, StateBadgeClasses } from "../../../constants/test_requests_constants.js";
import { USER_ROLES } from "../../../constants/role_constants.js";
import { TestRequestAPI } from "../../../api/TestRequestAPI.js";
import { UserAPI } from "../../../api/UserAPI.js";
import { useLoader } from "../../loader/LoaderContext.js";
import ComponentIdConnector from "../../connectors/ComponentIdConnector/ComponentIdConnector.js";
import { notification } from "antd";
import { formatDate } from "../../../utils/utils.js";
import UserIdConnector from "../../connectors/UserIdConnector/UserIdConnector.jsx";
import { useNavigate } from "react-router-dom";

const TestingRequests = () => {
	const testRequestStates = [...TestRequestStateLabels, { label: "All", value: '' }];
	const { showLoader, hideLoader } = useLoader();
	const [filterState, setFilterState] = useState(TestRequestStateConstants.TEST_REQUEST_STATUS_PENDING);
	const [userRoles, setUserRoles] = useState([USER_ROLES.ROLE_ID_ASSESSEE]);
	const [testRequests, setTestRequests] = useState([]);
	const navigate = useNavigate();

	const fetchTestRequests = () => {
		TestRequestAPI.getTestRequestsByState(filterState).then((res) => {
			setTestRequests(res.content);
		}).catch((err) => {
			notification.error({
				placement: "bottomRight",
				message: 'Oops! Error fetching test requests!'
			});
			console.log(err);
		});
	};

	useEffect(() => {
		UserAPI.viewUser().then((res) => {
			setUserRoles(res.roleIds);
		});
		fetchTestRequests();
	}, [filterState])

	const changeState = (testRequestId, state) => {
		showLoader();
		TestRequestAPI.changeState(testRequestId, state)
			.then((res) => {
				notification.success({
					placement: "bottomRight",
					message: 'Status updated successfully!'
				});
				fetchTestRequests();
				hideLoader();
			})
			.catch((err) => {
				notification.error({
					placement: "bottomRight",
					message: 'Oops! Something went wrong!'
				});
				console.log(err);
				hideLoader();
			})
	}

	return (
		<div>
			<div id="wrapper">
				<div className="col-12 pt-3">

					<div className="row mb-2 justify-content-between">
						<div className="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12">
							<div className="custom-input custom-input-sm mb-3">
								<div className="d-flex align-items-baseline">
									<span className="pe-3 text-nowrap">Status :</span>
									<div className="mb-3">
										<select
											onChange={(e) => { setFilterState(e.target.value) }}
											value={filterState}
											className="form-select custom-select custom-select-sm"
											aria-label="Default select example"
										>
											{
												testRequestStates.map(testRequestState => (
													<option value={testRequestState.value} key={testRequestState.value}>{testRequestState.label}</option>
												))
											}
										</select>
									</div>
								</div>
							</div>
						</div>
						<div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
							<div className="d-flex align-items-baseline justify-content-end">
								<button onClick={() => { navigate("/dashboard/register-application") }} type="button" className="btn btn-sm btn-outline-secondary menu-like-item">
									<i className="bi bi-plus"></i>
									Register Application
								</button>
							</div>
						</div>

					</div>

					<div className="table-responsive">
						<table className=" data-table">
							<thead>
								<tr>
									<th>APP NAME</th>
									<th>DATE OF APPLICATION</th>
									<th>Assessee</th>
									<th>COMPONENTS</th>
									<th>APP URL</th>
									<th>STATUS</th>
									{
										userRoles.includes(USER_ROLES.ROLE_ID_ADMIN)
											&& (filterState === TestRequestStateConstants.TEST_REQUEST_STATUS_PENDING
												|| filterState === TestRequestStateConstants.TEST_REQUEST_STATUS_DRAFT) ?
											<th>Action</th>
											: null
									}
								</tr>
							</thead>
							<tbody>
								{
									testRequests.length === 0 ?
										<>
											<tr>
												<td className="text-center" colSpan={7}>No test requests found in the {TestRequestStateConstantNames[filterState]} stage</td>
											</tr>
										</>
										: null
								}
								{
									testRequests.map((testRequest) => (
										testRequest.testRequestUrls.map((testUrl, index) => (
											
											<tr key={testUrl.username}>
												{index === 0 ?
													<>
														<td rowSpan={testRequest.testRequestUrls.length}>{testRequest.productName}</td>
														<td rowSpan={testRequest.testRequestUrls.length}>{formatDate(testRequest.meta.updatedAt)}</td>
														<td rowSpan={testRequest.testRequestUrls.length}><UserIdConnector isLink={true} userId={testRequest.assesseeId} /></td>
													</>
													: null
												}
												<td className="row-spaned-td">
													<ComponentIdConnector componentId={testUrl.componentId} />
												</td>
												<td  className="row-spaned-td">{testUrl.baseUrl}</td>
												{
													index === 0 ?
														<>
															<td rowSpan={testRequest.testRequestUrls.length}>
																<span className={StateBadgeClasses[testRequest.state]}>{TestRequestStateConstantNames[testRequest.state]}</span>
															</td>
															{
																userRoles.includes(USER_ROLES.ROLE_ID_ADMIN)
																	&& testRequest.state === TestRequestStateConstants.TEST_REQUEST_STATUS_PENDING ?
																	(<td rowSpan={testRequest.testRequestUrls.length} className=" no-wrap">
																		<button onClick={() => { changeState(testRequest.id, TestRequestStateConstants.TEST_REQUEST_STATUS_ACCEPTED) }}
																			type="button" className="text-uppercase btn btn-sm approval-action-button text-uppercase">
																			<span>
																				<i className="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
																				APPROVE{" "}
																			</span>
																		</button>
																		<button onClick={() => { changeState(testRequest.id, TestRequestStateConstants.TEST_REQUEST_STATUS_REJECTED) }}
																			type="button" className="mx-1 btn btn-sm approval-action-button text-uppercase">
																			<i className="bi bi-x-circle-fill text-red font-size-16"></i>{" "}
																			REJECT{" "}
																		</button>
																	</td>)
																	: null
															}
														</>
														: null
												}
											</tr>
										))
									))
								}
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	);
};

export default TestingRequests;
