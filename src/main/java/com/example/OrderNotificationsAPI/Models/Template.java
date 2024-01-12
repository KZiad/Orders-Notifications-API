package com.example.OrderNotificationsAPI.Models;

public class Template {
    String name;
    String body;
    String language;
    String channel;

    public Template(String name, String body, String language, String channel) {
        this.name = name;
        this.body = body;
        this.language = language;
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
