/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.bot.info;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.LongStream;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.p03.uubeautyi.bot.document.spi.DocumentMarshalerAggregator;
import ru.p03.uubeauty.model.ClsDocType;
import ru.p03.uubeauty.bot.schema.Action;
import ru.p03.uubeauty.AppEnv;
import ru.p03.uubeauty.State;
import ru.p03.uubeauty.StateHolder;
import ru.p03.uubeauty.model.ClsEmployee;
import ru.p03.uubeauty.model.repository.ClassifierRepository;
import ru.p03.uubeauty.model.repository.ClassifierRepositoryImpl;
import ru.p03.uubeauty.model.repository.RegScheduleRepositoryImpl;
import ru.p03.uubeauty.util.UpdateUtil;

/**
 *
 * @author timofeevan
 */
public class ScheduleInfoManager {

    private long maxHour = 20L;
    private long minHour = 8L;
    private long maxNextDays = 7L;
    private LocalDateTime now;
    private Locale ru = new Locale.Builder()
            .setLanguage("ru")
            .setRegion("RU")
            .build();

    private List<LocalDateTime> getNextDays() {
        final List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime _now = LocalDateTime.of(now.toLocalDate(), now.toLocalTime());
        LongStream.rangeClosed(0L, maxNextDays).forEach((i) -> {
            dates.add(_now.plusDays(i));
        });
        return dates;
    }
    public static final String SELECT_DATE_ACTION = "SELDTA";
    public static final String SELECT_HOUR_ACTION = "SELHR";
    private final DocumentMarshalerAggregator marshalFactory;
    private final StateHolder stateHolder;

    private final RegScheduleRepositoryImpl regScheduleRepository;
    private final ClassifierRepositoryImpl classifierRepository;

    public ScheduleInfoManager(ClassifierRepository classifierRepository, 
            RegScheduleRepositoryImpl regScheduleRepository, LocalDateTime now, 
            DocumentMarshalerAggregator marshalFactory, StateHolder stateHolder) {
        this.now = now;
        this.marshalFactory = marshalFactory;
        this.stateHolder = stateHolder;
        this.regScheduleRepository = regScheduleRepository;
        this.classifierRepository = (ClassifierRepositoryImpl)classifierRepository;
    }

    private InlineKeyboardButton dayToButton(User from, LocalDateTime ldt) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        String text = null;
        if (ldt.getYear() == now.getYear() && ldt.getDayOfYear() == now.getDayOfYear()) {
            text = "Сегодня";
        } else {
            text = ldt.getDayOfWeek().getDisplayName(TextStyle.FULL, ru)
                    + " - " + ldt.getDayOfMonth() + " "
                    + ldt.getMonth().getDisplayName(TextStyle.FULL, ru);
        }
        button.setText(text);
        Action action = new Action();
        action.setName(SELECT_DATE_ACTION);
        action.setValue(ldt.toLocalDate().toString());
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
    }

    private InlineKeyboardButton dayHoursToButton(User from, Long ldt) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        String text = ldt + ":00";
        button.setText(text);
        Action action = new Action();
        action.setName(SELECT_HOUR_ACTION);
        action.setValue(ldt.toString());
        String clbData = marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
        button.setCallbackData(clbData);
        return button;
    }

    public List<InlineKeyboardButton> buttons(User from) {
        final List<InlineKeyboardButton> buttons = new ArrayList<>();

        getNextDays().stream().forEach((ldt) -> {
            buttons.add(dayToButton(from, ldt));
        });
        return buttons;
    }

    private List<Long> getNextHours(Update update, Action action) {
        final List<Long> h = new ArrayList<>();
        LocalDate ld = LocalDate.parse(action.getValue());

        long minHourDate = minHour;
        if (ld.compareTo(now.toLocalDate()) == 0) {
            minHourDate = now.getHour() > minHourDate ? (now.getHour() + 1) : minHour;
        }
        
        State emplState = stateHolder.getLast(update, EmployeeManager.SELECT_EMPLOYEE);
        final ClsEmployee employee = classifierRepository.find(ClsEmployee.class, Long.decode(emplState.getAction().getValue()));

        if (employee == null){
            LongStream.rangeClosed(minHourDate, maxHour).forEach((i) -> {
                h.add(i);
            });
        }else{
            LongStream.rangeClosed(minHourDate, maxHour).forEach((i) -> {
                LocalDateTime ldt = LocalDateTime.of(ld, LocalTime.of((int)i, 0));
                if (regScheduleRepository.isFree(employee, ldt)){
                    h.add(i);
                }
            });
        }
        return h;
    }

    public List<InlineKeyboardButton> buttonsInDay(Update update, Action action) {
        final List<InlineKeyboardButton> buttons = new ArrayList<>();

        getNextHours(update, action).stream().forEach((ldt) -> {
            buttons.add(dayHoursToButton(UpdateUtil.getUserFromUpdate(update), ldt));
        });
        return buttons;
    }

    public InlineKeyboardMarkup keyboard(User from) {
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        buttons(from).stream().forEach((t) -> {
            keyboard.add(Arrays.asList(t));
        });
        keyboard.add(Arrays.asList(AppEnv.getContext().getMenuManager().buttonMain()));
        markup.setKeyboard(keyboard);
        return markup;

    }

    public InlineKeyboardMarkup keyboardInDay(Update update, Action action) {
        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        final List<InlineKeyboardButton> strBtn = new ArrayList<>();
        final int max = 3;
        for (InlineKeyboardButton ikb : buttonsInDay(update, action)) {
            strBtn.add(ikb);
            if (strBtn.size() == max) {
                keyboard.add(new ArrayList<>(strBtn));
                strBtn.clear();
            }
        }
        if (!strBtn.isEmpty()) {
            keyboard.add(new ArrayList<>(strBtn));
            strBtn.clear();
        }
//        buttonsInDay(update, action).stream().forEach((t) -> {
//            strBtn.add(t);
//            if (strBtn.size() == max) {
//                keyboard.add(new ArrayList<>(strBtn));
//                strBtn.clear();
//            }
//        });
        keyboard.add(Arrays.asList(AppEnv.getContext().getMenuManager().buttonMain()));
        markup.setKeyboard(keyboard);
        return markup;
    }

    public SendMessage processCallbackQuery(Update update) {
        SendMessage answerMessage = null;
        try {
            String data = update.getCallbackQuery().getData();
            User from = update.getCallbackQuery().getFrom();
            if (data == null) {
                return null;
            }

            Action action = marshalFactory.<Action>unmarshal(data, ClsDocType.ACTION);

            if (action == null) {
                return null;
            }

            if (MenuManager.OPEN_SHEDULE.equals(action.getName())) {
                answerMessage = new SendMessage();
                answerMessage.setText("<b>Выберите день записи</b>");
                InlineKeyboardMarkup markup = keyboard(from);
                answerMessage.setReplyMarkup(markup);
            }

            if (SELECT_DATE_ACTION.equals(action.getName())) {
                answerMessage = new SendMessage();
                answerMessage.setText("Вы выбрали " + action.getValue()
                        + "\n<b>Выберите час записи</b>");
                InlineKeyboardMarkup markup = keyboardInDay(update, action);
                answerMessage.setReplyMarkup(markup);
                stateHolder.put(update, new State(action, null));
            }

            if (SELECT_HOUR_ACTION.equals(action.getName())) {
                answerMessage = new SendMessage();
                InlineKeyboardMarkup markup = null;
                boolean isEmployeeSelect = stateHolder.contains(update, EmployeeManager.SELECT_EMPLOYEE);
                boolean isServiceSelect = stateHolder.contains(update, ServiceManager.SELECT_SERVICE);

                stateHolder.put(update, new State(action, null));

                if (isEmployeeSelect && isServiceSelect) {
                    markup = AppEnv.getContext().getMenuManager().keyboardAceptOrder();
                    answerMessage.setText(AppEnv.getContext().getMenuManager().getOrderDescription(update)
                            + "\n<b>Осталось подтвердить запись</b>");
                } else {
                    markup = AppEnv.getContext().getMenuManager().keyboard(update,
                            !isEmployeeSelect, false, !isServiceSelect, false, true);
                    answerMessage.setText("Вы выбрали " + action.getValue()
                            + "\n<b>Продолжаем:</b>");

                }
                answerMessage.setReplyMarkup(markup);
            }

        } catch (Exception ex) {
            Logger.getLogger(ScheduleInfoManager.class.getName()).log(Level.SEVERE, null, ex);
            answerMessage = AppEnv.getContext().getMenuManager().errorMessage();
        }
        return answerMessage;
    }
}
