// state_constants.js

export const TestRequestStateConstants = {
    TEST_REQUEST_STATUS_DRAFT: "test.request.status.draft",
    TEST_REQUEST_STATUS_PENDING: "test.request.status.pending",
    TEST_REQUEST_STATUS_ACCEPTED: "test.request.status.accepted",
    TEST_REQUEST_STATUS_REJECTED: "test.request.status.rejected",
    TEST_REQUEST_STATUS_INPROGRESS: "test.request.status.inprogress",
    TEST_REQUEST_STATUS_FINISHED: "test.request.status.finished",
    TEST_REQUEST_STATUS_PUBLISHED: "test.request.status.published"
};

export const TestRequestStateConstantNames = {
    "test.request.status.draft": "DRAFT",
    "test.request.status.pending": "Request Pending",
    "test.request.status.accepted": "Ready For Testing",
    "test.request.status.rejected": "Request Declined",
    "test.request.status.inprogress": "Testing Started",
    "test.request.status.finished": "Testing Completed",
    "test.request.status.skipped": "Testing Skipped",
    "test.request.status.published" :" Test Request Published"
  };

export const StateBadgeClasses = {
    "test.request.status.draft": "badges-orange",
    "test.request.status.pending": "badges-orange bg-warning",
    "test.request.status.accepted": "badges-green-dark bg-success",
    "test.request.status.rejected": "badges-orange bg-danger",
    "test.request.status.inprogress": "badges-blue bg-info",
    "test.request.status.finished": "badges-green-dark bg-success",
    "test.request.status.published": "badges-purple dark-bg-success"
};

export const TestRequestActionStateLabels = [
    {label: "Request Pending",   value:"test.request.status.pending"},
    {label:"Request Declined",   value:"test.request.status.rejected"},
    {label: "Ready For Testing", value: "test.request.status.accepted"},
    {label: "Testing Started",    value: "test.request.status.inprogress"},
    {label: "Testing Completed", value: "test.request.status.finished"},
    {label: "Request Published", value: "test.request.status.published"}
];

export const TestRequestActionStateLabelsForPublisher=[
    {label: "Request Published", value: "test.request.status.published"},
    {label: "Testing Completed", value: "test.request.status.finished"},
]

export const StateClasses = {
    "test.request.status.accepted": {
        btnClass: "btn btn-sm w-75 btn-outline-success",
        iconClass: "bi bi-play-fill  font-size-16",
        btnText: "Start"
    },
    "test.request.status.inprogress": {
        btnClass: "btn btn-sm w-75 btn-outline-success",
        iconClass: "bi bi-play-fill  font-size-16",
        btnText: "Resume"
    },
    "test.request.status.finished": {
        btnClass: "btn btn-sm w-75 btn-outline-secondary",
        iconClass: "bi bi-file-text text-green-50 font-size-16",
        btnText: "Report"
    }
};

export const stateTransitionMap = {
    'test.request.status.pending': ['test.request.status.accepted', 'test.request.status.rejected'],
    'test.request.status.accepted': ['test.request.status.inprogress', 'test.request.status.finished', 'test.request.status.skipped'],
    'test.request.status.inprogress': ['test.request.status.skipped'],
    'test.request.status.skipped': ['test.request.status.accepted'],
    'test.request.status.rejected': ['test.request.status.accepted'],
    'test.request.status.published': ['test.request.status.finished'],
    'test.request.status.finished': ['test.request.status.published']
  };