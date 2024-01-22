import React, { useEffect, useState } from "react";
import { AdminUserAPI } from "../../../api/AdminUserAPI";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import "./admin-user.scss";
import { useNavigate } from "react-router-dom";
import { Pagination } from "@mui/material";
import { Button, Modal } from "antd";
import sortIcon from "../../../styles/images/sort-icon.png";
const AdminUsers = () => {
  const navigate = useNavigate();
  const [adminUsers, setAdminUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deleteUserId, setDeleteUserId] = useState();
  const [sortDirection, setSortDirection] = useState({
    name: "desc",
    email: "desc",
  });
  const [sortFieldName, setSortFieldName] = useState();
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);

  const handleOk = () => {
    AdminUserAPI.updateUserState(deleteUserId, "user.status.inactive")
      .then(() => {
        getAllUsers(sortFieldName, sortDirection,currentPage);
        setIsModalOpen(false);
      })
      .catch((error) => {
        console.error("Error updating user state:", error);
        setIsModalOpen(false);
      });
  };
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  useEffect(() => {
    getAllUsers(sortFieldName, sortDirection,currentPage);
  }, []);

  const getAllUsers = (sortFieldName,sortDirection,currentPage) => {
    AdminUserAPI.fetchAllUsers(
      sortFieldName,
      sortDirection[sortFieldName],
      currentPage-1,
      pageSize
    )
      .then((data) => {
        const activeUsers = data.content
          .filter((user) => user.state !== "user.status.inactive")
          .map((user) => ({
            ...user,
            roleIds: [user.roleIds[0].slice(5)],
          }));
        setAdminUsers(activeUsers);
        setTotalPages(data.totalPages)
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleEdit = (userId) => {
    navigate(`/dashboard/admin-users/update-admin-user?userId=${userId}`);
  };

  const handleDelete = (userId) => {
    setIsModalOpen(true);
    setDeleteUserId(userId);
  };
  const handleSort = (sortFieldName) => {
    setSortFieldName(sortFieldName);
    const newSortDirection = { ...sortDirection };
    newSortDirection[sortFieldName] =
      sortDirection[sortFieldName] === "asc" ? "desc" : "asc";
    setSortDirection(newSortDirection);
    getAllUsers(sortFieldName, newSortDirection, currentPage);
  };

  const handleChangePage = (event, newPage) => {
    setCurrentPage(newPage);
    getAllUsers(sortFieldName,sortDirection,newPage);
  };
  return (
    <div>
      <div id="wrapper">
        <div className="col-12 pt-3">
          <div className="row mb-2 justify-content-between">
            <div className="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12">
              <div className="custom-input custom-input-sm mb-3">
                <input
                  type="text"
                  className="form-control"
                  placeholder="Search"
                />
              </div>
            </div>
            <div className="col-auto ml-auto">
              <button
                className="btn btn-primary add-admin-user"
                onClick={() =>
                  navigate("/dashboard/admin-users/add-admin-user")
                }
              >
                ADD USER
              </button>
            </div>
          </div>

          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>
                    NAME
                    <a
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("name")}
                    >
                      <img src={sortIcon} alt="e" />
                    </a>
                  </th>
                  <th>
                    EMAIL
                    <a
                      className="ps-1"
                      href="#"
                      onClick={() => handleSort("email")}
                    >
                      <img src={sortIcon} alt="e" />
                    </a>
                  </th>
                  <th>ROLE</th>
                  <th>ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {adminUsers.map((user) => (
                  <tr key={user.id}>
                    <td>{user.name}</td>
                    <td>{user.email}</td>
                    <td>{user.roleIds[0]}</td>
                    <td className="action-icons-container">
                      <span
                        className="action-icon"
                        onClick={() => handleEdit(user.id)}
                      >
                        <EditOutlined />
                      </span>
                      <span
                        className="action-icon"
                        onClick={() => handleDelete(user.id)}
                      >
                        <DeleteOutlined />
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
        <Modal
          title="Deletion Confirmation"
          open={isModalOpen}
          onOk={handleOk}
          onCancel={handleCancel}
        >
          <p>Are you sure you want to delete?</p>
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
