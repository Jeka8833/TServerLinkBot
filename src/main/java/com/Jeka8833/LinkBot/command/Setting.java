package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.Util;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import com.Jeka8833.LinkBot.kpi.KPI;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Setting implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Setting(TelegramLongPollingBot pollingBot) {
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
        final String[] args = text.split(" ");
        switch (args[0].toLowerCase()) {
            case "weekshift":
                try {
                    LinkBotDB.shiftWeek = Integer.parseInt(args[1]);
                    LinkBotDB.write(LinkBotDB.Table.SETTING);
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception ex) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "onnotification":
                try {
                    LinkBotDB.onNotification = Integer.parseInt(args[1]);
                    LinkBotDB.write(LinkBotDB.Table.SETTING);
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception ex) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "addlink":
                try {
                    LinkBotDB.urls.put(Integer.parseInt(args[1]), args[2]);
                    LinkBotDB.write(LinkBotDB.Table.LINK);
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception ex) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            case "reload":
                try {
                    KPI.init();
                    LinkBotDB.read();
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Удачно");
                } catch (Exception throwables) {
                    Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", "Произошла ошибка");
                }
                break;
            default:
                Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", """
                        Команды:
                        - weekShift [int]
                        - onnotification [int]
                        - addLink [int(Key)] [String(link)]
                        - reload""");
        }
    }
}
