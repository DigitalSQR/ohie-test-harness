// SpecQuestionDTO

interface MetaData {
    createdAt: string;
    createdBy: string;
    updatedAt: string;
    updatedBy: string;
    version: number;
}

interface Option {
    description: string;
    id: string;
    meta: MetaData;
    name: string;
    rank: number;
    state: string;
    success: boolean;
    testcaseId: string;
}

interface Question {
    id: string;
    metaVersion: number;
    description: string;
    question: string;
    rank: number;
    testcase: {
        className: null | string;
        description: string;
        id: string;
        manual: boolean;
        meta: MetaData;
        name: string;
        rank: number;
        specificationId: string;
        state: string;
    };
    options: Option[];
}

export { Question, Option };
