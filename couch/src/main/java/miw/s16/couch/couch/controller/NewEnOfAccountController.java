package miw.s16.couch.couch.controller;

import miw.s16.couch.couch.model.BankAccount;
import miw.s16.couch.couch.model.dao.BankAccountDao;
import miw.s16.couch.couch.model.dao.RetailUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class NewEnOfAccountController {

    @Autowired
    RetailUserDao retailUserDao;

    @Autowired
    BankAccountDao bankAccountDao;




    @GetMapping(value = "new_enof_account")
    public String newEnOfAccountHandler(@RequestParam("id") int bankAccountId, Model model) {
        System.out.println("bank acc id is " + bankAccountId);
        BankAccount bankAccount = bankAccountDao.findByBankAccountId(bankAccountId);
        model.addAttribute("bankAccount", bankAccount);
        model.addAttribute("bankAccountId", bankAccountId);
        return "new_enof_account";
    }

    @PostMapping(value = "new_enof_account")
    public String addUserToAccountHandler(@Valid BankAccount bankAccount, BindingResult bindingResult){
        if(bindingResult.hasErrors()){

            return "new_enof_account";
        } else {
            System.out.println("iban is "+ bankAccount.getIBAN());
            System.out.println("koppelcode is "+ bankAccount.getKoppelcode());
            bankAccountDao.save(bankAccount);
            return "overview";
        }
    }
}

