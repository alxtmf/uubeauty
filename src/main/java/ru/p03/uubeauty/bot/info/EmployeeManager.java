/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.bot.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.p03.uubeautyi.bot.document.spi.DocumentMarshalerAggregator;
import ru.p03.uubeauty.model.ClsDocType;
import ru.p03.uubeauty.bot.schema.Action;
import ru.p03.uubeauty.bot.schema.InfoMessage;
import ru.p03.uubeauty.bot.schema.InfoMessageList;
import ru.p03.uubeauty.bot.schema.Param;
import ru.p03.uubeauty.AppEnv;
import static ru.p03.uubeauty.bot.info.MenuManager.OPEN_EMPLOYEE_LIST;
import ru.p03.uubeauty.model.ClsEmployee;
import ru.p03.uubeauty.model.repository.ClassifierRepository;

/**
 *
 * @author timofeevan
 */
public class EmployeeManager {

    public static final String VIEW_MESSAGE_INFO = "VIEW_MSG";
    public static final String MESSAGE_CODE = "MESSAGE_CODE";

    //private final InfoMessageList data;
    private final DocumentMarshalerAggregator marshalFactory;
    private final ClassifierRepository classifierRepository;

    public EmployeeManager(ClassifierRepository classifierRepository, DocumentMarshalerAggregator marshalFactory) {
        this.classifierRepository = classifierRepository;
        this.marshalFactory = marshalFactory;
    }


    public List<InlineKeyboardButton> buttons(Update update) {
        final List<InlineKeyboardButton> buttons = new ArrayList<>();

        List<ClsEmployee> inf = classifierRepository.getAll(ClsEmployee.class);
//        inf.sort((new Comparator<InfoMessage>() {
//            @Override
//            public int compare(InfoMessage o1, InfoMessage o2) {
//                return o1.getCode().compareTo(o2.getCode());
//            }
//        }));

        inf.stream().forEach((t) -> {
            buttons.add(fromClsEmployee(t, update));
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

    private InlineKeyboardButton fromClsEmployee(ClsEmployee t, Update update) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(t.getFamiliaIO());
        Action action = new Action();
        action.setName(VIEW_MESSAGE_INFO);
//        Param param = new Param();
//        param.setName(MESSAGE_CODE);
        action.setValue(t.getId().toString());
        action.setId(update.getCallbackQuery().getFrom().getId().toString());
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

            if (MenuManager.OPEN_EMPLOYEE_LIST.equals(action.getName())) {
                answerMessage = new SendMessage();
                answerMessage.setText("<b>Выберите мастера</b>");
                InlineKeyboardMarkup markup = keyboard(update);             
                answerMessage.setReplyMarkup(markup);
            }

//            if (VIEW_MESSAGE_INFO.equals(action.getName())) {
//                answerMessage = infoMessage(action);
//                InlineKeyboardMarkup markup = AppEnv.getContext().getMenuManager().keyboardMain();
//                answerMessage.setReplyMarkup(markup);
//            }

        } catch (Exception ex) {
            Logger.getLogger(EmployeeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answerMessage;
    }

//    private SendMessage infoMessage(Action action) {
//        SendMessage answerMessage = null;
//        String value = action.getValue(); //getParamList().getParam();
//
//        List<InfoMessage> im = data.getInfoMessage().stream().filter((t) -> {
//            return t.getCode().equals(value);
//        }).collect(Collectors.toList());
//
//        String text = "";
//        for (InfoMessage infoMessage : im) {
//            text += infoMessage.getMessage();
//        }
//        answerMessage = new SendMessage();
//        answerMessage.setText(text);
//
//        return answerMessage;
//    }
}
