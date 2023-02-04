package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        int dayShift = 0;
        try {
            dayShift = Integer.parseInt(text);
        } catch (Exception ignore) {
        }

        LocalDate date = KPI.nowDate();
        List<Lesson> lessons = new ArrayList<>();

        if (dayShift == 0) {
            for (int day = 1; day <= DayOfWeek.values().length * KPI.MAX_CLASS_WEEKS + 1; day++) {
                date = KPI.nowDate().plusDays(day);
                lessons = KPI.getDayLessons(date).stream()
                        .filter(lesson -> !user.isSkipLesson(lesson.lesson_id))
                        .toList();
                if (!lessons.isEmpty()) break;
            }
        } else {
            date = KPI.nowDate().plusDays(dayShift);
            lessons = KPI.getDayLessons(date).stream()
                    .filter(lesson -> !user.isSkipLesson(lesson.lesson_id))
                    .toList();
        }

        if (lessons.isEmpty()) {
            if(dayShift == 0) {
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "",
                        "А у тебя пары вообще существуют?");
            } else {
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "",
                        "Нет пар в этот день");
            }
        } else {
            StringBuilder sb = new StringBuilder("Расписание на " +
                    Util.translateDayOfWeek(date.getDayOfWeek()) + "\n");

            int maxLessonNumber = KPI.maxLessonNumber(lessons);
            for (int i = 1; i <= maxLessonNumber; i++) {
                boolean found = false;
                for (Lesson lesson : lessons) {
                    if (lesson.lesson_number != i) continue;
                    sb.append("\uD83D\uDD39Пара: ").append(lesson.lesson_number).append("(").append(lesson.time_start).append(" - ")
                            .append(lesson.time_end).append(")")
                            .append("\nНазвание: ").append(lesson.lesson_name)
                            .append("\nТип: ").append(lesson.lesson_type)
                            .append(lesson.online ? " Онлайн" : "")
                            .append(lesson.choice ? " Факультатив" : "")
                            .append("\nПреподаватель: ").append(lesson.teacher_name)
                            .append(lesson.online ? "\nСсылка: " + LinkBotDB.urls.getOrDefault(lesson.lesson_id, "-")
                                    : "\nАудитория: " + lesson.lesson_class).append("\n\n");

                    found = true;
                }
                if (!found) {
                    sb.append("♦️Пара: ").append(i).append("\n> Нема\n\n");
                }
            }
            Util.sendMessage(pollingBot, user.chatId + "", sb.toString());
        }
    }
}