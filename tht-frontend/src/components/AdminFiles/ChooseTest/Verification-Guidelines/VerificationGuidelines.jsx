import React from "react";
import guidelineImage from "../../../../styles/images/guideline.png";
const VerificationGuidelines = (props) => {
  return (
    <div style={{ cursor: "auto"}}>
      <div class="p-3">
        <h3>{props.title}</h3>
        <button
          type="button"
          className="btn-close text-reset closebtn"
          data-bs-dismiss="offcanvas"
          aria-label="Close"
        ></button>
        <img src={guidelineImage} alt="Guideline" />
        <div>
          <ul>
            {props.guidelines.map((item) => (
              <li>{item}</li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default VerificationGuidelines;
