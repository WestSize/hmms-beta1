package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.services.ApplicationServices.ApplicationService;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.IncomeServices.IncomeService;
import com.example.hmmsbeta1.web.services.SalaryServices.SalaryService;
import com.example.hmmsbeta1.web.services.UserService;
import com.example.hmmsbeta1.web.services.WorkScheduleServices.WorkScheduleService;
import com.example.hmmsbeta1.web.services.WorkerServices.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;


@Controller
public class CompaniesController {

    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private WorkScheduleService workScheduleService;
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private SalaryService salaryService;

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    private Long companyId = null;

    @RequestMapping(value = "/company-home", method = RequestMethod.GET)
    public ModelAndView companyHome(Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("newcompany", new Company());
        model.addAttribute("usercompanies", companyService.showOnlyUsersCompanies(principal.getName()));
        User me = userService.findByEmail(principal.getName());
        model.addAttribute("userInfo", me);
        if (me.getWorkingStatus().equals("worker")) {
            model.addAttribute("workerInfo", workerService.showWorkerByUserId(me.getId()));
            model.addAttribute("myWorkSchedulesCounter", workScheduleService.showLast12WorkSchedulesByUserId(me.getId()).size());
            model.addAttribute("myWorkSchedule", workScheduleService.showLast12WorkSchedulesByUserId(me.getId()));
            Worker worker = workerService.showWorkerByUserId(me.getId());
            model.addAttribute("salariesCounter", salaryService.showLast12WorkerSalariesByWorkerId(worker.getId()).size());
            model.addAttribute("lastSalaries", salaryService.showLast12WorkerSalariesByWorkerId(worker.getId()));
        }
        return new ModelAndView("company-home");
    }

    @RequestMapping(value = "/company-home", method = RequestMethod.POST)
    public String processCompany(@Valid Company company, Principal principal) {
        if(company.getInvestment()<0){
            return "redirect:/company-home?noMinusError";
        }
        companyService.save(company, principal);
        incomeService.save(company);
        Long companyId = company.getId();
        return "redirect:/company-page?id="+companyId;
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.GET)
    public String companyDetails(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        Long ownerId = userService.findByEmail(principal.getName()).getId();
        Company company = companyService.getOne(id);
        if (company.getUser().getId() == ownerId) {
            model.addAttribute("companyInfo", companyService.getOne(id));
            model.addAttribute("companyOwnerId", companyService.getOne(id).getUser().getId());
            model.addAttribute("updateCompany", new Company());
            companyId = id;
            return "/company-page";
        } else if (userService.findByEmail(principal.getName()).getWorkingStatus().equals("owner")) {
            model.addAttribute("companyInfo", companyService.getOne(id));
            return "/company-details-no-application";
        } else if (userService.findByEmail(principal.getName()).getWorkingStatus().equals("worker")) {
            model.addAttribute("companyInfo", companyService.getOne(id));
            return "/company-details-no-application";
        } else {
            User user = userService.findByEmail(principal.getName());
            Application application = null;
            List<Application> allApps = applicationService.findAll();
            for (int i = 0; i < allApps.size(); i++) {
                Application nowApp = allApps.get(i);
                Long compId = nowApp.getCompanyId();
                Long candidateId = nowApp.getUser().getId();
                if (compId.equals(company.getId()) && candidateId.equals(user.getId())) {
                    application = nowApp;
                }
            }
            if (application != null) {
                if (application.getCompanyId().equals(company.getId()) && application.getUser().getId().equals(user.getId())) {
                    model.addAttribute("companyInfo", companyService.getOne(id));
                    companyId = id;
                    return "/company-details-no-application";
                } else {
                    model.addAttribute("companyInfo", companyService.getOne(id));
                    companyId = id;
                    return "/company-details";
                }
            } else {
                model.addAttribute("companyInfo", companyService.getOne(id));
                companyId = id;
                return "/company-details";
            }
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("files") MultipartFile[] files, Company company, Principal principal, Application application) throws IOException {
        applicationService.save(files, company, principal, application);
        return "redirect:/company-list?uploadok";
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.POST)
    public String updateUserCompany(Company company) {
        Company oldCompany = companyService.getOne(companyId);
        if (company.getInvestment()<0){
            return "redirect:/company-home?noMinusError";
        }
        companyService.update(company, companyId);
        companyId=null;
        return "redirect:/company-page?id=" + oldCompany.getId();
    }

    @RequestMapping(value = "/company-list", method = RequestMethod.GET)
    public String companyList(Model model, Principal principal) {
        User me = userService.findByEmail(principal.getName());
        if(me.getWorkingStatus().equals("owner")){
            return "redirect:/company-home?notWorker";
        }
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("companiesCounter", companyService.findAll().size());
        model.addAttribute("allcompanies", companyService.findAll());
        return "/company-list";
    }
}