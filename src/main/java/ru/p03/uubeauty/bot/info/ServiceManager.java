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
import ru.p03.uubeauty.AppEnv;
import ru.p03.uubeauty.State;
import ru.p03.uubeauty.StateHolder;
import ru.p03.uubeauty.model.ClsService;
import ru.p03.uubeauty.model.repository.ClassifierRepository;

/**
 *
 * @author timofeevan
 */
public class ServiceManager {

    public static final String SELECT_SERVICE = "SESE";
    //public static final String MESSAGE_CODE = "MESSAGE_CODE";

    private final ClassifierRepository classifierRepository;
    private final DocumentMarshalerAggregator marshalFactory;
    private final StateHolder stateHolder;

    public ServiceManager(ClassifierRepository classifierRepository,
            DocumentMarshalerAggregator marshalFactory, StateHolder stateHolder) {
        this.classifierRepository = classifierRepository;
        this.marshalFactory = marshalFactory;
        this.stateHolder = stateHolder;
    }

    public List<InlineKeyboardButton> buttons(Update update) {
        final List<InlineKeyboardButton> buttons = new ArrayList<>();

        List<ClsService> inf = classifierRepository.getAll(ClsService.class);
//        inf.sort((new Comparator<InfoMessage>() {
//            @Override
//            public int compare(InfoMessage o1, InfoMessage o2) {
//                return o1.getCode().compareTo(o2.getCode());
//            }
//        }));

        inf.stream().forEach((t) -> {
            buttons.add(fromClsService(t, update));
        });
        return buttons;
    }

    public InlineKeyboardMarkup keyboard(Update update) {
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        buttons(update).stream().forEach((t) -> {
            keyboard.add(Arrays.asList(t));
        });
        keyboard.add(Arrays.asList(AppEnv.getContext().getMenuManager().buttonMain()));
        markup.setKeyboard(keyboard);
        return markup;
    }

    private InlineKeyboardButton fromClsService(ClsService t, Update update) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(t.getName());
        Action action = new Action();
        action.setName(SELECT_SERVICE);
//        Param param = new Param();
//        param.setName(MESSAGE_CODE);
        action.setValue(t.getId().toString());
        //action.setId(update.getCallbackQuery().getFrom().getId().toString());
        //action.getParamList().getParam().add(param);
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
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

            if (MenuManager.OPEN_SERVICE_LIST.equals(action.getName())) {
                answerMessage = new SendMessage();
                answerMessage.setText("<b>Выберите услугу:</b>");
                InlineKeyboardMarkup markup = keyboard(update);
                answerMessage.setReplyMarkup(markup);
            }

            if (SELECT_SERVICE.equals(action.getName())) {
                answerMessage = new SendMessage();
                InlineKeyboardMarkup markup = null;
                boolean isEmployeeSelect = stateHolder.contains(update, EmployeeManager.SELECT_EMPLOYEE);
                boolean isSheduleSelect = stateHolder.contains(update, ScheduleInfoManager.SELECT_DATE_ACTION,
                        ScheduleInfoManager.SELECT_HOUR_ACTION);
                
                stateHolder.put(update, new State(action, null));
                
                if (isEmployeeSelect && isSheduleSelect) {
                    markup = AppEnv.getContext().getMenuManager().keyboardAceptOrder();
                    answerMessage.setText(AppEnv.getContext().getMenuManager().getOrderDescription(update)
                            + "\n<b>Осталось подтвердить запись</b>");
                } else {
                    markup = AppEnv.getContext().getMenuManager().keyboard(
                            !isEmployeeSelect, !isSheduleSelect, false);
                    
                    ClsService service = classifierRepository.find(ClsService.class, Long.decode(action.getValue()));
                    
                    answerMessage.setText("Вы выбрали " + service.getName() 
                            + "\n<b>Продолжаем:</b>"); 
                }
                answerMessage.setReplyMarkup(markup);
            }

        } catch (Exception ex) {
            Logger.getLogger(ServiceManager.class.getName()).log(Level.SEVERE, null, ex);
            answerMessage = AppEnv.getContext().getMenuManager().errorMessage();
        }
        return answerMessage;
    }
}
