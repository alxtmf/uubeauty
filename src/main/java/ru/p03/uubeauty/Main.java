/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.p03.uubeauty.model.ClsCustomer;

/**
 *
 * @author timofeevan
 */
public class Main {

    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    public static void main(String[] args) {

        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, "Бот. Начало работы");

        if (args.length > 0) {
            AppEnv.getContext(args[0].replaceFirst("-", ""));//.init(args[0]);
        } else {
            AppEnv.getContext();//.init();
        }

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        Runnable r = () -> {
            Bot bot = null;
            HttpHost proxy = AppEnv.getContext().getProxyIfAbsetnt();
            if (proxy == null) {
                bot = new Bot();
            } else {
                DefaultBotOptions instance = ApiContext.getInstance(DefaultBotOptions.class);
                RequestConfig rc = RequestConfig.custom().setProxy(proxy).build();
                instance.setRequestConfig(rc);
                bot = new Bot(instance);
            }

            List<ClsCustomer> list = AppEnv.getContext().getClsCustomerRepository().findClsCustomerEntities();
            for (ClsCustomer c : list) {
                if (c.getIdChat() != null) {
                    SendMessage sm = new SendMessage(c.getIdChat(), "пыщ пыщ, " + c.getIm());
                    try {
                        bot.sendMessage(sm);
                    } catch (TelegramApiException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            //HttpHost proxy = new HttpHost("10.3.62.2", 3128);
            try {
                botsApi.registerBot(bot);
            } catch (TelegramApiRequestException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        };

        r.run();

        while (true) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "bot!");
            try {
                Thread.sleep(80000L);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
