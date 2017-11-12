/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.common.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author timofeevan
 */
public class FormatUtils {

    private static final int NUM_LEADING_ZERO = 6;

    public static String formatLedingZero(int num, int digits) {
        char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

        return df.format(num);
    }

    public static String getNextRegNumber(Date date, Integer incrementalNumber) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        int year = c.get(Calendar.YEAR);
        return formatLedingZero(incrementalNumber, NUM_LEADING_ZERO);
    }

    public static String formatAsDDMMYYY(Date date) {
        String str = new SimpleDateFormat("dd.MM.yyyy").format(date);
        return str;
    }
    
    public static String formatAsDDMMYYYHHmm(Date date) {
        String str = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date);
        return str;
    }
}
