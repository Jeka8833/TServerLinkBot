package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Time implements Command {

    private static final Map<String, Timer> timers = new HashMap<>();
    private final TelegramLongPollingBot pollingBot;

    public Time(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        send(update.getMessage().getChatId() + "");
    }

    public void send(final String chatId) {
        if (timers.containsKey(chatId))
            timers.get(chatId).cancel();

        try {
            final User user = Util.getUser(Long.parseLong(chatId));
            if (user == null) {
                Util.sendMessage(pollingBot, chatId, "Ты кто? Напиши '/start', а уже потом '/time'");
                return;
            }
            List<Lesson> lessons = KPI.getDayLessons().stream()
                    .filter(lesson -> !user.isSkipLesson(lesson.lesson_id))
                    .toList();

            final SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.enableMarkdown(true);
            message.setText(messageGenerate(lessons));
            final int messageIndex = pollingBot.execute(message).getMessageId();

            final long time = System.currentTimeMillis();
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (System.currentTimeMillis() - time > 2 * 60 * 60 * 1000 || isLessonsComplete(lessons))
                            throw new NullPointerException();
                        final EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setMessageId(messageIndex);
                        editMessageText.enableMarkdown(true);
                        editMessageText.setText(messageGenerate(lessons));
                        pollingBot.execute(editMessageText);
                    } catch (TelegramApiException | NullPointerException ignored) {

                    } catch (Exception e) {
                        timer.cancel();
                        timers.remove(chatId);
                        e.printStackTrace();
                    }
                }
            }, 0, 3000);
            timers.put(chatId, timer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String messageGenerate(final List<Lesson> lessons) {
        if (lessons == null || lessons.isEmpty()) return "Сегодня пар нет";

        final StringBuilder sb = new StringBuilder();
        sb.append("Рассписание на ").append(Util.translateDayOfWeek(KPI.nowDate().getDayOfWeek())).append(":\n");
        for (Lesson lesson : lessons) {
            sb.append(lesson.lesson_number).append(") ")
                    .append(lesson.lesson_name).append(" `")
                    .append(lesson.lesson_type).append('`').append('\n');
            sb.append("-> Время: ");
            Duration durationEnd = Duration.between(KPI.nowTime(), lesson.timeToEnd());
            if (durationEnd.isNegative()) {
                sb.append("Пара уже прошла");
            } else {
                Duration durationStart = Duration.between(KPI.nowTime(), lesson.timeToStart());
                if (durationStart.isNegative())
                    sb.append("Now");
                else
                    sb.append(Util.toString(durationStart));
                sb.append(" - ").append(Util.toString(durationEnd));
            }
            sb.append('\n');
        }
        return sb.toString();
    }


    /**
     * Return true if all lessons finished
     */
    @Contract(pure = true)
    private static boolean isLessonsComplete(final @NotNull List<Lesson> lessons) {
        final LocalTime currentTime = KPI.nowTime();
        for (Lesson lesson : lessons)
            if (lesson.timeToEnd().isAfter(currentTime)) return false;
        return true;
    }
}
