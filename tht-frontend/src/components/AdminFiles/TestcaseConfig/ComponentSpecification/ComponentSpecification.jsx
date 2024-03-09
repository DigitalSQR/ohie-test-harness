import React, { useEffect, useState } from "react";
import "./ComponentSpecification.scss";
import { useNavigate } from "react-router-dom";
import { EditFilled, EyeOutlined } from "@ant-design/icons";
import { useLoader } from "../../../loader/LoaderContext";
import { SpecificationAPI } from "../../../../api/SpecificationAPI";
import { Switch, Modal } from "antd";
import { useDispatch } from "react-redux";
import { set_header } from "../../../../reducers/homeReducer";
import { useParams } from "react-router-dom";
import { Pagination } from "@mui/material";
import ComponentSpecificationUpsertModal from "./ComponentSpecificationUpsertModal/ComponentSpecificationUpsertModal";
import { ComponentAPI } from "../../../../api/ComponentAPI";
export default function ComponentSpecification() {
  const navigate = useNavigate();
  const [specifications, setSpecifications] = useState();
  const { showLoader, hideLoader } = useLoader();
  const [sortDirection, setSortDirection] = useState({
    name: "asc",
    isRequired: "desc",
    isFunctional: "desc",
  });
  const [sortFieldName, setSortFieldName] = useState("name");
  const [componentDetails, setComponentDetails] = useState();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [specificationId, setSpecificationId] = useState();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 10;
  const { componentId } = useParams();
  const dispatch = useDispatch();

  useEffect(() => {
    refreshAllSpecifications();
    FetchComponentDetails(componentId);
  }, []);

  const FetchComponentDetails = (componentId) => {
    ComponentAPI.getComponentById(componentId)
      .then((responseData) => {
        setComponentDetails(responseData);
        dispatch(set_header(responseData?.name));
      })
      .catch((error) => {});
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

  const handleSort = (newSortFieldName) => {
    setSortFieldName(newSortFieldName);
    const newSortDirection = { ...sortDirection };
    newSortDirection[newSortFieldName] =
      sortDirection[newSortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    fetchData(
      newSortFieldName,
      newSortDirection[newSortFieldName],
      currentPage,
      pageSize
    );
  };

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    fetchData(sortFieldName, sortDirection[sortFieldName], newPage, pageSize);
  };

  const refreshAllSpecifications = () => {
    fetchData(sortFieldName, sortDirection[sortFieldName], currentPage, pageSize);
  };

  const fetchData = async (
    sortFieldName,
    sortDirection,
    currentPage,
    pageSize
  ) => {
    showLoader();
    const params = {
      componentId,
    };
    params.page = currentPage - 1;
    params.size = pageSize;
    params.sort = `${sortFieldName},${sortDirection}`;
    try {
      const resp = await SpecificationAPI.getSpecificationsByComponentId(
        params
      );
      setSpecifications(resp.content);
      setTotalPages(resp.totalPages);
    } catch (error) {
      console.error("Error fetching specifications:", error);
      setSpecifications(undefined);
    } finally {
      hideLoader();
    }
  };

  const handleUpsert = () => {
    setIsModalOpen(true);
  };

  const changeSpecificationState = (specificationId, state) => {
    const newState =
      state === "specification.status.active"
        ? "specification.status.inactive"
        : "specification.status.active";

    const confirmStateChange = () => {
      Modal.confirm({
        title: "State Change",
        content: "Are you sure about changing state to Inactive ?",
        okText: "Save",
        cancelText: "Cancel",
        onOk() {
          handleStateChange(newState);
        },
      });
    };

    const handleStateChange = (newState) => {
      SpecificationAPI.changeState(specificationId, newState)
        .then((res) => {
          hideLoader();

          let temp = specifications;
          const idx = temp.findIndex((t) => t.id === specificationId);
          temp[idx] = res.data;

          setSpecifications(temp);
          refreshAllSpecifications();
        })
        .catch((error) => {});
    };

    if (state === "specification.status.active") {
      confirmStateChange();
    } else {
      handleStateChange(newState);
    }
  };

  return (
    <div id="componentSpecification">
      <div id="wrapper" className="component-specification-page">
        <div>
          <div className="d-flex justify-content-between align-items-center">
            <div className="bcca-breadcrumb">
              <div className="bcca-breadcrumb-item">
                {componentDetails?.name}
              </div>
              <div
                className="bcca-breadcrumb-item"
                onClick={() => {
                  navigate("/testcase-config");
                }}
              >
                Components
              </div>
            </div>
            <div>
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary menu-like-item"
                onClick={() => handleUpsert()}
              >
                <i className="bi bi-plus"></i>
                Create Specification
              </button>
            </div>
          </div>
        </div>
        <div>
          {specifications && specifications?.length > 0 ? (
            <div className="table-responsive mt-3">
              <table className="data-table">
                <thead>
                  <tr>
                    <th className="col-4">
                      Specifications{" "}
                      <a
                        className="ps-1"
                        href="#"
                        onClick={() => handleSort("name")}
                      >
                        {renderSortIcon("name")}
                      </a>{" "}
                    </th>
                    <th className="col-2">
                      Specification Type
                      <a
                        className="ps-1"
                        href="#"
                        onClick={() => handleSort("isFunctional")}
                      >
                        {renderSortIcon("isFunctional")}
                      </a>{" "}
                    </th>
                    <th className="col-2">
                      Required / Recommended
                      <a
                        className="ps-1"
                        href="#"
                        onClick={() => handleSort("isRequired")}
                      >
                        {renderSortIcon("isRequired")}
                      </a>{" "}
                    </th>
                    <th className="col-2">Action</th>
                    <th className="col-1">Edit</th>
                    <th className="col-1">View Testcases</th>
                  </tr>
                </thead>
                <tbody>
                  {specifications?.map((specification) => (
                    <tr key={specification.name}>
                      <td>{specification.name}</td>
                      <td>
                        {specification.functional === true
                          ? "Functional"
                          : "Workflow"}
                      </td>
                      <td>
                        {specification.required === true
                          ? "Required"
                          : "Recommended"}
                      </td>
                      <td className="action-icons-container">
                        <Switch
                          checked={
                            specification?.state ===
                            "specification.status.active"
                          }
                          onChange={() =>
                            changeSpecificationState(
                              specification?.id,
                              specification?.state
                            )
                          }
                          checkedChildren="ACTIVE"
                          unCheckedChildren="INACTIVE"
                        />
                      </td>

                      <td className="col-1.5">
                        <button
                          className="edit-badge"
                          onClick={() => {
                            setSpecificationId(specification?.id);
                            setIsModalOpen(true);
                          }}
                        >
                          <EditFilled />
                          EDIT
                        </button>
                      </td>
                      <td className="col-1.5">
                        {" "}
                        <button
                          className="edit-badge"
                          onClick={() =>
                            navigate(
                              `/testcase-config/manual-testcases/${specification?.id}`
                            )
                          }
                        >
                          <EyeOutlined />
                          VIEW
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="text-center mt-5  ">
              <h4>
                <i>No specifications found</i>
              </h4>
            </div>
          )}
        </div>
        <ComponentSpecificationUpsertModal
          isModalOpen={isModalOpen}
          setIsModalOpen={setIsModalOpen}
          specificationId={specificationId}
          componentId={componentId}
          setSpecificationId={setSpecificationId}
          refreshAllSpecifications={refreshAllSpecifications}
        />
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
}
