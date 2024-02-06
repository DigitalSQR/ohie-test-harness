import React, { useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { USER_ROLES, USER_ROLE_NAMES } from "../../../constants/role_constants";
import { userStateConstantNames } from "../../../constants/user_constants";
import { Modal, Button } from "react-bootstrap";
const UserIdConnector = ({ userId, isLink }) => {
  const [user, setUser] = useState();
  // const [show, setShow] = useState(false);
  // const handleClose = () => setShow(false);
  useEffect(() => {
    UserAPI.getUserById(userId).then((user) => {
      setUser(user);
    });
  }, []);

  // const showPopup = (event) => {
  //   event.preventDefault();
  //   setShow(true);
  // };

  return (
    <div>
      {user?.name ? <p>{user?.name}</p> : null}
      {/* <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>DETAILS</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div>
            <span>
              <strong>Name:</strong> {user?.name}
            </span>
          </div>
          <div>
            <span>
              <strong>Email:</strong> {user?.email}
            </span>
          </div>
          <div>
            <span>
              <strong>Role:</strong> {USER_ROLE_NAMES[user?.roleIds[0]]}
            </span>
          </div>
          <div>
            <span>
              <strong>State:</strong> {userStateConstantNames[user?.state]}
            </span>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="primary" onClick={handleClose}>
            CLOSE
          </Button>
        </Modal.Footer>
      </Modal> */}
    </div>
  );
};

export default UserIdConnector;
