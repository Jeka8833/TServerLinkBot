package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.dataBase.DatabaseManager;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import com.Jeka8833.LinkBot.kpi.KPI;
import com.google.gson.Gson;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static final Gson GSON = new Gson();

    public static void main(String[] args) throws TelegramApiException {
        DatabaseManager.initConnect(Util.getParam(args, "-db_ip"), Util.getParam(args, "-db_user"),
                Util.getParam(args, "-db_password"));
        KPI.init();
        LinkBotDB.read();
        var botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new BotSetup(Util.getParam(args, "-name"), Util.getParam(args, "-token")));
    }
}
