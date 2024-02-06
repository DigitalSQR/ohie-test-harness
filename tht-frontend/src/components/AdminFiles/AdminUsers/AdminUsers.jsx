import React, { useEffect, useState } from "react";
import { AdminUserAPI } from "../../../api/AdminUserAPI";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import "./admin-user.scss";
import { useNavigate } from "react-router-dom";
import { Pagination } from "@mui/material";
import { Button, Modal } from "antd";
import sortIcon from "../../../styles/images/sort-icon.png";
import { useLoader } from "../../loader/LoaderContext";
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
  const [currentPageUsers, setCurrentPageUsers] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const handleOk = () => {
    showLoader();
    AdminUserAPI.updateUserState(deleteUserId, "user.status.inactive")
      .then(() => {
        getAllUsers();
        hideLoader();
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
        (user) => user?.state !== "user.status.inactive" && user?.roleIds[0]!=="role.assessee"
      );
      setTotalPages(Math.ceil(activeUsers.length / pageSize));
      
      setAdminUsers(activeUsers);
      const startIndex = (currentPage - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      const activeUsersSlice = activeUsers.slice(startIndex, endIndex);
      setCurrentPageUsers(activeUsersSlice);
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
            <div className="col-lg-4 col-md-4 col-sm-5 col-xxl-2 col-xl-3 col-12">
              <div className="custom-input custom-input-sm mb-3">
                <input
                  type="text"
                  className="form-control"
                  placeholder="Search"
                  autoComplete="off"
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
                    <td>{user.roleIds[0].replace("role.","").toUpperCase()}</td>
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
