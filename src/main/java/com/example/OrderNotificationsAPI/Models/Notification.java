package com.example.OrderNotificationsAPI.Models;
import java.util.ArrayList;

public class Notification {

    Template template;
    ArrayList<String> placeholders;
    Long orderNum;

    public Notification(Template template, ArrayList<String> placeholders) {
        this.template = template;
        this.placeholders = placeholders;
    }

    public Template getTemplate(){
        return this.template;
    }
    public ArrayList<String> getPlaceholders(){
        return this.placeholders;
    }

}
