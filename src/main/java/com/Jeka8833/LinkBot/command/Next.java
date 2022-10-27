package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Next implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Next(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        final User user = Util.getUser(update.getMessage().getChatId());
        if (user == null) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "",
                    "Ты кто? Напиши '/start', а уже потом '/now'");
            return;
        }
        int day = KPI.getDay() + 1;
        int week = KPI.getWeek();
        List<Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            if (day > 6) {
                week = (week + 1) % 2;
                day = 1;
            }
            lessons = KPI.getDayLessons(week, day++).stream()
                    .filter(lesson -> !user.isSkipLesson(lesson.lesson_id)).collect(Collectors.toList());
            if (!lessons.isEmpty())
                break;
        }
        if (lessons.isEmpty()) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "",
                    "А у тебя пары вообще существуют?");
        } else {
            StringBuilder sb = new StringBuilder("Расписание на " + Util.getDayName((day - 1)) + "\n");
            int maxLessonsPerDay = lessons.stream()
                    .mapToInt(value -> value.lesson_number)
                    .min().orElse(0);

            for (int i = 1; i < maxLessonsPerDay; i++) {
                sb.append("♦️Пара: ").append(i).append("\n> Нема\n\n");
            }

            for (Lesson lesson : lessons) {
                sb.append("\uD83D\uDD39 Пара: ").append(lesson.lesson_number)
                        .append("(").append(lesson.time_start).append(" - ").append(lesson.time_end).append(")")
                        .append("\nНазвание: ").append(lesson.lesson_name)
                        .append("\nТип: ").append(lesson.lesson_type)
                        .append(lesson.online ? " Онлайн" : "")
                        .append(lesson.choice ? " Факультатив" : "")
                        .append("\nПреподаватель: ").append(lesson.teacher_name)
                        .append(lesson.online ? "\nСсылка: " +
                                LinkBotDB.urls.getOrDefault(lesson.lesson_id, "-")
                                : "\nАудитория: " + lesson.lesson_class).append("\n\n");

            }
            Util.sendMessage(pollingBot, user.chatId + "", sb.toString());
        }
    }
}