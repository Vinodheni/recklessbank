package com.bank.recklessbank.controller;

import com.bank.recklessbank.model.Account;
import com.bank.recklessbank.model.TransferRecord;
import com.bank.recklessbank.request.CreateAccountRequest;
import com.bank.recklessbank.request.DepositRequest;
import com.bank.recklessbank.request.TransferRequest;
import com.bank.recklessbank.request.WithdrawRequest;
import com.bank.recklessbank.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    // GET /api/accounts
    @GetMapping
    public Collection<Account> getAllAccounts() {
        return bankService.getAllAccounts();
    }

    // GET /api/accounts/{id}
    @GetMapping("/{id}")
    public Account getAccount(@PathVariable String id) {
        return bankService.getAccount(id);
    }

    // POST /api/accounts
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = bankService.createAccount(
                request.getOwnerName(),
                request.getInitialBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    // POST /api/accounts/{id}/deposit
    @PostMapping("/{id}/deposit")
    public Account deposit(@PathVariable String id, @RequestBody DepositRequest request) {
        return bankService.deposit(id, request.getAmount());
    }

    // POST /api/accounts/{id}/withdraw
    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable String id, @RequestBody WithdrawRequest request) {
        return bankService.withdraw(id, request.getAmount());
    }

    // POST /api/accounts/{id}/transfer
    @PostMapping("/{id}/transfer")
    public ResponseEntity<Void> transfer(@PathVariable String id, @RequestBody TransferRequest request) {
        bankService.transfer(id, request.getToAccountId(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    // GET /api/accounts/{id}/transfers
    @GetMapping("/{id}/transfers")
    public List<TransferRecord> getTransfers(@PathVariable String id) {
        return bankService.getOutgoingTransfers(id);
    }

    // Exception handlers
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleUnprocessable(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", ex.getMessage()));
    }
}
