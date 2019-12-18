package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Income;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.IncomeServices.IncomeService;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;


@Controller
public class IncomesController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private IncomeService incomeService;

    @RequestMapping(value = "/income-page", method = RequestMethod.GET)
    public String companyIncome(Model model, Principal principal, Long id){
        Company company = companyService.getOne(id);
        User me = userService.findByEmail(principal.getName());
        if(!company.getUser().equals(me)){
            return "redirect:/company-home?notYourCompany";
        } else {
            model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
            model.addAttribute("companyIncomesCounter", incomeService.showAllIncomesByCompanyId(id).size());
            model.addAttribute("companyIncomes", incomeService.showLast12IncomesByCompanyId(id));
            model.addAttribute("fullIncomeCash", company.getIncome());
            return "/income-page";
        }
    }

    @RequestMapping(value = "/income-page", method = RequestMethod.POST)
    public String insertIncome(Principal principal, Income income){
        Company company = companyService.showOneUserCompany(principal.getName());
        if(income.getIncomePrice()<0){
            return "redirect:/income-page?id="+company.getId()+"&noMinusError";
        }
        incomeService.save(company);
        companyService.setCompanyIncomeAndProfitPlusOne(company, income);
        return "redirect:/income-page?id="+company.getId();
    }

    @RequestMapping(value = "/income-by-date", method = RequestMethod.GET)
    public String getIncomeByDate(Model model, Principal principal, @RequestParam("incomeDay") String incomeDay){
        User me = userService.findByEmail(principal.getName());
        if(!me.getWorkingStatus().equals("owner")){
            return "redirect:/comapny-home?notOwner";
        }
        Company company = companyService.showOneUserCompany(principal.getName());
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("incomeDetailsCounter", incomeService.showAllIncomesByCompanyIdAndDate(company.getId(), incomeDay).size());
        model.addAttribute("incomeDetails", incomeService.showAllIncomesByCompanyIdAndDate(company.getId(), incomeDay));
        model.addAttribute("incomeDay", incomeDay);

        return "/income-by-date";
    }

    @RequestMapping("/deleteIncome")
    public String deleteIncome(Principal principal, Long id){
        Company company = companyService.showOneUserCompany(principal.getName());
        Income income = incomeService.getOne(id);
        incomeService.deleteById(id);
        companyService.setCompanyIncomeAndProfitMinus(company, income);
        return "redirect:/income-page?id="+company.getId()+"&incomeDeleted";
    }

    @RequestMapping("/hideIncome")
    public String hideIncome(Principal principal, Long id){
        Company company = companyService.showOneUserCompany(principal.getName());
        incomeService.deleteById(id);
        return "redirect:/income-page?id="+company.getId()+"&incomeHided";
    }
}
