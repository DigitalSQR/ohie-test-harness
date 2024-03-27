import React, { Fragment, useEffect, useState } from "react";
import "./assessee.scss";
import { UserAPI } from "../../../api/UserAPI";
import { Empty, notification } from "antd";
import { Pagination } from "@mui/material";
import {
  userBadgeClasses,
  userStateConstantNames,
} from "../../../constants/user_constants";
import { useLoader } from "../../loader/LoaderContext";
import { userStatusActionLabels } from "../../../constants/user_constants";
import {
  ROLE_ID_ADMIN,
  ROLE_ID_ASSESSEE,
  USER_ROLES,
} from "../../../constants/role_constants";
import unsorted from "../../../styles/images/unsorted.png";
import sortedUp from "../../../styles/images/sort-up.png";
import sortedDown from "../../../styles/images/sort-down.png";
import { store } from "../../../store/store";

/**
 * Assessee Component:
 * This component displays a table of assessee users along with their details like name, email, requested date, company, and status.
 * It allows an admin user to approve, reject, or disable users, depending on their current status.
 * The table supports sorting by various fields such as name, email, requested date, company, and status.
 */

const Assessee = () => {
  const [availableUsers, setAvailableUsers] = useState([]);
  const initialState = userStatusActionLabels.find(
    (item) => item.label === "All"
  ).value;
  const [state, setState] = useState(initialState);
  const obj = {
    name: "desc",
    email: "desc",
    createdAt: "desc",
    companyName: "desc",
    state: "desc",
    default: "asc",
  };
  const [sortDirection, setSortDirection] = useState(obj);

  const userStates = [...userStatusActionLabels];
  const [userRoles, setUserRoles] = useState([]);

  const [sortFieldName, setSortFieldName] = useState("default");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

   //This is the default table header width according to tester/role.
  const [thPercentage, setThPercentage] = useState({
    name: "19%",
    email: "25%",
    requestedDate: "20%",
    company: "19%",
    status: "17%",
    action: "0%",
  });
  const role = ROLE_ID_ASSESSEE;
  const { showLoader, hideLoader } = useLoader();
  const pageSize = 10;
  const fetchUserByState = (
    sortFieldName,
    sortDirection,
    currentPage,
    pageSize,
    
  ) => {
    showLoader();

    UserAPI.getUserByState(
      sortFieldName,
      sortDirection,
      currentPage - 1,
      pageSize,
      state,
      role
    )
      .then((res) => {
        hideLoader();
        setAvailableUsers(res.content);
        setTotalPages(res.totalPages);
      })
      .catch((error) => {
        hideLoader();
      });
  };


  //useEffect that is used to set the user role and change with column width to certain values if the user role is admin
  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserRoles(userInfo.roleIds);

    //table header width values for admin role
    if (userInfo.roleIds.includes(ROLE_ID_ADMIN)) { 
      setThPercentage((prevState) => ({
        ...prevState,
        name: "15%",
        email: "20%",
        requestedDate: "10%",
        company: "15%",
        status: "15%",
        action: "25%",
      }));
    }
  }, []);

    //Function to handle the sorting functionality based upon a certain field name
  const handleSort = (newSortFieldName) => {
    setSortFieldName(newSortFieldName);
    const newSortDirection = { ...obj };
    newSortDirection[newSortFieldName] =
      sortDirection[newSortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    fetchUserByState(
      newSortFieldName,
      newSortDirection[newSortFieldName],
      currentPage,
      pageSize
    );
  };

    //Function to toggle between the sort icons depending upon whether the current sort direction is ascending or descending
  const renderSortIcon = (fieldName) => {
    if (sortFieldName === fieldName) {
      return (
        <img
          className="cursor-pointer"
          style={{ width: "8px" }}
          src={sortDirection[fieldName] === "asc" ? sortedUp : sortedDown}
        ></img>
      );
    }
    return (
      <img
        className="cursor-pointer"
        style={{ width: "10px" }}
        src={unsorted}
      />
    );
  };

  //Function to change the state of the assessee from active to inactive and vice verca
  const changeState = (userId, state, newState, index) => {
    showLoader();
    UserAPI.changeState(userId, state)
      .then((res) => {
        hideLoader();
        notification.success({
          className:"notificationSuccess",
          placement: "top",
          message:"Success",
          description: `Request has been ${newState}`,
        });
        availableUsers[index] = res.data;
        setAvailableUsers(availableUsers);
      })
      .catch((error) => {
        hideLoader();
      });
  };

    //Function to handle the page change in pagination
  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    fetchUserByState(
      sortFieldName,
      sortDirection[sortFieldName],
      newPage,
      pageSize
    );
  };

  //useEffect to fetch the users by state when the component initially loads or when we want to fetch the users based upon a certain state
  useEffect(() => {
    fetchUserByState(
      sortFieldName,
      sortDirection[sortFieldName],
      currentPage,
      pageSize,
      state
    );
  }, [state]);

  return (
    <div id="assessee">
      <div id="wrapper">
        <div className="col-12">
          <div className="row mb-4 justify-content-between">
            <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
              <div className="d-flex align-items-baseline ">
                <span className="pe-3 text-nowrap ">Status : </span>
                <div className="">
                  <select
                    className="form-select custom-select custom-select-sm"
                    aria-label="Default select example"
                    value={state}
                    onChange={(e) => {
                      if (e.target.value.includes(",")) {
                        setState(e.target.value.split(","));
                      } else {
                        setState(e.target.value);
                      }
                    }}
                  >
                    {userStates.map(
                      (userState) => (
                        console.log(userState.value),
                        (
                          <option value={userState.value} key={userState.value}>
                            {userState.label}
                          </option>
                        )
                      )
                    )}
                  </select>
                </div>
              </div>
            </div>
          </div>
          <div className="table-responsive">
            <table className=" data-table capitalize-words">
              <thead>
                <tr>
                  <th style={{ width: thPercentage.name }}>
                    NAME
                    <span
                      className="ps-1 "
                      href="#"
                      onClick={() => handleSort("name")}
                    >
                      {renderSortIcon("name")}
                    </span>
                  </th>
                  <th style={{ width: thPercentage.email }}>
                    Email
                    <span
                      className="ps-1"
                      href="# "
                      onClick={() => handleSort("email")}
                    >
                      {renderSortIcon("email")}
                    </span>
                  </th>
                  <th style={{ width: thPercentage.requestedDate }}>
                    requested date
                    <span
                      className="ps-1"
                      href="# "
                      onClick={() => handleSort("createdAt")}
                    >
                      {renderSortIcon("createdAt")}
                    </span>
                  </th>
                  <th style={{ width: thPercentage.company }}>
                    Company
                    <span
                      className="ps-1"
                      href="# "
                      onClick={() => handleSort("companyName")}
                    >
                      {renderSortIcon("companyName")}
                    </span>
                  </th>
                  <th style={{ width: thPercentage.status }}>
                    Status
                    <span
                      className="ps-1"
                      href="# "
                      onClick={() => handleSort("state")}
                    >
                      {renderSortIcon("state")}
                    </span>
                  </th>
                  {userRoles.includes(USER_ROLES.ROLE_ID_ADMIN) && (
                    <th style={{ width: thPercentage.action }}>
                      ACTIONS
                      <span
                        className="ps-1"
                        href="# "
                        onClick={() => handleSort("default")}
                      >
                        {renderSortIcon("default")}
                      </span>
                    </th>
                  )}
                </tr>
              </thead>
              <tbody>
                {availableUsers.length === 0 ? (
                  <tr>
                    <td className="text-center " colSpan="6">
                      <Empty description="No Record Found." />
                    </td>
                  </tr>
                ) : null}
                {availableUsers.map((user, index) => {
                  const formattedDate = new Date(
                    user.meta.createdAt
                  ).toLocaleDateString("en-GB", {
                    day: "numeric",
                    month: "short",
                    year: "numeric",
                  });
                  let currentStatus = userStateConstantNames[user.state];
                  return (
                    <Fragment key={user.id}>
                      <tr>
                        <td className="fw-bold">{user.name}</td>
                        <td className="toLowerCase-words">{user.email}</td>
                        <td>{formattedDate}</td>
                        {user.companyName ? (
                          <td className="fw-bold">{user.companyName}</td>
                        ) : (
                          <td>&ndash;</td>
                        )}

                        <td>
                          <span
                            className={"status " + userBadgeClasses[user.state]}
                          >
                            {currentStatus.toLowerCase()}
                          </span>
                        </td>
                        {userRoles.includes(USER_ROLES.ROLE_ID_ADMIN) && (
                          <td className=" no-wrap">
                            {" "}
                            {user.state === "user.status.active" && (
                              <Fragment>
                                <span
                                  className="cursor-pointer text-warning font-size-12 fw-bold"
                                  onClick={() => {
                                    changeState(
                                      user.id,
                                      "user.status.inactive",
                                      "DISABLE",
                                      index
                                    );
                                  }}
                                >
                                  <i className="bi bi-ban text-warning font-size-16"></i>{" "}
                                  DISABLE
                                </span>
                              </Fragment>
                            )}
                            {user.state === "user.status.approval.pending" && (
                              <Fragment>
                                <span
                                  className="cursor-pointer text-success font-size-12 fw-bold"
                                  onClick={() => {
                                    changeState(
                                      user.id,
                                      "user.status.active",
                                      "APPROVE",
                                      index
                                    );
                                  }}
                                >
                                  <span>
                                    <i className="bi bi-check-circle-fill  font-size-16"></i>{" "}
                                    APPROVE{" "}
                                  </span>
                                </span>
                                &nbsp;
                                <span
                                  className="ps-3 cursor-pointer text-danger font-size-12 fw-bold"
                                  onClick={() => {
                                    changeState(
                                      user.id,
                                      "user.status.inactive",
                                      "REJECT",
                                      index
                                    );
                                  }}
                                >
                                  <i className="bi bi-x-circle-fill  font-size-16"></i>{" "}
                                  REJECT{" "}
                                </span>
                              </Fragment>
                            )}
                            {user.state === "user.status.inactive" && (
                              <span
                                className="cursor-pointer text-success font-size-12 fw-bold"
                                onClick={() => {
                                  changeState(
                                    user.id,
                                    "user.status.active",
                                    "Active",
                                    index
                                  );
                                }}
                              >
                                <span>
                                  <i className="bi bi-check-circle-fill text-success font-size-16"></i>{" "}
                                  ENABLE
                                </span>
                              </span>
                            )}
                          </td>
                        )}
                      </tr>
                    </Fragment>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
        {totalPages > 1 && (
          <Pagination
            className="pagination-ui"
            count={totalPages}
            page={currentPage}
            onChange={handleChangePage}
            variant="outlined"
            shape="rounded"
          />
        )}
      </div>
    </div>
  );
};

export default Assessee;
