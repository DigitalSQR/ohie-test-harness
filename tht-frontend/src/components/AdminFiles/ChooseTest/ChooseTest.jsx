import workflow_logo from "../../../styles/images/workflow-testing.png";
import functional_logo from "../../../styles/images/functional-testing.png";
import { useNavigate } from "react-router-dom";
import "./choose-test.scss";
export default function ChooseTest() {
  const navigate = useNavigate();
  return (
    <div id="wrapper">
      <div className="col-12 pt-3">
        <h5>Choose Testing Type</h5>
        <p className="text-gray">
          Select the type to start testing application with OpenHIE.{" "}
        </p>

        <div className="d-flex flex-wrap">
          <div
            className="testing-grid"
            onClick={() => navigate("/dashboard/functional-testing")}
          >
            <div className="icon-box">
              <img src={functional_logo} />
            </div>
            <div className="text-box">
              <h6 className="">Manual Testing</h6>
              <p className="mb-0">
                If you need more info, please check out{" "}
                <a className="text-blue" href="#">
                  Guideline.
                </a>
              </p>
              {/* <div className="progress-bar-line"> */}
                {/* <div className="progress-fill"></div> */}
                {/* <div className="progress-value">20%</div>  */}
              {/* </div> */}
            </div>
          </div>
          <div
            className="testing-grid"
            onClick={() => navigate("/dashboard/workflow-testing")}
          >
            <div className="icon-box">
              <img src={workflow_logo} />
            </div>
            <div className="text-box">
              <h6 className="">Automated Testing</h6>
              <p className="mb-0">
                If you need more info, please check out{" "}
                <a className="text-blue" href="#">
                  Guideline.
                </a>
              </p>
              {/* <div className="progress-bar-line"></div> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
