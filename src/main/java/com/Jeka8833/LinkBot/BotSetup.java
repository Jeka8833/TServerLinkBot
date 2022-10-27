package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.command.*;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.Jeka8833.LinkBot.kpi.Lesson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

public class BotSetup extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(BotSetup.class);

    private final Time table;

    private final Map<String, Command> commandMap = new HashMap<>();

    final String name;
    final String token;

    public BotSetup(String name, String token) {
        this.name = name;
        this.token = token;
        commandMap.put("/setting", new Setting(this));
        commandMap.put("/now", new Now(this));
        commandMap.put("/next", new Next(this));
        commandMap.put("/list", new ListCmd(this));
        commandMap.put("/help", new Help(this));
        commandMap.put("/notification", new Notification(this));
        commandMap.put("/start", new Start(this));
        commandMap.put("/say", new Say(this));
        commandMap.put("/time", table = new Time(this));
        commandMap.put("/hide", new Hide(this));

        final TelegramLongPollingBot pollingBot = this;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (LinkBotDB.onNotification != 0) {
                    final List<Lesson> lessons = KPI.getDayLessons();
                    if (lessons.isEmpty())
                        return;

                    List<Integer> secondList = new ArrayList<>(lessons.size());
                    final int time = KPI.getTimeInSecond();

                    for (Lesson lesson : lessons) {
                        secondList.add((lesson.timeToStart() - time) / 60);
                    }

                    for (User user : LinkBotDB.users) {
                        if (user.notification == 0)
                            continue;
                        boolean send = false;
                        for (int i = 0; i < secondList.size(); i++) {
                            if (user.notification == secondList.get(i)) {
                                final Lesson lesson = lessons.get(i);
                                if (user.isSkipLesson(lesson.lesson_id))
                                    continue;
                                Util.sendMessage(pollingBot, user.chatId + "", "Скоро будет пара:" +
                                        "\nПара: " + lesson.lesson_number + "(" + lesson.time_start + " - " + lesson.time_end + ")" +
                                        "\nНазвание: " + lesson.lesson_name +
                                        "\nТип: " + lesson.lesson_type + (lesson.online ? " Онлайн" : "") + (lesson.choice ? " Факультатив" : "") +
                                        "\nПреподаватель: " + lesson.teacher_name +
                                        (lesson.online ? "\nСсылка: " + LinkBotDB.urls.getOrDefault(lesson.lesson_id, "-")
                                                : "\nАудитория: " + lesson.lesson_class));
                                send = true;
                            }
                        }
                        if (send)
                            table.send(user.chatId + "");
                    }
                }
            }
        }, 0, 60 * 1000);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            final long chatId = update.getMessage().getChatId();
            final String messageText = update.getMessage().getText();
            LOGGER.info(update.getMessage().getFrom().getUserName() + " " + update.getMessage().getFrom().getFirstName()
                    + " " + update.getMessage().getFrom().getLastName() + " " + chatId + " -> " + messageText);
            final String[] arg = messageText.split(" ", 2);
            if (commandMap.containsKey(arg[0].toLowerCase()))
                commandMap.get(arg[0].toLowerCase()).receiveListener(update, arg.length > 1 ? arg[1] : "");
            else
                Util.sendMessage(this, chatId + "", "Если была бы погрешность +-2500%, то, может," +
                        " команда была и правильной. А так, я машина и не могу понять ваши погрешности.");
        }
    }
}
