package com.Jeka8833.LinkBot.kpi;

import com.Jeka8833.LinkBot.Main;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;

import java.util.*;

public class KPI {

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Kiev");
    public static Calendar calendar = Calendar.getInstance(TIME_ZONE);

    public static List<Lesson> lessons;

    public static void init() {
        lessons = Arrays.asList(Main.GSON.fromJson(SavedBD.data, Lesson[].class));
    }

    private static void updateTime() {
        calendar = Calendar.getInstance(TIME_ZONE);
    }

    public static int getTimeInSecond() {
        updateTime();
        return calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
    }

    public static int getWeek() {
        updateTime();
        return (calendar.get(Calendar.WEEK_OF_YEAR) + LinkBotDB.shiftWeek) % 2;
    }

    public static int getDay() {
        updateTime();
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static List<Lesson> getDayLessons(final int week, final int day) {
        final List<Lesson> lessons = new ArrayList<>();
        for (Lesson lesson : KPI.lessons)
            if (lesson.lesson_week == week + 1 && lesson.day_number == day)
                lessons.add(lesson);
        lessons.sort(null);
        return lessons;
    }

    public static List<Lesson> getDayLessons() {
        return getDayLessons(getWeek(), getDay());
    }
}
