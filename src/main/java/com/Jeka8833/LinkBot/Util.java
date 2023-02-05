package com.Jeka8833.LinkBot;

import com.Jeka8833.LinkBot.dataBase.LinkBotDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.DayOfWeek;
import java.time.Duration;

public class Util {
    private static final Logger LOGGER = LogManager.getLogger(Util.class);

    public static void sendMessage(final TelegramLongPollingBot bot, final String chatId, final String text) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.enableMarkdown(true);
        message.setText(text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.warn("Fail send message:", e);
        }
    }

    public static boolean isAdmin(final long userId) {
        return LinkBotDB.users.stream().anyMatch(user -> user.isAdmin && user.chatId == userId);
    }

    @NotNull
    @Contract(pure = true, value = "_->new")
    public static String toString(@NotNull Duration duration) {
        long HH = duration.toHours();
        int MM = duration.toMinutesPart();
        int SS = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

    public static User getUser(final long userId) {
        for (User user : LinkBotDB.users)
            if (user.chatId == userId)
                return user;
        return null;
    }

    public static String getParam(final String[] args, final String key) {
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equalsIgnoreCase(key))
                return args[i + 1];
        return System.getenv(key.substring(1).toUpperCase());
    }


    public static byte[] hexStringToByteArray(String s) {
        if (s == null)
            return new byte[0];
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String translateDayOfWeek(@NotNull DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "*Понедельник*";
            case TUESDAY -> "*Вторник*";
            case WEDNESDAY -> "*Среда*";
            case THURSDAY -> "*Четверг*";
            case FRIDAY -> "*Пятница*";
            case SATURDAY -> "*Суббота*";
            case SUNDAY -> "*Воскресенье*";
        };
    }
}
