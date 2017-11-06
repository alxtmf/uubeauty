/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.util;

import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import ru.p03.uubeauty.model.ClsCustomer;

/**
 *
 * @author altmf
 */
public class ClsCustomerBuilder {
    ClsCustomer clsCustomer =  new ClsCustomer();
    
    public ClsCustomer build(){
        return clsCustomer;
    }
    
    public ClsCustomer build(User user){
        setIdTelegram(Long.decode(user.getId().toString()))
                .setFam(user.getLastName())
                .setIm(user.getFirstName());
        return clsCustomer;
    }
    
    public ClsCustomer build(Update update){
        User user = UpdateUtil.getUserFromUpdate(update);
        Chat chat = UpdateUtil.getChatFromUpdate(update);
        setIdTelegram(Long.decode(user.getId().toString()))
                .setIdChat(chat.getId())
                .setFam(user.getLastName())
                .setIm(user.getFirstName())
                .setIsDeleted(0);
        return clsCustomer;
    }

    
    
    public ClsCustomerBuilder setId(Long id) {
        clsCustomer.setId(id);
        return this;
    }
    
    public ClsCustomerBuilder setIdChat(Long id) {
        clsCustomer.setIdChat(id);
        return this;
    }

    public ClsCustomerBuilder setIsDeleted(Integer isDeleted) {
        clsCustomer.setIsDeleted(isDeleted);
        return this;
    }


    public ClsCustomerBuilder setFam(String fam) {
        clsCustomer.setFam(fam);
        return this;
    }


    public ClsCustomerBuilder setIm(String im) {
        clsCustomer.setIm(im);
        return this;
    }

    public ClsCustomerBuilder setOtc(String otc) {
        clsCustomer.setOtc(otc);
        return this;
    }


    public ClsCustomerBuilder setIdTelegram(Long idTelegram) {
        clsCustomer.setIdTelegram(idTelegram);
        return this;
    }
}
