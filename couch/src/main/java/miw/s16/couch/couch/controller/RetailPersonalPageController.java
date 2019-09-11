package miw.s16.couch.couch.controller;


import miw.s16.couch.couch.model.RetailUser;
import miw.s16.couch.couch.model.Transaction;
import miw.s16.couch.couch.model.User;
import miw.s16.couch.couch.model.BankAccount;
import miw.s16.couch.couch.model.dao.BankAccountDao;
import miw.s16.couch.couch.model.dao.RetailUserDao;
import miw.s16.couch.couch.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RetailPersonalPageController<retailUser> {


    @Autowired
    RetailUserDao retailUserDao;

    @Autowired
    TransactionService transactionService;

    @Autowired
    BankAccountDao bankAccountDao;

    BankAccount bankAccount = new BankAccount();




    @PostMapping(value = "transactionRequest")
    public String pageHandler(@ModelAttribute User user, Model model, HttpServletRequest request) {
        Transaction transaction = new Transaction();
        // log in session
        HttpSession session = request.getSession (true);
        String userName = (String) session.getAttribute("userName");
        RetailUser retailUser1  = (RetailUser) session.getAttribute("retailUser");
        int userId = (int) session.getAttribute("userId");
        BankAccount bankAccountFrom = retailUser1.getBankAccounts().get(0);

        transaction.setBankAccount(bankAccount);
        transaction.setFromAccount(bankAccount.getIBAN());
        System.out.println("datum - tijd is: " + transaction.getTransactionDate().toString());
        model.addAttribute("transaction", transaction);
        model.addAttribute("date_time", transaction.getTransactionDate().toString());
        model.addAttribute("bankAccountFrom", bankAccountFrom.getIBAN());
        model.addAttribute("bankAccountTo", transaction.getToAccount());
        model.addAttribute("userName", userName);
        model.addAttribute("user", user);
        model.addAttribute("balance", bankAccountFrom.getBalance());
        return "transaction";
    }



    @PostMapping(value = "newAccountRequest")
    public String newAccountRequestHandler(@ModelAttribute User user, Model model, HttpServletRequest request) {
        // log in session
        HttpSession session = request.getSession (true);
        String userName = (String) session.getAttribute("userName");
        RetailUser retailUser1  = (RetailUser) session.getAttribute("retailUser");
        int userId = (int) session.getAttribute("userId");

        //nieuwe IBAN wordt aangemaakt, aan retailuser gekoppeld en in DB opgeslagen
        BankAccount newBankAccount = new BankAccount();
        retailUser1.addBankAccount(newBankAccount);
        bankAccountDao.save(newBankAccount);
        retailUserDao.save(retailUser1);

        List<BankAccount> bankAccountsList = retailUser1.getBankAccounts();
        model.addAttribute("userName", userName);
        model.addAttribute("allBankAccounts", bankAccountsList);

        return "personal_page";
    }

    //@GetMapping(value = "bankAccountDetails")
    //public String testHandler(@RequestParam(name = "id") int id, Model model){
    //    return test
    //}
    //coding AV & PH
    @GetMapping(value = "bankAccountDetails")
    public String bankAccountDetailsHandler(@ModelAttribute User user, Model model, HttpServletRequest request) {
        // log in session
        HttpSession session = request.getSession (true);
        String userName = (String) session.getAttribute("userName");
        //RetailUser retailUser1  = (RetailUser) session.getAttribute("retailUser");
        //int userId = (int) session.getAttribute("userId");
        //geclickte Iban incl saldo (overzicht transacties) ophalen uit DB en in scherm BankAccountDetails laat zien
        //List<BankAccount> bankAccountsList = retailUser1.getBankAccounts();
        model.addAttribute("userName", userName);
       // model.addAttribute("allBankAccounts", bankAccountsList);

        return "bank_account_details";
    }

}
