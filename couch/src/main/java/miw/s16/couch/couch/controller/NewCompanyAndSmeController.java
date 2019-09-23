package miw.s16.couch.couch.controller;

import miw.s16.couch.couch.model.BankAccount;
import miw.s16.couch.couch.model.BankUser;
import miw.s16.couch.couch.model.Company;
import miw.s16.couch.couch.model.SMEUser;
import miw.s16.couch.couch.model.dao.BankAccountDao;
import miw.s16.couch.couch.model.dao.BankUserDao;
import miw.s16.couch.couch.model.dao.CompanyDao;
import miw.s16.couch.couch.model.dao.SMEUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class NewCompanyAndSmeController {

    @Autowired
    SMEUserDao smeUserDao;

    @Autowired
    BankAccountDao bankAccountDao;

    @Autowired
    CompanyDao companyDao;

    @Autowired
    BankUserDao bankUserDao;

    private List<String> companyForm = new ArrayList<>();
    private List<String> sectors = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
    private SMEUser newSmeUser = new SMEUser();
    private BankAccount bankAccount = new BankAccount();
    private Company newCompany = new Company();
    // for testing
    private BankUser bankUser = new BankUser();



    @GetMapping(value = "couch-zakelijk")  //van select type -> new zakelijk user
    public String newCompanyHandler(Model model) {
        SMEUser smeUser = new SMEUser();
        Company company = new Company();
        if (companyForm.size() == 0 && sectors.size() == 0) {
            Collections.addAll(companyForm, "B.V.", "Eenmanszaak", "Vereniging of Stichting", "V.O.F", "Andere ondernemingsvorm");
            // branch-informatie van KvK
            Collections.addAll(sectors, "ICT", "Advies en Consultancy", "Bouw, installatie en infra", "Energie",
                    "Financiële dienstverlening", "Water en afval", "Industrie", "Groothandel", "Agrosector", "Horeca", "Onderwijs en training",
                    "Persoonlijke dienstverlening en not-for-profit", "Gezondheidszorg", "Vervoer, post en opslag");
        }
        model.addAttribute("companyForm", companyForm);
        model.addAttribute("sectors", sectors);
        model.addAttribute("smeUser", smeUser);
        model.addAttribute("company",company);
        return "new_company";
    }

    @PostMapping(value = "couch-zakelijk")
    public String newCompanyHandler(@ModelAttribute("company") @RequestBody Company company, Model model) {
//        if (bindingResult.hasErrors()) {
//            return "new_company";
//        } else {
        if (roles.size() == 0 ){
            Collections.addAll(roles, "Eigenaar", "Medewerker", "Admin");
        }
        bankAccountDao.save(bankAccount);

        bankUser.setRole("account manager");
        newCompany.setAccountManager(bankUser);
        newCompany.addCompanyAccount(bankAccount);
        newCompany.setCompanyName(company.getCompanyName());
        newCompany.setChamberOfCommerceId(company.getChamberOfCommerceId());
        newCompany.setCompanyType(company.getCompanyType());
        newCompany.setPinCode(1234);
        newCompany.setSector(company.getSector());
        newCompany.addCompanyEmployee(newSmeUser);
        companyDao.save(newCompany);
        model.addAttribute("roles", roles);
        model.addAttribute("role", "");
        model.addAttribute("company", newCompany);
        model.addAttribute("smeUser", newSmeUser);
        model.addAttribute("companySector", "");
//        }
        return "new_SMEUser";
    }

    @PostMapping(value = "newSMEUser")
    public String newSMEUserHandler(@ModelAttribute("smeUser") @RequestBody SMEUser smeUser, Model model) {
//        if (bindingResult.hasErrors()) {
//            return "new_company";
//        } else {

        newSmeUser.setUserName(smeUser.getUserName());
        newSmeUser.setUserPassword(smeUser.getUserPassword());
        newSmeUser.setRoleEmployee(smeUser.getRoleEmployee());
        smeUserDao.save(newSmeUser);
       //}
        return "new_SMEUser_success";
    }


}

