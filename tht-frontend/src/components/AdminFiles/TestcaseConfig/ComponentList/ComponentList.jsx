import React, { useEffect, useState } from "react";
import "./componentList.scss";
import { ComponentAPI } from "../../../../api/ComponentAPI";
import { Switch, Modal, Empty } from "antd";
import { useLoader } from "../../../loader/LoaderContext";
import ComponentUpsertModal from "./ComponentUpsertModal/ComponentUpsertModal";
import { ComponentsActionStateLabels } from "../../../../constants/components_constants";
import { set_header } from "../../../../reducers/homeReducer";
import { Pagination } from "@mui/material";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { FileSearchOutlined } from "@ant-design/icons";
import unsorted from "../../../../styles/images/unsorted.png";
import sortedUp from "../../../../styles/images/sort-up.png";
import sortedDown from "../../../../styles/images/sort-down.png";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import { SearchOutlined } from "@ant-design/icons";

export default function ComponentList() {
  const navigate = useNavigate();
  const [sortDirection, setSortDirection] = useState({
    rank: "asc",
  });
  const [sortFieldName, setSortFieldName] = useState("rank");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [components, setComponents] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const [componentId, setComponentId] = useState();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [totalElements, setTotalElements] = useState();
  const [pageSize, setPageSize] = useState(10);
  const dispatch = useDispatch();

  const initialState = ComponentsActionStateLabels.find(
    (item) => item.label === "All"
  ).value;

  const [componentSearchFilter, setComponentSearchFilter] = useState({
    name: "",
    state: initialState,
    rank: "",
  });

  const updateFilter = (field, value) => {
    setComponentSearchFilter((prevFilter) => ({
      ...prevFilter,
      [field]: value,
    }));
  };

  const handleComponentSearch = () => {
    setCurrentPage(1);
    getAllComponents(sortFieldName, sortDirection[sortFieldName], 1, pageSize);
  };

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

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    getAllComponents(
      sortFieldName,
      sortDirection[sortFieldName],
      newPage,
      pageSize
    );
  };

  useEffect(() => {
    dispatch(set_header("Components"));
    refreshAllComponents();
  }, []);

  const getAllComponents = (
    sortFieldName,
    sortDirection,
    currentPage,
    pageSize
  ) => {
    showLoader();
    let params = {};
    params.sort = `${sortFieldName},${sortDirection}`;
    params.page = currentPage - 1;
    params.size = pageSize;

    const filteredComponentSearchFilter = Object.keys(componentSearchFilter)
      .filter((key) => {
        const value = componentSearchFilter[key];
        return typeof value === "string" ? value.trim() !== "" : !!value;
      })
      .reduce((acc, key) => {
        if (typeof componentSearchFilter[key] === "string")
          acc[key] = componentSearchFilter[key].trim();
        else acc[key] = componentSearchFilter[key];
        return acc;
      }, {});
    params = { ...params, ...filteredComponentSearchFilter };

    ComponentAPI.getComponents(params)
      .then((res) => {
        hideLoader();
        setComponents(res.content);
        setTotalElements(res.totalElements);
        setTotalPages(res.totalPages);
      })
      .catch((err) => {
        hideLoader();
      });
  };

  const refreshAllComponents = () => {
    getAllComponents(
      sortFieldName,
      sortDirection[sortFieldName],
      currentPage,
      pageSize
    );
  };

  const componentRequestStates = [
    ...ComponentsActionStateLabels,
    { label: "All", value: "" },
  ];

  const changeComponentState = (componentId, state) => {
    const newState =
      state === "component.status.active"
        ? "component.status.inactive"
        : "component.status.active";

    const confirmStateChange = () => {
      Modal.confirm({
        cancelButtonProps: {
          id: "componentList-deactivateComponent-cancelButton",
        },
        okButtonProps: { id: "componentList-deactivateComponent-okButton" },
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
      ComponentAPI.changeState(componentId, newState)
        .then((res) => {
          hideLoader();
          refreshAllComponents();
        })
        .catch((error) => {
          hideLoader();
        });
    };

    if (state === "component.status.active") {
      confirmStateChange();
    } else {
      handleStateChange(newState);
    }
  };

  const onDragEnd = (result) => {
      const { destination, source, draggableId } = result;
      
      if (!destination || (destination.droppableId === source.droppableId && destination.index === source.index)) {
        return;
      }
      let { droppableId } = destination;
      droppableId = JSON.parse(droppableId);
  
      const reorderedItems = [...components];
      const [reorderedItem] = reorderedItems.splice(source.index, 1);
      reorderedItems.splice(destination.index, 0, reorderedItem);
  
      const newRank =  droppableId[destination.index].rank;
      setComponents(reorderedItems);
  
      ComponentAPI.changeRank(draggableId, newRank)
        .then((res) => {
          hideLoader();
          refreshAllComponents();
        })
        .catch((err) => {
          hideLoader();
        });  
  };

  return (
    <div id="componentList">
      <div id="wrapper">
        <div className="col-12">
          <div className="d-flex justify-content-between mb-3">
            <div></div>
            <div className="d-flex align-items-baseline justify-content-end">
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary me-2"
                id="componentList-navToValidateConfig"
                onClick={() => navigate("/validate-config")}
              ><FileSearchOutlined className="me-1" />Validate Configuration</button>
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary menu-like-item"
                id="componentList-createComponent"
                onClick={() => setIsModalOpen(true)}
              >
                <i className="bi bi-plus"></i>
                Create Component
              </button>
            </div>
          </div>
          <div className="table-responsive">
            <DragDropContext onDragEnd={onDragEnd}>
              <Droppable droppableId={components?JSON.stringify(components):""}>
                {(provided) => (
                  <table
                    className="data-table capitalize-words"
                    {...provided.droppableProps}
                    ref={provided.innerRef}
                  >
                    <thead>
                      <tr>
                        {/* <th className="col-1"></th> */}
                        <th className="col-4">
                          Component Name{" "}
                          <span
                            className="ps-1"
                            id="componentList-sortByName"
                            onClick={() => handleSort("name")}
                          >
                            {renderSortIcon("name")}
                          </span>{" "}
                          <div className="filter-box">
                            <input
                              type="text"
                              placeholder="Search by Component"
                              className="form-control filter-input"
                              value={componentSearchFilter.name}
                              onChange={(e) =>
                                updateFilter("name", e.target.value)
                              }
                            />
                          </div>
                        </th>
                        <th className="col-2">
                          Status{" "}
                          <div className="filter-box">
                            <select
                              className="form-select custom-select custom-select-sm filter-input"
                              aria-label="Default select example"
                              value={componentSearchFilter.state}
                              onChange={(e) => {
                                updateFilter("state", e.target.value);
                              }}
                            >
                              {ComponentsActionStateLabels.map(
                                (componentState) => (
                                  <option
                                    value={componentState.value}
                                    key={componentState.value}
                                  >
                                    {componentState.label}
                                  </option>
                                )
                              )}
                            </select>
                          </div>
                        </th>
                        <th className="col-2">
                          Rank{" "}
                          <span
                            className="ps-1"
                            id="componentList-sortByRank"
                            onClick={() => handleSort("rank")}
                          >
                            {renderSortIcon("rank")}
                          </span>{" "}
                          <div className="filter-box">
                            <input
                              type="number"
                              placeholder="Search by Rank"
                              className="form-control filter-input"
                              value={componentSearchFilter.rank}
                              onChange={(e) =>
                                updateFilter("rank", e.target.value)
                              }
                            />
                          </div>
                        </th>
                        <th className="col-4">
                          Actions{" "}
                          <div className="filter-box">
                            <button
                              className="search-button"
                              onClick={handleComponentSearch}
                              id="handleUserSearch"
                            >
                              <SearchOutlined />
                              Search
                            </button>
                          </div>
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                    {components && components.length > 0 ? (
                      components.map((component, index) => (
                        <Draggable key={component.id} draggableId={component.id} index={index} isDragDisabled={true}>
                          {(provided) => (
                            <tr 
                              {...provided.draggableProps}
                              ref={provided.innerRef}
                              key={component.name}
                            >
                              {/* <td {...provided.dragHandleProps}>
                                <i className="bi bi-list" style={(sortFieldName == 'rank')? {} : {cursor: 'not-allowed'}} title={(sortFieldName == 'rank')? "" : "Sort by rank to enable drag and drop rank modification."}>
                                </i>                              
                              </td> */}
                              <td className="fw-bold">{component.name}</td>
                              <td>
                                <Switch
                                  id="componentList-switch-componentStatus"
                                  checked={component.state === "component.status.active"}
                                  onChange={() => changeComponentState(component.id, component.state)}
                                  checkedChildren="ACTIVE"
                                  unCheckedChildren="INACTIVE"
                                />
                              </td>
                              <td>{component.rank}</td>
                              <td>
                                <span
                                  className="cursor-pointer font-size-12 text-blue fw-bold"
                                  id={`componentList-editComponent-${index}`}
                                  onClick={() => {
                                    setComponentId(component.id);
                                    setIsModalOpen(true);
                                  }}
                                >
                                  <i className="bi bi-pencil-square font-size-16 "></i>{" "}
                                  EDIT
                                </span>
                                &nbsp;
                                <span
                                  className="cursor-pointer ps-2 font-size-12 text-blue fw-bold"
                                  id={`componentList-openSpecification-${index}`}
                                  onClick={() =>
                                    navigate(`/testcase-config/component-specification/${component.id}`)
                                  }
                                >
                                  <i className="bi bi-eye font-size-16  "></i>{" "}
                                  SPECIFICATIONS
                                </span>
                              </td>
                            </tr>
                          )}
                        </Draggable>
                      ))) : (
                        <tr>
                          <td colSpan={6}>
                            <Empty description="No Record Found." />
                          </td>
                        </tr>
                      )}
                      {provided.placeholder}
                    </tbody>
                  </table>
                )}
              </Droppable>
            </DragDropContext>
          </div>
        </div>
        <div>
          <ComponentUpsertModal
            isModalOpen={isModalOpen}
            setIsModalOpen={setIsModalOpen}
            componentId={componentId}
            setComponentId={setComponentId}
            refreshAllComponents={refreshAllComponents}
          />
        </div>
        <div className="row mt-4">
          <div className="col-md-6 text-end">

        
        {totalPages > 1 && (
          <Pagination
            className="pagination-ui mt-0 justify-content-end"
            count={totalPages}
            page={currentPage}
            onChange={handleChangePage}
            variant="outlined"
            shape="rounded"
          />
        )}
          </div>
        <div className="col-md-6 text-end justify-content-end ">
        {totalElements > 10 && (
          <div className="page-size-selector ms-auto">
            <select
              className="form-select custom-select custom-select-sm"
              aria-label="Default select example"
              value={pageSize}
              onChange={(e) => {
                setPageSize(e.target.value);
                setCurrentPage(1);
                getAllComponents(
                  sortFieldName,
                  sortDirection[sortFieldName],
                  1,
                  e.target.value,        
                );
              }}
            >
              <option value="10" key="10">
                10
              </option>
              <option value="20" key="20">
                20
              </option>
              <option value="30" key="30">
                30
              </option>
              <option value="" key="all">
                All
              </option>
            </select>
          </div>
        )}
        </div>
        </div>
      </div>
    </div>
  );
}
