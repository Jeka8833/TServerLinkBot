package com.Jeka8833.LinkBot.kpi;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Lesson implements Comparable<Lesson> {

    public int lesson_id;
    public int day_number;
    public String lesson_name;
    public int lesson_number;
    public String lesson_type;
    public String teacher_name;
    public int lesson_week;
    public String time_start;
    public String time_end;
    public String lesson_class;
    public boolean online;
    public boolean choice;

    @NotNull
    @Contract(" -> new")
    public LocalTime timeToStart() {
        return LocalTime.parse(time_start, DateTimeFormatter.ofPattern("H:m:s"));
    }

    @NotNull
    @Contract(" -> new")
    public LocalTime timeToEnd() {
        return LocalTime.parse(time_end, DateTimeFormatter.ofPattern("H:m:s"));
    }

    @Override
    public int compareTo(@NotNull Lesson o) {
        return Integer.compare(lesson_number, o.lesson_number);
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lesson_id=" + lesson_id +
                ", day_number=" + day_number +
                ", lesson_name='" + lesson_name + '\'' +
                ", lesson_number=" + lesson_number +
                ", lesson_type='" + lesson_type + '\'' +
                ", teacher_name='" + teacher_name + '\'' +
                ", lesson_week=" + lesson_week +
                ", time_start='" + time_start + '\'' +
                ", time_end='" + time_end + '\'' +
                '}';
    }
}
