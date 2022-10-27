package com.Jeka8833.LinkBot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {

    void receiveListener(final Update update, final String text);
}
