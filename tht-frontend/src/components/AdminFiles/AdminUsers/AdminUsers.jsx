import React, { useEffect, useState } from "react";
import { AdminUserAPI } from "../../../api/AdminUserAPI";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import "./admin-user.scss";
import { useNavigate } from "react-router-dom";
import { Pagination } from "@mui/material";
import { Button, Modal } from "antd";
import { useLoader } from "../../loader/LoaderContext";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";
import { store } from "../../../store/store";
import unsorted from "../../../styles/images/unsorted.png";
import sortedUp from "../../../styles/images/sort-up.png";
import sortedDown from "../../../styles/images/sort-down.png";
import { Switch, notification } from "antd";
import {
  ROLE_ID_ADMIN,
  ROLE_ID_TESTER,
} from "../../../constants/role_constants";
/**
 * Admin Users Component:
 * This component handles the users that have either the role of a tester or admin.
 * It provides functionality to update or add a new user and assign certain roles to them.
 */
const AdminUsers = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deleteUserId, setDeleteUserId] = useState();
  const [userInfo, setUserInfo] = useState();
  const dispatch = useDispatch();
  const obj = {
    name: "asc",
    email: "desc",
  };
  const [sortDirection, setSortDirection] = useState(obj);
  const [sortFieldName, setSortFieldName] = useState("name");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPageUsers, setCurrentPageUsers] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const pageSize = 10;

  //Function to handle the state change from active to inactive and vice verca
  const handleOk = (userState) => {
    const changeState =
      userState == "user.status.inactive"
        ? "user.status.active"
        : "user.status.inactive";
    showLoader();
    AdminUserAPI.updateUserState(deleteUserId, changeState)
      .then(() => {
        getAllUsers(sortFieldName, sortDirection);
        hideLoader();
        setIsModalOpen(false);
        setDeleteUserId(null);
      })
      .catch((error) => {
        console.error("Error updating user state:", error);
        setIsModalOpen(false);
      });
    hideLoader();
  };

  //Function to close the user state inactivation confirmation modal
  const handleCancel = () => {
    setIsModalOpen(false);
    setDeleteUserId(null);
  };

  //useEffect hook to update the header title, fetch the user details and call the getAllUsers function
  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserInfo(userInfo);
    dispatch(set_header("User Management"));
    getAllUsers(sortFieldName, sortDirection);
  }, []);

  //Function to get all the users that have role of either Admin or Tester to display in the component
  const getAllUsers = (sortFieldName, sortDirection) => {
    showLoader();
    AdminUserAPI.fetchAllUsers(
      sortFieldName,
      sortDirection[sortFieldName]
    ).then((data) => {
      hideLoader();
      const activeUsers = data.content.filter(
        (user) =>
          user?.roleIds.includes(ROLE_ID_ADMIN) ||
          user?.roleIds.includes(ROLE_ID_TESTER)
      );
      setTotalPages(Math.ceil(activeUsers.length / pageSize));

      setUsers(activeUsers);
      const startIndex = (currentPage - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      const activeUsersSlice = activeUsers.slice(startIndex, endIndex);
      setCurrentPageUsers(activeUsersSlice);
    });
  };

  //Function that navigates to the update user component
  const handleEdit = (userId) => {
    navigate(`/user-management/update-admin-user?userId=${userId}`);
  };

  //Function to toggle between the user state
  const handleToggleChange = (userId) => {
    setDeleteUserId(userId);
  };

  useEffect(() => {
    let user = currentPageUsers.filter(user => user.id == deleteUserId)[0];
    if (user?.state == "user.status.active") {
      setIsModalOpen(true);
    } else if (user?.state) {
      handleOk(user.state);
    }
  }, [deleteUserId])

  //Function to sort the rows based on sortFieldName passed as the parameter
  const handleSort = (sortFieldName) => {
    setSortFieldName(sortFieldName);
    const newSortDirection = { ...obj };
    newSortDirection[sortFieldName] =
      sortDirection[sortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    getAllUsers(sortFieldName, newSortDirection);
  };

  //Function that toggles between the sort icons corresponding to ascending or descending order
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

  //Function to handle the change page in pagination
  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <div id="adminUsers">
      <div id="wrapper">
        <div className="col-12">
          <div className="row justify-content-between mb-4">
            <div className="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12"></div>
            <div className="col-auto ml-auto">
              <button
                onClick={() => {
                  navigate("/user-management/create-user");
                }}
                type="button"
                className="btn btn-sm btn-outline-secondary menu-like-item"
              >
                <i className="bi bi-plus"></i>
                Create User
              </button>
            </div>
          </div>

          <div className="table-responsive">
            <table className="data-table capitalize-words">
              <thead>
                <tr>
                  <th style={{width:"25%"}}>
                    NAME
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("name")}
                    >
                      {renderSortIcon("name")}
                    </span>
                  </th>
                  <th style={{width:"20%"}}>
                    EMAIL
                    <span
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("email")}
                    >
                      {renderSortIcon("email")}
                    </span>
                  </th>
                  <th style={{width:"25%"}}>ROLE</th>
                  <th style={{width:"15%"}}>STATUS</th>
                  <th style={{width:"15%"}}>ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {currentPageUsers?.map((user) => (
                  <tr key={user.id}>
                    <td className="fw-bold">{user.name}</td>
                    <td className = "toLowerCase-words">{user.email}</td>
                    <td>
                      {user?.roleIds.map((roleId) => (
                        <span  className="badge text-bg-secondary me-1 font-size-12 fw-normal" key={roleId}>{roleId.replace("role.", "")}</span>
                      ))}
                    </td>
                    <td>
                    {userInfo?.id !== user?.id && (
                        <span className="">
                          <Switch
                            defaultChecked= "true"
                            checked={
                              user?.state === "user.status.active"
                            }
                            onChange={() =>
                              handleToggleChange(user.id)
                            }
                            checkedChildren="ACTIVE"
                            unCheckedChildren="INACTIVE"
                          />
                        </span>
                      )}
                    </td>
                    <td className="action-icons-container">
                      <span className="cursor-pointer font-size-12 text-blue fw-bold"
                        onClick={() => handleEdit(user.id)}
                      >
                        <i className="bi bi-pencil-square  font-size-16"></i>{" "}
                        EDIT
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
        <Modal
          title="Inactivation Confirmation"
          open={isModalOpen}
          onOk={handleOk}
          onCancel={handleCancel}
        >
          <p>Are you sure you want to inactive the user?</p>
        </Modal>
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

export default AdminUsers;
