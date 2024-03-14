package com.argusoft.path.tht.systemconfiguration.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CommonUtil {

    private CommonUtil() {
    }

    public static Pageable getPageable(Pageable pageable) {
        Sort.Order order = pageable.getSort().getOrderFor("default");
        if (order == null) {
            return pageable;
        }
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.unsorted()
        );
    }
}
