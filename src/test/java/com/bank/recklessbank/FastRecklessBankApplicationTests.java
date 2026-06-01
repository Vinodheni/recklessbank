package com.bank.recklessbank;

import com.bank.recklessbank.model.Account;
import com.bank.recklessbank.model.TransferRecord;
import com.bank.recklessbank.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FastRecklessBankApplicationTests {

	private BankService bankService;

	@BeforeEach
	void setUp() {
		bankService = new BankService(); // fresh instance before every test
	}

	// ── CREATE ACCOUNT ────────────────────────────────────────────────────

	@Test
	void createAccount_validInput_accountIsCreated() {
		Account account = bankService.createAccount("Alice", BigDecimal.valueOf(1000));

		assertNotNull(account.getId());
		assertEquals("Alice", account.getOwnerName());
		assertEquals(BigDecimal.valueOf(1000), account.getBalance());
	}

	@Test
	void createAccount_blankName_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				bankService.createAccount("", BigDecimal.valueOf(500)));
	}

	@Test
	void createAccount_negativeBalance_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				bankService.createAccount("Bob", BigDecimal.valueOf(-100)));
	}

	// ── GET ALL ACCOUNTS ──────────────────────────────────────────────────

	@Test
	void getAllAccounts_afterCreatingTwo_returnsBoth() {
		bankService.createAccount("Alice", BigDecimal.valueOf(1000));
		bankService.createAccount("Bob", BigDecimal.valueOf(500));

		assertEquals(2, bankService.getAllAccounts().size());
	}

	// ── GET SINGLE ACCOUNT ────────────────────────────────────────────────

	@Test
	void getAccount_existingId_returnsCorrectAccount() {
		Account created = bankService.createAccount("Alice", BigDecimal.valueOf(1000));

		Account fetched = bankService.getAccount(created.getId());

		assertEquals(created.getId(), fetched.getId());
		assertEquals("Alice", fetched.getOwnerName());
	}

	@Test
	void getAccount_nonExistingId_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				bankService.getAccount("fake-id-999"));
	}

	// ── DEPOSIT ───────────────────────────────────────────────────────────

	@Test
	void deposit_validAmount_balanceIncreases() {
		Account account = bankService.createAccount("Alice", BigDecimal.valueOf(500));

		bankService.deposit(account.getId(), BigDecimal.valueOf(250));

		assertEquals(BigDecimal.valueOf(750), bankService.getAccount(account.getId()).getBalance());
	}

	@Test
	void deposit_zeroAmount_throwsException() {
		Account account = bankService.createAccount("Alice", BigDecimal.valueOf(500));

		assertThrows(IllegalArgumentException.class, () ->
				bankService.deposit(account.getId(), BigDecimal.ZERO));
	}

	@Test
	void deposit_nonExistingAccount_throwsException() {
		assertThrows(IllegalArgumentException.class, () ->
				bankService.deposit("fake-id-999", BigDecimal.valueOf(100)));
	}

	// ── WITHDRAW ──────────────────────────────────────────────────────────

	@Test
	void withdraw_validAmount_balanceDecreases() {
		Account account = bankService.createAccount("Alice", BigDecimal.valueOf(1000));

		bankService.withdraw(account.getId(), BigDecimal.valueOf(400));

		assertEquals(BigDecimal.valueOf(600), bankService.getAccount(account.getId()).getBalance());
	}

	@Test
	void withdraw_insufficientFunds_throwsException() {
		Account account = bankService.createAccount("Alice", BigDecimal.valueOf(100));

		assertThrows(IllegalStateException.class, () ->
				bankService.withdraw(account.getId(), BigDecimal.valueOf(999)));
	}

	@Test
	void withdraw_zeroAmount_throwsException() {
		Account account = bankService.createAccount("Alice", BigDecimal.valueOf(500));

		assertThrows(IllegalArgumentException.class, () ->
				bankService.withdraw(account.getId(), BigDecimal.ZERO));
	}

	// ── TRANSFER ──────────────────────────────────────────────────────────

	@Test
	void transfer_validTransfer_updatesBothBalances() {
		Account from = bankService.createAccount("Alice", BigDecimal.valueOf(1000));
		Account to   = bankService.createAccount("Bob",   BigDecimal.valueOf(500));

		bankService.transfer(from.getId(), to.getId(), BigDecimal.valueOf(300));

		assertEquals(BigDecimal.valueOf(700), bankService.getAccount(from.getId()).getBalance());
		assertEquals(BigDecimal.valueOf(800), bankService.getAccount(to.getId()).getBalance());
	}

	@Test
	void transfer_insufficientFunds_throwsException() {
		Account from = bankService.createAccount("Alice", BigDecimal.valueOf(100));
		Account to   = bankService.createAccount("Bob",   BigDecimal.valueOf(500));

		assertThrows(IllegalStateException.class, () ->
				bankService.transfer(from.getId(), to.getId(), BigDecimal.valueOf(999)));
	}

	@Test
	void transfer_toSameAccount_throwsException() {
		Account account = bankService.createAccount("Alice", BigDecimal.valueOf(500));

		assertThrows(IllegalArgumentException.class, () ->
				bankService.transfer(account.getId(), account.getId(), BigDecimal.valueOf(100)));
	}

	@Test
	void transfer_nonExistingToAccount_throwsException() {
		Account from = bankService.createAccount("Alice", BigDecimal.valueOf(500));

		assertThrows(IllegalArgumentException.class, () ->
				bankService.transfer(from.getId(), "fake-id-999", BigDecimal.valueOf(100)));
	}

	// ── TRANSFER HISTORY ──────────────────────────────────────────────────

	@Test
	void getTransfers_afterTransfer_recordIsStored() {
		Account from = bankService.createAccount("Alice", BigDecimal.valueOf(1000));
		Account to   = bankService.createAccount("Bob",   BigDecimal.valueOf(500));

		bankService.transfer(from.getId(), to.getId(), BigDecimal.valueOf(200));

		List<TransferRecord> history = bankService.getOutgoingTransfers(from.getId());
		assertEquals(1, history.size());
		assertEquals(from.getId(), history.get(0).getFromAccountId());
		assertEquals(to.getId(),   history.get(0).getToAccountId());
		assertEquals(BigDecimal.valueOf(200), history.get(0).getAmount());
	}

	@Test
	void getTransfers_noTransfersMade_returnsEmptyList() {
		Account account = bankService.createAccount("Alice", BigDecimal.valueOf(500));

		List<TransferRecord> history = bankService.getOutgoingTransfers(account.getId());

		assertTrue(history.isEmpty());
	}

	@Test
	void getTransfers_over50Transfers_ringBufferCapsAt50() {
		Account from = bankService.createAccount("Alice", BigDecimal.valueOf(999999));
		Account to   = bankService.createAccount("Bob",   BigDecimal.valueOf(0));

		for (int i = 0; i < 60; i++) {
			bankService.transfer(from.getId(), to.getId(), BigDecimal.valueOf(1));
		}

		assertEquals(50, bankService.getOutgoingTransfers(from.getId()).size());
	}
}