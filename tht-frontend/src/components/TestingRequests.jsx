import React, { useEffect, useState } from "react";
import "../scss/application-status.scss";
import "../scss/_table.scss";
import { TestRequestStateLabels, TestRequestStateConstants, TestRequestStateConstantNames, StateBadgeClasses } from "../constants/test_requests_constants.js";
import { USER_ROLES } from "../constants/role_constants.js";
import { TestRequestAPI } from "../api/TestRequestAPI";
import { UserAPI } from "../api/UserAPI.js";
import { useLoader } from "./loader/LoaderContext.js";
import ComponentIdConnector from "./connectors/ComponentIdConnector/ComponentIdConnector.js";
import { notification } from "antd";
import { formatDate } from "../utils/utils.js";

const TestingRequests = () => {
	const testRequestStates = [...TestRequestStateLabels, { label: "All", value: '' }];
	const { showLoader, hideLoader } = useLoader();
	const [filterState, setFilterState] = useState('');
	const [userRoles, setUserRoles] = useState([USER_ROLES.ROLE_ID_ASSESSEE]);
	const [testRequests, setTestRequests] = useState([]);


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

					<div class="row justify-content-between">
						<div class="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
							<div class="d-flex align-items-baseline">
								<span class="pe-3 text-nowrap">Status :</span>
								<div class="mb-3">
									<select
										onChange={(e) => { setFilterState(e.target.value) }}
										value={filterState}
										class="form-select custom-select custom-select-sm"
										aria-label="Default select example"
									>
										{
											testRequestStates.map(testRequestState => (
												<option value={testRequestState.value}>{testRequestState.label}</option>
											))
										}
									</select>
								</div>
							</div>
						</div>
					</div>

					<div className="table-responsive">
						<table className=" data-table">
							<thead>
								<tr>
									<th>APP NAME</th>
									<th>DATE OF APPLICATION</th>
									<th>APP URL</th>
									<th>COMPONENTS</th>
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
												<td className="text-center" colSpan={6}>No test requests found in the {TestRequestStateConstantNames[filterState]} stage</td>
											</tr>
										</>
										: null
								}
								{
									testRequests.map((testRequest) => (
										testRequest.testRequestUrls.map((testUrl, index) => (
											<tr>
												{index === 0 ?
													<>
														<td rowSpan={testRequest.testRequestUrls.length}>{testRequest.productName}</td>
														<td rowSpan={testRequest.testRequestUrls.length}>{formatDate(testRequest.meta.updatedAt)}</td>
													</>
													: null
												}
												<td>{testUrl.baseUrl}</td>
												<td>
													<ComponentIdConnector componentId={testUrl.componentId} />
												</td>
												{
													index === 0 ?
														<>
															<td rowSpan={testRequest.testRequestUrls.length}>
																<span className={StateBadgeClasses[testRequest.state]}>{TestRequestStateConstantNames[testRequest.state]}</span>
															</td>
															{
																userRoles.includes(USER_ROLES.ROLE_ID_ADMIN)
																	&& testRequest.state === TestRequestStateConstants.TEST_REQUEST_STATUS_PENDING ?
																	(<td class=" no-wrap">
																		<button onClick={() => { changeState(testRequest.id, TestRequestStateConstants.TEST_REQUEST_STATUS_ACCEPTED) }}
																			type="button" className="text-uppercase btn btn-sm approval-action-button text-uppercase">
																			<span>
																				<i class="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
																				APPROVE{" "}
																			</span>
																		</button>
																		<button onClick={() => { changeState(testRequest.id, TestRequestStateConstants.TEST_REQUEST_STATUS_REJECTED) }}
																			type="button" className="btn btn-sm approval-action-button text-uppercase">
																			<i class="bi bi-x-circle-fill text-red font-size-16"></i>{" "}
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
