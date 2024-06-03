import React, { Fragment, useEffect, useState } from "react";
import "./assessee.scss";
import { UserAPI } from "../../../api/UserAPI";
import { Empty, Modal, notification } from "antd";
import moment from "moment";
import { SearchOutlined } from "@ant-design/icons";
import { Pagination, PaginationItem } from "@mui/material";
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
import { InfoCircleOutlined } from "@ant-design/icons";
import { Popover } from "antd";
import { DatePicker } from "antd";
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
  const obj = {
    name: "desc",
    email: "desc",
    createdAt: "desc",
    companyName: "desc",
    state: "desc",
  };
  const [sortDirection, setSortDirection] = useState(obj);

  const userStates = [...userStatusActionLabels];
  const [userRoles, setUserRoles] = useState([]);

  const [sortFieldName, setSortFieldName] = useState("name");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [reasonForRejection, setReasonForRejection] = useState();
  const [isReasonModalOpen, setIsReasonModalOpen] = useState(false);
  const [currentAssesseeId, setCurrentAssesseeId] = useState();
  const [currentIndex, setCurrentIndex] = useState();
  const [newState, setNewState] = useState();

  const [userSearchFilter, setUserSearchFilter] = useState({
    name: "",
    email: "",
    companyName: "",
    state: initialState,
    requestDate: "",
  });

  const updateFilter = (field, value) => {
    setUserSearchFilter((prevFilter) => ({
      ...prevFilter,
      [field]: value,
    }));
  };

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

  const [filterDate, setFilterDate] = useState();

  const handleUserSearch = () => {
    setCurrentPage(1);
    fetchUserByFilter(
      sortFieldName,
      sortDirection[sortFieldName],
      1
    );
  };

  const fetchUserByFilter = (
    sortFieldName,
    sortDirection,
    currentPage,
  ) => {
    showLoader();

    let params = {};
    params.sort = `${sortFieldName},${sortDirection}`;
    params.page = currentPage - 1;
    params.size = pageSize;
    params.role = role;

    const filteredUserSearchFilter = Object.keys(userSearchFilter)
      .filter((key) => {
        const value = userSearchFilter[key];
        return typeof value === "string" ? value.trim() !== "" : !!value;
      })
      .reduce((acc, key) => {
        if (typeof userSearchFilter[key] === "string")
          acc[key] = userSearchFilter[key].trim();
        else acc[key] = userSearchFilter[key];
        return acc;
      }, {});
    params = { ...params, ...filteredUserSearchFilter };

    UserAPI.getUserByFilter(params)
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
        email: "18%",
        requestedDate: "18%",
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
    fetchUserByFilter(
      newSortFieldName,
      newSortDirection[newSortFieldName],
      currentPage,
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
    UserAPI.changeState(userId, state, reasonForRejection)
      .then((res) => {
        setIsReasonModalOpen(false);
        setReasonForRejection();
        setNewState();
        hideLoader();
        notification.success({
          className: "notificationSuccess",
          placement: "top",
          message: `Assessee ${
            newState === "active" || newState === "inactive" ? "" : "request "
          }has been ${
            newState === "active"
              ? "marked as active"
              : newState === "inactive"
              ? "marked as inactive"
              : newState === "approve"
              ? "accepted"
              : newState === "reject"
              ? "rejected"
              : ""
          } successfully!`,
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
    fetchUserByFilter(
      sortFieldName,
      sortDirection[sortFieldName],
      newPage,
    );
  };

  //useEffect to fetch the users by state when the component initially loads or when we want to fetch the users based upon a certain state
  useEffect(() => {
    fetchUserByFilter(
      sortFieldName,
      sortDirection[sortFieldName],
      currentPage,
    );
  }, []);

  return (
    <div id="assessee">
      <div id="wrapper">
        <div className="col-12">
          <div className="table-responsive">
            <table className=" data-table capitalize-words">
              <thead>
                <tr>
                  <th style={{ width: thPercentage.name }}>
                    <div className="header-cell">
                      <div className="header-title">
                        NAME
                        <span
                          id="assessee-sortByName"
                          className="ps-1"
                          href="#"
                          onClick={() => handleSort("name")}
                        >
                          {renderSortIcon("name")}
                        </span>
                      </div>
                      <div className="filter-box">
                        <input
                          id="AssesseeNameSearchFilter"
                          type="text"
                          placeholder="Search by Name"
                          className="form-control filter-input"
                          value={userSearchFilter.name}
                          onChange={(e) => updateFilter("name", e.target.value)}
                        />
                      </div>
                    </div>
                  </th>
                  <th style={{ width: thPercentage.company }}>
                    <div className="header-cell">
                      <div className="header-title">
                        Company
                        <span
                          id="assessee-sortBycompanyName"
                          className="ps-1"
                          href="#"
                          onClick={() => handleSort("companyName")}
                        >
                          {renderSortIcon("companyName")}
                        </span>
                      </div>
                      <div className="filter-box">
                        <input
                          id="AssesseeCompanyNameSearchFilter"
                          type="text"
                          placeholder="Search by Company"
                          className="form-control filter-input"
                          value={userSearchFilter.companyName}
                          onChange={(e) =>
                            updateFilter("companyName", e.target.value)
                          }
                        />
                      </div>
                    </div>
                  </th>
                  <th style={{ width: thPercentage.email }}>
                    <div className="header-cell">
                      <div className="header-title">
                        Email
                        <span
                          id="assessee-sortByEmail"
                          className="ps-1"
                          href="#"
                          onClick={() => handleSort("email")}
                        >
                          {renderSortIcon("email")}
                        </span>
                      </div>
                      <div className="filter-box">
                        <input
                          id="AssesseeEmailSearchFilter"
                          type="text"
                          placeholder="Search by Email"
                          className="form-control filter-input"
                          value={userSearchFilter.email}
                          onChange={(e) =>
                            updateFilter("email", e.target.value)
                          }
                        />
                      </div>
                    </div>
                  </th>
                  <th style={{ width: thPercentage.requestedDate }}>
                    <div className="header-cell">
                      <div className="header-title">
                        Request Date
                        <span
                          id="assessee-sortBycreatedAt"
                          className="ps-1"
                          href="#"
                          onClick={() => handleSort("createdAt")}
                        >
                          {renderSortIcon("createdAt")}
                        </span>
                      </div>
                      <div className="filter-box">
                        <DatePicker
                          id="AssesseeDateSearchFilter"
                          className="form-control filter-input"
                          placeholder="Select Date"
                          value={filterDate}
                          onChange={(date) => {
                            setFilterDate(date);
                            updateFilter(
                              "requestDate",
                              date ? date.format("YYYY-MM-DD") : null
                            );
                          }}
                        />
                      </div>
                    </div>
                  </th>
                  <th style={{ width: thPercentage.status }}>
                    <div className="header-cell">
                      <div className="header-title">
                        Status
                        <span
                          id="assessee-sortByState"
                          className="ps-1"
                          href="#"
                          onClick={() => handleSort("state")}
                        >
                          {renderSortIcon("state")}
                        </span>
                      </div>
                      <div className="filter-box">
                        <select
                          id="AssesseeStatusSearchFilter"
                          className="form-select custom-select custom-select-sm filter-input"
                          aria-label="Default select example"
                          value={userSearchFilter.state}
                          onChange={(e) => {
                            updateFilter("state", e.target.value);
                          }}
                        >
                          {userStates.map((userState) => (
                            <option
                              value={userState.value}
                              key={userState.value}
                            >
                              {userState.label}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>
                  </th>
                  {userRoles.includes(USER_ROLES.ROLE_ID_ADMIN) && (
                    <th style={{ width: thPercentage.action }}>
                      <div className="header-cell">
                        <div className="header-title">
                          ACTIONS
                          <span
                            id="assessee-sortByActions"
                            className="ps-1"
                            href="#"
                            onClick={() => handleSort("default")}
                          >
                            {renderSortIcon("default")}
                          </span>
                        </div>
                        <div className="filter-box">
                          <button
                            className="search-button"
                            onClick={handleUserSearch}
                            id="handleUserSearch"
                          >
                            <SearchOutlined />
                            Search
                          </button>
                        </div>
                      </div>
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
                  const formattedDate = moment(user.meta.createdAt).format(
                    "Do MMMM, YYYY"
                  );

                  let currentStatus = userStateConstantNames[user.state];
                  return (
                    <Fragment key={user.id}>
                      <tr>
                        <td className="fw-bold">{user.name}</td>
                        {user.companyName ? (
                          <td className="fw-bold">{user.companyName}</td>
                        ) : (
                          <td>&ndash;</td>
                        )}
                        <td className="toLowerCase-words">{user.email}</td>
                        <td>{formattedDate}</td>
                        <td>
                          <Fragment>
                            <span
                              className={
                                "status " + userBadgeClasses[user.state]
                              }
                            >
                              {currentStatus.toLowerCase()}
                            </span>
                            {user.state === "user.status.inactive" && (
                              <Popover title={<div>{user?.message}</div>}>
                                <InfoCircleOutlined
                                  style={{
                                    marginLeft: "0.5rem",
                                    marginTop: "0.7rem",
                                  }}
                                />
                              </Popover>
                            )}
                          </Fragment>
                        </td>
                        {userRoles.includes(USER_ROLES.ROLE_ID_ADMIN) && (
                          <td className=" no-wrap">
                            {" "}
                            {user.state === "user.status.active" && (
                              <Fragment>
                                <span
                                  id={`assessee-disable-${index}`}
                                  className="cursor-pointer"
                                  onClick={() => {
                                    setIsReasonModalOpen(true);
                                    setCurrentAssesseeId(user.id);
                                    setCurrentIndex(index);
                                    setNewState("inactive");
                                  }}
                                >
                                  <i className="bi bi-ban text-warning font-size-16"></i>
                                  <span className="text-warning font-size-12 fw-bold ps-1">
                                    DISABLE
                                  </span>
                                </span>
                              </Fragment>
                            )}
                            {user.state === "user.status.approval.pending" && (
                              <Fragment>
                                <span
                                  className="cursor-pointer text-success font-size-12 fw-bold"
                                  id={`assessee-approve-${index}`}
                                  onClick={() => {
                                    changeState(
                                      user.id,
                                      "user.status.active",
                                      "approve",
                                      index
                                    );
                                  }}
                                >
                                  <i className="bi bi-check-circle-fill  font-size-16"></i>
                                  <span className="ps-1">APPROVE</span>
                                </span>

                                <span
                                  className="ps-3 cursor-pointer text-danger font-size-12 fw-bold"
                                  id={`assessee-decline-${index}`}
                                  onClick={() => {
                                    setIsReasonModalOpen(true);
                                    setCurrentAssesseeId(user.id);
                                    setCurrentIndex(index);
                                    setNewState("reject");
                                  }}
                                >
                                  <i className="bi bi-x-circle-fill  font-size-16"></i>{" "}
                                  DECLINE{" "}
                                </span>
                              </Fragment>
                            )}
                            {user.state === "user.status.inactive" && (
                              <span
                                id={`assessee-enable-${index}`}
                                className="cursor-pointer text-success font-size-12 fw-bold"
                                onClick={() => {
                                  changeState(
                                    user.id,
                                    "user.status.active",
                                    "active",
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
            renderItem={(item) => {
              if (item.type === "page") {
                return (
                  <PaginationItem
                    {...item}
                    id={`Assessee-page-${item.page}`}
                    component="button"
                    onClick={() => handleChangePage(null, item.page)}
                  />
                );
              } else if (item.type === "previous") {
                return (
                  <PaginationItem
                    {...item}
                    id="Assessee-previous-page-button"
                    component="button"
                    onClick={() => handleChangePage(null, currentPage - 1)}
                  />
                );
              } else if (item.type === "next") {
                return (
                  <PaginationItem
                    {...item}
                    id="Assessee-next-page-button"
                    component="button"
                    onClick={() => handleChangePage(null, currentPage + 1)}
                  />
                );
              }
              return null;
            }}
          />
        )}
      </div>
      <Modal
        okButtonProps={{ id: "assessee-disable-okButton" }}
        cancelButtonProps={{ id: "assessee-disable-cancelButton" }}
        closable={false}
        open={isReasonModalOpen}
        onCancel={() => {
          setIsReasonModalOpen(false);
          setReasonForRejection();
          setCurrentAssesseeId();
          setCurrentIndex();
          setNewState();
        }}
        onOk={() => {
          if (!reasonForRejection) {
            notification.error({
              className: "notificationError",
              message: "Please provide a reason for disapproval.",
              placement: "bottomRight",
            });
          } else {
            changeState(
              currentAssesseeId,
              "user.status.inactive",
              newState,
              currentIndex
            );
          }
        }}
        destroyOnClose={true}
      >
        <div className="custom-input mb-3">
          <label htmlFor="reason" className="form-label">
            <b>
              {" "}
              {newState === "reject"
                ? "Please provide a reason for Disapproval."
                : "Please provide a reason for Disabling."}
            </b>
          </label>
          <input
            id="reason"
            className="form-control"
            type="text"
            value={reasonForRejection}
            onChange={(e) => {
              setReasonForRejection(e.target.value);
            }}
          ></input>
        </div>
      </Modal>
    </div>
  );
};

export default Assessee;
