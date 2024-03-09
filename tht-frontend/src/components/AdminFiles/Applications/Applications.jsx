import React, { Fragment, useEffect, useState } from "react";
import "./_table.scss";
import "./applications.scss";
import sortIcon from "../../../styles/images/sort-icon.png";
import { useNavigate } from "react-router-dom";
import {
  StateBadgeClasses,
  StateClasses,
  TestRequestActionStateLabels,
  TestRequestStateConstantNames,
} from "../../../constants/test_requests_constants.js";
import { TestRequestAPI } from "../../../api/TestRequestAPI.js";
import { notification } from "antd";
import { formatDate } from "../../../utils/utils.js";
import UserIdEmailConnector from "../../connectors/UserIdEmailConnector/UserIdEmailConnector.js";
import { Pagination } from "@mui/material";
import { useLoader } from "../../loader/LoaderContext";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer.jsx";
import { UserAPI } from "../../../api/UserAPI";
const Applications = () => {
  const testRequestStates = [
    ...TestRequestActionStateLabels,
    { label: "All", value: "" },
  ];

  const obj = {
    name: "desc",
    productName: "desc",
    approver: "desc",
    state: "desc",
    default: "asc",
  };
  const [filterState, setFilterState] = useState("");
  const [testRequests, setTestRequests] = useState([]);
  const [sortDirection, setSortDirection] = useState(obj);
  const [sortFieldName, setSortFieldName] = useState("default");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [userRole, setUserRole] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const pageSize = 10;

  useEffect(() => {
    dispatch(set_header("Applications"));
    var state = filterState;
    if (!state || state == "") {
      state = TestRequestActionStateLabels.map((state) => state.value);
    }
    if (state) {
      getAllTestRequests(state, sortFieldName, sortDirection, currentPage);
    }
  }, [filterState]);

  useEffect(() => {
    UserAPI.viewUser()
      .then((res) => {
        setUserRole(res.roleIds);
      })
      .catch((error) => {});
  },[]);
  const getAllTestRequests = (
    filterState,
    sortFieldName,
    sortDirection,
    newPage
  ) => {
    if (!filterState || filterState == "") {
      filterState = TestRequestActionStateLabels.map((state) => state.value);
    }
    showLoader();
    TestRequestAPI.getTestRequestsByState(
      filterState,
      sortFieldName,
      sortDirection[sortFieldName],
      newPage - 1,
      pageSize
    )
      .then((res) => {
        hideLoader();
        setTestRequests(res.content);
        setTotalPages(res.totalPages);
      })
      .catch((err) => {
        hideLoader();
      });
  };

  const handleSort = (sortFieldName) => {
    setSortFieldName(sortFieldName);
    const newSortDirection = { ...obj };
    newSortDirection[sortFieldName] =
    sortDirection[sortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    getAllTestRequests(
      filterState,
      sortFieldName,
      newSortDirection,
      currentPage
    );
  };

  const renderSortIcon = (fieldName) => {
    if (sortFieldName === fieldName) {
      return (
        <span
          className={`bi ${
            sortDirection[fieldName] === "asc"
              ? "bi bi-sort-up-alt"
              : "bi bi-sort-down"
          }`}
        ></span>
      );
    }
    return <span className="bi bi-arrow-down-up"></span>;
  };

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    getAllTestRequests(filterState, sortFieldName, sortDirection, newPage);
  };

  const viewReport = (testRequestId) => {
    navigate(`/application-report/${testRequestId}`);
  };

  return (
    <div id="applications">
      <div id="wrapper">
        <div className="col-12">
          <div className="row mb-2 justify-content-between">
            <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
              <div className="d-flex align-items-baseline">
                <span className="pe-3 text-nowrap">Status :</span>
                <div className="mb-3">
                  <select
                    onChange={(e) => {
                      setFilterState(e.target.value);
                    }}
                    value={filterState}
                    className="form-select custom-select custom-select-sm"
                    aria-label="Default select example"
                  >
                    {testRequestStates.map((testRequestState) => (
                      <option
                        value={testRequestState.value}
                        key={testRequestState.value}
                      >
                        {testRequestState.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
          </div>
          <div className="table-responsive">
            <table className=" data-table capitalize-words">
              <thead>
                <tr>
                  <th className="col-2">
                    APP NAME{" "}
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("name")}
                    >
                      {renderSortIcon("name")}
                    </span>
                  </th>
                  <th className="col-2">
                    COMPANY NAME{" "}
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("productName")}
                    >
                      {renderSortIcon("productName")}
                    </span>
                  </th>
                  <th className="col-2">
                    DATE OF APPLICATION
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("createdAt")}
                    >
                      {renderSortIcon("createdAt")}
                    </span>
                  </th>
                  <th className="col-2">EMAIL ID</th>
                  <th className="col-2">
                    STATUS
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("state")}
                    >
                      {renderSortIcon("state")}
                    </span>
                  </th>
                  <th className="col-2">
                    ACTION
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("default")}
                    >
                      {renderSortIcon("default")}
                    </span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {testRequests.length === 0 ? (
                  <>
                    <tr>
                      <td className="text-center" colSpan={6}>
                        No test requests found. Register one or wait for test
                        request to be approved
                      </td>
                    </tr>
                  </>
                ) : null}
                {testRequests?.map((testRequest) => (
                  <tr key={testRequest.id}>
                    <td>{testRequest.name}</td>
                    <td>
                      {testRequest.productName !== ""
                        ? testRequest.productName
                        : "-"}
                    </td>
                    <td>{formatDate(testRequest.meta.createdAt)}</td>
                    <td className = "toLowerCase-words">
                      <UserIdEmailConnector
                        userId={testRequest.assesseeId}
                      ></UserIdEmailConnector>
                    </td>
                    <td>
                      {testRequest.state !== "test.request.status.finished" ? (
                        <Fragment>
                          <span
                            className={`status badge ${
                              StateBadgeClasses[testRequest.state]
                            }`}
                          >
                            {TestRequestStateConstantNames[testRequest.state]}
                          </span>
                        </Fragment>
                      ) : (
                        <Fragment>
                          <span
                            className={`status badge ${
                              StateBadgeClasses[testRequest.state]
                            }`}
                          >
                            {TestRequestStateConstantNames[testRequest.state]}
                          </span>
                        </Fragment>
                      )}
                    </td>
                    <td>
                      {testRequest.state !== "test.request.status.finished" ? (
                        userRole.includes("role.tester") ||
                        userRole.includes("role.admin") ? (
                          <button
                            className={
                              StateClasses[testRequest.state]?.btnClass
                            }
                            onClick={() => {
                              navigate(`/choose-test/${testRequest.id}`);
                            }}
                          >
                            {" "}
                            <i
                              className={
                                StateClasses[testRequest.state]?.iconClass
                              }
                            ></i>{" "}
                            {StateClasses[testRequest.state]?.btnText}
                          </button>
                        ) : (
                          <></>
                        )
                      ) : (
                        <button
                        style={{ cursor: "pointer" }}
                        className={StateClasses[testRequest.state]?.btnClass}
                          onClick={() => viewReport(testRequest.id)}
                        >
                          {" "}
                          <i
                            className={
                              StateClasses[testRequest.state]?.iconClass
                            }
                          ></i>{" "}Report
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
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

export default Applications;
