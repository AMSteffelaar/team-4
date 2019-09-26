package miw.s16.couch.couch.controller;

import miw.s16.couch.couch.model.*;

import miw.s16.couch.couch.model.dao.RetailUserDao;
import miw.s16.couch.couch.model.dao.SMEUserDao;
import miw.s16.couch.couch.model.dao.UserDao;
import miw.s16.couch.couch.service.HibernateLab;
import miw.s16.couch.couch.service.PasswordValidator;
import miw.s16.couch.couch.service.TestdataCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    PasswordValidator validator;

    @Autowired
    HibernateLab lab;

    @Autowired
    TestdataCreator testData;

    @Autowired
    RetailUserDao retailUserDao;

    @Autowired
    SMEUserDao smeUserDao;

    @Autowired
    UserDao userDao;

    @GetMapping
    public String indexHandler(Model model) {
        lab.dbinit();
        User user = new User();
        RetailUser retailUser = new RetailUser();
        SMEUser smeUser = new SMEUser();
        BankUser bankUser = new BankUser();
        model.addAttribute("user", user);
        model.addAttribute("smeUser", smeUser);
        model.addAttribute("bankUser", bankUser);
        model.addAttribute("retailUser", retailUser);
        return "index";
    }

    // user log in & user validation and direction to personal page
    @PostMapping(value = "overview")
    public String loginHandler(@ModelAttribute User user, Model model, HttpServletRequest request) {
        boolean loginOk = validator.validateMemberPassword(user);


        if (loginOk) {
            HttpSession session = request.getSession(true);
            List<RetailUser> loggedInRetailUser = retailUserDao.findByUserName(user.getUserName());
            //ophalen & opslaan alle bankaccounts die bij deze user horen
            List<BankAccount> loggedInBankAccounts = loggedInRetailUser.get(0).getBankAccounts();
            // -- for login session ---
            session.setAttribute("userName", user.getUserName());
            session.setAttribute("retailUser", loggedInRetailUser.get(0));
            session.setAttribute("userId", user.getUserId());
            model.addAttribute("userName", loggedInRetailUser.get(0).getUserName());
            model.addAttribute("allBankAccounts", loggedInBankAccounts);
            return "personal_page";
        }
        return "login_failed";
    }


    @GetMapping(value ="zakelijk-klant")
    public String newCompanyHandler(Model model) {
        SMEUser smeUser = new SMEUser();
        Company company = new Company();
        model.addAttribute("company", company);
        model.addAttribute("smeUser", smeUser);
        return "company_login";
    }


    @GetMapping(value = "newUser")
    public String newUserHandler() {
        return "new_user_select_type";
    }
}
