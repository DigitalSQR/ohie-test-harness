import React, { useEffect, useState } from "react";
import { AdminUserAPI } from "../../../api/AdminUserAPI";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import "./admin-user.scss";
import { useNavigate } from "react-router-dom";
import { Pagination } from "@mui/material";
import { Button, Modal } from "antd";
import sortIcon from "../../../styles/images/sort-icon.png";
import { useLoader } from "../../loader/LoaderContext";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";
import { store } from "../../../store/store";
import { Switch, notification } from "antd";
import { ROLE_ID_ADMIN, ROLE_ID_TESTER } from "../../../constants/role_constants";
const AdminUsers = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deleteUserId, setDeleteUserId] = useState();
  const [userInfo, setUserInfo] = useState();
  const dispatch = useDispatch();
  const [sortDirection, setSortDirection] = useState({
    name: "desc",
    email: "desc",
  });
  const [sortFieldName, setSortFieldName] = useState("name");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPageUsers, setCurrentPageUsers] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const pageSize = 10;

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

  const handleCancel = () => {
    setIsModalOpen(false);
    setDeleteUserId(null);
  };

  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserInfo(userInfo);
    dispatch(set_header("User Management"));
    getAllUsers(sortFieldName, sortDirection);
  }, []);

  const getAllUsers = (sortFieldName, sortDirection) => {
    showLoader();
    AdminUserAPI.fetchAllUsers(sortFieldName, sortDirection[sortFieldName]).then((data) => {
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

  const handleEdit = (userId) => {
    navigate(`/admin-users/update-admin-user?userId=${userId}`);
  };

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

  const handleSort = (sortFieldName) => {
    setSortFieldName(sortFieldName);
    const newSortDirection = { ...sortDirection };
    newSortDirection[sortFieldName] =
      sortDirection[sortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    getAllUsers(sortFieldName, newSortDirection);
  };
  const renderSortIcon = (fieldName) => {
    if (sortFieldName === fieldName) {
      return (
        <span
          className={`bi ${
            sortDirection[fieldName] === "asc"
              ? "bi-caret-up-fill"
              : "bi-caret-down-fill"
          }`}
        ></span>
      );
    }
    return <span className="bi-caret-down-fill"></span>;
  };

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <div>
      <div id="wrapper">
        <div className="col-12 pt-3">
          <div className="row mb-2 justify-content-between">
            <div className="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12"></div>
            <div className="col-auto ml-auto">
              <button
                onClick={() => {
                  navigate("/admin-users/add-admin-user");
                }}
                type="button"
                className="btn btn-sm btn-outline-secondary menu-like-item"
              >
                <i className="bi bi-plus"></i>
                Add Admin User
              </button>
            </div>
          </div>

          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th className="col-3">
                    NAME
                    <a
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("name")}
                    >
                      {renderSortIcon("name")}
                    </a>
                  </th>
                  <th className="col-3">
                    EMAIL
                    <a
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("email")}
                    >
                      {renderSortIcon("email")}
                    </a>
                  </th>
                  <th className="col-3">ROLE</th>
                  <th className="col-3">ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {currentPageUsers?.map((user) => (
                  <tr key={user.id}>
                    <td>{user.name}</td>
                    <td>{user.email}</td>
                    <td>
                      {user?.roleIds.map((roleId) => (
                        <span className="badges-green my-1">{roleId.replace("role.", "").toUpperCase()}</span>
                      ))}
                    </td>
                    <td className="action-icons-container">
                      <span
                        className="action-icon"
                        onClick={() => handleEdit(user.id)}
                      >
                        <EditOutlined />
                      </span>
                      {userInfo?.id !== user?.id && (
                        <span className="form-check form-switch">
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
