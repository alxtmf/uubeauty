/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty;

import ru.p03.uubeautyi.bot.document.spi.DocumentMarshalerAggregator;
import ru.p03.uubeauty.bot.schema.Action;

/**
 *
 * @author timofeevan
 */
public class ActionBuilder {
    
    private final Action action;
    
    public ActionBuilder(DocumentMarshalerAggregator marshalFactory){
        action = new Action();
    }
    
    public ActionBuilder setName(String name){
        action.setName(name);
        return this;
    }
    
    public ActionBuilder setCommand(String cmd){
        action.setCommand(cmd);
        return this;
    }
    
    public ActionBuilder setValue(String val){
        action.setValue(val);
        return this;
    }
    
    public ActionBuilder setDepart(String dep){
        action.setId(dep);
        return this;
    }
    
    public Action toAction(){
        return action;
    }
    
//    @Override
//    public String toString(){
//        return marshalFactory.<Action>marshal(action, ClsDocType.ACTION);
//    }
}
