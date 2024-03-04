import React, { Fragment, useEffect, useState } from "react";
import "./assessee.scss";
import { UserAPI } from "../../../api/UserAPI";
import { notification } from "antd";
import { Pagination } from "@mui/material";
import { userBadgeClasses } from "../../../constants/user_constants";
import { useLoader } from "../../loader/LoaderContext";
import { userStatusActionLabels } from "../../../constants/user_constants";
import { ROLE_ID_ASSESSEE } from "../../../constants/role_constants";
const Assessee = () => {
  const [availableUsers, setAvailableUsers] = useState([]);
  const [state, setState] = useState("");
  const [sortDirection, setSortDirection] = useState({
    name: "desc",
    email: "desc",
    createdAt: "desc",
  });

  const userStates = [...userStatusActionLabels, { label: "All", value: "" }];
  const [sortFieldName, setSortFieldName] = useState("name");
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

  const handleSort = (newSortFieldName) => {
    setSortFieldName(newSortFieldName);
    const newSortDirection = { ...sortDirection };
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
        <span
          className={`bi ${
            sortDirection[fieldName] === "asc"
              ? "bi-caret-up-fill"
              : "bi-caret-down-fill"
          }`}
        ></span>
      );
    }
    return <span className="bi-caret-down-fill"></span>;
  };

  const changeState = (userId, state, newState) => {
    showLoader();
    UserAPI.changeState(userId, state)
      .then((res) => {
        hideLoader();
        notification.success({
          description: `Request has been ${newState}`,
          placement: "bottom-left",
        });
        fetchUserByState(
          sortFieldName,
          sortDirection[sortFieldName],
          currentPage,
          pageSize
        );
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
    <div id="wrapper">
      <div className="col-12">
        <div className="row mb-2 justify-content-between">
          <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
            <div className="d-flex align-items-baseline ">
              <span className="pe-3 text-nowrap">Status :</span>
              <div className="mb-3">
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
          <table className=" data-table">
            <thead>
              <tr>
                <th className="col-1.8">
                  NAME
                  <a
                    className="ps-1"
                    href="#"
                    onClick={() => handleSort("name")}
                  >
                    {renderSortIcon("name")}
                  </a>
                </th>
                <th className="col-1.8">
                  Email
                  <a
                    className="ps-1"
                    href="# "
                    onClick={() => handleSort("email")}
                  >
                    {renderSortIcon("email")}
                  </a>
                </th>
                <th className="col-1.8">Company</th>
                <th className="col-1.8">requested date</th>
                <th className="col-1.8">
                  Status
                  <a
                    className="ps-1"
                    href="# "
                    onClick={() => handleSort("state")}
                  >
                    {renderSortIcon("state")}
                  </a>
                </th>
                <th className="col-3">CHOOSE ACTION</th>
              </tr>
            </thead>
            <tbody>
              {availableUsers.length === 0 ? (
                <tr>
                  <td className="text-center" colSpan="6">
                    There are no user registration requests for this state
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
                if (user.state === "user.status.verification.pending") {
                  currentStatus = "Email not verified";
                } else if (user.state) {
                  const parts = user.state.split(".");

                  currentStatus = parts[parts.length - 1];
                }
                return (
                  <Fragment key={user.id}>
                    <tr>
                      <td>{user.name}</td>
                      <td>{user.email}</td>
                      {user.companyName ? (
                        <td>{user.companyName}</td>
                      ) : (
                        <td>&ndash;</td>
                      )}
                      <td>{formattedDate}</td>
                      <td>
                        <span className={userBadgeClasses[user.state]}>
                          {currentStatus.toUpperCase()}
                        </span>
                      </td>
                      <td className=" no-wrap">
                        {user.state === "user.status.active" && (
                          <Fragment>
                            <button
                              type="button"
                              className="text-uppercase btn btn-sm approval-action-button text-uppercase"
                              onClick={() => {
                                changeState(
                                  user.id,
                                  "user.status.inactive",
                                  "Inactive"
                                );
                              }}
                            >
                              <i class="bi bi-slash-circle-fill text-red"></i>{" "}
                              DISABLE
                            </button>
                          </Fragment>
                        )}
                        {user.state === "user.status.approval.pending" && (
                          <Fragment>
                            <button
                              type="button"
                              className="text-uppercase btn btn-sm approval-action-button text-uppercase  "
                              onClick={() => {
                                changeState(
                                  user.id,
                                  "user.status.active",
                                  "Approved"
                                );
                              }}
                            >
                              <span>
                                <i className="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
                                APPROVE
                              </span>
                            </button>
                            <button
                              type="button"
                              className=" mx-1 text-uppercase btn btn-sm approval-action-button text-uppercase"
                              onClick={() => {
                                changeState(
                                  user.id,
                                  "user.status.rejected",
                                  "Rejected"
                                );
                              }}
                            >
                              <i className="bi bi-x-circle-fill text-red font-size-16"></i>{" "}
                              REJECT{" "}
                            </button>
                          </Fragment>
                        )}

                        {user.state === "user.status.inactive" && (
                          <button
                            type="button"
                            className="text-uppercase btn btn-sm approval-action-button text-uppercase"
                            onClick={() => {
                              changeState(
                                user.id,
                                "user.status.active",
                                "Active"
                              );
                            }}
                          >
                            <span>
                              <i className="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
                              Enable
                            </span>
                          </button>
                        )}
                      </td>
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
  );
};

export default Assessee;
