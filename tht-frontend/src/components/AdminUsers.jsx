import React, { useEffect, useState } from "react";
import { AdminUserAPI } from "../api/AdminUserAPI";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import "../scss/admin-user.scss";
import { useNavigate } from "react-router-dom";
import { Button, Modal } from "antd";
const AdminUsers = () => {
  const navigate = useNavigate();
  const [adminUsers, setAdminUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deleteUserId, setDeleteUserId] = useState();
  const handleOk = () => {
    AdminUserAPI.updateUserState(deleteUserId, "user.status.inactive")
      .then(() => {
        getAllUsers();
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
  }, []);

  const getAllUsers = () => {
    AdminUserAPI.fetchAllUsers()
      .then((data) => {
        const sortedUsers = data.content.sort(
          (a, b) => new Date(b.meta.createdAt) - new Date(a.meta.createdAt)
        );
        const activeUsers = sortedUsers
          .filter((user) => user.state !== "user.status.inactive")
          .map((user) => ({
            ...user,
            roleIds: [user.roleIds[0].slice(5)], // Fix the typo and reassign to user.roleIds
          }));
        setAdminUsers(activeUsers);
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
                  <th>NAME</th>
                  <th>EMAIL</th>
                  <th>ROLE</th>
                  <th>ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {adminUsers?.map((user) => (
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
      </div>
      <Modal
        title="Deletion Confirmation"
        open={isModalOpen}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <p>Are you sure you want to delete?</p>
      </Modal>
    </div>
  );
};

export default AdminUsers;
