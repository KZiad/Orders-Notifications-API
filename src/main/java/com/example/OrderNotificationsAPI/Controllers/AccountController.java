package com.example.OrderNotificationsAPI.Controllers;

import com.example.OrderNotificationsAPI.Models.Account;
import com.example.OrderNotificationsAPI.Repositories.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/accounts")
public class AccountController {

    public static final AccountRepository accountRepository = new AccountRepository();

    @GetMapping(value = "/get")
    public ResponseEntity<Account> getAccount(@RequestParam(value = "email") String email){
        Account account = accountRepository.getAccount(email);
        if (account != null){
            return ResponseEntity.status(HttpStatus.OK).body(account);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping(value = "/add")
    public ResponseEntity<Account> addAccount(@RequestBody Account account){
        if (accountRepository.addAccount(account)){
            return ResponseEntity.status(HttpStatus.CREATED).body(account);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PutMapping(value = "/update")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account){
        if (accountRepository.getAccount(account.getEmail()) == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else if (accountRepository.updateAccount(account)){
            return ResponseEntity.status(HttpStatus.OK).body(account);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping(value = "/addBalance")
    public ResponseEntity<Account> addBalance(@RequestParam String email, @RequestParam float balance){
        Account account = accountRepository.getAccount(email);
        if (account == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        account.setBalance(account.getBalance() + balance);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<Account> deleteAccount(@RequestParam(value = "email") String email){
        if (accountRepository.deleteAccount(email)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
