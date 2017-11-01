/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

/**
 *
 * @author altmf
 */
public class StateHolder {
    Map<User, List<State>> states =  new HashMap<>();
    
    public StateHolder(){
        
    }
    
    public void put (User user, State state){
        if (states.containsKey(user)){
            states.get(user).add(state);
        }else{
            states.put(user, new ArrayList<>(Arrays.asList(state)));
        }
    }
    
    public List<State> get(User user){
        return states.get(user) == null ? new ArrayList<>() : states.get(user);
    }
    
    public List<State> remove (User user){
        return states.remove(user);
    }
    
    public State getLast(User user){
        State s = null;
        if (states.containsKey(user)){
            s = states.get(user).get(states.get(user).size() - 1);
        }
        return s;
    }
    
    public State getFirst(User user){
        State s = null;
        if (states.containsKey(user)){
            s = states.get(user).get(0);
        }
        return s;
    }
    
    public void put (Update update, State state){
        User u = update.getMessage().getFrom();
        put(u, state);
    }
    
    public List<State> get(Update update){
        User u = update.getMessage().getFrom();
        return get(u);
    }
    
    public List<State> remove (Update update){
        User u = update.getMessage().getFrom();
        return remove(u);
    }
    
    public State getLast(Update update){
        User u = update.getMessage().getFrom();
        return getLast(u);
    }
    
    public State getFirst(Update update){
        User u = update.getMessage().getFrom();
        return getFirst(u);
    }
}
