package com.thirdcc.webapp.utils;

public class FishLevelUtils {

    public static final String FIRST_YEAR_FISH_LEVEL = "Junior Fish";
    public static final String SECOND_YEAR_FISH_LEVEL = "Senior Fish";
    public static final String OLDER_YEAR_FISH_LEVEL = "Elder Fish";

    public static String getFishLevelBySemesterStudied(int semesterStudied) {
        if (semesterStudied < 0 ) throw new IllegalArgumentException("semester studied cannot be a negative number");
        if (semesterStudied < 2) return FIRST_YEAR_FISH_LEVEL;
        if (semesterStudied < 4) return SECOND_YEAR_FISH_LEVEL;
        return OLDER_YEAR_FISH_LEVEL;
    }
}
