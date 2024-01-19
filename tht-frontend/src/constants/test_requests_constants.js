// state_constants.js

export const TestRequestStateConstants = {
    TEST_REQUEST_STATUS_DRAFT: "test.request.status.draft",
    TEST_REQUEST_STATUS_PENDING: "test.request.status.pending",
    TEST_REQUEST_STATUS_ACCEPTED: "test.request.status.accepted",
    TEST_REQUEST_STATUS_REJECTED: "test.request.status.rejected",
    TEST_REQUEST_STATUS_INPROGRESS: "test.request.status.inprogress",
    TEST_REQUEST_STATUS_FINISHED: "test.request.status.finished"
};

export const TestRequestStateLabels = [
    {label: "Draft", value: "test.request.status.draft"},
    {label: "Pending", value: "test.request.status.pending"},
    {label: "Accepted", value: "test.request.status.accepted"},
    {label: "Rejected", value: "test.request.status.rejected"},
    {label: "Inprogress", value: "test.request.status.inprogress"},
    {label: "Finished", value: "test.request.status.finished"},
];

export const TestRequestStateConstantNames = {
    "test.request.status.draft": "Draft",
    "test.request.status.pending": "Pending",
    "test.request.status.accepted": "Accepted",
    "test.request.status.rejected": "Rejected",
    "test.request.status.inprogress": "Inprogress",
    "test.request.status.finished": "Finished",
}

export const StateBadgeClasses = {
    "test.request.status.draft": "badges-orange",
    "test.request.status.pending": "badges-orange",
    "test.request.status.accepted": "badges-green-dark",
    "test.request.status.rejected": "badges-orange",
    "test.request.status.inprogress": "badges-orange",
    "test.request.status.finished": "badges-green-dark",
};

export const TestRequestActionStateLabels = [
    {label: "Accepted", value: "test.request.status.accepted"},
    {label: "Inprogress", value: "test.request.status.inprogress"},
    {label: "Finished", value: "test.request.status.finished"},
];

export const StateClasses = {
    "test.request.status.accepted": {
        btnClass: "btn btn-green-sm",
        iconClass: "bi bi-play-fill",
        btnText: "Start"
    },
    "test.request.status.inprogress": {
        btnClass: "btn btn-orange-sm",
        iconClass: "bi bi-play-fill",
        btnText: "Resume"
    },
    "test.request.status.finished": {
        btnClass: "btn btn-blue-sm",
        iconClass: "bi bi-file-text",
        btnText: "Report"
    }
};