interface MetaData {
    createdAt: string;
    createdBy: string;
    updatedAt: string;
    updatedBy: string;
    version: number;
}

interface Specification {
    componentId: string;
    description: string;
    functional: boolean;
    id: string;
    meta: MetaData;
    name: string;
    rank: number;
    required: boolean;
    state: string;
    testcaseIds: string[];
}

interface Pageable {
    sort: { sorted: boolean; empty: boolean; unsorted: boolean };
    pageNumber: number;
    pageSize: number;
    offset: number;
    paged: boolean;
}

interface SpecificationDTO {
    content: Specification[];
    pageable: Pageable;
    last: boolean;
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    numberOfElements: number;
    first: boolean;
    empty: boolean;
}

export { MetaData, Specification, Pageable, SpecificationDTO };
