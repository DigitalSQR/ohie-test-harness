import { useEffect, useState } from "react";
import { TestResultAPI } from "../../../../api/TestResultAPI";
import passImg from "../../../../styles/images/success.svg";
import failImg from "../../../../styles/images/failure.svg";
import skipImg from "../../../../styles/images/skip.svg";
import stopImg from "../../../../styles/images/stop.svg";

export default function AutomatedResultStateRefresher({ testResultId, isDuration }) {
    const [item, setItem] = useState();

    const getResultDisplay = (item) => {
        let state = item?.state;
        let success = item?.success;
        if (state === "testcase.result.status.finished") {
            if (!!success) {
                return <img className="finished" src={passImg} alt="PASS" />;
            } else {
                return <img className="finished" src={failImg} alt="FAIL" />;
            }
        } else if (state === "testcase.result.status.pending") {
            return <img className="finished" src={stopImg} alt="PENDING" />;
        } else if (state === "testcase.result.status.skip") {
            return <img className="finished" src={skipImg} alt="SKIP" />;
        } else if (state === "testcase.result.status.inprogress") {
            return <div className="spinner-border" role="status"></div>;
        }
    };

    useEffect(() => {
        const fetchTestResultStatus = async () => {
            const response = await TestResultAPI.getTestcaseResultStatus(testResultId, { automated: true });
            setItem(response);
            if (response?.state !== "testcase.result.status.finished") {
                // Call again till finished
                setTimeout(fetchTestResultStatus, 2000);
            }
        };

        fetchTestResultStatus(); // Initial call
    }, [testResultId]);

    return (
        <>{
            !isDuration ?
            getResultDisplay(item)
            : !!item?.duration ? item?.duration + ' ms' : '-'}</>
    );
}
