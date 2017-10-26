/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty.model;

import java.util.Arrays;
import java.util.List;
import ru.p03.classifier.model.Classifier;

/**
 *
 * @author timofeevan
 */
public class ClsDocType extends Classifier {
    
    public static final String SCHEDULE_INFO = "DataList";
    public static final String ACTION = "Action";
    public static final String EMPLOYEE_LIST = "EMPLOYEE_LIST";
    public static final String SERVICE_INFO = "SERVICE_INFO";
    
    private String code;
    private Long id;

    public  ClsDocType(){
        
    }
    
    public  ClsDocType(String code, Long id){
        this.code = code;
        this.id = id;
    }
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    public static List<ClsDocType> types(){
        return Arrays.asList(new ClsDocType(ClsCustomer.class.getSimpleName(), 1L),
                new ClsDocType(ClsEmployee.class.getSimpleName(), 2L),
                new ClsDocType(ClsUser.class.getSimpleName(), 3L),
                new ClsDocType(RegCustomerContact.class.getSimpleName(), 4L),
                new ClsDocType(RegSchedule.class.getSimpleName(), 5L),
                new ClsDocType(SCHEDULE_INFO, 6L),
                new ClsDocType(ACTION, 7L),
                new ClsDocType(EMPLOYEE_LIST, 8L),
                new ClsDocType(SERVICE_INFO, 9L));
    }

    @Override
    public Integer getIsDeleted() {
        return 0;
    }

    @Override
    public void setIsDeleted(Integer isDeleted) {
        
    }
}
