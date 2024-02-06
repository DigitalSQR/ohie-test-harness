export const TestcaseResultStateConstants = {
    TESTCASE_RESULT_STATUS_SKIP : "testcase.result.status.skip",
    TESTCASE_RESULT_STATUS_DRAFT : "testcase.result.status.draft",
    TESTCASE_RESULT_STATUS_PENDING : "testcase.result.status.pending",
    TESTCASE_RESULT_STATUS_INPROGRESS : "testcase.result.status.inprogress",
    TESTCASE_RESULT_STATUS_FINISHED : "testcase.result.status.finished",
};

export const StateClasses = {
    "testcase.result.status.draft": {
        cardClass: "grey",
        iconClass: "bi bi-stop-circle-fill",
        text: "PENDING"
    },
    "testcase.result.status.pending": {
        cardClass: "grey",
        iconClass: "bi bi-stop-circle-fill",
        text: "PENDING"
    },
    "testcase.result.status.skip": {
        cardClass: "orange",
        iconClass: "bi bi-arrow-right-circle-fill",
        text: "SKIP"
    },
    "testcase.result.status.inprogress": {
        cardClass: "blue",
        iconClass: "bi bi-arrow-repeat",
        text: "INPROGRESS"
    },
    "testcase.result.status.finished": {
        cardClass: "green",
        iconClass: "bi bi-check-circle-fill",
        text: "FINISHED"
    },
    "testcase.result.status.finished.pass": {
        cardClass: "green",
        iconClass: "bi bi-check-circle-fill",
        text: "PASS"
    },
    "testcase.result.status.finished.fail": {
        cardClass: "red",
        iconClass: "bi bi-x-circle-fill",
        text: "FAIL"
    }
};