package miw.s16.couch.couch.controller;

import miw.s16.couch.couch.model.RetailUser;
import miw.s16.couch.couch.model.User;
import miw.s16.couch.couch.service.HibernateLab;
import miw.s16.couch.couch.service.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    PasswordValidator validator;

    @Autowired
    HibernateLab lab;

    @GetMapping
    public String indexHandler(Model model){
       lab.dbinit();
       User user = new User();
       RetailUser retailUser = new RetailUser();
       model.addAttribute("user", user);
       model.addAttribute("retailUser", retailUser);
       return "index";
    }


    // depending on user type, user goes to specific overview page
    @PostMapping(value="overview")
    public String loginHandler(@ModelAttribute User user, Model model){
      boolean loginOk = validator.validateMemberPassword(user);
        if (loginOk) {
            System.out.println("de naam van de current user is: " + user.getUserName() );
            model.addAttribute("userName", user.getUserName());
            model.addAttribute("bankAccount", "NL123456");
           return "personal_page";
       }
       return"login_failed";
    }


    @GetMapping(value = "newUser")
    public String newUserHandler(){
        return "new_user_select_type";
    }


}
