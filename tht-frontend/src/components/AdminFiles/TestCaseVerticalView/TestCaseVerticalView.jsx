import TestcaseVertical from "../TestcaseVertical/TestcaseVertical";

export default function TestCaseVerticalView(props) {
  const {
    currentTestcaseIndex,
    currentTestcase,
    currentSpecification,
    selectTestcase,
    selectNextTestcase,
    refreshCurrentTestcase,
    isLastQuestion,
    selectNextSpecification
  } = props;
  console.log(currentSpecification);
  return (
    <div className="vertical-layout">
      <div className="row question-header">
        <div className="col-md-9 col-12 p-0">
          <h2>Question</h2>
        </div>

        <div className="col-md-3 col-12 d-md-flex d-none p-0">
          <h2 className="border-left">Reference</h2>
        </div>
      </div>
      <TestcaseVertical
        currentTestcaseIndex={currentTestcaseIndex}
        currentTestcase={currentTestcase}
        currentSpecification={currentSpecification}
        selectTestcase={selectTestcase}
        selectNextTestcase={selectNextTestcase}
        refreshCurrentTestcase={refreshCurrentTestcase}
        isLastQuestion={isLastQuestion}
        selectNextSpecification={selectNextSpecification}

      ></TestcaseVertical>
    </div>
  );
}
