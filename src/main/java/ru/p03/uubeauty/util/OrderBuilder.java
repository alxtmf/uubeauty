/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import ru.p03.uubeauty.model.ClsCustomer;
import ru.p03.uubeauty.model.ClsEmployee;
import ru.p03.uubeauty.model.ClsService;
import ru.p03.uubeauty.model.RegSchedule;

/**
 *
 * @author altmf
 */
public class OrderBuilder {
    RegSchedule reg = new  RegSchedule();
    
    public RegSchedule build(){
        return reg;
    }
    
    public OrderBuilder setEmployee(ClsEmployee employee){
        reg.setIdEmployee(employee.getId());
        return this;
    }
    
    public OrderBuilder setService(ClsService service){
        reg.setIdService(service.getId());
        return this;
    }
    
    public OrderBuilder setCustomer(ClsCustomer customer){
        reg.setIdCustomer(customer.getId());
        return this;
    }
    
    public OrderBuilder setDate(LocalDate date){       
        reg.setDateReg(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return this;
    }
    
    public OrderBuilder setHour(LocalDate date, Integer hour){    
        LocalDateTime ldt = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), hour, 0);
        reg.setDateTimeServiceBegin(Date.from(ldt.toInstant(ZoneOffset.UTC)));
        return this;
    }
    
    public OrderBuilder setIsDeleted(Integer isDeleted){
        reg.setIsDeleted(isDeleted);
        return this;
    }
}
