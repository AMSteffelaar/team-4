package miw.s16.couch.couch.controller;

import miw.s16.couch.couch.model.*;
import miw.s16.couch.couch.model.dao.BankAccountDao;
import miw.s16.couch.couch.model.dao.RetailUserDao;
import miw.s16.couch.couch.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
public class TransactionController implements WebMvcConfigurer {


    @Autowired
    RetailUserDao retailUserDao;

    @Autowired
    TransactionService transactionService;

    @Autowired
    BankAccountDao bankAccountDao;

    BankAccount accountTo = new BankAccount();


    @RequestMapping(value = "transactionRequest")
    public String pageHandlerGet(@ModelAttribute User user, Model model, HttpServletRequest request) {
        Transaction transaction = new Transaction();
        HttpSession session = request.getSession (true);
        String iban = (String) session.getAttribute("clickedIBAN");
        BankAccount bankAccountFrom1 = bankAccountDao.findByIban(iban);
        transaction.setBankAccount(accountTo);
        transaction.setFromAccount(iban);
        model.addAttribute("transaction", transaction);
        model.addAttribute("date", transaction.getTransactionDay());//BvB
        model.addAttribute("bankAccountFrom", iban);
        model.addAttribute("bankAccountType", bankAccountFrom1.getAccountType()); // adding logic for company transactions
        model.addAttribute("bankAccountTo", transaction.getToAccount());
        model.addAttribute("fullNames", session.getAttribute("fullNames"));//BvB
        model.addAttribute("balance", bankAccountFrom1.twoDecimalBalance(bankAccountFrom1.getBalance()));
        return "transaction";
    }


    @PostMapping(value="transactionConfirmation")
    public String transactionHandler(@Valid @ModelAttribute(value = "transaction")Transaction transaction, @RequestParam("id") String ibanId, BindingResult bindingResult, Model model, HttpServletRequest request) {
        boolean error = false;
        HttpSession session = request.getSession (true);
        String userName = (String) session.getAttribute("userName");
        // bank account from
        BankAccount bankAccountFrom = bankAccountDao.findByIban(ibanId);
        //check for duplicate account to and from IBAN
        if(transaction.getToAccount().equals(bankAccountFrom.getIBAN())){
            error = true;
        }
        // check for error in user input
        if(bindingResult.hasErrors()) {
            error = true;
        }
        if (error) {
            model.addAttribute("transaction", transaction);
//            model.addAttribute("date_time", transaction.getTransactionDate().toString());
            model.addAttribute("date", transaction.getTransactionDay());//BvB
            model.addAttribute("bankAccountFrom", bankAccountFrom.getIBAN());
            model.addAttribute("bankAccountTo", transaction.getToAccount());
            model.addAttribute("userName", userName);
            model.addAttribute("balance", bankAccountFrom.twoDecimalBalance(bankAccountFrom.getBalance()));
            model.addAttribute("bankAccountType", bankAccountFrom.getAccountType());
            return "transaction";
        } else {
            String feedback = transactionService.TransactionCalculation(transaction.getToAccount(), bankAccountFrom,
                    transaction.getAmount(), transaction.getTransactionDate(), transaction.getDescription(), transaction.getPin());
            model.addAttribute("bankAccountType", bankAccountFrom.getAccountType());
            model.addAttribute("feedback", feedback);
            model.addAttribute("fullNames", session.getAttribute("fullNames"));
            return "transaction_feedback";
        }
    }
}
