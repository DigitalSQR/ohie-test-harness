import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../scss/_registrationApplication.scss";
import { notification } from "antd";
import { AdminUserAPI } from "../api/AdminUserAPI";
import { useLocation } from "react-router-dom";
import { useLoader } from "../components/loader/LoaderContext";
const UpdateAdminUser = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const userId = new URLSearchParams(location.search).get("userId");
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    roleIds: "",
    meta: "",
  });
  const { showLoader, hideLoader } = useLoader();
  const [state, setState ] = useState();
  const handleChange = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id]: value });
  };
  const handleSubmit = async () => {
    showLoader();
    const body = {
      ...formData,
      roleIds: ["role." + formData.roleIds],
      id: userId,
      state: state,
    };
    console.log(body);
    AdminUserAPI.updateUserDetails(body).then(
      (response) => {
        hideLoader();
        notification.success({
          placement: "bottomRight",
          description: `User Updated Successfully`,
        });
        navigate("/dashboard/admin-users");
      },
      (response) => {
        hideLoader();
        if (response.response.data && response.response.data.length > 0) {
          response.response.data.forEach((element, index) => {
            notification.error({
              placement: "bottomRight",
              description: `${element.message}`,
            });
          });
        } else {
          notification.error({
            placement: "bottomRight",
            description: `${response.response.data[0].message}`,
          });
        }
      }
    );
  };
  useEffect(() => {
    if (userId) {
      AdminUserAPI.fetchUserDetails(userId)
        .then((userData) => {
          setState(userData.state);
          setFormData({
            name: userData.name,
            email: userData.email,
            password: userData.password,
            roleIds: userData.roleIds[0]?.substring(5),
            meta: userData.meta,
          });
        })
        .catch((error) => {
          console.error("Error fetching user details:", error);
        });
    }
  }, [userId]);

  return (
    <div>
      <div id="wrapper">
        <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div className="form-bg-white">
            <span className="heading-line-up">Update User Details</span>
            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="name" className="form-label">
                    Name
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id="name"
                    placeholder="Your Name"
                    value={formData.name}
                    onChange={handleChange}
                  />
                </div>
              </div>
            </div>

            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="email" className="form-label">
                    Email
                  </label>
                  <input
                    type="email"
                    className="form-control"
                    id="email"
                    placeholder="Your Email"
                    value={formData.email}
                    onChange={handleChange}
                  />
                </div>
              </div>
            </div>

            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="password" className="form-label">
                    Password
                  </label>
                  <input
                    type="password"
                    className="form-control"
                    id="password"
                    placeholder="Your Password"
                    value={formData.password}
                    onChange={handleChange}
                  />
                </div>
              </div>
            </div>

            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="role" className="form-label">
                    Role
                  </label>
                  <select
                    className="form-select"
                    id="roleIds"
                    value={formData.roleIds}
                    onChange={handleChange}
                  >
                    <option value="" disabled>
                      Select Role
                    </option>
                    <option value="tester">Tester</option>
                    <option value="admin">Admin</option>
                  </select>
                </div>
              </div>
            </div>

            <div className="my-4 text-end">
              <button
                className="btn btn-primary btn-white py-2 font-size-14"
                onClick={() => {
                  navigate("/dashboard/admin-users");
                }}
              >
                Cancel
              </button>
              <button
                className="btn btn-primary btn-blue btn-submit py-2 font-size-14"
                onClick={handleSubmit}
              >
                Update
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UpdateAdminUser;
