import workflow_logo from "../styles/images/workflow-testing.png";
import functional_logo from "../styles/images/functional-testing.png";
import "../scss/functional-testing.scss";
import { useNavigate } from "react-router-dom";
export default function ChooseTest() {
  const navigate = useNavigate();
  return (
    <div id="wrapper">
      <div class="col-12 pt-3">
        <h5>Choose Testing Type</h5>
        <p class="text-gray">
          Select the type to start testing application with OpenHIE.{" "}
        </p>

        <div class="d-flex flex-wrap">
          <div
            class="testing-grid"
            onClick={() => navigate("/dashboard/functional-testing")}
          >
            <div class="icon-box">
              <img src={functional_logo} />
            </div>
            <div class="text-box">
              <h6 class="">Functional Testing</h6>
              <p class="mb-0">
                If you need more info, please check out{" "}
                <a class="text-blue" href="#">
                  Guideline.
                </a>
              </p>
              <div class="progress-bar-line">
                <div class="progress-fill"></div>
                <div class="progress-value">20%</div>
              </div>
            </div>
          </div>
          <div
            class="testing-grid"
            onClick={() => navigate("/dashboard/workflow-testing")}
          >
            <div class="icon-box">
              <img src={workflow_logo} />
            </div>
            <div class="text-box">
              <h6 class="">Workflow Testing</h6>
              <p class="mb-0">
                If you need more info, please check out{" "}
                <a class="text-blue" href="#">
                  Guideline.
                </a>
              </p>
              <div class="progress-bar-line"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
