// EditQuestionDTO

interface TestCaseMeta {
  createdAt: string | null;
  createdBy: string | null;
  updatedAt: string;
  updatedBy: string;
  version: number;
}

interface TestCase {
  className: null;
  description: string;
  id: string;
  manual: boolean;
  meta: TestCaseMeta;
  name: string;
  rank: number;
  specificationId: string;
  componentId: string;
  state: string;
}

interface OptionMeta {
  createdAt: string;
  createdBy: string;
  updatedAt: string;
  updatedBy: string;
  version: number;
}

interface Option {
  description: string;
  id: string;
  meta: OptionMeta;
  name: string;
  rank: number;
  state: string;
  success: boolean;
  testCaseId: string;
  changesMade: boolean;
}

interface EditedOption {
  label: string;
  metaVersion: number;
  checked: boolean;
  status: string;
  changesMade: boolean;
}

interface Question {
  id: string;
  metaVersion: number;
  description: string;
  question: string;
  rank: number;
  testcase: TestCase;
  options: Option[];
}

export { TestCaseMeta, TestCase, OptionMeta, Option, Question, EditedOption };
