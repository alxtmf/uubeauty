/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.bot.info;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.p03.common.util.FormatUtils;
import ru.p03.uubeauty.Bot;
import ru.p03.uubeauty.EmployeeSender;
import ru.p03.uubeauty.State;
import ru.p03.uubeauty.StateHolder;
import ru.p03.uubeautyi.bot.document.spi.DocumentMarshalerAggregator;
import ru.p03.uubeauty.model.ClsDocType;
import ru.p03.uubeauty.bot.schema.Action;
import ru.p03.uubeauty.model.ClsCustomer;
import ru.p03.uubeauty.model.ClsEmployee;
import ru.p03.uubeauty.model.ClsRole;
import ru.p03.uubeauty.model.ClsService;
import ru.p03.uubeauty.model.RegSchedule;
import ru.p03.uubeauty.model.repository.ClassifierRepository;
import ru.p03.uubeauty.model.repository.ClassifierRepositoryImpl;
import ru.p03.uubeauty.model.repository.ClsCustomerRepositoryImpl;
import ru.p03.uubeauty.model.repository.RegScheduleRepositoryImpl;
import ru.p03.uubeauty.model.repository.RegUseRoleRepository;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;
import ru.p03.uubeauty.util.ActionBuilder;
import ru.p03.uubeauty.util.ClsCustomerBuilder;
import ru.p03.uubeauty.util.InlineKeyboardButtonBuilder;
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
    public static final String OPEN_MY_ORDER_LIST = "MORL";
    public static final String OPEN_MY_APPOINT_ORDER_LIST = "AORL";
    public static final String OPEN_ALL_APPOINT_ORDER_LIST = "AARL";

    public static final String ACEPT_ORDER = "ACPO";
    public static final String APROVE_ORDER = "APRO";

    private final DocumentMarshalerAggregator marshalFactory;
    private final StateHolder stateHolder;
    private final ClassifierRepositoryImpl classifierRepository;
    private final ClsCustomerRepositoryImpl clsCustomerRepository;
    private final RegScheduleRepositoryImpl regScheduleRepository;
    private final RegUseRoleRepository regUseRoleRepository;

    private Bot bot;

    private final ExecutorService sendEmployeeMessageExecutorService = Executors.newCachedThreadPool();

    ;

    public MenuManager(ClassifierRepository classifierRepository, ClsCustomerRepositoryImpl clsCustomerRepository,
            RegScheduleRepositoryImpl regScheduleRepository, RegUseRoleRepository regUseRoleRepository,
            DocumentMarshalerAggregator marshalFactory, StateHolder stateHolder) {
        this.marshalFactory = marshalFactory;
        this.stateHolder = stateHolder;
        this.classifierRepository = (ClassifierRepositoryImpl) classifierRepository;
        this.clsCustomerRepository = clsCustomerRepository;
        this.regScheduleRepository = regScheduleRepository;
        this.regUseRoleRepository = regUseRoleRepository;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
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
            InlineKeyboardMarkup markup = keyboard(update);

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
            Action action = new ActionBuilder(marshalFactory).buld(update);
//            String data = update.getCallbackQuery().getData();
//            if (data == null) {
//                return null;
//            }
//
//            Action action = marshalFactory.<Action>unmarshal(data, ClsDocType.ACTION);

            if (action == null) {
                return null;
            }

            if (MenuManager.OPEN_MAIN.equals(action.getName()) || MenuManager.APROVE_ORDER.equals(action.getName())) {

                answerMessage = new SendMessage();
                InlineKeyboardMarkup markup = keyboard(update);

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
                        .setDateTimeServiceBegin(ld, hour, 0)
                        .setDateTimeServiceEnd(ld, hour, 59)
                        .setIsDeleted(0)
                        .build();
                regScheduleRepository.edit(rs);

                answerMessage.setText("Спасибо, вы записаны");
                answerMessage.setReplyMarkup(keyboardMain());
                stateHolder.remove(update);

                String text = "К вам записан " + dateState.getAction().getValue() + " " + hourState.getAction().getValue()
                        + " к " + customer.getFamiliaIO() + " на " + service.getName();
                EmployeeSender es = new EmployeeSender(bot, employee, text);
                sendEmployeeMessageExecutorService.submit(es);
            }

            if (MenuManager.OPEN_MY_ORDER_LIST.equals(action.getName())) {
                answerMessage = new SendMessage();
                ClsCustomer customer = stateHolder.getCustomer(update);
                List<RegSchedule> list = regScheduleRepository.findFromCustomer(customer);

                String text = "";
                for (RegSchedule rs : list) {
                    ClsEmployee employee = classifierRepository.find(ClsEmployee.class, rs.getIdEmployee());
                    ClsService service = classifierRepository.find(ClsService.class, rs.getIdService());
                    String s = "Вы записаны на " + FormatUtils.formatAsDDMMYYYHHmm(rs.getDateTimeServiceBegin())
                            + " к " + employee.getFamiliaIO() + " на " + service.getName() + "\n";
                    text += s;
                }

                if (text.isEmpty()) {
                    text = "У вас нет заказов";
                }

                answerMessage.setText(text);

            }

            if (MenuManager.OPEN_MY_APPOINT_ORDER_LIST.equals(action.getName())) {
                answerMessage = new SendMessage();
                String text = "";
                ClsEmployee finded = classifierRepository.findEmployee(UpdateUtil.getUserFromUpdate(update).getId());
                if (finded != null) {
                    List<RegSchedule> list = regScheduleRepository.findFromEmployee(finded, new Date());
                    if (!list.isEmpty()) {
                        text = "Ко мне записаны:\n";
                        int i = 1;
                        for (RegSchedule rs : list) {
                            ClsCustomer customer = classifierRepository.find(ClsCustomer.class, rs.getIdCustomer());
                            ClsService service = classifierRepository.find(ClsService.class, rs.getIdService());
                            String s = i + ". на " + FormatUtils.formatAsDDMMYYYHHmm(rs.getDateTimeServiceBegin())
                                    + " к " + customer.getFamiliaIO() + " на " + service.getName() + "\n";
                            text += s;
                            i++;
                        }
                    }
                }
                if (text.isEmpty()) {
                    text = "К вам никто не записан";

                }
                answerMessage.setText(text);
            }

            if (MenuManager.OPEN_ALL_APPOINT_ORDER_LIST.equals(action.getName())) {
                answerMessage = new SendMessage();
                String text = "";
                List<ClsEmployee> finded = classifierRepository.find(ClsEmployee.class, false);
                for (ClsEmployee employee : finded) {
                    List<RegSchedule> list = regScheduleRepository.findFromEmployee(employee, new Date());
                    if (!list.isEmpty()) {
                        text = "К " + employee.getFamiliaIO() + " записаны:\n";
                        int i = 1;
                        for (RegSchedule rs : list) {
                            ClsCustomer customer = classifierRepository.find(ClsCustomer.class, rs.getIdCustomer());
                            ClsService service = classifierRepository.find(ClsService.class, rs.getIdService());
                            String s = i + ". " + customer.getFamiliaIO() + " на " + FormatUtils.formatAsDDMMYYYHHmm(rs.getDateTimeServiceBegin())
                                    + " на " + service.getName() + "\n";
                            text += s;
                            i++;
                        }
                    }
                }
                if (text.isEmpty()) {
                    text = "Записей к мастерам нет";

                }
                answerMessage.setText(text);
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

    public InlineKeyboardMarkup keyboard(Update update) {
        return keyboard(update, true, true, true);
    }

    public InlineKeyboardMarkup keyboard(Update update,
            boolean showEmployeeList, boolean showSheduleList, boolean showServiceList) {
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

        keyboard.add(Arrays.asList(myOrderListButton()));
        ClsEmployee finded = classifierRepository.findEmployee(UpdateUtil.getUserFromUpdate(update).getId());
        if (finded != null && regUseRoleRepository.hasRole(finded, ClsRole.EMPLOYEE)) {
            keyboard.add(Arrays.asList(appointOrderListButton()));
        }
        if (finded != null && regUseRoleRepository.hasRole(finded, ClsRole.ADMIN)) {
            keyboard.add(Arrays.asList(appointAllOrderListButton()));
        }

        markup.setKeyboard(keyboard);
        return markup;
    }

    private InlineKeyboardButton myOrderListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Мои заказы");
        String clbData = new ActionBuilder(marshalFactory)
                .setName(OPEN_MY_ORDER_LIST)
                .asString();
        button.setCallbackData(clbData);
        return button;
    }

    private InlineKeyboardButton appointOrderListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Ко мне записаны");
        String clbData = new ActionBuilder(marshalFactory)
                .setName(OPEN_MY_APPOINT_ORDER_LIST)
                .asString();
        button.setCallbackData(clbData);
        return button;
    }

    private InlineKeyboardButton appointAllOrderListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Записаны к мастерам");
        String clbData = new ActionBuilder(marshalFactory)
                .setName(OPEN_ALL_APPOINT_ORDER_LIST)
                .asString();
        button.setCallbackData(clbData);
        return button;
    }

    private InlineKeyboardButton employeeListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Выбрать мастера");
        String clbData = new ActionBuilder(marshalFactory)
                .setName(OPEN_EMPLOYEE_LIST)
                .asString();
        button.setCallbackData(clbData);
        return button;
    }

    private InlineKeyboardButton sheduleListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Выбрать время записи");
        String clbData = new ActionBuilder(marshalFactory)
                .setName(OPEN_SHEDULE)
                .asString();
        button.setCallbackData(clbData);
        return button;
    }

    private InlineKeyboardButton serviceListButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Выбрать услугу");
        String clbData = new ActionBuilder(marshalFactory)
                .setName(OPEN_SERVICE_LIST)
                .asString();
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
        InlineKeyboardButton button = new InlineKeyboardButtonBuilder()
                .setText("Главное меню")
                .setCallbackData(new ActionBuilder(marshalFactory)
                        .setName(OPEN_MAIN)
                        .asString())
                .build();
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
