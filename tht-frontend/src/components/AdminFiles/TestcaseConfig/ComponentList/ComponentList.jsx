import React, { useEffect, useState } from "react";
import "./componentList.scss";
import { EditFilled, EyeOutlined } from "@ant-design/icons";
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
import ValidateConfigFacts from "../ValidateConfigFacts/ValidateConfigFacts.jsx";
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';

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
  const [filterState, setFilterState] = useState("");
  const [componentId, setComponentId] = useState();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [totalElements, setTotalElements] = useState();
  const [pageSize, setPageSize] = useState(10);
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

  const renderSortIcon = (fieldName) => {
    if (sortFieldName === fieldName) {
      return (
        <img
          className="cursor-pointer"
          style={{ width: "8px" }}
          src={
            sortDirection[fieldName] === "asc"
              ? sortedUp
              : sortedDown
          }
        ></img>
      );
    }
    return <img className="cursor-pointer" style={{ width: "10px" }} src={unsorted} />;
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

  useEffect(() => {
    dispatch(set_header("Components"));
    refreshAllComponents();
  }, [filterState]);

  const getAllComponents = (
    sortFieldName,
    sortDirection,
    currentPage,
    pageSize,
    filterState
  ) => {
    const params = {};
    params.sort = `${sortFieldName},${sortDirection}`;
    params.page = currentPage - 1;
    params.size = pageSize;
    params.state = filterState;
    showLoader();
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
    getAllComponents(sortFieldName, sortDirection[sortFieldName], currentPage, pageSize, filterState);
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
          let temp = components;
          const idx = temp.findIndex((t) => t.id === componentId);
          temp[idx] = res.data;
          if (filterState) {
            temp = temp.filter((t) => t.id !== componentId);
          }
          setComponents(temp);
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
          <div className="d-flex justify-content-between">
            <div className="d-flex align-items-baseline">
              <span className="pe-3 text-nowrap">Status :</span>
              <div className="mb-4">
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
                className="btn btn-sm btn-outline-secondary me-2"
                onClick={() => navigate("/validate-config")}
              ><FileSearchOutlined className="me-1" />Validate Configuration</button>
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary menu-like-item"
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
                  <table className="data-table capitalize-words" {...provided.droppableProps} ref={provided.innerRef}>
                    <thead>
                      <tr>
                        {/* <th className="col-1"></th> */}
                        <th className="col-4">
                          Component Name{" "}
                          <span className="ps-1" onClick={() => handleSort("name")}>
                            {renderSortIcon("name")}
                          </span>{" "}
                        </th>
                        <th className="col-2">Status</th>
                        <th className="col-2">
                          Rank{" "}
                          <span className="ps-1" onClick={() => handleSort("rank")}>
                            {renderSortIcon("rank")}
                          </span>{" "}
                        </th>
                        <th className="col-4">Actions</th>
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
                              <td>{component.name}</td>
                              <td>
                                <Switch
                                  checked={component.state === "component.status.active"}
                                  onChange={() => changeComponentState(component.id, component.state)}
                                  checkedChildren="ACTIVE"
                                  unCheckedChildren="INACTIVE"
                                />
                              </td>
                              <td>{component.rank}</td>
                              <td>
                                <span
                                  className="cursor-pointer"
                                  onClick={() => {
                                    setComponentId(component.id);
                                    setIsModalOpen(true);
                                  }}
                                >
                                  <i className="bi bi-pencil-square font-size-16 text-green-50"></i>{" "}
                                  EDIT
                                </span>
                                &nbsp;
                                <span
                                  className="cursor-pointer ps-2"
                                  onClick={() =>
                                    navigate(`/testcase-config/component-specification/${component.id}`)
                                  }
                                >
                                  <i className="bi bi-eye font-size-16 text-blue-50"></i>{" "}
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
        <div className="d-flex justify-content-end">
        {totalElements > 10 && (
          <div className="page-size-selector mt-4">
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
                  filterState
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
  );
}
