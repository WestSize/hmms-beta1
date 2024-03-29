package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Office;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.OfficeServices.OfficeService;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Controller
public class OfficesController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private OfficeService officeService;

    @RequestMapping(value = "/offices", method = RequestMethod.GET)
    public String showOffices(Principal principal, Model model, Long id){
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        User me = userService.findByEmail(principal.getName());
        Company company = companyService.getOne(id);
        if(!me.getWorkingStatus().equals("owner")){
            return "redirect:/company-home?notOwner";
        } else if (!me.equals(company.getUser())){
            return "redirect:/company-home?notYourCompany2";
        }
        List companyOffices = officeService.showOfficesByCompanyId(id);
        if(companyOffices != null) {
            model.addAttribute("companyOfficesCounter", companyOffices.size());
            model.addAttribute("comapnyOffices", companyOffices);
        } else {
            model.addAttribute("companyOfficesCounter", "0");
        }
        model.addAttribute("companyInfo", company);
        model.addAttribute("me", me);
        return "offices";
    }
}
