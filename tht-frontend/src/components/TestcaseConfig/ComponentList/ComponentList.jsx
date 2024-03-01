import React, { useEffect, useState } from "react";
import "./componentList.scss";
import { EditFilled } from "@ant-design/icons";
import { ComponentAPI } from "../../../api/ComponentAPI";
import { Switch, notification } from "antd";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import { useLoader } from "../../loader/LoaderContext";
import { ComponentsActionStateLabels } from "../../../constants/components_constants";
import { set_header } from "../../../reducers/homeReducer";
import { Pagination } from "@mui/material";
import { useDispatch } from "react-redux";
import { Modal } from "antd";
import sortIcon from "../../../styles/images/sort-icon.png";
export default function ComponentList() {
  
  const [sortDirection, setSortDirection] = useState({
    name: "asc",
  });
  const [sortFieldName, setSortFieldName] = useState("name");
  const [initialValues, setInitialValues] = useState({
    name: "",
    description: "",
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [pageSize, setPageSize] = useState(5);
  const [components, setComponents] = useState();
  const { showLoader, hideLoader } = useLoader();
  const [filterState, setFilterState] = useState("");
  const [updateResponse, setUpdateResponse] = useState();
  const dispatch = useDispatch();
  const handleSort = (newSortFieldName) => {
    setSortFieldName(newSortFieldName);
    const newSortDirection = { ...sortDirection };
    newSortDirection[newSortFieldName] =
      sortDirection[newSortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    getAllComponents(
      newSortFieldName,
      newSortDirection[newSortFieldName],
      currentPage,
      pageSize,
      filterState
    );
  };

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    getAllComponents(
      sortFieldName,
      sortDirection[sortFieldName],
      newPage,
      pageSize,
      filterState
    );
  };

  const validationSchema = Yup.object({
    name: Yup.string().required("Name is required *"),
    description: Yup.string().required("Description is required *"),
  });

  useEffect(() => {
    dispatch(set_header("Components"));
    var state = filterState;
    getAllComponents(
      sortFieldName,
      sortDirection[sortFieldName],
      currentPage,
      pageSize,
      state
    );
  }, [filterState]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const upsertComponent = (componentId) => {
    if (!!componentId) {
      ComponentAPI.getComponentById(componentId)
        .then((responseData) => {
          setInitialValues({
            name: responseData.name,
            description: responseData.description,
          });
          setUpdateResponse(responseData);
        })
        .catch((error) => {
          error?.response?.data?.forEach((item) => {
            notification.error({
              placement: "bottomRight",
              message: item.message,
            });
          });
        });
    }
    setIsModalOpen(true);
  };
  const handleSubmit = (values, { setSubmitting }) => {
    if (Object.values(initialValues).some((value) => !value.trim())) {
      //create component
      ComponentAPI.createComponent(values)
        .then(() => {
          notification.success({
            placement: "bottomRight",
            message: "Component Created Successfully",
          });
          getAllComponents(
            sortFieldName,
            sortDirection[sortFieldName],
            currentPage,
            pageSize,
            filterState
          );
          setIsModalOpen(false);
        })
        .catch((error) => {
          error?.response?.data?.forEach((item) => {
            notification.error({
              placement: "bottomRight",
              message: item.message,
            });
          });

          getAllComponents(
            sortFieldName,
            sortDirection[sortFieldName],
            currentPage,
            pageSize,
            filterState
          );
        });
    } else {
      //update component
      const data = {
        ...values,
        state: updateResponse.state,
        rank: updateResponse.rank,
        id: updateResponse.id,
        meta: updateResponse.meta,
      };
      ComponentAPI.updateComponent(data)
        .then(() => {
          notification.success({
            placement: "bottomRight",
            message: "Component Updated Successfully",
          });
          getAllComponents(
            sortFieldName,
            sortDirection[sortFieldName],
            currentPage,
            pageSize,
            filterState
          );
          setIsModalOpen(false);
        })
        .catch((error) => {
          error?.response?.data?.forEach((item) => {
            notification.error({
              placement: "bottomRight",
              message: item.message,
            });
          });
        });
    }
    setInitialValues({ name: "", description: "" });
    setUpdateResponse(null);
    setSubmitting(false);
  };
  const handleCancel = () => {
    setInitialValues({ name: "", description: "" });
    setUpdateResponse(null);
    setIsModalOpen(false);
  };
  const getAllComponents = (
    sortFieldName,
    sortDirection,
    currentPage,
    pageSize,
    filterState
  ) => {
    const params ={};
    params.sort = `${sortFieldName},${sortDirection}`;
    params.page=currentPage-1;
    params.size=pageSize;
    params.state=filterState;
    showLoader();
    ComponentAPI.getComponents(params)
      .then((res) => {
        hideLoader();
        setComponents(res.content);
        setTotalPages(res.totalPages);
      })
      .catch((err) => {
        notification.error({
          placement: "bottomRight",
          message: err.data?.message,
        });
      });
  };
  const componentRequestStates = [
    ...ComponentsActionStateLabels,
    { label: "All", value: "" },
  ];
  const handleToggleChange = (componentId, state) => {
    showLoader();
    const newState =
      state === "component.status.active"
        ? "component.status.inactive"
        : "component.status.active";
    ComponentAPI.changeState(componentId, newState)
      .then((res) => {
        hideLoader();
        let temp = components;
        const idx = temp.findIndex((t) => t.id === componentId);
        temp[idx] = res.data;
        if (filterState) {
          temp = temp.filter((t) => t.id !== componentId);
        }
        setComponents(temp);
        getAllComponents(
          sortFieldName,
          sortDirection[sortFieldName],
          currentPage,
          pageSize,
          filterState
        );
      })
      .catch((error) => {
        throw error;
      });
  };
  return (
    <div id="wrapper">
      <div className="col-12">
        <div className="d-flex justify-content-between">
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
                {componentRequestStates.map((componentRequestState) => (
                  <option
                    value={componentRequestState.value}
                    key={componentRequestState.value}
                  >
                    {componentRequestState.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="d-flex align-items-baseline justify-content-end">
            <button
              type="button"
              className="btn btn-sm btn-outline-secondary menu-like-item"
              onClick={() => upsertComponent()}
            >
              <i className="bi bi-plus"></i>
              Create Component
            </button>
          </div>
        </div>
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th className="col-8">
                  Component Name{" "}
                  <a
                    className="ps-1"
                    href="#"
                    onClick={() => handleSort("name")}
                  >
                    <img src={sortIcon} alt="e" />
                  </a>{" "}
                </th>
                <th className="col-2">Status</th>
                <th className="col-2">Action</th>
              </tr>
            </thead>
            <tbody>
              {components?.length === 0 ? (
                <tr>
                  <td className="text-center" colSpan={2}>
                    There are no components registered.
                  </td>
                </tr>
              ) : (
                components?.map((component) => (
                  <tr key={component?.id}>
                    <td>
                      {component?.name}
                      <p className="description">{component.description}</p>
                    </td>
                    <td>
                      <Switch
                        defaultChecked={
                          component?.state === "component.status.active"
                        }
                        onChange={(checked) =>
                          handleToggleChange(component.id, component.state)
                        }
                        checkedChildren="ACTIVE"
                        unCheckedChildren="INACTIVE"
                      />
                    </td>
                    <td>
                      <button
                        className="btn btn-primary edit-badge"
                        onClick={() => upsertComponent(component?.id)}
                      >
                        <EditFilled />
                        EDIT
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
      <div>
        <Modal open={isModalOpen} onCancel={handleCancel} footer={null}>
          <h4 className="mb-4">
            {!!updateResponse ? "Update Component" : "Create Component"}
          </h4>
          <Formik
            initialValues={initialValues}
            validationSchema={validationSchema}
            onSubmit={handleSubmit}
            enableReinitialize
          >
            <Form>
              <div className="row">
                <div className="col-12">
                  <div className="custom-input mb-3">
                    <label htmlFor="name" className="form-label">
                      Name
                    </label>
                    <Field
                      type="text"
                      id="name"
                      name="name"
                      className="form-control"
                      placeholder="Name"
                    />
                    <ErrorMessage
                      name="name"
                      component="div"
                      className="error-message"
                    />
                  </div>
                </div>
              </div>
              <div className="row">
                <div className="col-12">
                  <div className="custom-input mb-3">
                    <label htmlFor="email" className="form-label">
                      Description
                    </label>
                    <Field
                      as="textarea"
                      type="description"
                      id="description"
                      name="description"
                      className="form-control"
                      placeholder="Description"
                      rows="5"
                    />
                    <ErrorMessage
                      name="description"
                      component="div"
                      className="error-message"
                    />
                  </div>
                </div>
              </div>
              <div className="my-4 text-end">
                <button
                  type="button"
                  className="btn btn-primary btn-white py-1 font-size-10"
                  onClick={handleCancel}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="btn btn-primary btn-blue btn-submit py-1 font-size-10"
                >
                  Submit
                </button>
              </div>
            </Form>
          </Formik>
        </Modal>
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
}
