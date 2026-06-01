import './App.css';

import { useEffect, useState } from "react";

const BASE_URL = "/api/accounts";

export default function App() {
  const [accounts, setAccounts] = useState([]);
  const [error, setError] = useState("");

  // Create Account
  const [newName, setNewName] = useState("");
  const [newBalance, setNewBalance] = useState("");

  // Deposit / Withdraw
  const [accountId, setAccountId] = useState("");
  const [amount, setAmount] = useState("");

  // Transfer
  const [transferFrom, setTransferFrom] = useState("");
  const [transferTo, setTransferTo] = useState("");
  const [transferAmount, setTransferAmount] = useState("");

  // Transfer history
  const [historyId, setHistoryId] = useState("");
  const [history, setHistory] = useState(null);

  // ── Helpers ──────────────────────────────────────────────────────────────

  const loadAccounts = async () => {
    const res = await fetch(BASE_URL);
    const data = await res.json();
    setAccounts(data);
  };

  useEffect(() => { loadAccounts(); }, []);

  const call = async (fn) => {
    setError("");
    try {
      await fn();
      loadAccounts();
    } catch (e) {
      setError(e.message);
    }
  };

  const post = async (url, body) => {
    const res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      throw new Error(data.error || `Request failed (${res.status})`);
    }
    return res;
  };

  // ── Handlers ──────────────────────────────────────────────────────────────

  const createAccount = () => call(async () => {
    await post(BASE_URL, { ownerName: newName, initialBalance: Number(newBalance) });
    setNewName(""); setNewBalance("");
  });

  const deposit = () => call(async () => {
    await post(`${BASE_URL}/${accountId}/deposit`, { amount: Number(amount) });
    setAccountId(""); setAmount("");
  });

  const withdraw = () => call(async () => {
    await post(`${BASE_URL}/${accountId}/withdraw`, { amount: Number(amount) });
    setAccountId(""); setAmount("");
  });

  const transfer = () => call(async () => {
    await post(`${BASE_URL}/${transferFrom}/transfer`, {
      toAccountId: transferTo,
      amount: Number(transferAmount),
    });
    setTransferFrom(""); setTransferTo(""); setTransferAmount("");
  });

  const loadHistory = async () => {
    setError("");
    const res = await fetch(`${BASE_URL}/${historyId}/transfers`);
    if (!res.ok) { setError("Account not found"); return; }
    setHistory(await res.json());
  };

  // ── Styles ────────────────────────────────────────────────────────────────

  const styles = {
    app:     { maxWidth: 700, margin: "0 auto", padding: 24, fontFamily: "Arial, sans-serif" },
    h1:      { marginBottom: 4 },
    sub:     { color: "#888", marginBottom: 24, fontSize: 13 },
    section: { background: "#f8f8f8", border: "1px solid #ddd", borderRadius: 8, padding: 16, marginBottom: 16 },
    h3:      { marginTop: 0, marginBottom: 12 },
    row:     { display: "flex", gap: 8, flexWrap: "wrap", alignItems: "center" },
    input:   { padding: "7px 10px", border: "1px solid #ccc", borderRadius: 5, fontSize: 14, flex: 1, minWidth: 120 },
    btn:     { padding: "7px 16px", background: "#222", color: "#fff", border: "none", borderRadius: 5, cursor: "pointer", fontSize: 14 },
    btnSm:   { padding: "5px 12px", background: "#555", color: "#fff", border: "none", borderRadius: 5, cursor: "pointer", fontSize: 13 },
    error:   { background: "#fff0f0", border: "1px solid #ffaaaa", color: "#c00", padding: "8px 12px", borderRadius: 5, marginBottom: 12 },
    table:   { width: "100%", borderCollapse: "collapse", fontSize: 14 },
    th:      { textAlign: "left", padding: "6px 10px", borderBottom: "2px solid #ddd", background: "#f0f0f0" },
    td:      { padding: "6px 10px", borderBottom: "1px solid #eee" },
    mono:    { fontFamily: "monospace", fontSize: 12, color: "#555" },
  };

  // ── Render ────────────────────────────────────────────────────────────────

  return (
    <div style={styles.app}>
      <h1 style={styles.h1}>⚡ Fast &amp; Reckless Bank</h1>
      <p style={styles.sub}>In-memory. Zero persistence. Zero regrets.</p>

      {error && <div style={styles.error}>❌ {error}</div>}

      {/* CREATE ACCOUNT */}
      <div style={styles.section}>
        <h3 style={styles.h3}>Create Account</h3>
        <div style={styles.row}>
          <input style={styles.input} placeholder="Owner name"
            value={newName} onChange={e => setNewName(e.target.value)} />
          <input style={styles.input} placeholder="Initial balance" type="number" min="0"
            value={newBalance} onChange={e => setNewBalance(e.target.value)} />
          <button style={styles.btn} onClick={createAccount}>Create</button>
        </div>
      </div>

      {/* DEPOSIT / WITHDRAW */}
      <div style={styles.section}>
        <h3 style={styles.h3}>Deposit / Withdraw</h3>
        <div style={styles.row}>
          <input style={styles.input} placeholder="Account ID"
            value={accountId} onChange={e => setAccountId(e.target.value)} />
          <input style={styles.input} placeholder="Amount" type="number" min="0.01"
            value={amount} onChange={e => setAmount(e.target.value)} />
          <button style={styles.btn} onClick={deposit}>Deposit</button>
          <button style={{ ...styles.btn, background: "#c0392b" }} onClick={withdraw}>Withdraw</button>
        </div>
      </div>

      {/* TRANSFER */}
      <div style={styles.section}>
        <h3 style={styles.h3}>Transfer Money</h3>
        <div style={styles.row}>
          <input style={styles.input} placeholder="From Account ID"
            value={transferFrom} onChange={e => setTransferFrom(e.target.value)} />
          <input style={styles.input} placeholder="To Account ID"
            value={transferTo} onChange={e => setTransferTo(e.target.value)} />
          <input style={styles.input} placeholder="Amount" type="number" min="0.01"
            value={transferAmount} onChange={e => setTransferAmount(e.target.value)} />
          <button style={{ ...styles.btn, background: "#1a6b3a" }} onClick={transfer}>Transfer</button>
        </div>
      </div>

      {/* ACCOUNTS LIST */}
      <div style={styles.section}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
          <h3 style={{ margin: 0 }}>Accounts ({accounts.length})</h3>
          <button style={styles.btnSm} onClick={loadAccounts}>↻ Refresh</button>
        </div>
        {accounts.length === 0 ? (
          <p style={{ color: "#888", margin: 0 }}>No accounts yet.</p>
        ) : (
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>ID</th>
                <th style={styles.th}>Owner</th>
                <th style={styles.th}>Balance</th>
              </tr>
            </thead>
            <tbody>
              {accounts.map(acc => (
                <tr key={acc.id}>
                  <td style={{ ...styles.td, ...styles.mono }}>{acc.id.slice(0, 8)}…</td>
                  <td style={styles.td}>{acc.ownerName}</td>
                  <td style={{ ...styles.td, fontWeight: "bold" }}>
                    ${Number(acc.balance).toFixed(2)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* TRANSFER HISTORY */}
      <div style={styles.section}>
        <h3 style={styles.h3}>Last 50 Outgoing Transfers</h3>
        <div style={{ ...styles.row, marginBottom: 12 }}>
          <input style={styles.input} placeholder="Account ID"
            value={historyId} onChange={e => { setHistoryId(e.target.value); setHistory(null); }} />
          <button style={styles.btn} onClick={loadHistory}>Load History</button>
        </div>
        {history !== null && (
          history.length === 0 ? (
            <p style={{ color: "#888", margin: 0 }}>No outgoing transfers for this account.</p>
          ) : (
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>To Account</th>
                  <th style={styles.th}>Amount</th>
                  <th style={styles.th}>Date</th>
                </tr>
              </thead>
              <tbody>
                {[...history].reverse().map((t, i) => (
                  <tr key={i}>
                    <td style={{ ...styles.td, ...styles.mono }}>{t.toAccountId.slice(0, 8)}…</td>
                    <td style={{ ...styles.td, fontWeight: "bold" }}>${Number(t.amount).toFixed(2)}</td>
                    <td style={{ ...styles.td, ...styles.mono }}>{new Date(t.timestamp).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )
        )}
      </div>
    </div>
  );
}