import React, { Fragment, useEffect, useState } from "react";
import sortIcon from "../styles/images/sort-icon.png";
import "../scss/_registrationRequest.scss";
import { UserAPI } from "../api/UserAPI";
import { useLoader } from "./loader/LoaderContext";
import { notification } from "antd";
import { userBadgeClasses } from "../constants/user_constants";

const UserRegistration = () => {
	const { showLoader, hideLoader } = useLoader();
	const [availableUsers, setAvailableUsers] = useState([]);
	const [filter, setFilter] = useState("");
	const pendingstate = "user.status.approval.pending";
	

	const fetchUserByState = ()=>{
		UserAPI.getUserByState(filter).then((res)=>{
			setAvailableUsers(res.content);
		});
	}

	const changeState = (userId, state,newState) => {
		UserAPI.changeState(userId, state).then((res) => {
			notification.success({
				description:`Request has been ${newState}`,
				placement:"bottom-left"
			})
			fetchUserByState();
		}).catch((error)=>{
			notification.warning({
				description:"Request Change failed.",
				placement:"bottom-left"
			})
			throw error;})
	}

	useEffect(()=>{
		fetchUserByState();
	},[filter]); 

	return (
		<div id="wrapper">
			<div class="col-12">
				<div class="row mb-2 justify-content-between">
					{/* <div class="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12">
						<div class="custom-input custom-input-sm mb-3">
							<input
								type="text"
								class="form-control"
								placeholder="Search"
							/>
						</div>
					</div> */}
					<div class="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
						<div class="d-flex align-items-baseline ">
							<span class="pe-3 text-nowrap">Status :</span>
							<div class="mb-3">
								<select
									class="form-select custom-select custom-select-sm"
									aria-label="Default select example"
									value={filter}
									onChange={(e) => {
										setFilter(e.target.value);
									}}
								>
									<option value="">
										All
									</option>
									<option value="user.status.active">
										Accepted
									</option>
									<option value="user.status.rejected">
										Rejected
									</option>
									<option value="user.status.approval.pending">Pending</option>
								</select>
							</div>
						</div>
					</div>
				</div>

				{/* --filter END-- */}

				<div class="table-responsive">
					<table class=" data-table">
						<thead>
							<tr>
								<th>
									NAME{" "}
									<a class="ps-1" href="#">
										<img src={sortIcon} alt="e" />
									</a>
								</th>
								<th>
									Email{" "}
									<a class="ps-1" href="# ">
										<img src={sortIcon} alt="e" />
									</a>
								</th>
								<th>requested date</th>
								{/* <th>
									Company{" "}
									<a class="ps-1" href="#v">
										<img src={sortIcon} alt="e" />
									</a>
								</th> */}
								<th>
									Status{" "}
									<a class="ps-1" href="# ">
										<img src={sortIcon} alt="e" />
									</a>
								</th>
								<th>CHOOSE ACTION</th>
							</tr>
						</thead>
						<tbody>
							{availableUsers.length === 0 ? (
								<tr>
									<td className="text-center" colSpan="6">
										There are no user registration requests
										for this state
									</td>
								</tr>
								) : null}
							{availableUsers.map((user) => {
								const formattedDate = new Date(
									user.meta.createdAt
								).toLocaleDateString("en-GB", {
									day: "numeric",
									month: "short",
									year: "numeric",
								});
								let currentStatus = "";
								if (
									user.state ==
									"user.status.verification.pending"
								) {
									currentStatus = "Email not verified";
								} else if(user.state) {
									const parts = user.state.split(".");

									currentStatus = parts[parts.length - 1];
								}
								return (
									<Fragment>
										<tr>
											<td>{user.name}</td>
											<td>{user.email}</td>
											<td>{formattedDate}</td>
											{/* <td>{user.company}</td> */}
											<td>
												<span className={userBadgeClasses[user.state]}>
													{currentStatus.toUpperCase()}
												</span>
											</td>
											<td class=" no-wrap">
												{user.state == pendingstate && (
													<Fragment>
														<button
															type="button"
															class="btn  btn-sm text-uppercase approval-action-button"
															onClick={() => {
																changeState(
																	user.id,
																	"user.status.active",
																	"Approved"
																);
															}}
														>
															<i
																class="bi bi-check-circle-fill text-green-50 font-weight-light  font-size-16"
																style={{
																	fontWeight:
																		"lighter  !important",
																}}
															></i>{" "}
															APPROVE
														</button>
														<button
															type="button"
															class="btn  btn-sm text-uppercase approval-action-button"
															onClick={() => {
																changeState(
																	user.id,
																	"user.status.rejected",
																	"Rejected"
																);
															}}
														>
															<i
																class="bi bi-x-circle-fill text-red font-size-16l  font-weight-light  font-size-16"
																style={{
																	fontWeight:
																		"lighter  !important",
																}}
															></i>{" "}
															REJECT
														</button>
													</Fragment>
												)}
											</td>{" "}
										</tr>
									</Fragment>
								);
							})}
						</tbody>
					</table>
				</div>
			</div>
		</div>
	);
};

export default UserRegistration;
