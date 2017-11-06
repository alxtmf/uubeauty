/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.bot.info;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.p03.uubeauty.State;
import ru.p03.uubeauty.StateHolder;
import ru.p03.uubeautyi.bot.document.spi.DocumentMarshalerAggregator;
import ru.p03.uubeauty.model.ClsDocType;
import ru.p03.uubeauty.bot.schema.Action;
import ru.p03.uubeauty.model.ClsCustomer;
import ru.p03.uubeauty.model.ClsEmployee;
import ru.p03.uubeauty.model.ClsService;
import ru.p03.uubeauty.model.RegSchedule;
import ru.p03.uubeauty.model.repository.ClassifierRepository;
import ru.p03.uubeauty.model.repository.ClsCustomerRepositoryImpl;
import ru.p03.uubeauty.model.repository.RegScheduleRepositoryImpl;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;
import ru.p03.uubeauty.util.ClsCustomerBuilder;
import ru.p03.uubeauty.util.OrderBuilder;
import ru.p03.uubeauty.util.UpdateUtil;

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
    private final ClassifierRepository classifierRepository;
    private final ClsCustomerRepositoryImpl clsCustomerRepository;
    private final RegScheduleRepositoryImpl regScheduleRepository;

    public MenuManager(ClassifierRepository classifierRepository, ClsCustomerRepositoryImpl clsCustomerRepository,
            RegScheduleRepositoryImpl regScheduleRepository,
            DocumentMarshalerAggregator marshalFactory, StateHolder stateHolder) {
        this.marshalFactory = marshalFactory;
        this.stateHolder = stateHolder;
        this.classifierRepository = classifierRepository;
        this.clsCustomerRepository = clsCustomerRepository;
        this.regScheduleRepository = regScheduleRepository;
    }

    public SendMessage processCommand(Update update) {
        SendMessage answerMessage = null;
        String text = update.getMessage().getText();
        if ("/start".equalsIgnoreCase(text)) {

            try {
                register(update);
            } catch (Exception ex) {
                Logger.getLogger(MenuManager.class.getName()).log(Level.SEVERE, null, ex);
                answerMessage = errorMessage();
            }

            answerMessage = new SendMessage();
            InlineKeyboardMarkup markup = keyboard();

            ClsCustomer customer = stateHolder.getCustomer(update);
            answerMessage.setText("Здравствуйте, "
                    + (customer != null ? customer.getIm() : "") + "!"
                    + "\n<b>Нажмите на кнопку, чтобы начать запись</b>");

            answerMessage.setReplyMarkup(markup);
        }
        return answerMessage;
    }

    private void register(Update update) throws NonexistentEntityException, Exception {
        User user = UpdateUtil.getUserFromUpdate(update);
        ClsCustomer customer = stateHolder.getCustomer(user);
        if (customer == null) {
            customer = clsCustomerRepository.findFromTelegramId(user.getId());
            if (customer == null) {
                customer = new ClsCustomerBuilder().build(update);//319361219
                clsCustomerRepository.edit(customer);
            }
            stateHolder.put(user, customer);
        }
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

                if (stateHolder.isActual(update)) {
                    answerMessage.setText("<b>Нажмите на кнопку, чтобы начать запись</b>");
                } else {

                    try {
                        register(update);
                    } catch (Exception ex) {
                        Logger.getLogger(MenuManager.class.getName()).log(Level.SEVERE, null, ex);
                        answerMessage = errorMessage();
                    }

                    ClsCustomer customer = stateHolder.getCustomer(update);
                    answerMessage.setText("Здравствуйте, "
                            + (customer != null ? customer.getIm() : "") + "!"
                            + "\n<b>Нажмите на кнопку, чтобы начать запись</b>");
                }

                answerMessage.setReplyMarkup(markup);
                stateHolder.remove(update);
            }

            if (MenuManager.ACEPT_ORDER.equals(action.getName())) {
                answerMessage = new SendMessage();

                State emplState = stateHolder.getLast(update, EmployeeManager.SELECT_EMPLOYEE);
                State servState = stateHolder.getLast(update, ServiceManager.SELECT_SERVICE);
                State dateState = stateHolder.getLast(update, ScheduleInfoManager.SELECT_DATE_ACTION);
                State hourState = stateHolder.getLast(update, ScheduleInfoManager.SELECT_HOUR_ACTION);

                LocalDate ld = LocalDate.parse(dateState.getAction().getValue());
                Integer hour = Integer.decode(hourState.getAction().getValue());
                ClsEmployee employee = classifierRepository.find(ClsEmployee.class, Long.decode(emplState.getAction().getValue()));
                ClsService service = classifierRepository.find(ClsService.class, Long.decode(servState.getAction().getValue()));
                ClsCustomer customer = stateHolder.getCustomer(update);

                RegSchedule rs = new OrderBuilder().setEmployee(employee)
                        .setCustomer(customer)
                        .setService(service)
                        .setDate(ld)
                        .setHour(ld, hour)
                        .setIsDeleted(0)
                        .build();                
                regScheduleRepository.edit(rs);

                answerMessage.setText("Спасибо, вы записаны");
                stateHolder.remove(update);
            }
        } catch (Exception ex) {
            Logger.getLogger(ScheduleInfoManager.class.getName()).log(Level.SEVERE, null, ex);
            answerMessage = errorMessage();
        }
        return answerMessage;
    }

    public String getOrderDescription(Update update) {
        State emplState = stateHolder.getLast(update, EmployeeManager.SELECT_EMPLOYEE);
        State servState = stateHolder.getLast(update, ServiceManager.SELECT_SERVICE);
        State dateState = stateHolder.getLast(update, ScheduleInfoManager.SELECT_DATE_ACTION);
        State hourState = stateHolder.getLast(update, ScheduleInfoManager.SELECT_HOUR_ACTION);

        ClsEmployee employee = classifierRepository.find(ClsEmployee.class, Long.decode(emplState.getAction().getValue()));
        ClsService service = classifierRepository.find(ClsService.class, Long.decode(servState.getAction().getValue()));

        String text = "Вы записаны на " + dateState.getAction().getValue() + " " + hourState.getAction().getValue()
                + " к " + employee.getFamiliaIO() + " на " + service.getName();

        return text;
    }

    public SendMessage errorMessage() {
        SendMessage answerMessage = new SendMessage();
        answerMessage.setText("Ой, что-то пошло не так, попробуйте еще раз или перейдите в главное меню");
        InlineKeyboardMarkup keyboardMain = keyboardMain();
        answerMessage.setReplyMarkup(keyboardMain);
        return answerMessage;
    }

    public SendMessage aceptOrderMessage() {
        SendMessage answerMessage = new SendMessage();
        answerMessage.setText("Подтвердите предварительную запись");
        InlineKeyboardMarkup keyboardMain = keyboardMain();
        answerMessage.setReplyMarkup(keyboardMain);
        return answerMessage;
    }

    public InlineKeyboardMarkup keyboard() {
        return keyboard(true, true, true);
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

    public InlineKeyboardMarkup keyboardMain() {
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

    public InlineKeyboardMarkup keyboardAceptOrder() {
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
