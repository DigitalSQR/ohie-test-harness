import React, { useEffect, useState } from "react";
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
const Applications = () => {
  const testRequestStates = [
    ...TestRequestActionStateLabels,
    { label: "All", value: "" },
  ];
  const [filterState, setFilterState] = useState("");
  const [testRequests, setTestRequests] = useState([]);
  const [sortDirection, setSortDirection] = useState({
    name: "desc",
    email: "desc",
  });
  const [sortFieldName, setSortFieldName] = useState();
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const { showLoader, hideLoader } = useLoader();
  const navigate = useNavigate();

  useEffect(() => {
    var state = filterState;
    if (!state || state == "") {
      state = TestRequestActionStateLabels.map((state) => state.value);
    }
    if (state) {
      getAllTestRequests(state, sortFieldName, sortDirection, currentPage);
    }
  }, [filterState]);

  const getAllTestRequests = (
    filterState,
    sortFieldName,
    sortDirection,
    currentPage
  ) => {
    showLoader();
    TestRequestAPI.getTestRequestsByState(
      filterState,
      sortFieldName,
      sortDirection[sortFieldName],
      currentPage - 1,
      pageSize
    )
      .then((res) => {
        hideLoader();
        setTestRequests(res.content);
        console.log(res.content);
        setTotalPages(res.totalPages);
      })
      .catch((err) => {
        notification.error({
          placement: "bottomRight",
          message: "Oops! Error fetching test requests!",
        });
        console.log(err);
      });
  };

  const handleSort = (sortFieldName) => {
    setSortFieldName(sortFieldName);
    const newSortDirection = { ...sortDirection };
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

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    getAllTestRequests(filterState, sortFieldName, sortDirection, newPage);
  };

  const viewReport = (testRequestId) => {
    navigate(`/application-report/${testRequestId}`);
  };

  return (
    <div>
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
            <table className=" data-table">
              <thead>
                <tr>
                  <th className="col-2">
                    APP NAME{" "}
                    <a className="ps-1" href="#">
                      <img
                        src={sortIcon}
                        alt="Sort"
                        onClick={() => handleSort("name")}
                      />
                    </a>
                  </th>
                  <th className="col-2">
                    COMPANY NAME{" "}
                    <a className="ps-1" href="#">
                      <img
                        src={sortIcon}
                        alt="Sort"
                        onClick={() => handleSort("productName")}
                      />
                    </a>
                  </th>
                  <th className="col-2">DATE OF APPLICATION</th>
                  <th className="col-2">
                    EMAIL ID <a className="ps-1" href="#"></a>
                  </th>
                  <th className="col-4">CHOOSE ACTION</th>
                </tr>
              </thead>
              <tbody>
                {testRequests.length === 0 ? (
                  <>
                    <tr>
                      <td className="text-center" colSpan={5}>
                        No test requests found. Register one or wait for test
                        request to be approved
                      </td>
                    </tr>
                  </>
                ) : null}
                {testRequests?.map((testRequest) => (
                  <tr key={testRequest.name}>
                    <td>{testRequest.name}</td>
                    <td>{testRequest.productName !== "" ? testRequest.productName : '-'}</td>
                    <td>{formatDate(testRequest.meta.updatedAt)}</td>
                    <td>
                      <UserIdEmailConnector
                        userId={testRequest.assesseeId}
                      ></UserIdEmailConnector>
                    </td>
                    <td>
                      {testRequest.state !== "test.request.status.finished" ? (
                        <button
                          className={StateClasses[testRequest.state]?.btnClass}
                          onClick={() => {
                            navigate(
                              `/dashboard/choose-test/${testRequest.id}`
                            );
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
                        <span
                          className={`badge ${
                            StateBadgeClasses[testRequest.state]
                          }`}
                        >
                          {TestRequestStateConstantNames[testRequest.state]}
                        </span>
                      )}
                      <button
                        class="btn btn-blue-sm report"
                        onClick={() => viewReport(testRequest.id)}
                      >
                        {" "}
                        <i class="bi bi-file-text"></i> Report
                      </button>
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
