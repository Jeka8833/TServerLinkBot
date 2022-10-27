package com.Jeka8833.LinkBot.kpi;

import com.Jeka8833.LinkBot.Util;

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

    public int timeToStart() {
        return Util.parseTime(time_start);
    }

    public int timeToEnd() {
        return Util.parseTime(time_end);
    }

    @Override
    public int compareTo(Lesson o) {
        return lesson_number;
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
