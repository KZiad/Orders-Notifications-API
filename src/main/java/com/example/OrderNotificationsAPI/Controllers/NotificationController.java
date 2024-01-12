package com.example.OrderNotificationsAPI.Controllers;

import com.example.OrderNotificationsAPI.Models.*;
import com.example.OrderNotificationsAPI.Services.EmailNotifier;
import com.example.OrderNotificationsAPI.Services.SMSNotifier;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class NotificationController {

    HashMap<String, Integer> bestAccount = new HashMap<>();
    HashMap<String, Integer> bestTemplate = new HashMap<>();
    static Queue<Notification> readyQueue = new ArrayDeque<>();
    ArrayList<Template> OrderShipmentTemplates = new ArrayList<>();
    ArrayList<Template> OrderPlacementTemplates = new ArrayList<>();
    static Queue<Notification> notificationQueue = new ArrayDeque<>();
    private static final NotificationController instance = new NotificationController();
    private final ScheduledExecutorService scheduler =  Executors.newScheduledThreadPool(1);


    private NotificationController(){
        FillTemplateList();
        scheduler.scheduleAtFixedRate(this::parseQueue, 0, 45, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::executeQueue, 0, 60, TimeUnit.SECONDS);
    }

    private void stopScheduler(){
        scheduler.shutdown();
    }

    // Public method to get the singleton instance
    public static NotificationController getInstance() {
        return instance;
    }

    private void FillTemplateList(){
        Template temp1 = new Template("OrderPlacement1", "Dear {x}, your order of {y} has been confirmed. Thanks for using our store.",
                "EN", "Email");
        Template temp2 = new Template("OrderPlacement2", "عزيزي {x}، لقد تم تأكيد طلبك لـ {y}. شكرا لاستخدام متجرنا.",
                "AR", "Email");
        OrderPlacementTemplates.add(temp1);
        OrderPlacementTemplates.add(temp2);

        Template temp3 = new Template("OrderShipment1", "Dear {x}, your order of {y} has been been shipped.",
                "EN", "SMS");
        Template temp4 = new Template("OrderShipment2", "عزيزي {x}، لقد تم شحن طلبك {y}.",
                "AR", "SMS");
        OrderShipmentTemplates.add(temp3);
        OrderShipmentTemplates.add(temp4);
    }

    public void addPlacementNotification(Account account, Product[] products){
        ArrayList<String> placeholders = new ArrayList<>();

        placeholders.add(account.getEmail());
        placeholders.add(account.getPhoneNumber());

        for (Product product : products){
            placeholders.add(product.getName());
        }

        Template randomTemplate = OrderPlacementTemplates.get(ThreadLocalRandom.current().nextInt(OrderPlacementTemplates.size()));
        Notification notification = new Notification(randomTemplate, placeholders);

        notificationQueue.add(notification);
    }

    public void addShipmentNotification(Account account,Long orderNum){
        ArrayList<String> placeholders = new ArrayList<>();

        placeholders.add(account.getEmail());
        placeholders.add(account.getPhoneNumber());
        placeholders.add(orderNum.toString());

        Template randomTemplate = OrderPlacementTemplates.get(ThreadLocalRandom.current().nextInt(OrderShipmentTemplates.size()));
        Notification notification = new Notification(randomTemplate, placeholders);

        notificationQueue.add(notification);
    }

    public void parseQueue(){
        for (Notification notification : notificationQueue){
            Template template = notification.getTemplate();
            String body = template.getBody();
            ArrayList<String> placeholders = notification.getPlaceholders();
            StringBuilder orders = new StringBuilder();

            // To increase the frequency of a repeated template
            if (bestTemplate.containsKey(template.getName())){
                bestTemplate.replace(template.getName(), bestTemplate.get(template.getName()) + 1);
            }
            else {
                bestTemplate.put(template.getName(), 1);
            }

            // To increase the frequency of a repeated account
            String accountName = notification.getPlaceholders().get(0);
            if (bestAccount.containsKey(accountName)){
                bestAccount.replace(accountName, bestAccount.get(accountName + 1));
            }
            else {
                bestAccount.put(accountName, 1);
            }

            if (!placeholders.isEmpty()){
                body = body.replace("{x}", placeholders.get(0));
            }

            // Placeholders
            // (0) = account email
            // (1) = account phone number
            // (2 >=) = product info

            // Fill string with placeholders for all orders
            for (int i = 2; i < placeholders.size(); i++){
                orders.append("{" + "y").append(i).append("} ");
            }

            // Replace the single {y} placeholder with all the others to be able to easily replace them later
            body = body.replace("{y}", orders);

            for (int i = 2; i < placeholders.size(); i++){
                if (i == placeholders.size() - 1){
                    body = body.replace("{y" + i +"}", placeholders.get(i));
                }else {
                    body = body.replace("{y" + i +"}", placeholders.get(i) + ", ");
                }
            }

            notification.getTemplate().setBody(body);
            readyQueue.add(notification);
            notificationQueue.remove(notification);
        }
    }

    public void executeQueue(){
        for (Notification notification : readyQueue){
            Template template = notification.getTemplate();

            if (template.getChannel().equals("Email")){
                EmailNotifier emailSender = new EmailNotifier();
                emailSender.sendEmail(notification.getPlaceholders().get(0), "Order Notification", template.getBody());
            }

            else if (template.getChannel().equals("SMS")){
                SMSNotifier smsNotifier = new SMSNotifier();
                String number = notification.getPlaceholders().get(1);

                smsNotifier.sendSMS(number, template.getBody());
            }

            readyQueue.remove(notification);
        }
    }

    public ArrayList<String> getStatistics(){
        ArrayList<String> frequencyTable = new ArrayList<>();

        // Cast it into a list then sort it
        List<Map.Entry<String, Integer>> templateEntryList = new ArrayList<>(bestTemplate.entrySet());
        List<Map.Entry<String, Integer>> accountsEntryList = new ArrayList<>(bestAccount.entrySet());

        templateEntryList.sort(Map.Entry.comparingByValue());
        accountsEntryList.sort(Map.Entry.comparingByValue());

        frequencyTable.add(templateEntryList.get(0).getKey());
        frequencyTable.add(templateEntryList.get(0).getValue().toString());
        frequencyTable.add(accountsEntryList.get(1).getKey());
        frequencyTable.add(accountsEntryList.get(1).getValue().toString());

        return frequencyTable;
    }
}
