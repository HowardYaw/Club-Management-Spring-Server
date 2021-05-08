package com.thirdcc.webapp.utils;


import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class YearSessionUtilsTest {

    private static final String DEFAULT_VALID_YEAR_SESSION = "2019/2020";
    private static final String DEFAULT_INVALID_YEAR_SESSION = "aaaa/aaaa";

    private static final String YEAR_SESSION_BEFORE_DEFAULT = "2018/2019";

    private static final int DEFAULT_FIRST_YEAR = 2019;

    @Test
    public void isValidYearSession() {
        boolean isValid = YearSessionUtils.isValidYearSession(DEFAULT_VALID_YEAR_SESSION);
        assertThat(isValid).isTrue();
    }

    @Test
    public void isValidYearSession_WithInvalidYearSession() {
        boolean isValid = YearSessionUtils.isValidYearSession(DEFAULT_INVALID_YEAR_SESSION);
        assertThat(isValid).isFalse();
    }

    @Test
    public void getCurrentYearSession() {
        String expectedYearSession = YearSessionUtils.getCurrentYearSession();
        String actualYearSession = YearSessionUtils.toYearSession(Instant.now());

        assertThat(expectedYearSession).isEqualTo(actualYearSession);
    }

    @Test
    public void toYearSession_WithIsLastMonthOfYearSession() {
        Month firstMonthOfYearSession = YearSessionUtils.getFirstMonthOfYearSession();
        Month lastMonthOfYearSession = Month.of(firstMonthOfYearSession.getValue() - 1);
        LocalDate localDate = LocalDate.of(DEFAULT_FIRST_YEAR, lastMonthOfYearSession, 20);
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        String yearSession = YearSessionUtils.toYearSession(instant);

        assertThat(yearSession).isEqualTo(YEAR_SESSION_BEFORE_DEFAULT);
    }

    @Test
    public void toYearSession_WithIsFirstMonthOfYearSession() {
        Month firstMonthOfYearSession = YearSessionUtils.getFirstMonthOfYearSession();
        LocalDate localDate = LocalDate.of(DEFAULT_FIRST_YEAR, firstMonthOfYearSession, 20);
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        String yearSession = YearSessionUtils.toYearSession(instant);

        assertThat(yearSession).isEqualTo(DEFAULT_VALID_YEAR_SESSION);
    }

    @Test
    public void getFirstMonthOfYearSession() {
        Month firstMonthOfYearSession = YearSessionUtils.getFirstMonthOfYearSession();

        assertThat(firstMonthOfYearSession).isEqualByComparingTo(Month.SEPTEMBER);
    }

    @Test
    public void getAllMonthsOfYearSession() {
        List<Month> actualMonths = YearSessionUtils.getAllMonthsOfYearSession();
        List<Month> expectedMonths = Arrays.asList(
            Month.SEPTEMBER,
            Month.OCTOBER,
            Month.NOVEMBER,
            Month.DECEMBER,
            Month.JANUARY,
            Month.FEBRUARY,
            Month.MARCH,
            Month.APRIL,
            Month.MAY,
            Month.JUNE,
            Month.JULY,
            Month.AUGUST
        );

        assertThat(actualMonths).isEqualTo(expectedMonths);
    }

    @Test
    public void getFirstInstantOfYearSession() {
        Instant actualInstant = YearSessionUtils.getFirstInstantOfYearSession(DEFAULT_VALID_YEAR_SESSION);

        Month firstMonthOfYearSession = YearSessionUtils.getFirstMonthOfYearSession();
        LocalDate firstDayOfYearSession = LocalDate.of(DEFAULT_FIRST_YEAR, firstMonthOfYearSession, 1);
        Instant expectedInstant = firstDayOfYearSession.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        assertThat(actualInstant).isEqualTo(expectedInstant);
    }

    @Test
    public void getFirstInstantOfYearSession_WithInvalidYearSession() {
        Throwable thrown = catchThrowable(() -> {
            YearSessionUtils.getFirstInstantOfYearSession(DEFAULT_INVALID_YEAR_SESSION);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void isBefore_WithInvalidYearSession() {

        Throwable thrown1 = catchThrowable(() -> {
            YearSessionUtils.isBefore(DEFAULT_INVALID_YEAR_SESSION, DEFAULT_VALID_YEAR_SESSION);
        });
        assertThat(thrown1).isInstanceOf(IllegalArgumentException.class);

        Throwable thrown2 = catchThrowable(() -> {
            YearSessionUtils.isBefore(DEFAULT_VALID_YEAR_SESSION, DEFAULT_INVALID_YEAR_SESSION);
        });
        assertThat(thrown2).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void isBefore_WithTrue() {
        boolean actual = YearSessionUtils.isBefore(YEAR_SESSION_BEFORE_DEFAULT, DEFAULT_VALID_YEAR_SESSION);
        assertThat(actual).isTrue();
    }

    @Test
    public void isBefore_WithFalse() {
        boolean actual = YearSessionUtils.isBefore(DEFAULT_VALID_YEAR_SESSION, YEAR_SESSION_BEFORE_DEFAULT);
        assertThat(actual).isFalse();
    }

}
