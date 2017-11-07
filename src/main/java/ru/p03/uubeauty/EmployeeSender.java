/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.p03.uubeauty.model.ClsEmployee;

/**
 *
 * @author altmf
 */
public class EmployeeSender implements Runnable{
    private final Bot bot;
    private final String message;
    private final ClsEmployee employee;
    
    public EmployeeSender(Bot bot, ClsEmployee employee, String message){
        this.bot = bot;
        this.message = message;
        this.employee = employee;
    }

    @Override
    public void run() {
        try {
            SendMessage sm = new SendMessage(employee.getIdChat(), message);
            bot.sendMessage(sm);
        } catch (TelegramApiException ex) {
            Logger.getLogger(EmployeeSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
