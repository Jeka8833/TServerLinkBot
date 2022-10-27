package com.Jeka8833.LinkBot.command;

import com.Jeka8833.LinkBot.User;
import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Start implements Command {
    private final TelegramLongPollingBot pollingBot;

    public Start(TelegramLongPollingBot pollingBot) {
        this.pollingBot = pollingBot;
    }

    @Override
    public void receiveListener(Update update, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow one = new KeyboardRow();
        one.add("/next");
        one.add("/time");
        one.add("/list");
        KeyboardRow two = new KeyboardRow();
        two.add("/notification");
        two.add("/hide");
        two.add("/help");
        KeyboardRow three = new KeyboardRow();
        three.add("/now");
        keyboard.add(one);
        keyboard.add(two);
        keyboard.add(three);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setChatId(update.getMessage().getChatId() + "");
        sendMessage.setText("Жизнь боль");
        try {
            pollingBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        for (User user : LinkBotDB.users) {
            if (user.chatId.equals(update.getMessage().getChatId()))
                return;
        }
        LinkBotDB.users.add(new User(update.getMessage().getChatId(), (byte) 0, false, ""));
        LinkBotDB.write(LinkBotDB.Table.NOTIFICATION);
    }
}
