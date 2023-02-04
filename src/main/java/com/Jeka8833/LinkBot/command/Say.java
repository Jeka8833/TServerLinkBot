package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Say implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Say(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        if (!LinkBotDB.users.isEmpty()) {
            if (!Util.isAdmin(update.getMessage().getChatId())) {
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Ты не админ");
                return;
            }
        }
        final String[] args = text.split(" ", 2);
        switch (args[0].toLowerCase()) {
            case "text" -> {
                for (User user : LinkBotDB.users) {
                    Util.sendMessage(pollingBot, String.valueOf(user.chatId), args[1]);
                }
            }
            case "lesson" -> {
                final int id = Integer.parseInt(args[1]);
                Lesson lesson = null;
                for (Lesson search : KPI.lessons) {
                    if (search.lesson_id == id) {
                        lesson = search;
                        break;
                    }
                }
                if (lesson == null) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Lesson not found");
                    return;
                }
                for (User user : LinkBotDB.users) {
                    Util.sendMessage(pollingBot, user.chatId + "", "Быстро все на пару:" +
                            "\nНазвание: " + lesson.lesson_name +
                            "\nТип: " + lesson.lesson_type +
                            "\nПреподаватель: " + lesson.teacher_name +
                            "\nСсылка: " + LinkBotDB.urls.getOrDefault(lesson.lesson_id, "-"));
                }
            }
            default -> Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", """
                    Команды:
                    - text [Text]
                    - lesson [int]""");
        }
    }
}
