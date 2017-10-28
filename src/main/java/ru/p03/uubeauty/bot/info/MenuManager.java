/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.bot.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.p03.uubeautyi.bot.document.spi.DocumentMarshalerAggregator;
import ru.p03.uubeauty.model.ClsDocType;
import ru.p03.uubeauty.bot.schema.Action;

/**
 *
 * @author timofeevan
 */
public class MenuManager {
    
    public static final String OPEN_SHEDULE = "OPEN_SHEDULE";
    public static final String OPEN_MESSAGE_INFO = "OPEN_MESSAGE_INFO";
    public static final String OPEN_MAIN = "OPEN_MAIN";
    public static final String OPEN_FIND_SNILS = "OPEN_FS";
    
    public static final String OPEN_EMPLOYEE_LIST = "OPEN_EMPLOYEE_LIST";
    public static final String OPEN_SERVICE_LIST = "OPEN_SERVICE_LIST";
    
    private final DocumentMarshalerAggregator marshalFactory;
    
    public MenuManager(DocumentMarshalerAggregator marshalFactory) {
        this.marshalFactory = marshalFactory;
    }

    public SendMessage processCommand(Update update) {
        SendMessage answerMessage = null;
        String text = update.getMessage().getText();
        if ("/start".equalsIgnoreCase(text)) {
            answerMessage = new SendMessage();
            InlineKeyboardMarkup markup = keyboard();
            answerMessage.setReplyMarkup(markup);
        }
        return answerMessage;
    }    
    
    public SendMessage processCallbackQuery(Update update) {
        SendMessage answerMessage = null;
        try {
            String data = update.getCallbackQuery().getData();
            if (data == null) {
                return null;
            }
            
            Action action = marshalFactory.<Action>unmarshal(data, ClsDocType.ACTION);
            
            if (action == null){
                return null;
            }
            
            if (MenuManager.OPEN_MAIN.equals(action.getName())){
                answerMessage = new SendMessage();
                InlineKeyboardMarkup markup = keyboard();
                answerMessage.setReplyMarkup(markup);
            }
        } catch (Exception ex) {
            Logger.getLogger(ScheduleInfoManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answerMessage;
    }
    
    public InlineKeyboardMarkup keyboard(){
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(Arrays.asList(employeeListButton()));
        keyboard.add(Arrays.asList(sheduleListButton()));
        keyboard.add(Arrays.asList(serviceListButton()));
        markup.setKeyboard(keyboard);
        return markup;
    }
    
    private InlineKeyboardButton employeeListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Выбрать мастера");
        Action action = new Action();
        action.setName(OPEN_EMPLOYEE_LIST);
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
    }
    
    private InlineKeyboardButton sheduleListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Выбрать время записи");
        Action action = new Action();
        action.setName(OPEN_SHEDULE);
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
    }
    
    private InlineKeyboardButton serviceListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Выбрать услугу");
        Action action = new Action();
        action.setName(OPEN_SERVICE_LIST);
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
    }
    
    public InlineKeyboardMarkup keyboardMain(){
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(Arrays.asList(buttonMain()));
        markup.setKeyboard(keyboard);
        return markup;
    }
    
    public InlineKeyboardButton buttonMain() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Главное меню");
        Action action = new Action();
        action.setName(OPEN_MAIN);
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
    }
}
