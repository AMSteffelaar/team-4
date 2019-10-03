package miw.s16.couch.couch.controller;

import miw.s16.couch.couch.model.*;
import miw.s16.couch.couch.model.dao.BankAccountDao;
import miw.s16.couch.couch.model.dao.CompanyDao;
import miw.s16.couch.couch.model.dao.SMEUserDao;
import miw.s16.couch.couch.service.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
public class CompanyPageController {

    @Autowired
    PasswordValidator validator;

    @Autowired
    SMEUserDao smeUserDao;

    @Autowired
    CompanyDao companyDao;

    @Autowired
    BankAccountDao bankAccountDao;

    // zakelijk klant login & validation
    @PostMapping(value = "zakelijk-klant")
    public String smeLoginHandler(@ModelAttribute User user, Model model, HttpServletRequest request) {
        boolean loginOk = validator.validateMemberPassword(user);
        List<SMEUser> loggedInUser = smeUserDao.findByUserName(user.getUserName());
        Company company = loggedInUser.get(0).getCompany();
        // validation that smeuser is member of the company & login details
        if (loginOk && company != null) {
            HttpSession session = request.getSession(true);
            String companyData = company.getCompanyName() + "kvk: " + company.getChamberOfCommerceId() + " access : " + loggedInUser.get(0).getRoleEmployee();
            session.setAttribute("companyKvK", loggedInUser.get(0).getCompany().getChamberOfCommerceId());
            session.setAttribute("userName", loggedInUser.get(0).getUserName());
            session.setAttribute("fullNames", companyData);
            model.addAttribute("smeUser", loggedInUser.get(0));
            model.addAttribute("userName", loggedInUser.get(0).getUserName());
            model.addAttribute("role", loggedInUser.get(0).getRoleEmployee());
            model.addAttribute("company", company);
            model.addAttribute("employees", company.getEmployees());
            model.addAttribute("allBankAccounts", loggedInUser.get(0).getCompany().getCompanyAccounts());
            return "sme_page";
        }
        return "login_failed";
    }

    // user goes back to page
    @GetMapping(value = "zakelijk")
    public String smeHandler(@ModelAttribute User user, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        int kvkNr = (int) session.getAttribute("companyKvK");
        Company currentCompany = companyDao.findBychamberOfCommerceId(kvkNr);
        String userName = (String) session.getAttribute("userName");
        SMEUser smeUser = smeUserDao.findByUserName(userName).get(0);
        model.addAttribute("smeUser", smeUser);
        model.addAttribute("userName", userName);
        model.addAttribute("role", smeUser.getRoleEmployee());
        model.addAttribute("company", currentCompany);
        model.addAttribute("allBankAccounts", currentCompany.getCompanyAccounts());
        model.addAttribute("employees", currentCompany.getEmployees());
        return "sme_page";

    }

    // request for a new company account
    @PostMapping(value = "newCompanyAccountRequest")
    public String newCompanyAccountRequestHandler(@ModelAttribute User user, Model model, HttpServletRequest request) {
        // log in session
        HttpSession session = request.getSession(true);
        String userName = (String) session.getAttribute("userName");
        int kvkNr = (int) session.getAttribute("companyKvK");
        Company currentCompany = companyDao.findBychamberOfCommerceId(kvkNr);
        SMEUser loggedInUser = smeUserDao.findByUserName(userName).get(0);
        BankAccount newBankAccount = new BankAccount();
        newBankAccount.setAccountType("Zakelijk");
        currentCompany.addCompanyAccount(newBankAccount);
        bankAccountDao.save(newBankAccount);
        companyDao.save(currentCompany);
        List<BankAccount> bankAccountsList = currentCompany.getCompanyAccounts();
        model.addAttribute("company", loggedInUser.getCompany());
        model.addAttribute("smeUser",  loggedInUser);
        model.addAttribute("userName", loggedInUser.getRoleEmployee());
        model.addAttribute("role", loggedInUser.getRoleEmployee());
        model.addAttribute("companyName", loggedInUser.getCompany().getCompanyName());
        model.addAttribute("allBankAccounts", bankAccountsList);
        model.addAttribute("employees", currentCompany.getEmployees());
        return "sme_page";
    }

    // company details per clicked bank account
    @GetMapping(value = "companyAccountDetails")
    public String companyAccountDetailsHandler(@RequestParam("id") int bankAccountId, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        BankAccount clickedBankAccount = bankAccountDao.findByBankAccountId(bankAccountId);
        session.setAttribute("clickedIBAN", clickedBankAccount.getIBAN());
        List <Transaction> transactionList = clickedBankAccount.getTransactions();
        List <Transaction> transactionToList = clickedBankAccount.getTransactionsTo();
        transactionList.addAll(transactionToList);
        Collections.sort(transactionList);
        Collections.reverse(transactionList);
        model.addAttribute("iban", clickedBankAccount.getIBAN());
        model.addAttribute("balance", clickedBankAccount.twoDecimalBalance(clickedBankAccount.getBalance()));
        model.addAttribute("allTransactions", clickedBankAccount.getTransactions());
        model.addAttribute("fullNames", session.getAttribute("fullNames"));
        session.setAttribute("bankAccountId", clickedBankAccount.getBankAccountId());
        return "company_account_details";
    }
}
