import doc_logo from "../styles/images/doc.png";
import pdf_logo from "../styles/images/pdf.png";
import img_logo from "../styles/images/img.png";
import question_img_logo from "../styles/images/question-img.png";
import "../scss/functional-testing.scss";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { log_out } from "../reducers/authReducer";
export default function FunctionalTesting() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  return (
    <div id="wrapper" class="stepper-wrapper">
      <div class="col-12 non-fuctional-requirement">
        <div class="container">
          <h5 class="text-blue pb-3">Non-Functional Requirements</h5>
          <div class="row heading">
            <div class="col-md-9 col-12 p-0">
              <h2>Question</h2>
            </div>
            <div class="col-md-3 col-12 d-md-flex d-none p-0">
              <h2 class="border-left">Reference</h2>
            </div>
          </div>
          <div class="row question-box">
            <div class="col-md-9 col-12 p-0 question">
              <h2>
                CR.Q1. Are you able to find the user interface from where you
                can set up rules to find patients who might be the same? To test
                this try accessing the system and check if there is a dedicated
                feature or section for patiennt matching rule.
              </h2>
              <div class="custom_radio">
                <input type="radio" id="featured-1" name="featured" checked />
                <label for="featured-1">
                  Yes, I easily found the User Interface to set up patient
                  matching rules.
                </label>
                <input type="radio" id="featured-2" name="featured" />
                <label for="featured-2">
                  No, I couldn't locate the User Interface for configuring
                  patient matching rules.
                </label>
                <input type="radio" id="featured-3" name="featured" />
                <label for="featured-3">
                  I found it but I encountered difficulties, and it wasn't clear
                  where to find the User Interface for configuring matching
                  rules.
                </label>
              </div>
              <div class="text-end mb-3">
                <div
                  class="cst-btn-group btn-group"
                  role="group"
                  aria-label="Basic example"
                  style={{ margin: '0 15px' }}
                >
                  <button type="button" class="btn cst-btn-default">
                    <i
                      style={{ transform: 'rotate(-45.975deg)' }}
                      class="bi bi-paperclip"
                    ></i>
                    Add Attachments
                  </button>
                  <button type="button" class="btn cst-btn-default">
                    <i class="bi bi-chat-right-text"></i>Add Notes
                  </button>
                </div>
              </div>
              <div class="doc-badge-wrapper">
                <div class="doc-badge">
                  <img src={doc_logo} />
                  <p>A_125 Documents of req</p>
                </div>
                <div class="doc-badge">
                  <img src={pdf_logo} />
                  <p> A_125 Documents of req sadf </p>
                </div>
                <div class="doc-badge">
                  <img src={img_logo} />
                  <p> A_125 Documents of req </p>
                </div>
              </div>
            </div>
            <div class="col-md-3 col-12 p-0">
              <div class=" p-2 pt-5 q-img">
                <img src={question_img_logo} />
                <a>
                  <i class="bi bi-zoom-in"></i> Click to enlarge
                </a>
              </div>
            </div>
          </div>
          <div class="row question-box">
            <div class="col-md-9 col-12 p-0 question">
              <h2>
                CR.Q2. In ‘Potential Matches’ page, select the actions that you
                are able to see.
              </h2>
              <div class="custom-multiselect field-checkbox">
                <div class="field-box">
                  <input
                    type="checkbox"
                    name="checkbox-choice"
                    id="checkbox-choice-1"
                    value="choice-1"
                  />
                  <label for="checkbox-choice-1">
                    Able to see ‘link’ records action
                  </label>
                </div>
                <div class="field-box">
                  <input
                    type="checkbox"
                    name="checkbox-choice"
                    id="checkbox-choice-2"
                    value="choice-2"
                  />
                  <label for="checkbox-choice-2">
                    Able to see ’merge’ records action
                  </label>
                </div>
                <div class="field-box">
                  <input
                    type="checkbox"
                    name="checkbox-choice"
                    id="checkbox-choice-3"
                    value="choice-3"
                  />
                  <label for="checkbox-choice-3">
                    Able to see ‘mark incorrect matches’ action
                  </label>
                </div>
                <div class="field-box">
                  <input
                    type="checkbox"
                    name="checkbox-choice"
                    id="checkbox-choice-4"
                    value="choice-4"
                  />
                  <label for="checkbox-choice-4">
                    Not able to see any of the mentioned actions
                  </label>
                </div>
              </div>
              <div class="text-end mb-3">
                <div
                  class="cst-btn-group btn-group"
                  role="group"
                  aria-label="Basic example"
                  style={{margin: '0 15px'}}
                >
                  <button type="button" class="btn cst-btn-default">
                    <i
                      style={{transform: 'rotate(-45.975deg)'}}
                      class="bi bi-paperclip"
                    ></i>
                    Add Attachments
                  </button>
                  <button type="button" class="btn cst-btn-default">
                    <i class="bi bi-chat-right-text"></i>Add Notes
                  </button>
                </div>
              </div>
            </div>
            <div class="col-md-3 col-12 p-0">
              <div class=" p-2 pt-5 q-img">
                <img src={question_img_logo} />
                <a>
                  <i class="bi bi-zoom-in"></i> Click to enlarge
                </a>
              </div>
            </div>
          </div>
          <div class="d-flex justify-content-between">
            <button class="btn btn-primary btn-white py-2 font-size-14" onClick={()=>{navigate("/dashboard")}}>
              Save & Exit
            </button>
            <button class="btn btn-primary btn-blue py-2 font-size-14">
              Next
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
