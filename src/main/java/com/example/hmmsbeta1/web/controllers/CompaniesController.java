package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class CompaniesController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @RequestMapping(value = "/company-home", method = RequestMethod.GET)
    public ModelAndView companyHome(Model model, Principal principal){
        model.addAttribute("newcompany", new Company());
        model.addAttribute("usercompanies", companyRepository.showOnlyUsersCompanies(principal.getName()));
        return new ModelAndView("company-home");
    }

    @RequestMapping(value = "/company-home", method = RequestMethod.POST)
    public String processCompany(@Valid Company company, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        company.setUser(user);
        company.setNumberOfOffices(0);
        company.setNumberOfWorkers(0);
        company.setProfit((company.getIncome()-company.getInvestment())*-1);
        user.setWorkingStatus("owner");
        companyRepository.save(company);
        return "/company-home";
    }
}
