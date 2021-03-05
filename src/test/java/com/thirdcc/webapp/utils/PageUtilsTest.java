package com.thirdcc.webapp.utils;


import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageUtilsTest {

    @Test
    public void toPage() {
        List<Integer> integerList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        Pageable pageable = PageRequest.of(1, 1);

        Page<Integer> page = PageUtils.toPage(integerList, pageable);

        int pageSize = page.getContent().size();
        assertThat(pageSize).isEqualTo(1);
        int firstElement = page.getContent().get(0);
        assertThat(firstElement).isEqualTo(2);
    }
}
