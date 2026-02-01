package com.SecureLibrarySystem.webapp.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.SecureLibrarySystem.webapp.dao.TransactionDAO;
import com.SecureLibrarySystem.webapp.model.Transaction;
import com.SecureLibrarySystem.webapp.util.DigitalSignatureUtil;

import jakarta.servlet.http.HttpSession;

@Controller
public class TransactionViewController {

    @Autowired
    private TransactionDAO transactionDAO;

    @GetMapping("/transactions")
    public String viewTransactions(HttpSession session, Model model) {

        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");

        List<Transaction> transactions;

        // ADMIN & LIBRARIAN → ALL
        if ("ADMIN".equals(role) || "LIBRARIAN".equals(role)) {
            transactions = transactionDAO.findAll();
        }
        // STUDENT → OWN ONLY
        else {
            transactions = transactionDAO.findByUsername(username);
        }

        // ======================
        // VERIFY DIGITAL SIGNATURE
        // ======================
        for (Transaction tx : transactions) {

            LocalDateTime ts = tx.getTimestamp().withNano(0);

            String data =
                    tx.getUsername() + "|" +
                    tx.getBookId() + "|" +
                    tx.getActionType().replace(" ❌ TAMPERED", "") + "|" +
                    ts.toString();

            boolean valid = DigitalSignatureUtil.verify(data, tx.getDigitalSignature());

            if (!valid) {
                tx.setActionType(tx.getActionType() + " ❌ TAMPERED");
            }
        }

        model.addAttribute("transactions", transactions);
        return "transactions";
    }
}
