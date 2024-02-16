interface Pageable {
  sort: { sorted: boolean; empty: boolean; unsorted: boolean };
  pageNumber: number;
  pageSize: number;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

interface MyContent {
  description: string;
  id: string;
  meta: {
    createdAt: string;
    createdBy: string;
    updatedAt: string;
    updatedBy: string;
    version: number;
  };
  name: string;
  rank: number;
  specificationIds: string[];
  state: string;
}

interface ComponentDTO {
  content: MyContent[];
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

export { Pageable, MyContent, ComponentDTO }