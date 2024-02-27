import React, { useEffect, useState } from "react";
import { useFormik } from "formik";
import { useLoader } from "../../loader/LoaderContext";
import { UserAPI } from "../../../api/UserAPI";
import { useNavigate } from "react-router-dom";
import { notification } from "antd";
import { userinfo_success } from "../../../reducers/UserInfoReducer";
import { useDispatch, useSelector } from "react-redux";
import { store } from "../../../store/store";
import { getHighestPriorityRole } from "../../../utils/utils";
const UserProfile = () => {
  const { showLoader, hideLoader } = useLoader();
  const [userDetails, setUserDetails] = useState();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const validate = (values) => {
    const errors = {};
    if (values.name.length === 0) {
      errors.name = "Please enter your name.";
    } else if (values.companyName.length === 0) {
      errors.companyName = "Please enter your company's name.";
    }
    return errors;
  };

  useEffect(() => {
    fetchUser();
  }, []);
  const fetchUser = () => {
    UserAPI.viewUser()
      .then((res) => {
        setUserDetails(res);
        console.log(res);
        formik.values.name = res.name;
        formik.values.companyName = res.companyName;
        formik.values.email = res.email;
        formik.values.roleIds = getHighestPriorityRole(res);
      })
      .catch((error) => {
        const errorMessage =
          error.response.data && error.response.data.length > 0
            ? error.response.data.map((element) => element.message).join(", ")
            : "Unknown error occurred";
        notification.error({
          placement: "bottomRight",
          description: errorMessage,
        });
      });
  };

  const formik = useFormik({
    initialValues: {
      name: "",
      email: "",
      roleIds: [],
      companyName: "",
    },
    validate: validate,
    onSubmit: (values) => {
      showLoader();
      console.log(values);
      const body = {
        ...userDetails,
        name: values.name,
        companyName: values.companyName,
      };
      console.log(body);
      UserAPI.UpdateExistingUser(body)
        .then((response) => {
          notification.success({
            placement: "bottomRight",
            description: `User Updated Successfully`,
          });
          dispatch(userinfo_success(body));
          navigate("/dashboard");
          window.location.reload();
        })
        .catch((response) => {
          const errorMessage =
            response.response.data && response.response.data.length > 0
              ? response.response.data
                  .map((element) => element.message)
                  .join(", ")
              : "Unknown error occurred";
          notification.error({
            placement: "bottomRight",
            description: errorMessage,
          });
        });
      hideLoader();
    },
  });
  return (
    <div>
      <div id="wrapper">
        <div className="col-lg-9 col-xl-7 col-xxl-5 col-md-11 mx-auto pt-5">
          <div className="form-bg-white">
            <span className="heading-line-up">User Profile</span>

            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="name" className="form-label">
                    <b>Name</b>
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id="name"
                    placeholder="Your Name"
                    name="name"
                    value={formik.values.name}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                  />
                  {formik.touched.name && formik.errors.name && (
                    <div className="text-danger">{formik.errors.name}</div>
                  )}
                </div>
              </div>
            </div>
            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="companyName" className="form-label">
                    <b>Company Name</b>
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id="companyName"
                    placeholder="Company Name"
                    name="companyName"
                    value={formik.values.companyName}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                  />
                  {formik.touched.companyName && formik.errors.companyName && (
                    <div className="text-danger">
                      {formik.errors.companyName}
                    </div>
                  )}
                </div>
              </div>
            </div>
            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="roleIds" className="form-label">
                    <b>Role</b>
                  </label>
                  <input
                    type="text"
                    name="roleIds"
                    id="roleIds"
                    value={formik.values.roleIds}
                    className="form-control"
                    disabled
                  ></input>
                </div>
              </div>
            </div>
            <div className="row">
              <div className="col-12">
                <div className="custom-input mb-3">
                  <label htmlFor="email" className="form-label">
                    <b>Email</b>
                  </label>
                  <input
                    type="email"
                    className="form-control"
                    id="email"
                    placeholder="Your Email"
                    name="email"
                    disabled
                    value={formik.values.email}
                  />
                </div>
              </div>
            </div>

            {/* <div className="my-4 text-end"> */}
            <div className="my-4" style={{ display: "flex", justifyContent: "flex-end" }}>

              <button
                className="btn btn-primary btn-white py-2 font-size-14 "
                style={{ marginRight: "auto" }}
                onClick={() => {
                  navigate("/reset-password");
                  formik.resetForm();
                }}
              >
                Reset Password
              </button>
              <button
                className="btn btn-primary btn-white py-2 font-size-14"
                onClick={() => {
                  navigate("/dashboard");
                  formik.resetForm();
                }}
              >
                Cancel
              </button>
              <button
                disabled={!(formik.isValid && formik.dirty)}
                onClick={formik.handleSubmit}
                className="btn btn-primary btn-blue btn-submit py-2 font-size-14"
                type="submit"
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
export default UserProfile;
