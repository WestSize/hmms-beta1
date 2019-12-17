package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Office;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.OfficeRepositories.OfficeRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Controller
public class OfficesRepository {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @RequestMapping(value = "/offices", method = RequestMethod.GET)
    public String showOffices(Principal principal, Model model, Long id){
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        User me = userRepository.findByEmail(principal.getName());
        Company company = companyRepository.getOne(id);
        if(!me.getWorkingStatus().equals("owner")){
            return "redirect:/company-home?notOwner";
        } else if (!me.equals(company.getUser())){
            return "redirect:/company-home?notYourCompany2";
        }
        List<Office> companyOffices = officeRepository.showOfficesByCompanyId(id);
        model.addAttribute("companyOfficesCounter", companyOffices.size());
        model.addAttribute("comapnyOffices", companyOffices);
        model.addAttribute("companyInfo", company);
        model.addAttribute("me", me);
        return "offices";
    }
}
