package com.thirdcc.webapp.utils;


import com.thirdcc.webapp.domain.YearSession;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YearSessionUtilsTest {

    @Test
    public void isValidYearSession() {
        String yearSession = "2019/2020";
        boolean isValid = YearSessionUtils.isValidYearSession(yearSession);
        assertThat(isValid).isTrue();
    }

    @Test
    public void isValidYearSession_WithInvalidYearSession() {
        String yearSession = "aaaa/aaaa";
        boolean isValid = YearSessionUtils.isValidYearSession(yearSession);
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
        LocalDate localDate = LocalDate.of(2019, lastMonthOfYearSession, 20);
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        String yearSession = YearSessionUtils.toYearSession(instant);

        assertThat(yearSession).isEqualTo("2018/2019");
    }

    @Test
    public void toYearSession_WithIsFirstMonthOfYearSession() {
        Month firstMonthOfYearSession = YearSessionUtils.getFirstMonthOfYearSession();
        LocalDate localDate = LocalDate.of(2019, firstMonthOfYearSession, 20);
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        String yearSession = YearSessionUtils.toYearSession(instant);

        assertThat(yearSession).isEqualTo("2019/2020");
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
        Instant actualInstant = YearSessionUtils.getFirstInstantOfYearSession("2019/2020");

        Month firstMonthOfYearSession = YearSessionUtils.getFirstMonthOfYearSession();
        LocalDate firstDayOfYearSession = LocalDate.of(2019, firstMonthOfYearSession, 1);
        Instant expectedInstant = firstDayOfYearSession.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        assertThat(actualInstant).isEqualTo(expectedInstant);
    }

    @Test
    public void getFirstInstantOfYearSession_WithInvalidYearSession() {
        String invalidYearSession = "aaaa/bbbb";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            YearSessionUtils.getFirstInstantOfYearSession(invalidYearSession);
        });

        String expectedMessage = "invalid yearSession of aaaa/bbbb";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
