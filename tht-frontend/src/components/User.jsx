import { useState } from "react";
// import "../styles/User.css";
import { Fragment } from "react";
import { Button, notification } from "antd";
import "antd/dist/reset.css";
import { useFormik } from "formik";
export default function User() {
  const [users, setUsers] = useState([]);
  const [addUser, setAddUser] = useState(false);
  const [editUser, setEditUser] = useState(false);

  //formik updates
  const formik = useFormik({
    initialValues: {
      //these should be same as the name attributes of the input fields
      firstname: "",
      lastname: "",
      role: "",
      email: "",
      contactinfo: "",
      id: "",
    },
    validateOnBlur: true,
    onSubmit: (values) => {
      const id = Math.trunc(Math.random() * 1000);
      const userinfo = { ...values, id };
      console.log(userinfo);
      setUsers([...users, userinfo]);
      console.log("the users array has ", users);
      setAddUser(!addUser);
      formik.resetForm();
    },
    validate: (values) => {
      //validate.firstname, validate.lastname and etc.
      //must return an object
      //keys of returned object must match with those of the values field.
      //the value of the keys of the object must be a string indicating what the error is.
      const errors = {};
      if (!values.firstname) {
        errors.firstname = "First Name is required";
      }

      if (!values.lastname) {
        errors.lastname = "Last Name is required";
      }

      if (!values.contactinfo) {
        errors.contactinfo = "Contact Number is required";
      }

      if (!values.email) {
        errors.email = "Email is required";
      } else if (
        !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(values.email)
      ) {
        errors.email = "Invalid format";
      }
      if (!values.role) {
        errors.role = "Role is a required field";
      }
      return errors;
    },
  });

  // console.log("the errors are ", formik.errors);
  //formik code updates

  //code for popup box for delete confirmation
  function DeleteConfirmation(id) {
    const key = `delete{Date.now()}`;

    const confirmDelete = () => {
      DeleteHandler(id);
      notification.destroy(key);
    };

    notification.open({
      message: "Delete Confirmation",
      key,
      placement: "top",
      duration: 0,
      description: (
        <div>
          <p>Are you sure you want to delete this item</p>
          <Button
            type="default"
            size="small"
            onClick={() => notification.destroy(key)}
          >
            Cancel
          </Button>
          {"    "}
          <Button type="primary" size="small" onClick={confirmDelete}>
            Confirm
          </Button>
        </div>
      ),
      closeIcon: <></>,
      className: "delete-confirmation",
    });
  }
  //on Confirming delete user notification
  const DeleteHandler = (user_id) => {
    const updated_users = users.filter((user) => user.id !== user_id);
    notification.warning({
      placement: "top",
      description: "User has been deleted.",
      duration: 2,
    });
    setUsers(updated_users);
  };
  //on Updating User
  const UpdateHandler = (user_id) => {
    setEditUser(!editUser);

    const updatedUsers = [...users];

    const index = updatedUsers.findIndex((user) => user.id === user_id);

    if (index !== -1) {
      updatedUsers[index] = {
        ...updatedUsers[index],
        firstname: formik.values.firstname,
        lastname: formik.values.lastname,
        role: formik.values.role,
        contactinfo: formik.values.contactinfo,
        email: formik.values.email,
      };

      setUsers(updatedUsers);
    }

    // window.confirm("User has been updated")
  };
  
  return (
    <div className="d-flex" id="wrapper">
      <div className="d-inline-flex flex-grow-1 main-container bg-transparent">
        <div className="data-wrapper w-100">
          <div className="heading-title d-flex align-items-center gap-2 font-title">
            Available Users
          </div>
          <div>
            {/* DISPLAYING USERS */}
            <Fragment>
              <table className="table">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>ID</th>
                    <th>Role</th>
                    <th>Email</th>
                    <th>Contact Info</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {users.map((user) => {
                    return (
                      <tr key={user.id}>
                        <td>
                          {user.firstname} {user.lastname}
                        </td>
                        <td>{user.id}</td>
                        <td>{user.role}</td>
                        <td>{user.email}</td>
                        <td>{user.contactinfo}</td>
                        <td>
                          <button
                            className="btn btn-danger btn-sm"
                            onClick={() => {
                              DeleteConfirmation(user.id);
                            }}
                          >
                            Delete
                          </button>{" "}
                          <button
                            className="btn btn-primary btn-sm"
                            onClick={() => {
                              setEditUser(!editUser);
                            }}
                          >
                            Edit User
                          </button>
                        </td>
                          {/* below form is for updating user info */}
                        {editUser && (
                          <div id="infoPopup" className="popup">
                            <div className="popup-content">
                              <span
                                className="close"
                                id="closePopupButton"
                                onClick={() => {
                                  setEditUser(!editUser);
                                }}
                              >
                                &times;
                              </span>
                              <h2>Edit Information</h2>
                              <form
                                id="userForm"
                                onSubmit={() => UpdateHandler(user.id)}
                              >
                                <div className="fname-field">
                                  <label htmlFor="firstName">First Name:</label>
                                  <input
                                    type="text"
                                    id="firstName"
                                    name="firstname"
                                    placeholder={user.firstname}
                                    required
                                    onChange={formik.handleChange}
                                  />
                                </div>
                                <div className="lname-field">
                                  <label htmlFor="lastName">Last Name:</label>
                                  <input
                                    type="text"
                                    id="lastName"
                                    name="lastname"
                                    placeholder={user.lastname}
                                    required
                                    onChange={formik.handleChange}
                                  />
                                </div>
                                <div>
                                  <label htmlFor="role">Select a Role:</label>
                                  <select
                                    id="role"
                                    name="role"
                                    // value={user.role}
                                    onChange={formik.handleChange}
                                  >
                                    <option value="">Select...</option>
                                    <option value="Super-Admin">
                                      Super Admin
                                    </option>
                                    <option value="Admin">Admin</option>
                                    <option value="Tester">Tester</option>
                                    <option value="Testee">Testee</option>
                                  </select>
                                </div>
                                <div className="email-field">
                                  <label htmlFor="email">Email:</label>
                                  <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    placeholder={user.email}
                                    required
                                    onChange={formik.handleChange}
                                  />
                                </div>
                                <div className="contact-info">
                                  <label htmlFor="phone">Phone Number:</label>
                                  <input
                                    type="tel"
                                    id="phone"
                                    name="contactinfo"
                                    placeholder={user.contactinfo}
                                    required
                                    onChange={formik.handleChange}
                                  />
                                </div>
                                <button type="submit">Update</button>
                              </form>
                            </div>
                          </div>
                        )}
                      </tr>
                    );
                  })}
                </tbody>
              </table>

              {/* popup for editing user information */}
            </Fragment>
          </div>
        </div>

        <div className="addUserButton">
          <button
            className="addUserButton"
            onClick={() => {
              setAddUser(!addUser);
              console.log(addUser);
            }}
          >
            <h6>Add User</h6>
          </button>
        </div>
      </div>
      {/* popup for adding user */}
      {addUser && (
        <div id="infoPopup" className="popup">
          <div className="popup-content">
            <span
              className="close"
              id="closePopupButton"
              onClick={() => {
                setAddUser(!addUser);
                formik.resetForm();
              }}
            >
              &times;
            </span>
            <h4 style={{ textAlign: "center" }}>User Information</h4>
            <form
              id="userForm"
              noValidate={true}
              onSubmit={formik.handleSubmit}
            >
              <div className="fname-field d-flex">
                <label
                  htmlFor="firstName"
                  style={{ fontSize: "18px", paddingTop: "6px" }}
                >
                  Firstname:
                </label>
                <input
                  style={{ marginLeft: "7px" }}
                  type="text"
                  id="firstName"
                  name="firstname"
                  required
                  onChange={formik.handleChange}
                />
              </div>
              {formik.errors.firstname && <div>{formik.errors.firstname}</div>}

              <div
                className="lname-field d-flex "
                style={{ paddingTop: "20px" }}
              >
                <label
                  htmlFor="lastName"
                  style={{ fontSize: "18px", paddingTop: "6px" }}
                >
                  Lastname:
                </label>
                <input
                  style={{ marginLeft: "10px" }}
                  type="text"
                  id="lastName"
                  name="lastname"
                  required
                  onChange={formik.handleChange}
                />
              </div>
              {formik.errors.lastname && <div>{formik.errors.lastname}</div>}

              <div className="d-flex" style={{ paddingTop: "20px" }}>
                <label
                  htmlFor="role"
                  style={{ fontSize: "18px", paddingTop: "6px" }}
                >
                  Role:
                </label>
                <select
                  style={{ marginLeft: "55px" }}
                  id="role"
                  name="role"
                  value={formik.values.role}
                  onChange={formik.handleChange}
                >
                  <option value="">Select...</option>
                  <option value="Super Admin">Super Admin</option>
                  <option value="Admin">Admin</option>
                  <option value="Tester">Tester</option>
                  <option value="Testee">Testee</option>
                </select>
              </div>
              {formik.errors.role && <div>{formik.errors.role}</div>}
              <div
                className="email-field d-flex"
                style={{ paddingTop: "20px" }}
              >
                <label
                  htmlFor="email"
                  style={{ fontSize: "18px", paddingTop: "6px" }}
                >
                  Email:
                </label>
                <input
                  style={{ marginLeft: "46px" }}
                  type="email"
                  id="email"
                  name="email"
                  required
                  onChange={formik.handleChange}
                />
              </div>
              {formik.errors.email && (
                <div style={{ paddingRight: "40px" }}>
                  {formik.errors.email}
                </div>
              )}

              <div
                className="contact-info d-flex"
                style={{ paddingTop: "20px" }}
              >
                <label
                  htmlFor="phone"
                  style={{ fontSize: "18px", paddingTop: "6px" }}
                >
                  Contact:
                </label>
                <input
                  style={{ marginLeft: "26px" }}
                  type="tel"
                  id="phone"
                  name="contactinfo"
                  required
                  onChange={formik.handleChange}
                />
              </div>
              {formik.errors.contactinfo && (
                <div style={{ paddingLeft: "30px" }}>
                  {formik.errors.contactinfo}
                </div>
              )}

              <button type="submit">Submit</button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}