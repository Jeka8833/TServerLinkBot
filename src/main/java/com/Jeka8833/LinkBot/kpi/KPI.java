package com.Jeka8833.LinkBot.kpi;

import com.Jeka8833.LinkBot.Main;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class KPI {

    public static final int MAX_CLASS_WEEKS = 2;

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Kiev");
    public static List<Lesson> lessons;

    public static void init() {
        lessons = Arrays.asList(Main.GSON.fromJson(SavedDB.data, Lesson[].class));
    }

    @NotNull
    @Contract(" -> new")
    public static LocalDate nowDate() {
        return LocalDate.now(ZONE_ID);
    }

    @NotNull
    @Contract(" -> new")
    public static LocalTime nowTime() {
        return LocalTime.now(ZONE_ID);
    }

    @Contract(pure = true)
    @Range(from = 1, to = MAX_CLASS_WEEKS)
    public static int getClassWeek(@NotNull LocalDate date) {
        return ((date.get(
                WeekFields.of(
                        Locale.getDefault()
                ).weekOfWeekBasedYear()) + LinkBotDB.shiftWeek) % MAX_CLASS_WEEKS) + 1;
    }

    @NotNull
    @Contract("_ -> new")
    public static List<Lesson> getDayLessons(@NotNull LocalDate date) {
        return getDayLessons(getClassWeek(date), date.getDayOfWeek());
    }

    @NotNull
    @Contract("_, _ -> new")
    public static List<Lesson> getDayLessons(@Range(from = 1, to = MAX_CLASS_WEEKS) int classWeek,
                                             @NotNull DayOfWeek dayOfWeek) {
        return lessons.stream()
                .filter(lesson -> lesson.lesson_week == classWeek &&
                        lesson.day_number == dayOfWeek.getValue())
                .sorted()
                .toList();
    }

    @NotNull
    @Contract(" -> new")
    public static List<Lesson> getDayLessons() {
        return getDayLessons(nowDate());
    }

    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int maxLessonNumber(@NotNull List<Lesson> lessons) {
        return lessons.stream()
                .map(lesson -> lesson.lesson_number)
                .max(Integer::compare)
                .orElse(0);
    }
}
