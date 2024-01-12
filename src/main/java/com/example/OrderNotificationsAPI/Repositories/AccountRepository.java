package com.example.OrderNotificationsAPI.Repositories;

import java.util.HashMap;
import com.example.OrderNotificationsAPI.Models.Account;
import org.springframework.stereotype.Repository;


@Repository
public class AccountRepository {
    private final HashMap<String, Account> accounts = new HashMap<>();
    public Account getAccount(String email) {
        if (accounts.containsKey(email)){
            return accounts.get(email);
        }
        return null;
    }

    public Boolean addAccount(Account account){
        if(!accounts.containsKey(account.getEmail())){
            accounts.put(account.getEmail(), account);
            return true;
        }
        return false;
    }

    public Boolean updateAccount(Account account){
        if (accounts.containsKey(account.getEmail())){
            accounts.put(account.getEmail(), account);
            return true;
        }
        return false;
    }

    public Boolean deleteAccount(String email){
        if (accounts.containsKey(email)) {
            accounts.remove(email);
            return true;
        }
        return false;
    }
}
