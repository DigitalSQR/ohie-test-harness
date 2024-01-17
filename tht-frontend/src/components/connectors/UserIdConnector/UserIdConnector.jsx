import React, { useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { USER_ROLES, USER_ROLE_NAMES } from "../../../constants/role_constants";
import { userStateConstantNames } from "../../../constants/user_constants";

const UserIdConnector = ({userId, isLink}) => {
    const [user, setUser] = useState();

    useEffect(() => {
        UserAPI.getUserById(userId).then((user) => {
            setUser(user);
        });
    }, []);

    const showPopup = () => {
        var userDetails = 
        `Name: ${user.name}\nEmail: ${user.email}\nRole: ${USER_ROLE_NAMES[user.roleIds[0]]}\nState: ${userStateConstantNames[user.state]}`;
        alert(userDetails);
    }

    return (
        user?.name ?
        <a onClick={() => showPopup()} href="javascript:void(0)">
            {user.name}
        </a>
        : null 
    );
}

export default UserIdConnector;