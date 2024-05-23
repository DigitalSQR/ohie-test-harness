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
import { SearchOutlined } from "@ant-design/icons";
import { Empty} from "antd";
import { Switch } from "antd";
import {
  ROLE_ID_SUPERADMIN,
  UserManagementRoleActionLabels,
} from "../../../constants/role_constants";
import AddAdminUser from "./AddAdminUsers/AddAdminUser";
import UpdateAdminUser from "./UpdateAdminUser/UpdateAdminUser";
import { UserManagementStateActionLabels } from "../../../constants/user_constants";
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
  const initialRoles = UserManagementRoleActionLabels.find(
    (item) => item.label === "All"
  ).value;

  const initialState = UserManagementStateActionLabels.find(
    (item) => item.label === "All"
  ).value;

  const [userSearchFilter, setUserSearchFilter] = useState({
    name: "",
    email: "",
    role: initialRoles,
    state: initialState,
  });

  const updateFilter = (field, value) => {
    setUserSearchFilter((prevFilter) => ({
      ...prevFilter,
      [field]: value,
    }));
  };

  const handleUserSearch = () => {
    setCurrentPage(1);
    getAllUsers(sortFieldName, sortDirection, 1);
  };

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

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    getAllUsers(sortFieldName, sortDirection, newPage);
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
    let params = {};
    params.sort = `${sortFieldName},${sortDirection[sortFieldName]}`;
    params.page = currentPage - 1;
    params.size = pageSize;

    const filteredUserSearchFilter = Object.keys(userSearchFilter)
      .filter((key) => {
        const value = userSearchFilter[key];
        return typeof value === "string" ? value.trim() !== "" : !!value;
      })
      .reduce((acc, key) => {
        if (typeof userSearchFilter[key] === "string")
          acc[key] = userSearchFilter[key].trim();
        else acc[key] = userSearchFilter[key];
        return acc;
      }, {});

    params = { ...params, ...filteredUserSearchFilter };
    AdminUserAPI.fetchAllUsers(params).then((data) => {
      const filteredData = data.content.filter(
        (user) => !user?.roleIds.includes(ROLE_ID_SUPERADMIN)
      );
      setUsers(filteredData);
      setTotalPages(data.totalPages);
      hideLoader();
    });
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
    let user = users.filter((user) => user.id == deleteUserId)[0];
    if (user?.state == "user.status.active") {
      setIsModalOpen(true);
    } else if (user?.state) {
      handleOk(user.state);
    }
  }, [deleteUserId]);

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
                  <th style={{ width: "20%" }}>
                    NAME
                    <span
                      id="adminUsers-sortByName"
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("name")}
                    >
                      {renderSortIcon("name")}
                    </span>
                    <div className="filter-box">
                      <input
                        id="AdminUserNameSearchFilter"
                        type="text"
                        placeholder="Search by Name"
                        className="form-control filter-input"
                        value={userSearchFilter.name}
                        onChange={(e) => updateFilter("name", e.target.value)}
                      />
                    </div>
                  </th>
                  <th style={{ width: "20%" }}>
                    EMAIL
                    <span
                      id="adminUsers-sortByEmail"
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("email")}
                    >
                      {renderSortIcon("email")}
                    </span>
                    <div className="filter-box">
                      <input
                        id="AdminUserEmailSearchFilter"
                        type="text"
                        placeholder="Search by Email"
                        className="form-control filter-input"
                        value={userSearchFilter.companyName}
                        onChange={(e) => updateFilter("email", e.target.value)}
                      />
                    </div>
                  </th>
                  <th style={{ width: "20%" }}>
                    ROLE{" "}
                    <div className="filter-box">
                      <select
                        id="AdminUserRoleSearchFilter"
                        className="form-select custom-select custom-select-sm filter-input"
                        aria-label="Default select example"
                        value={userSearchFilter.role}
                        onChange={(e) => {
                          updateFilter("role", e.target.value);
                        }}
                      >
                        {UserManagementRoleActionLabels.map((userRole) => (
                          <option value={userRole.value} key={userRole.value}>
                            {userRole.label}
                          </option>
                        ))}
                      </select>
                    </div>
                  </th>
                  <th style={{ width: "15%" }}>
                    STATUS{" "}
                    <div className="filter-box">
                      <select
                        className="form-select custom-select custom-select-sm filter-input"
                        aria-label="Default select example"
                        value={userSearchFilter.state}
                        onChange={(e) => {
                          updateFilter("state", e.target.value);
                        }}
                        id="UserManagementStateChange"
                      >
                        {UserManagementStateActionLabels.map((userState) => (
                          <option value={userState.value} key={userState.value}>
                            {userState.label}
                          </option>
                        ))}
                      </select>
                    </div>
                  </th>
                  <th style={{ width: "15%" }}>
                    ACTIONS{" "}
                    <div className="filter-box">
                      <button
                        className="search-button"
                        onClick={handleUserSearch}
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
                {users.length === 0 ? (
                  <tr>
                    <td className="text-center " colSpan="6">
                      <Empty description="No Record Found." />
                    </td>
                  </tr>
                ) : null}
                {users?.map((user, index) => (
                  <tr key={user.id}>
                    <td className="fw-bold">{user.name}</td>
                    <td className="toLowerCase-words">{user.email}</td>
                    <td>
                      {user?.roleIds.map((roleId) => (
                        <span
                          className="badge text-bg-secondary me-1 font-size-12 fw-normal"
                          key={roleId}
                        >
                          {roleId.replace("role.", "")}
                        </span>
                      ))}
                    </td>
                    <td>
                      {userInfo?.id !== user?.id && (
                        <span className="">
                          <Switch
                            defaultChecked="true"
                            checked={user?.state === "user.status.active"}
                            onChange={() => handleToggleChange(user.id)}
                            checkedChildren="ACTIVE"
                            unCheckedChildren="INACTIVE"
                            id={`adminUsers-switch-status-${index}`}
                          />
                        </span>
                      )}
                    </td>
                    <td className="action-icons-container">
                      <span
                        className="cursor-pointer font-size-12 text-blue fw-bold"
                        id={`adminUsers-editButton-${index}`}
                        onClick={() => {
                          handleEditUser();
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
          okButtonProps={{ id: "adminUsers-ok-inactiveButton" }}
          cancelButtonProps={{ id: "adminUsers-cancel-inactiveButton" }}
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
              if (item.type === "page") {
                return (
                  <PaginationItem
                    {...item}
                    id={`Adminusers-page-${item.page}`}
                    component="button"
                    onClick={() => handleChangePage(null, item.page)}
                  />
                );
              } else if (item.type === "previous") {
                return (
                  <PaginationItem
                    {...item}
                    id="AdminUsers-previous-page-button"
                    component="button"
                    onClick={() => handleChangePage(null, currentPage - 1)}
                  />
                );
              } else if (item.type === "next") {
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
