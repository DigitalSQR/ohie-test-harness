export const userStateConstantNames = {
  "user.status.active": "ACTIVE",
  "user.status.inactive": "INACTIVE",
  "user.status.approval.pending": "PENDING",
  "user.status.verification.pending": "UNVERIFIED",
};

export const userBadgeClasses = {
  "user.status.active": "badges-green-dark bg-success",
  "user.status.approval.pending": "badges-orange bg-warning",
  "user.status.verification.pending": "badges-orange bg-warning",
  "user.status.inactive": "badges-red bg-danger",
};

export const userStatusActionLabels = [
  {
    label: "All",
    value: [
      "user.status.active",
      "user.status.inactive",
      "user.status.approval.pending",
    ],
  },
  {
    label: "Active",
    value: "user.status.active",
  },
  {
    label: "Inactive",
    value: "user.status.inactive",
  },
  {
    label: "Pending",
    value: "user.status.approval.pending",
  },
];

export const UserManagementStateActionLabels = [
  {
    label: "All",
    value: [
      "user.status.active",
      "user.status.inactive",
    ],
  },
  {
    label: "Active",
    value: "user.status.active",
  },
  {
    label: "Inactive",
    value: "user.status.inactive",
  }
];
