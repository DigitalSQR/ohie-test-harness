import React, { useEffect, useState } from "react";
import "./ComponentSpecification.scss";
import { useNavigate } from "react-router-dom";
import { EditFilled, EyeOutlined } from "@ant-design/icons";
import { useLoader } from "../../../loader/LoaderContext";
import { SpecificationAPI } from "../../../../api/SpecificationAPI";
import { Switch, Modal, Empty, Breadcrumb } from "antd";
import { useDispatch } from "react-redux";
import { set_header } from "../../../../reducers/homeReducer";
import { useParams } from "react-router-dom";
import { Pagination } from "@mui/material";
import ComponentSpecificationUpsertModal from "./ComponentSpecificationUpsertModal/ComponentSpecificationUpsertModal";
import unsorted from "../../../../styles/images/unsorted.png";
import sortedUp from "../../../../styles/images/sort-up.png";
import sortedDown from "../../../../styles/images/sort-down.png";
import { ComponentAPI } from "../../../../api/ComponentAPI";
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';

export default function ComponentSpecification() {
  const navigate = useNavigate();
  const [specifications, setSpecifications] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const [sortDirection, setSortDirection] = useState({
    rank: "asc",
    isRequired: "desc",
    isFunctional: "desc",
  });
  const [sortFieldName, setSortFieldName] = useState("rank");
  const [componentDetails, setComponentDetails] = useState();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [specificationId, setSpecificationId] = useState();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalElements, setTotalElements]=useState();
  const [totalPages, setTotalPages] = useState(1);
  const [pageSize, setPageSize] = useState(10);
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
      setTotalElements(resp.totalElements);
      setSpecifications(resp.content);
      setTotalPages(resp.totalPages);
    } catch (error) {
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


  const onDragEnd = (result) => {

    const { destination, source, draggableId } = result;
    if (!destination || destination.droppableId === source.droppableId && destination.index === source.index) {
      return;
    }

    let { droppableId } = destination;
    droppableId = JSON.parse(droppableId);
  
    const reorderedItems = [...specifications];
    const [reorderedItem] = reorderedItems.splice(source.index, 1);
    reorderedItems.splice(destination.index, 0, reorderedItem);

    const newRank =  droppableId[destination.index].rank;
    setSpecifications(reorderedItems);

    SpecificationAPI.changeRank(draggableId, newRank)
      .then((res) => {
        hideLoader();
        refreshAllSpecifications();
      })
      .catch((err) => {
        hideLoader();
      });

  };

  useEffect(() => {
    fetchData(
      sortFieldName,
      sortDirection[sortFieldName],
      currentPage,
      pageSize
    );
  }, [pageSize]);


  return (
    <div id="componentSpecification">
      <div id="wrapper" className="component-specification-page">
        <div className="mb-3">
          <div className="d-flex justify-content-between align-items-center">
          <Breadcrumb className="custom-breadcrumb">
            <Breadcrumb.Item onClick={() => navigate(`/testcase-config`)} className="breadcrumb-item" href="">Components</Breadcrumb.Item>
            <Breadcrumb.Item className="breadcrumb-item">{componentDetails?.name}</Breadcrumb.Item>
          </Breadcrumb>
          
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
          <hr className="hr-light"/>
        </div>
        <div>
            <div className="table-responsive">
            <DragDropContext onDragEnd={onDragEnd}>
              <Droppable droppableId={specifications?JSON.stringify(specifications):""}>
                {(provided) => (
              <table className="data-table capitalize-words" {...provided.droppableProps} ref={provided.innerRef}>
                <thead>
                  <tr>
                    {/* <th style={{width:'5%'}}></th> */}
                    <th style={{width:'15%'}}>
                      Specifications{" "}
                      <a
                        className="ps-1"
                        href="#"
                        onClick={() => handleSort("name")}
                      >
                        {renderSortIcon("name")}
                      </a>{" "}
                    </th>
                    <th style={{width:'20%'}}>
                      Specification Type
                      <a
                        className="ps-1"
                        href="#"
                        onClick={() => handleSort("isFunctional")}
                      >
                        {renderSortIcon("isFunctional")}
                      </a>{" "}
                    </th>
                    <th style={{width:'20%'}}>
                      Required / Recommended
                      <a
                        className="ps-1"
                        href="#"
                        onClick={() => handleSort("isRequired")}
                      >
                        {renderSortIcon("isRequired")}
                      </a>{" "}
                    </th>
                    <th style={{width: "12%"}}>Status</th>
                    <th style={{width: "10%"}}>Rank{" "}
                      <a
                        className="ps-1"
                        href="#"
                        onClick={() => handleSort("rank")}
                      >
                        {renderSortIcon("rank")}
                      </a>{" "}
                    </th>
                    <th style={{width:'45%'}}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                {specifications && specifications.length > 0 ? (
                  specifications.map((specification, index) => (
                    <Draggable key={specification.id} draggableId={specification.id} index={index} isDragDisabled={true}>
                    {(provided) => (
                      <tr
                        {...provided.draggableProps}
                        ref={provided.innerRef}
                        key={specification.name}
                      >
                        {/* <td {...provided.dragHandleProps}>
                          <i className="bi bi-list" style={(sortFieldName == 'rank')? {} : {cursor: 'not-allowed'}} title={(sortFieldName == 'rank')? "" : "Sort by rank to enable drag and drop rank modification."}></i>
                        </td> */}
                      <td className="fw-bold">{specification.name}</td>
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
                      <td>
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
                      <td>
                        {specification?.rank}
                      </td>
                      <td className="action-icons-container">
                        <div className="d-flex">
                        <span
                          className="cursor-pointer text-blue font-size-12 fw-bold"
                          onClick={() => {
                            setSpecificationId(specification?.id);
                            setIsModalOpen(true);
                          }}
                        >
                          <i className="bi bi-pencil-square font-size-16 "></i>{" "}
                          EDIT
                        </span>&nbsp;
                        <span
                          className="cursor-pointer ps-2 text-blue font-size-12 fw-bold"
                          onClick={() =>
                            navigate(
                              `/testcase-config/manual-testcases/${specification?.id}`
                            )
                          }
                        >
                          <i className="bi bi-eye font-size-16 "></i>{" "}
                          TESTCASES
                        </span>
                        </div>
                      </td>
                    </tr>
                    )}
                    </Draggable>
                  )) ) : (
                  <tr>
                    <td colSpan={7}>
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
                fetchData(sortFieldName, sortDirection[sortFieldName], 1, e.target.value);
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
