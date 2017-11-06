/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.util;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

/**
 *
 * @author altmf
 */
public class UpdateUtil {

    public static User getUserFromUpdate(Update update) {
        return update.getMessage() != null ? update.getMessage().getFrom()
                : update.getCallbackQuery().getFrom();
    }

    public static Chat getChatFromUpdate(Update update) {
        update.getCallbackQuery().getMessage().getChatId();
        return update.getMessage() != null ? update.getMessage().getChat()
                : update.getCallbackQuery().getMessage().getChat();
    }
}
