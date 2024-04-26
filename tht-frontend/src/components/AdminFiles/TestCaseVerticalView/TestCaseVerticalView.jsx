import TestcaseVertical from "../TestcaseVertical/TestcaseVertical";

export default function TestCaseVerticalView(props) {
  const {
    currentTestcaseIndex,
    currentTestcase,
    currentSpecification,
    selectTestcase,
    selectNextTestcase,
    refreshCurrentTestcase,
    selectNextSpecification,
    isLastSpecification
  } = props;
  console.log(currentSpecification);
  return (
    <div className="vertical-layout">
      <div className="question-header">
        <div className="row">
        <div className="col-12">
          <h2>Question</h2>
        </div>
      </div>
      </div>
      <TestcaseVertical
        currentTestcaseIndex={currentTestcaseIndex}
        currentTestcase={currentTestcase}
        currentSpecification={currentSpecification}
        selectTestcase={selectTestcase}
        selectNextTestcase={selectNextTestcase}
        refreshCurrentTestcase={refreshCurrentTestcase}
        selectNextSpecification={selectNextSpecification}
        isLastSpecification={isLastSpecification}
      ></TestcaseVertical>
    </div>
  );
}
