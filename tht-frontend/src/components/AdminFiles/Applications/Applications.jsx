import React, { Fragment, useEffect, useState } from "react";
import "./applications.scss";
import sortIcon from "../../../styles/images/sort-icon.png";
import { useNavigate } from "react-router-dom";
import {
  StateBadgeClasses,
  StateClasses,
  TestRequestActionStateLabels,
  TestRequestStateConstants,
  TestRequestStateConstantNames,
} from "../../../constants/test_requests_constants.js";
import { TestRequestAPI } from "../../../api/TestRequestAPI.js";
import { Empty, notification, Modal } from "antd";
import { formatDate } from "../../../utils/utils.js";
import UserIdEmailConnector from "../../connectors/UserIdEmailConnector/UserIdEmailConnector.js";
import { Pagination, PaginationItem } from "@mui/material";
import { useLoader } from "../../loader/LoaderContext";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer.jsx";
import { UserAPI } from "../../../api/UserAPI";
import { USER_ROLES } from "../../../constants/role_constants.js";
import UserIdConnector from "../../connectors/UserIdConnector/UserIdConnector.jsx";
import unsorted from "../../../styles/images/unsorted.png";
import sortedUp from "../../../styles/images/sort-up.png";
import sortedDown from "../../../styles/images/sort-down.png";
import ComponentIdConnector from "../../connectors/ComponentIdConnector/ComponentIdConnector.js";
import UserIdNameEmailConnector from "../../connectors/UserIdNameEmailConnector/UserIdNameEmailConnector";
import { Popover } from "antd";
import moment from "moment";
import { InfoCircleOutlined } from "@ant-design/icons";
/**
 * Applications Component:
 * This component displays a table of applications with options to filter, sort, and update their statuses.
 * It fetches application data from an API and renders them in a tabular format.
 * Gives admin the access to approve or reject a verification request.
 */

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
  const [showPassword, setShowPassword] = useState(false);
  const [userRole, setUserRole] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const [reasonForRejection, setReasonForRejection] = useState();
  const [isReasonModalOpen, setIsReasonModalOpen] = useState(false);
  const [currentTestRequestId, setCurrentTestRequestId] = useState();
  const [currentIndex, setCurrentIndex] = useState();

  const navigate = useNavigate();
  const dispatch = useDispatch();
  const pageSize = 10;

  //useEffect to set the header, get all the test requests when the component loads
  useEffect(() => {
    dispatch(set_header("Applications"));
    var state = filterState;
    getAllTestRequests(state, sortFieldName, sortDirection, currentPage);
  }, [filterState]);

  //useEffect to set the user role
  useEffect(() => {
    UserAPI.viewUser()
      .then((res) => {
        setUserRole(res.roleIds);
      })
      .catch((error) => {});
  }, []);

  //Function to get all the test requests varying with the different state we want to fetch
  const getAllTestRequests = (
    filterState,
    sortFieldName,
    sortDirection,
    newPage
  ) => {
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

  //Function to toggle between the visibility of the password i.e. to either show or hide the password
  const togglePasswordVisibility = (testUrl) => {
    if (testUrl.showPass) {
      testUrl.showPass = !testUrl.showPass;
    } else {
      testUrl.showPass = true;
    }
    setShowPassword(!showPassword);
  };

  //Function to toggle the row that gives us the component details for a test request using accordian
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

  //Function to handle the sorting functionality based upon a certain field name
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

  //Function to handle the page change in pagination
  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    getAllTestRequests(filterState, sortFieldName, sortDirection, newPage);
  };

  //Function that navigates us to the application report page for a given test request id
  const viewReport = (testRequestId) => {
    navigate(`/application-report/${testRequestId}`);
  };

  //Function that handles whether the test request is to approved or rejected by the admin
  const changeState = (testRequestId, updatedState, index, proceedAnyways) => {
    showLoader();
    TestRequestAPI.validateChangeState(
      testRequestId,
      updatedState,
      reasonForRejection
    )
      .then((validationResults) => {
        let warnings = [];
        let errors = [];
        for (let validationResult of validationResults) {
          if (validationResult.level === "WARN") {
            warnings.push(validationResult.message);
          } else if (validationResult.level === "ERROR") {
            errors.push(validationResult.message);
          }
        }
        if (!!errors.length) {
          hideLoader();
          errors.forEach((error, i) => {
            notification.error({
              className: "notificationError",
              message: error,
              placement: "bottomRight",
            });
          });
        } else if (!!warnings.length && !proceedAnyways) {
          hideLoader();
          Modal.confirm({
            title: "Test Request Status Update",
            content: (
              <div>
                <p>
                  <strong>Please review the following warnings:</strong>
                </p>
                <ul style={{ paddingLeft: 20 }}>
                  {warnings.map((warning, i) => (
                    <li key={index}>{warning}</li>
                  ))}
                </ul>
              </div>
            ),
            okText: "Procced Anyway",
            cancelText: "Cancel",
            width: 600,
            onOk() {
              changeState(testRequestId, updatedState, index, true);
            },
            cancelButtonProps:{id:"applications-warning-cancelButton"},
            okButtonProps:{id:"applications-warning-okButton"}
            
          });
        } else {
          Modal.confirm({
            cancelButtonProps:{id:`applications-${ updatedState === "test.request.status.rejected"
                ? "reject" : "accept"}-cancelButton`},
            okButtonProps:{id:`applications-${ updatedState === "test.request.status.rejected"
            ? "reject" : "accept"}-okButton`},
            title: "Confirmation",
            content: `Are you sure you want to change the status of this application testing request to ${
              updatedState === "test.request.status.rejected"
                ? "rejected"
                : "accepted"
            }?`,
            onOk() {
              showLoader();
              TestRequestAPI.changeState(
                testRequestId,
                updatedState,
                reasonForRejection
              )
                .then((res) => {
                  notification.success({
                    className: "notificationSuccess",
                    placement: "top",
                    message: `Application testing request has been ${
                      updatedState === "test.request.status.rejected"
                        ? "rejected"
                        : "accepted"
                    } successfully!`,
                  });
                  const updatedTestRequests = [...testRequests];
                  updatedTestRequests[index] = res;
                  setTestRequests(updatedTestRequests);
                  hideLoader();
                })
                .catch((err) => {
                  hideLoader();
                })
                .finally(() => {
                  hideLoader(); // Ensure that hideLoader is always called after the operation
                });
            },
          });
        }
      })
      .catch((err) => {
        hideLoader();
      })
      .finally(() => {
        hideLoader();
      });
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
                    id="applications-searchByStatus"
                  >
                    {testRequestStates.map((testRequestState) => (
                      <option
                      id={`application-status-${testRequestState.label}`}
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
                  <th style={{ width: "12%" }}>
                    APPLICATION NAME{" "}
                    <span
                      id="applications-sortByName"
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("name")}
                    >
                      {renderSortIcon("name")}
                    </span>
                  </th>
                  <th style={{ width: "12%" }}>Assessee</th>
                  <th style={{ width: "12%" }}>Company</th>
                  <th style={{ width: "16%" }}>EMAIL ID</th>
                  <th style={{ width: "15%" }}>
                    REQUEST DATE
                    <span
                      id="applications-sortBycreatedAt"
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("createdAt")}
                    >
                      {renderSortIcon("createdAt")}
                    </span>
                  </th>
                  <th style={{ width: "15%" }}>
                    STATUS
                    <span
                      className="ps-1"
                      href="#"
                      id="applications-sortBystate"
                      onClick={() => handleSort("state")}
                    >
                      {renderSortIcon("state")}
                    </span>
                  </th>
                  <th style={{ width: "20%" }}>
                    ACTIONS
                    <span
                      className="ps-1"
                      href="#"
                      id="applications-sortByactions"
                      onClick={() => handleSort("default")}
                    >
                      {renderSortIcon("default")}
                    </span>
                  </th>
                  <th style={{ width: "1%" }}></th>
                </tr>
              </thead>
              <tbody>
                {testRequests.length === 0 ? (
                  <>
                    <tr>
                      <td className="text-center" colSpan={8}>
                        <Empty description="No Record Found." />
                      </td>
                    </tr>
                  </>
                ) : null}
                {testRequests?.map((testRequest, index) => {
                  const formattedDate = moment(testRequest.meta.createdAt).format("Do MMMM, YYYY");
                  return (
                    <React.Fragment key={testRequest.id}>
                    <tr
                      className={index % 2 == 0 ? "even" : "odd"}
                      key={testRequest.id}
                    >
                      <td className="fw-bold">{testRequest.name}</td>
                      {/* <td>
                      {testRequest.productName !== ""
                        ? testRequest.productName
                        : "-"}
                    </td> */}
                        <UserIdNameEmailConnector
                          className="fw-bold"
                          isLink={true}
                          userId={testRequest.assesseeId}
                        />
                        <td>{formattedDate}</td>
                        <td>
                          {testRequest?.state !==
                          TestRequestStateConstants.TEST_REQUEST_STATUS_REJECTED ? (
                            <Fragment>
                              <span
                                className={`status badge ${
                                  StateBadgeClasses[testRequest.state]
                                }`}
                              >
                                {TestRequestStateConstantNames[
                                  testRequest.state
                                ].toLowerCase()}
                              </span>
                            </Fragment>
                          ) : (
                            <Fragment>
                              <span
                                className={`status badge ${
                                  StateBadgeClasses[testRequest.state]
                                }`}
                              >
                                {TestRequestStateConstantNames[
                                  testRequest.state
                                ].toLowerCase()}
                              </span>
                              <Popover
                                title={<div>{testRequest?.message}</div>}
                              >
                                <InfoCircleOutlined style={{marginLeft:"0.5rem", marginTop:"0.7rem"}}/>
                              </Popover>
                            </Fragment>
                          )}
                        </td>
                        <td className=" no-wrap text-left">
                          {userRole.includes(USER_ROLES.ROLE_ID_ADMIN) &&
                          testRequest.state ==
                            TestRequestStateConstants.TEST_REQUEST_STATUS_PENDING ? (
                            <div className="">
                            <div class="row">
                            <div class="col-lg-6 col-md-12">
                              <div class="d-flex flex-column flex-md-row">
                                <div
                                  class="cursor-pointer text-success d-flex align-items-center font-size-12 fw-bold"
                                  id={`applications-acceptTestRequest-${index}`}
                                  onClick={() => {
                                    changeState(
                                      testRequest.id,
                                      TestRequestStateConstants.TEST_REQUEST_STATUS_ACCEPTED,
                                      index
                                    );
                                  }}
                                >
                                  <i class="bi bi-check-circle-fill text-success font-size-16"></i>
                                  <span className="ps-1"> 
                                    ACCEPT</span>
                                </div>
                                <div
                                  id={`applications-declineTestRequest-${index}`}
                                  class="cursor-pointer ps-md-3 text-danger font-size-12 fw-bold mt-2 mt-md-0 ml-lg-3 d-flex align-items-center"
                                  onClick={() => {
                                    setCurrentTestRequestId(testRequest.id);
                                    setCurrentIndex(index);
                                    setIsReasonModalOpen(true);
                                  }}
                                >
                                  <i class="bi bi-x-circle-fill text-danger font-size-16"></i>
                                  <span className="ps-1">
                                  DECLINE</span>
                                </div>
                              </div>
                            </div>
                          </div>
                          
                            </div>
                          ) : null}
                        {testRequest.state !==
                          TestRequestStateConstants.TEST_REQUEST_STATUS_PENDING &&
                        testRequest.state !==
                          TestRequestStateConstants.TEST_REQUEST_STATUS_REJECTED &&
                        testRequest.state !==
                          TestRequestStateConstants.TEST_REQUEST_STATUS_FINISHED ? (
                          userRole.includes("role.tester") ||
                          userRole.includes("role.admin") ? (
                            <button
                            id={`applications-actions-${index}`}
                              className="cursor-pointer glossy-button glossy-button--green d-flex align-items-center"
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
                              {StateClasses[
                                testRequest.state
                              ]?.btnText?.toUpperCase()}
                            </button>
                          ) : (
                            <></>
                          )
                        ) : (
                          testRequest.state ===
                            TestRequestStateConstants.TEST_REQUEST_STATUS_FINISHED && (
                            <button
                            id={`applications-viewReport-${index}`}
                              className="cursor-pointer glossy-button glossy-button--gold d-flex align-items-center"
                              onClick={() => viewReport(testRequest.id)}
                            >
                              <i className="bi bi-file-text font-size-16"></i>{" "}
                              REPORT{" "}
                            </button>
                          )
                        )}
                        </td>
                        <td>
                        <span
                          id={`toggleRow-${index}`}
                          onClick={() => toggleRow(testRequest.id)}
                          type="button"
                          className="approval-action-button float-end my-auto display"
                        >
                          {testRequest.class === "show" ? (
                            <i className="bi bi-arrow-up-circle-fill fs-5"></i>
                          ) : (
                            <i className="bi bi-arrow-down-circle-fill fs-5"></i>
                          )}
                        </span>
                        
                    </td>
                  </tr>
                  <tr className={"collapse " + testRequest.class} key={"collapseable--" + testRequest.id}>
                      <td colSpan="8" className="hiddenRow m-0 p-0 field-box">
                        <div id="Accordion" className="p-3 table-accordion-bg">
                            <table className="data-table-inner capitialize-words">
                              <thead>
                                <tr>
                                  <th style={{width:'20%'}}>Component</th>
                                  <th style={{width:'20%'}}>Fhir Api Base Url</th>
                                  <th style={{width:'20%'}}>Website/UI Base Url</th>
                                  <th style={{width:'20%'}}>Username</th>
                                  <th style={{width:'20%'}}>Password</th>
                                  <th></th>
                                </tr>
                              </thead>
                              <tbody>
                                {testRequest.testRequestUrls.length > 0 &&
                                  testRequest.testRequestUrls.map(
                                    (testUrls,index) => (
                                      <tr id={testUrls.componentId} key={testUrls.componentId}>
                                        <td className="fw-bold">
                                          <ComponentIdConnector
                                            componentId={testUrls.componentId}
                                          ></ComponentIdConnector>
                                        </td>
                                        <td className="no-capitalization">
                                          {testUrls.fhirApiBaseUrl}
                                        </td>
                                        <td className="no-capitalization">
                                          {testUrls.websiteUIBaseUrl}
                                        </td>
                                        <td className="toLowerCase-words">
                                          {testUrls.username}
                                        </td>
                                        <td
                                          className="toLowerCase-words"
                                          key={testRequest.id}
                                        >
                                          {testUrls.showPass
                                            ? testUrls.password
                                            : "*********"}
                                        </td>
                                        <td>
                                          <i
                                            id={`togglePassword-${index}`}
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
                      </td>
                    </tr>
                   </React.Fragment>
                  )
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
              if (item.type === 'page') {
                return (
                  <PaginationItem
                    {...item}
                    id={`Applications-page-${item.page}`}
                    component="button"
                    onClick={() => handleChangePage(null, item.page)}
                  />
                );
              } else if (item.type === 'previous') {
                return (
                  <PaginationItem
                    {...item}
                    id="Applications-previous-page-button"
                    component="button"
                    onClick={() => handleChangePage(null, currentPage - 1)}
                  />
                );
              } else if (item.type === 'next') {
                return (
                  <PaginationItem
                    {...item}
                    id="Applications-next-page-button"
                    component="button"
                    onClick={() => handleChangePage(null, currentPage + 1)}
                  />
                );
              }
              return null;
            }}
          />
        )}
        <Modal
        closable={false}
          open={isReasonModalOpen}
          onCancel={() => {
            setIsReasonModalOpen(false);
            setReasonForRejection();
          }}
          cancelButtonProps={{id:"applications-disable-cancelButton"}}
          okButtonProps={{id:"applications-disable-okButton"}}
          onOk={() => {
            if (!reasonForRejection) {
              notification.error({
                className: "notificationError",
                message: "Please provide a reason for disapproval.",
                placement: "bottomRight",
              });
            } else {
              changeState(
                currentTestRequestId,
                TestRequestStateConstants.TEST_REQUEST_STATUS_REJECTED,
                currentIndex
              );
              setIsReasonModalOpen(false);
              setReasonForRejection();
            }
          }}
          destroyOnClose={true}
        >
          <div className="custom-input mb-3">
            <label htmlFor="reason" className="form-label">
              <b>Please provide a reason for Disabling this Test request.</b>
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
        </Modal>{" "}
      </div>
    </div>
  );
};

export default Applications;
