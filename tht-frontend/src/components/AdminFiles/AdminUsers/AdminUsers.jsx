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
  const [sortFieldName, setSortFieldName] = useState();
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPageUsers, setCurrentPageUsers] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const handleOk = (userState) => {
    const changeState =
      userState == "user.status.inactive"
        ? "user.status.active"
        : "user.status.inactive";
    showLoader();
    AdminUserAPI.updateUserState(deleteUserId, changeState)
      .then(() => {
        getAllUsers();
        hideLoader();
        setIsModalOpen(false);
      })
      .catch((error) => {
        console.error("Error updating user state:", error);
        setIsModalOpen(false);
      });
    hideLoader();
  };

  const handleCancel = () => {
    setIsModalOpen(false);
  };

  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserInfo(userInfo);
    console.log(userInfo);
    dispatch(set_header("User Management"));
    getAllUsers();
  }, [currentPage, pageSize, sortFieldName, sortDirection]);

  const getAllUsers = () => {
    showLoader();
    AdminUserAPI.fetchAllUsers(
      sortFieldName,
      sortDirection[sortFieldName]
    ).then((data) => {
      console.log(data);
      hideLoader();
      const activeUsers = data.content.filter(
        (user) =>
          user?.roleIds.includes("role.admin") ||
          user?.roleIds.includes("role.tester")
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

  const handleToggleChange = (userId, userState) => {
    setDeleteUserId(userId);
    if (userState == "user.status.active") {
      setIsModalOpen(true);
    } else {
      handleOk(userState);
    }
  };

  const handleSort = (sortFieldName) => {
    setSortFieldName(sortFieldName);
    const newSortDirection = { ...sortDirection };
    newSortDirection[sortFieldName] =
      sortDirection[sortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    getAllUsers();
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
                      <img src={sortIcon} alt="e" />
                    </a>
                  </th>
                  <th className="col-3">
                    EMAIL
                    <a
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("email")}
                    >
                      <img src={sortIcon} alt="e" />
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
                        <span className="badges-green">{roleId.replace("role.", "").toUpperCase()}</span>
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
                            defaultChecked={
                              user?.state === "user.status.active"
                            }
                            onChange={() =>
                              handleToggleChange(user.id, user.state)
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
