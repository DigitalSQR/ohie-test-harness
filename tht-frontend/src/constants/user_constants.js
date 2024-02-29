export const userStateConstantNames = {
  "user.status.active": "ACTIVE",
  "user.status.inactive": "INACTIVE",
  "user.status.rejected": "REJECTED",
  "user.status.approval.pending": "PENDING",
  "user.status.verification.pending": "NOT VERIFIED",
};

export const userBadgeClasses = {
  "user.status.active": "badges-green-dark",
  "user.status.rejected": "badges-orange",
  "user.status.approval.pending": "badges-orange",
  "user.status.verification.pending": "badges-orange",
  "user.status.inactive": "badges-red",
};

export const userStatusActionLabels = [
  { label: "Accepted", value: "user.status.active" },
  {
    label: "Rejected",
    value: "user.status.rejected",
  },
  {
    label: "Pending",
    value: ["user.status.approval.pending", "user.status.verification.pending"],
  },
];
