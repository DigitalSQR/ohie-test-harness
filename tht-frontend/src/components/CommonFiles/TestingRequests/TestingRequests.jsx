import React, { useEffect, useState } from "react";
import "./testingRequest.scss";
import "./_table.scss";
import {
  TestRequestStateLabels,
  TestRequestStateConstants,
  TestRequestStateConstantNames,
  StateBadgeClasses,
} from "../../../constants/test_requests_constants.js";
import { USER_ROLES } from "../../../constants/role_constants.js";
import { TestRequestAPI } from "../../../api/TestRequestAPI.js";
import { useLoader } from "../../loader/LoaderContext.js";
import ComponentIdConnector from "../../connectors/ComponentIdConnector/ComponentIdConnector.js";
import { notification } from "antd";
import { formatDate } from "../../../utils/utils.js";
import UserIdConnector from "../../connectors/UserIdConnector/UserIdConnector.jsx";
import { useNavigate } from "react-router-dom";
import { store } from "../../../store/store.js";
import { Pagination } from "@mui/material";
const TestingRequests = () => {
  const testRequestStates = [
    ...TestRequestStateLabels,
    { label: "All", value: "" },
  ];
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const { showLoader, hideLoader } = useLoader();
  const [filterState, setFilterState] = useState("");
  const [userRoles, setUserRoles] = useState([USER_ROLES.ROLE_ID_ASSESSEE]);
  const [testRequests, setTestRequests] = useState([]);
  const [showPassword, setShowPassword] = useState(false);
  const pageSize = 10;

  const navigate = useNavigate();
  const [sortDirection, setSortDirection] = useState({
    productName: "desc",
    createdAt: "desc",
    default: "acs",
  });
  const [sortFieldName, setSortFieldName] = useState("default");
  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    fetchTestRequests(filterState, sortFieldName, sortDirection, newPage);
  };
  const fetchTestRequests = (
    filterState,
    sortFieldName,
    sortDirection,
    newPage
  ) => {
    showLoader();
    TestRequestAPI.getTestRequestsByState(
      filterState,
      sortFieldName,
      !!sortFieldName ? sortDirection[sortFieldName] : null,
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

  const togglePasswordVisibility = (testUrl) => {
    if (testUrl.showPass) {
      testUrl.showPass = !testUrl.showPass;
    } else {
      testUrl.showPass = true;
    }
    setShowPassword(!showPassword);
  };
  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserRoles(userInfo.roleIds);
    fetchTestRequests(filterState, sortFieldName, sortDirection, currentPage);
  }, [filterState]);

  const handleSort = (field) => {
    setSortFieldName(field);
    const newSortDirection = { ...sortDirection };
    newSortDirection[field] = sortDirection[field] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    fetchTestRequests(filterState, field, newSortDirection, currentPage);
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

  const changeState = (testRequestId, updatedState) => {
    showLoader();
    TestRequestAPI.changeState(testRequestId, updatedState)
      .then((res) => {
        notification.success({
          placement: "bottomRight",
          message: "Status updated successfully!",
        });
        fetchTestRequests(filterState, sortFieldName, sortDirection, currentPage);
        hideLoader();
      })
      .catch((err) => {       
        hideLoader();
      });
  };

  const toggleRow = (trid) => {
    setTestRequests((trs) => {
      return trs.map((tr) => {
        if (tr.id === trid) {
          tr.class = tr.class === "show" ? "hide" : "show";
        } else {
          tr.class = "hide";
        }
        return tr;
      });
    });
  };
  return (
    <div id="testingRequest">
      <div id="wrapper">
        <div className="col-12 pt-3">
          <div className="row mb-2 justify-content-between">
            <div className="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12">
              <div className="custom-input custom-input-sm mb-3">
                <div className="d-flex align-items-baseline">
                  <span className="pe-3 text-nowrap fw-bold">Status </span>
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
            
            {userRoles.includes(USER_ROLES.ROLE_ID_ASSESSEE) && (
            <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
              <div className="d-flex align-items-baseline justify-content-end">
                <button
                  onClick={() => {
                    navigate("/register-application");
                  }}
                  type="button"
                  className="btn btn-sm btn-outline-secondary menu-like-item"
                >
                  <i className="bi bi-plus"></i>
                  Register Test Request
                </button>
              </div>
            </div>
          )}
          </div>

          <div className="table-responsive">
            <table className="data-table capitalize-words">
              <thead>
                <tr>
                  <th className="app-name-column">
                  PRODUCT NAME
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("productName")}
                    >
                      {renderSortIcon("productName")}
                    </span>
                  </th>
                  <th className="date-column">
                    DATE OF APPLICATION{" "}
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("createdAt")}
                    >
                      {renderSortIcon("createdAt")}
                    </span>
                  </th>
                  <th className="assessee-column">Assessee</th>
                  <th className="status-column">STATUS</th>
                  <th className="actions-column">
                    <span
                      className={
                        userRoles.includes(USER_ROLES.ROLE_ID_ADMIN) && "mx-2"
                      }
                    >
                      Actions
                      <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("default")}
                    >
                      {renderSortIcon("default")}
                    </span>
                    </span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {testRequests.length === 0 ? (
                  <>
                    <tr>
                      <td className="text-center" colSpan={7}>
                        No test requests found in the{" "}
                        {TestRequestStateConstantNames[filterState]} stage
                      </td>
                    </tr>
                  </>
                ) : null}
                {testRequests.map((testRequest) => (
                  <>
                    <tr key={testRequest.id}>
                      <td>{testRequest.productName}</td>
                      <td>{formatDate(testRequest.meta.updatedAt)}</td>
                      <td>
                        <UserIdConnector
                          isLink={true}
                          userId={testRequest.assesseeId}
                        />
                      </td>
                      <td>
                        <span className={"status "+ StateBadgeClasses[testRequest.state]}>
                          {TestRequestStateConstantNames[testRequest.state]}
                        </span>
                      </td>
                      <td className=" no-wrap text-left">
                        {userRoles.includes(USER_ROLES.ROLE_ID_ADMIN) &&
                        testRequest.state ==
                          TestRequestStateConstants.TEST_REQUEST_STATUS_PENDING ? (
                          <>
                            <button
                              onClick={() => {
                                changeState(
                                  testRequest.id,
                                  TestRequestStateConstants.TEST_REQUEST_STATUS_ACCEPTED
                                );
                              }}
                              type="button"
                              className="btn btn-sm approval-action-button "
                            >
                              <span>
                                <i className="bi bi-check-circle-fill text-green-50 font-size-16"></i>{" "}
                                APPROVE{" "}
                              </span>
                            </button>
                            <button
                              onClick={() => {
                                changeState(
                                  testRequest.id,
                                  TestRequestStateConstants.TEST_REQUEST_STATUS_REJECTED
                                );
                              }}
                              type="button"
                              className="mx-1 btn btn-sm approval-action-button "
                            >
                              <i className="bi bi-x-circle-fill text-red font-size-16"></i>{" "}
                              REJECT{" "}
                            </button>
                          </>
                        ) : null}
                        <span
                          onClick={() => toggleRow(testRequest.id)}
                          type="button"
                          className="approval-action-button float-end my-auto display"
                        >
                          {testRequest.class === "show" ? (
                            <i class="bi bi-chevron-double-down"></i>
                          ) : (
                            <i class="bi bi-chevron-double-right"></i>
                          )}
                        </span>
                      </td>
                    </tr>
                    <tr key={"collapseable--" + testRequest.id}>
                      <td colSpan="5" className="hiddenRow m-0 field-box">
                        <div
                          className={"collapse " + testRequest.class}
                          id="Accordion"
                        >
                          <div className="mx-5 my-3">
                            <table className="data-table capitialize-words">
                              <thead>
                                <tr>
                                  <th style={{width:'24%'}}>Component</th>
                                  <th style={{width:'24%'}}>Base Url</th>
                                  <th style={{width:'24%'}}>Username</th>
                                  <th style={{width:'24%'}}>Password</th>
                                </tr>
                              </thead>
                              <tbody>
                                {testRequest.testRequestUrls.length > 0 &&
                                  testRequest.testRequestUrls.map(
                                    (testUrls) => (
                                      <tr>
                                        <td>
                                          <ComponentIdConnector
                                            componentId={testUrls.componentId}
                                          ></ComponentIdConnector>
                                        </td>
                                        <td className = "toLowerCase-words">{testUrls.baseUrl}</td>
                                        <td className = "toLowerCase-words">{testUrls.username}</td>
                                        <td className = "toLowerCase-words" key={testRequest.id}>
                                          {testUrls.showPass
                                            ? testUrls.password
                                            : "*********"}
                                        </td>
                                        <td>
                                        <i
                                            className={`bi ${
                                              testUrls.showPass
                                                ? "bi-eye-fill"
                                                : "bi-eye-slash-fill"
                                            } padding-icon`}
                                            key={testUrls.componentId}
                                            onClick={() =>
                                              togglePasswordVisibility(testUrls)
                                            }
                                          ></i>{" "}
                                        </td>
                                      </tr>
                                    )
                                  )}
                              </tbody>
                            </table>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </>
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

export default TestingRequests;
