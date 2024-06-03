// role_constants.js

export const ROLE_ID_ADMIN = "role.admin";
export const ROLE_ID_TESTER = "role.tester";
export const ROLE_ID_ASSESSEE = "role.assessee";
export const ROLE_ID_SUPERADMIN = "role.superadmin";
export const  ROLE_ID_PUBLISHER = "role.publisher";

export const USER_ROLES = {
    ROLE_ID_ADMIN: "role.admin",
    ROLE_ID_TESTER: "role.tester",
    ROLE_ID_ASSESSEE: "role.assessee",
    ROLE_ID_PUBLISHER: "role.publisher"
}

export const USER_ROLE_NAMES = {
    "role.admin": "Admin",
    "role.tester": "Tester",
    "role.assessee": "Assessee",
    "role.publisher": "Publisher"
}

export const UserManagementRoleActionLabels=[
    {
        label:"All",
        value:["role.tester","role.admin","role.publisher"]
    },
    {
        label:"Tester",
        value:"role.tester"
    },
    {
        label:"Admin",
        value:"role.admin"
    },
    {
        label:"Publisher",
        value:"role.publisher"
    }
]