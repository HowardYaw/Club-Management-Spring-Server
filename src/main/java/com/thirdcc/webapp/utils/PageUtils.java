package com.thirdcc.webapp.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageUtils {

    public static <T> Page<T> toPage(List<T> originalList, Pageable pageable) {
        int startIndex = (int)pageable.getOffset();
        int endIndex = (int) ((pageable.getOffset() + pageable.getPageSize()) > originalList.size() ?
            originalList.size() :
            pageable.getOffset() + pageable.getPageSize());
        List<T> subList = originalList.subList(startIndex, endIndex);
        return new PageImpl<T>(subList, pageable, originalList.size());
    }
}
