package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.Util;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Help implements Command {

    private final TelegramLongPollingBot pollingBot;

    public Help(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        Util.sendMessage(pollingBot, update.getMessage().getChatId() + "", """
                Помощь нуждающимся:
                /now - Показывает пару которая скоро будет или уже идёт
                /next - Показывает пару которая будет следующая
                /list - Полное рассписание на 2 недели
                /start - Регистрация и клавиатурный помощник
                /notification - Включение или отключение рассылки уведомлений о скором начале пары
                /hide - Скрытие пар которых у тебя нет за расписанием(Чтобы не приходили уведомление о начале)
                /time - Расписание времени пар(Автоматически обновляется раз в 3 секунды)""");
    }
}
