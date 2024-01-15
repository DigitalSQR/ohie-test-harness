// state_constants.js

export const TestRequestStateConstants = {
    TEST_REQUEST_STATUS_DRAFT: "component.status.draft",
    TEST_REQUEST_STATUS_PENDING: "component.status.pending",
    TEST_REQUEST_STATUS_ACCEPTED: "component.status.accepted",
    TEST_REQUEST_STATUS_REJECTED: "component.status.rejected",
    TEST_REQUEST_STATUS_INPROGRESS: "component.status.inprogress",
    TEST_REQUEST_STATUS_FINISHED: "component.status.finished"
};

export const TestRequestStateLabels = [
    {label: "Draft", value: "component.status.draft"},
    {label: "Pending", value: "component.status.pending"},
    {label: "Accepted", value: "component.status.accepted"},
    {label: "Rejected", value: "component.status.rejected"},
    {label: "Inprogress", value: "component.status.inprogress"},
    {label: "Finished", value: "component.status.finished"},
];

export const TestRequestStateConstantNames = {
    "component.status.draft": "Draft",
    "component.status.pending": "Pending",
    "component.status.accepted": "Accepted",
    "component.status.rejected": "Rejected",
    "component.status.inprogress": "Inprogress",
    "component.status.finished": "Finished",
}

export const StateBadgeClasses = {
    "component.status.draft": "badges-orange",
    "component.status.pending": "badges-orange",
    "component.status.accepted": "badges-green-dark",
    "component.status.rejected": "badges-orange",
    "component.status.inprogress": "badges-orange",
    "component.status.finished": "badges-green-dark",
};