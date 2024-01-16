import React, { useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";

const UserIdEmailConnector = ({userId}) => {
    const [user, setUser] = useState();

    useEffect(() => {
        UserAPI.getUserById(userId).then((user) => {
            setUser(user);
        });
    }, []);

    return (
        user?.email ?
        <div>
            {user.email}
        </div>
        : null 
    );
}

export default UserIdEmailConnector;