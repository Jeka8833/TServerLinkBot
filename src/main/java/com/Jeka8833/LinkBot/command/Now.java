package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class Now implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Now(TelegramLongPollingBot pollingBot) {
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
        List<Lesson> lessons = KPI.getDayLessons().stream()
                .filter(lesson -> !user.isSkipLesson(lesson.lesson_id))
                .toList();

        if (lessons.isEmpty()) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "",
                    "Сегодня пар не будет, если хочешь узнать пару на следующий день, напиши /next");
        } else {
            StringBuilder sb = new StringBuilder("Расписание на " + Util.getDayName(KPI.getDay()) + "\n");
            int maxLessonsPerDay = lessons.stream()
                    .mapToInt(value -> value.lesson_number)
                    .min().orElse(0);

            for (int i = 1; i < maxLessonsPerDay; i++) {
                sb.append("♦️Пара: ").append(i).append("\n> Нема\n\n");
            }

            for (Lesson lesson : lessons) {
                if (lesson.timeToStart() > KPI.getTimeInSecond())
                    sb.append("\uD83D\uDD39");
                else if (lesson.timeToEnd() < KPI.getTimeInSecond())
                    sb.append("♦️");
                else
                    sb.append("\uD83D\uDD38");
                sb.append("Пара: ").append(lesson.lesson_number).append("(").append(lesson.time_start).append(" - ")
                        .append(lesson.time_end).append(")")
                        .append("\nНазвание: ").append(lesson.lesson_name)
                        .append("\nТип: ").append(lesson.lesson_type)
                        .append(lesson.online ? " Онлайн" : "")
                        .append(lesson.choice ? " Факультатив" : "")
                        .append("\nПреподаватель: ").append(lesson.teacher_name)
                        .append(lesson.online ? "\nСсылка: " + LinkBotDB.urls.getOrDefault(lesson.lesson_id, "-")
                                : "\nАудитория: " + lesson.lesson_class).append("\n\n");

            }
            Util.sendMessage(pollingBot, user.chatId + "", sb.toString());
        }
    }
}
