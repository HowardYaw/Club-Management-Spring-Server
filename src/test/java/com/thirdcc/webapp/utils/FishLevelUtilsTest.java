package com.thirdcc.webapp.utils;

import org.junit.jupiter.api.Test;

import static com.thirdcc.webapp.utils.FishLevelUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


class FishLevelUtilsTest {

    @Test
    public void getFishLevelBySemesterStudied_WithNegativeSemesterStudied() {
        int semesterStudied = -1;
        Throwable thrown = catchThrowable(() -> {
            String actual = FishLevelUtils.getFishLevelBySemesterStudied(semesterStudied);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getFishLevelBySemesterStudied_With1SemesterStudied() {
        int semesterStudied = 1;
        String actual = FishLevelUtils.getFishLevelBySemesterStudied(semesterStudied);
        assertThat(actual).isEqualTo(FIRST_YEAR_FISH_LEVEL);
    }

    @Test
    public void getFishLevelBySemesterStudied_With2SemesterStudied() {
        int semesterStudied = 2;
        String actual = FishLevelUtils.getFishLevelBySemesterStudied(semesterStudied);
        assertThat(actual).isEqualTo(SECOND_YEAR_FISH_LEVEL);
    }

    @Test
    public void getFishLevelBySemesterStudied_With3SemesterStudied() {
        int semesterStudied = 3;
        String actual = FishLevelUtils.getFishLevelBySemesterStudied(semesterStudied);
        assertThat(actual).isEqualTo(SECOND_YEAR_FISH_LEVEL);
    }

    @Test
    public void getFishLevelBySemesterStudied_With4SemesterStudied() {
        int semesterStudied = 4;
        String actual = FishLevelUtils.getFishLevelBySemesterStudied(semesterStudied);
        assertThat(actual).isEqualTo(OLDER_YEAR_FISH_LEVEL);
    }

    @Test
    public void getFishLevelBySemesterStudied_WithMoreThan4SemesterStudied() {
        int semesterStudied = 10;
        String actual = FishLevelUtils.getFishLevelBySemesterStudied(semesterStudied);
        assertThat(actual).isEqualTo(OLDER_YEAR_FISH_LEVEL);
    }

}
