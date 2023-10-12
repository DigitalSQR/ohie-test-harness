import { useState } from "react";
import "../styles/User.css";
import { Fragment } from "react";
import { Button,notification } from "antd";
import 'antd/dist/reset.css';
export default function User() {
  const [users, setUsers] = useState([]);
  const [addUser, setAddUser] = useState(false);
  const [editUser, setEditUser] = useState(false);
  //States for user-info
  const [firstname, setFirstName] = useState("");
  const [lastname, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [contactinfo, setContactInfo] = useState("");
  const [role, setRole] = useState("");
  //on Submitiing
  const SubmitHandler = (event) => {
    event.preventDefault();
    const id = Math.trunc(Math.random() * 1000);
    const userinfo = { id, firstname, lastname, role, email, contactinfo };
    console.log(userinfo);
    setUsers([...users, userinfo]);
    console.log("the user array has ", users); //displays wrong array info as useState schedules changes for next render cycle.
    setAddUser(!addUser);
  };
  //code for popup box for delete confirmation
  function DeleteConfirmation  (id)  {
    const key = `delete{Date.now()}`;
  
  const confirmDelete = () => {
    DeleteHandler(id);
    notification.destroy(key)
  };
  

  notification.open({
    message: 'Delete Confirmation',
    key,
    duration:0,
    description:(
      <div>
        <p>Are you sure you want to delete this item</p>
        <Button type='default' size='small' onClick={()=>notification.destroy(key)}>
          Cancel
        </Button>
        <Button type='danger' size='small' onClick={confirmDelete}>
          Confirm
        </Button>
      </div>
    ),
    closeIcon:<></>,
    className:'delete-confirmation',
  })
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
        firstname: firstname,
        lastname: lastname,
        role:role,
        contactinfo: contactinfo,
        email: email,
      };

      setUsers(updatedUsers);
    }

    // window.confirm("User has been updated")
  };

  return (
    
      <div className="wrapper d-flex">
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
                                    <label htmlFor="firstName">
                                      First Name:
                                    </label>
                                    <input
                                      type="text"
                                      id="firstName"
                                      name="firstName"
                                      placeholder={user.firstname}
                                      required
                                      onChange={(e) => {
                                        setFirstName(e.target.value);
                                      }}
                                    />
                                  </div>
                                  <div className="lname-field">
                                    <label htmlFor="lastName">Last Name:</label>
                                    <input
                                      type="text"
                                      id="lastName"
                                      name="lastName"
                                      placeholder={user.lastname}
                                      required
                                      onChange={(e) => {
                                        setLastName(e.target.value);
                                      }}
                                    />
                                  </div>
                                  <div>
                                    <label htmlFor="role">Select a Role:</label>
                                    <select
                                      id="role"
                                      // value={user.role}
                                      onChange={(e) => {
                                        setRole(e.target.value);
                                        console.log(role);
                                      }}
                                    >
                                      <option value="">Select...</option>
                                      <option value="Super-Admin">Super Admin</option>
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
                                      onChange={(e) => {
                                        setEmail(e.target.value);
                                      }}
                                    />
                                  </div>
                                  <div className="contact-info">
                                    <label htmlFor="phone">Phone Number:</label>
                                    <input
                                      type="tel"
                                      id="phone"
                                      name="phone"
                                      placeholder={user.contactinfo}
                                      required
                                      onChange={(e) => {
                                        setContactInfo(e.target.value);
                                      }}
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
                }}
              >
                &times;
              </span>
              <h2>User Information</h2>
              <form id="userForm" onSubmit={SubmitHandler}>
                <div className="fname-field">
                  <label htmlFor="firstName">First Name:</label>
                  <input
                    type="text"
                    id="firstName"
                    name="firstName"
                    required
                    onChange={(e) => {
                      setFirstName(e.target.value);
                    }}
                  />
                </div>
                <div className="lname-field">
                  <label htmlFor="lastName">Last Name:</label>
                  <input
                    type="text"
                    id="lastName"
                    name="lastName"
                    required
                    onChange={(e) => {
                      setLastName(e.target.value);
                    }}
                  />
                </div>
                <div>
                  <label htmlFor="role">Select a Role:</label>
                  <select
                    id="role"
                    value={role}
                    onChange={(e) => {
                      setRole(e.target.value);
                    }}
                  >
                    <option value="">Select...</option>
                    <option value="Super Admin">Super Admin</option>
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
                    required
                    onChange={(e) => {
                      setEmail(e.target.value);
                    }}
                  />
                </div>
                <div className="contact-info">
                  <label htmlFor="phone">Phone Number:</label>
                  <input
                    type="tel"
                    id="phone"
                    name="phone"
                    required
                    onChange={(e) => {
                      setContactInfo(e.target.value);
                    }}
                  />
                </div>
                <button type="submit">Submit</button>
              </form>
            </div>
          </div>
        )}
      </div>
   
  );
}
