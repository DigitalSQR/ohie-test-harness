import React, { useEffect, useState } from "react";
import { AdminUserAPI } from "../../../api/AdminUserAPI";
import "./admin-user.scss";
import { Modal } from "antd";
import { useLoader } from "../../loader/LoaderContext";
import { Pagination, PaginationItem } from "@mui/material";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";
import { store } from "../../../store/store";
import unsorted from "../../../styles/images/unsorted.png";
import sortedUp from "../../../styles/images/sort-up.png";
import sortedDown from "../../../styles/images/sort-down.png";
import { Switch } from "antd";
import {
  ROLE_ID_ADMIN,
  ROLE_ID_TESTER,
  ROLE_ID_SUPERADMIN,
} from "../../../constants/role_constants";
import AddAdminUser from "./AddAdminUsers/AddAdminUser";
import UpdateAdminUser from "./UpdateAdminUser/UpdateAdminUser";
/**
 * Admin Users Component:
 * This component handles the users that have either the role of a tester or admin.
 * It provides functionality to update or add a new user and assign certain roles to them.
 */
const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isCreateUserModalOpen, setIsCreateUserModalOpen] = useState(false);
  const [isEditUserModalOpen, setIsEditUserModalOpen] = useState(false);
  const [userId, setUserId] = useState();

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 10;

  const [deleteUserId, setDeleteUserId] = useState();
  const [userInfo, setUserInfo] = useState();
  const dispatch = useDispatch();
  const obj = {
    name: "asc",
    email: "desc",
  };
  const [sortDirection, setSortDirection] = useState(obj);
  const [sortFieldName, setSortFieldName] = useState("name");
  const { showLoader, hideLoader } = useLoader();

  //Function to handle the state change from active to inactive and vice verca
  const handleOk = (userState) => {
    const changeState =
      userState == "user.status.inactive"
        ? "user.status.active"
        : "user.status.inactive";
    showLoader();
    AdminUserAPI.updateUserState(deleteUserId, changeState)
      .then(() => {
        getAllUsers(sortFieldName, sortDirection, currentPage);
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

  const handleChangePage = ( event, newPage) => {
    setCurrentPage(newPage);
    getAllUsers(sortFieldName, sortDirection, newPage)
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
    getAllUsers(sortFieldName, sortDirection, currentPage);
  }, []);

  //Function to get all the users that have role of either Admin or Tester to display in the component
  const getAllUsers = (sortFieldName, sortDirection, currentPage) => {
    showLoader();
    AdminUserAPI.fetchAllUsers(sortFieldName, sortDirection[sortFieldName], currentPage, pageSize)
    .then((data) => {
      // removing user if role is superadmin
      const filteredData = data.content.filter(
        (user) => !user?.roleIds.includes(ROLE_ID_SUPERADMIN)
      );
      setUsers(filteredData);
      setTotalPages(data.totalPages);
      hideLoader();
      })
  };

  
  //Function to handle adding admin user modal
  const handleCreateUser = () => {
    setIsCreateUserModalOpen(true);
  };

  //Function to handle updating admin user modal
  const handleEditUser = () => {
    setIsEditUserModalOpen(true);
  };
  
  //Function to refresh all components on editing and submitting of Admin user modal
  const refreshAllComponents = () => {
    getAllUsers(sortFieldName, sortDirection, currentPage);
  };

  //Function to toggle between the user state
  const handleToggleChange = (userId) => {
    setDeleteUserId(userId);
  };

  useEffect(() => {
    let user = users.filter(user => user.id == deleteUserId)[0];
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
    getAllUsers(sortFieldName, newSortDirection, currentPage);
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

  return (
    <div id="adminUsers">
      <div id="wrapper">
        <div className="col-12">
          <div className="row justify-content-between mb-4">
            <div className="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12"></div>
            <div className="col-auto ml-auto">
              <button
                onClick={handleCreateUser}
                id="adminUser-createUser"
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
                    id="adminUsers-sortByName"
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
                    id="adminUsers-sortByEmail"
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
                {users?.map((user,index) => (
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
                            id={`adminUsers-switch-status-${index}`}
                          />
                        </span>
                      )}
                    </td>
                    <td className="action-icons-container">
                      <span className="cursor-pointer font-size-12 text-blue fw-bold"
                        id={`adminUsers-editButton-${index}`}
                        onClick={() => {
                          handleEditUser()
                          setUserId(user.id);
                        }}
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
          closable={false}
          title="Inactivation Confirmation"
          open={isModalOpen}
          onOk={handleOk}
          onCancel={handleCancel}
          okButtonProps={{id:"adminUsers-ok-inactiveButton"}}
          cancelButtonProps={{id:"adminUsers-cancel-inactiveButton"}}
        >
          <p>Are you sure you want to inactive the user?</p>
        </Modal>
        <AddAdminUser  
        isModalOpen={isCreateUserModalOpen}
        setIsModalOpen={setIsCreateUserModalOpen}
        refreshAllComponents={refreshAllComponents}
        />
        <UpdateAdminUser  
        isModalOpen={isEditUserModalOpen}
        setIsModalOpen={setIsEditUserModalOpen}
        userId={userId}
        setUserId={setUserId}
        refreshAllComponents={refreshAllComponents}
        />
        {totalPages > 1 && (
        <Pagination
          id="adminUsers-pagination"
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
                  id={`Adminusers-page-${item.page}`}
                  component="button"
                  onClick={() => handleChangePage(null, item.page)}
                />
              );
            } else if (item.type === 'previous') {
              return (
                <PaginationItem
                  {...item}
                  id="AdminUsers-previous-page-button"
                  component="button"
                  onClick={() => handleChangePage(null, currentPage - 1)}
                />
              );
            } else if (item.type === 'next') {
              return (
                <PaginationItem
                  {...item}
                  id="AdminUsers-next-page-button"
                  component="button"
                  onClick={() => handleChangePage(null, currentPage + 1)}
                />
              );
            }
            return null;
          }}
        />
      )}
      </div>

    </div>
  );
};

export default AdminUsers;
