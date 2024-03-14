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
  const role = ROLE_ID_ASSESSEE;
  const { showLoader, hideLoader } = useLoader();
  const pageSize = 10;
  const fetchUserByState = (
    sortFieldName,
    sortDirection,
    currentPage,
    pageSize,
    state
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

  const thPercentage = { 
    //this is the width for the table header which will vary according to the role.
    //this is the default header width set according to the tester/assessee role
    name: "15%",
    email: "20%",
    requestedDate: "20%",
    company: "15%",
    status: "15%",
    action: "0%",
  };

  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserRoles(userInfo.roleIds);

    if(userInfo.roleIds.includes(ROLE_ID_ADMIN)){
      thPercentage.name= "15%";
      thPercentage.email= "20%";
      thPercentage.requestedDate= "10%";
      thPercentage.company= "15%";
      thPercentage.status= "15%";
      thPercentage.action= "25%";
    }
  }, []);

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

  const changeState = (userId, state, newState, index) => {
    showLoader();
    UserAPI.changeState(userId, state)
      .then((res) => {
        hideLoader();
        notification.success({
          description: `Request has been ${newState}`,
          placement: "bottom-left",
        });
        availableUsers[index] = res.data;
        setAvailableUsers(availableUsers);
      })
      .catch((error) => {
        hideLoader();
      });
  };

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    fetchUserByState(
      sortFieldName,
      sortDirection[sortFieldName],
      newPage,
      pageSize
    );
  };

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
                      className="ps-1"
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
                    <td className="text-center" colSpan="6">
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
                        <td>{user.name}</td>
                        <td className="toLowerCase-words">{user.email}</td>
                        <td>{formattedDate}</td>
                        {user.companyName ? (
                          <td>{user.companyName}</td>
                        ) : (
                          <td>&ndash;</td>
                        )}

                        <td>
                          <span
                            className={"status " + userBadgeClasses[user.state]}
                          >
                            {currentStatus.toUpperCase()}
                          </span>
                        </td>
                        {userRoles.includes(USER_ROLES.ROLE_ID_ADMIN) && (
                          <td className=" no-wrap">
                            {" "}
                            {user.state === "user.status.active" && (
                              <Fragment>
                                <span
                                  className="cursor-pointer"
                                  onClick={() => {
                                    changeState(
                                      user.id,
                                      "user.status.inactive",
                                      "DISABLE",
                                      index
                                    );
                                  }}
                                >
                                  <i className="bi bi-ban-fill text-red font-size-16"></i>{" "}
                                  DISABLE
                                </span>
                              </Fragment>
                            )}
                            {user.state === "user.status.approval.pending" && (
                              <Fragment>
                                <span
                                  className="cursor-pointer"
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
                                    <i className="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
                                    APPROVE{" "}
                                  </span>
                                </span>
                                &nbsp;
                                <span
                                  className="ps-3 cursor-pointer"
                                  onClick={() => {
                                    changeState(
                                      user.id,
                                      "user.status.inactive",
                                      "REJECT",
                                      index
                                    );
                                  }}
                                >
                                  <i className="bi bi-x-circle-fill text-red font-size-16"></i>{" "}
                                  REJECT{" "}
                                </span>
                              </Fragment>
                            )}
                            {user.state === "user.status.inactive" && (
                              <span
                                className="cursor-pointer"
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
                                  <i className="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
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
