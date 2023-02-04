package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.DayOfWeek;
import java.util.List;

public class Hide implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Hide(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        final User user = Util.getUser(update.getMessage().getChatId());
        if (user == null) {
            Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Ты кто? Напиши '/start', а уже потом '/hide'");
            return;
        }
        final String[] args = text.toLowerCase().split(" ");
        switch (args[0]) {
            case "add" -> {
                try {
                    if (user.addSkip(Integer.parseInt(args[1])))
                        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                    else
                        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Предмет не найден или уже есть");
                } catch (Exception exception) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Некорректный номер предмета");
                }
            }
            case "remove" -> {
                try {
                    if (user.removeSkip(Integer.parseInt(args[1])))
                        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                    else
                        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Предмета нет в списке пропусков");
                } catch (Exception exception) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Некорректный номер предмета");
                }
            }
            case "reset" -> {
                user.resetSkip();
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
            }
            case "list" -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Можно добавить в список пропусков:\n");
                for (int week = 1; week <= KPI.MAX_CLASS_WEEKS; week++) {
                    sb.append("_Неделя ").append(week).append("_\n");

                    for (DayOfWeek day : DayOfWeek.values()) {
                        List<Lesson> dayLesson = KPI.getDayLessons(week, day).stream()
                                .filter(lesson -> lesson.choice && !user.isSkipLesson(lesson.lesson_id))
                                .toList();
                        if (dayLesson.isEmpty()) continue;

                        sb.append(Util.translateDayOfWeek(day)).append('\n');

                        for (Lesson lesson : dayLesson) {
                            sb.append(lesson.lesson_id).append(" -> ").append(lesson.lesson_number).append(") ")
                                    .append(lesson.lesson_name).append(" `").append(lesson.lesson_type).append('`')
                                    .append('\n');
                        }
                        sb.append('\n');
                    }
                }
                sb.append("\n");
                sb.append("В списоке пропусков:\n");
                for (int week = 1; week <= KPI.MAX_CLASS_WEEKS; week++) {
                    sb.append("_Неделя ").append(week).append("_\n");

                    for (DayOfWeek day : DayOfWeek.values()) {
                        List<Lesson> dayLesson = KPI.getDayLessons(week, day).stream()
                                .filter(lesson -> user.isSkipLesson(lesson.lesson_id))
                                .toList();
                        if (dayLesson.isEmpty()) continue;

                        sb.append(Util.translateDayOfWeek(day)).append('\n');

                        for (Lesson lesson : dayLesson) {
                            sb.append(lesson.lesson_id).append(" -> ").append(lesson.lesson_number).append(") ")
                                    .append(lesson.lesson_name).append(" `").append(lesson.lesson_type).append('`')
                                    .append('\n');
                        }
                        sb.append('\n');
                    }
                }
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", sb.toString());
            }
            default -> Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", """
                    Комманды:
                    /hide list - показываеть список всех пар
                    /hide add - добавить в список пропусков
                    /hide remove - удалить из списка пропусков
                    /hide reset - сброс списка""");
        }
    }
}
