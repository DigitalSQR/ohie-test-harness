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
import { Empty, notification } from "antd";
import { formatDate } from "../../../utils/utils.js";
import UserIdEmailConnector from "../../connectors/UserIdEmailConnector/UserIdEmailConnector.js";
import { Pagination } from "@mui/material";
import { useLoader } from "../../loader/LoaderContext";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer.jsx";
import { UserAPI } from "../../../api/UserAPI";
import unsorted from "../../../styles/images/unsorted.png";
import sortedUp from "../../../styles/images/sort-up.png";
import sortedDown from "../../../styles/images/sort-down.png";
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
        <img
          className="cursor-pointer"
          style={{width:"8px"}}
          src={
            sortDirection[fieldName] === "asc"
              ? sortedUp
              : sortedDown
          }
        ></img>
      );
    }
    return <img className="cursor-pointer" style={{width:"10px"}} src={unsorted}/>;
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
                  <th style={{width:"12%"}}>
                    APP NAME{" "}
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("name")}
                    >
                      {renderSortIcon("name")}
                    </span>
                  </th>
                  <th style={{width:"13%"}}>
                    COMPANY NAME{" "}
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("productName")}
                    >
                      {renderSortIcon("productName")}
                    </span>
                  </th>
                  <th style={{width:"15%"}}>
                    DATE OF APPLICATION
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("createdAt")}
                    >
                      {renderSortIcon("createdAt")}
                    </span>
                  </th>
                  <th style={{width:"20%"}}>EMAIL ID</th>
                  <th style={{width:"15%"}}>
                    STATUS
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("state")}
                    >
                      {renderSortIcon("state")}
                    </span>
                  </th>
                  <th style={{width:"20%"}}>
                    ACTIONS
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
                        <Empty description="No Record Found." />
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
                          <span 
                            className="cursor-pointer"
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
                            {StateClasses[testRequest.state]?.btnText?.toUpperCase()}
                          </span>
                        ) : (
                          <></>
                        )
                      ) : (
                        <span
                        className="cursor-pointer"
                          onClick={() => viewReport(testRequest.id)}
                        >
                          <i className="bi bi-file-text text-green-50 font-size-16"></i>{" "}
                              REPORT{" "}
                        </span>
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
