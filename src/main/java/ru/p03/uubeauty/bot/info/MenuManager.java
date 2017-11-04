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
import ru.p03.uubeauty.StateHolder;
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
    
    public static final String ACEPT_ORDER = "ACPO";
    public static final String APROVE_ORDER = "APRO";

    private final DocumentMarshalerAggregator marshalFactory;
    private final StateHolder stateHolder;

    public MenuManager(DocumentMarshalerAggregator marshalFactory, StateHolder stateHolder) {
        this.marshalFactory = marshalFactory;
        this.stateHolder = stateHolder;
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

            if (action == null) {
                return null;
            }

            if (MenuManager.OPEN_MAIN.equals(action.getName()) || MenuManager.APROVE_ORDER.equals(action.getName())) {
                answerMessage = new SendMessage();
                InlineKeyboardMarkup markup = keyboard();
                answerMessage.setText("<b>Нажмите на кнопку, чтобы начать запись</b>");
                answerMessage.setReplyMarkup(markup);
                stateHolder.remove(update);
            }
            
            if (MenuManager.ACEPT_ORDER.equals(action.getName())) {
                answerMessage = new SendMessage();
                answerMessage.setText("Спасибо, вы записаны");
                stateHolder.remove(update);
            }
        } catch (Exception ex) {
            Logger.getLogger(ScheduleInfoManager.class.getName()).log(Level.SEVERE, null, ex);
            answerMessage = errorMessage();
        }
        return answerMessage;
    }
    
    public SendMessage errorMessage(){
        SendMessage answerMessage = new SendMessage();
        answerMessage.setText("Ой, что-то пошло не так, попробуйте еще раз или перейдите в главное меню");
        InlineKeyboardMarkup keyboardMain = keyboardMain();
        answerMessage.setReplyMarkup(keyboardMain);
        return answerMessage;
    }
    
    public SendMessage aceptOrderMessage(){
        SendMessage answerMessage = new SendMessage();
        answerMessage.setText("Подтвердите предварительную запись");
        InlineKeyboardMarkup keyboardMain = keyboardMain();
        answerMessage.setReplyMarkup(keyboardMain);
        return answerMessage;
    }

    public InlineKeyboardMarkup keyboard() {
        return keyboard(true, true, true);
//        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//        keyboard.add(Arrays.asList(employeeListButton()));
//        keyboard.add(Arrays.asList(sheduleListButton()));
//        keyboard.add(Arrays.asList(serviceListButton()));
//        markup.setKeyboard(keyboard);
//        return markup;
    }

    public InlineKeyboardMarkup keyboard(boolean showEmployeeList, boolean showSheduleList, boolean showServiceList) {
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        if (showEmployeeList) {
            keyboard.add(Arrays.asList(employeeListButton()));
        }
        if (showSheduleList) {
            keyboard.add(Arrays.asList(sheduleListButton()));
        }
        if (showServiceList) {
            keyboard.add(Arrays.asList(serviceListButton()));
        }
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
    
    public InlineKeyboardMarkup keyboardAceptOrder(){
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(Arrays.asList(buttonAceptOrder()));
        keyboard.add(Arrays.asList(buttonAproveOrder()));
        markup.setKeyboard(keyboard);
        return markup;
    }

    public InlineKeyboardButton buttonAceptOrder() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Я подтверждаю заказ");
        Action action = new Action();
        action.setName(ACEPT_ORDER);
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
    }
    
    public InlineKeyboardButton buttonAproveOrder() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Я отказываюсь");
        Action action = new Action();
        action.setName(APROVE_ORDER);
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
    }
}
