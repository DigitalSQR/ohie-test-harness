export const userStateConstantNames = {
  "user.status.active": "ACTIVE",
  "user.status.inactive": "INACTIVE",
  "user.status.approval.pending": "PENDING",
  "user.status.verification.pending": "UNVERIFIED",
};

export const userBadgeClasses = {
  "user.status.active": "badges-green-dark",
  "user.status.approval.pending": "badges-orange",
  "user.status.verification.pending": "badges-orange",
  "user.status.inactive": "badges-red",
};

export const userStatusActionLabels = [
  { 
    label: "Active", 
    value: "user.status.active" 
  },
  {
    label: "Inactive",
    value: "user.status.inactive",
  },
  {
    label: "Pending",
    value: ["user.status.approval.pending", "user.status.verification.pending"],
  },
];
