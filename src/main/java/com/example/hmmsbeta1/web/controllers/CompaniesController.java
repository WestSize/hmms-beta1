package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.IncomeRepositories.IncomeRepository;
import com.example.hmmsbeta1.web.repositories.SalaryRepositories.SalaryRepository;
import com.example.hmmsbeta1.web.repositories.WorkScheduleRepositories.WorkScheduleRepository;
import com.example.hmmsbeta1.web.repositories.WorkerRepositories.WorkerRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
public class CompaniesController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    private Long companyId = null;

    @RequestMapping(value = "/company-home", method = RequestMethod.GET)
    public ModelAndView companyHome(Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("newcompany", new Company());
        model.addAttribute("usercompanies", companyRepository.showOnlyUsersCompanies(principal.getName()));
        User me = userRepository.findByEmail(principal.getName());
        model.addAttribute("userInfo", me);
        if (me.getWorkingStatus().equals("worker")) {
            model.addAttribute("workerInfo", workerRepository.showWorkerByUserId(me.getId()));
            model.addAttribute("myWorkSchedulesCounter", workScheduleRepository.showLast12WorkSchedulesByUserId(me.getId()).size());
            model.addAttribute("myWorkSchedule", workScheduleRepository.showLast12WorkSchedulesByUserId(me.getId()));
            Worker worker = workerRepository.showWorkerByUserId(me.getId());
            model.addAttribute("salariesCounter", salaryRepository.showLast12WorkerSalariesByWorkerId(worker.getId()).size());
            model.addAttribute("lastSalaries", salaryRepository.showLast12WorkerSalariesByWorkerId(worker.getId()));
        }
        return new ModelAndView("company-home");
    }

    @RequestMapping(value = "/company-home", method = RequestMethod.POST)
    public String processCompany(@Valid Company company, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        if(company.getInvestment()<0){
            return "redirect:/company-home?noMinusError";
        }
        company.setUser(user);
        company.setNumberOfOffices(0);
        company.setNumberOfWorkers(0);
        company.setPaydayDate(formatter.format(date));
        company.setProfit((company.getIncome() - company.getInvestment()));
        company.setCircularLetter("Здравейте! Поканен сте на интервю за работата, която сте кандидатствали" +
                " във фирмата '" + company.getName() + "'. Моля напишете ни удобен ден и час за среща в наш офис или " +
                "се свържете с нас на тел: (не е зададен). Поздрави, " + user.getFirstName() + " " + user.getLastName() + " (управител). :)");
        company.setOutcome(0);
        user.setWorkingStatus("owner");
        companyRepository.save(company);
        Income income = new Income();
        income.setCompany(company);
        income.setIncomeDate(formatter.format(date));
        income.setIncomeDescription("Първоначално въведена инвестиция, при създаване на фирмата.");
        int incomeSum = company.getIncome();
        income.setIncomePrice(incomeSum);
        incomeRepository.save(income);
        Long companyId = company.getId();
        return "redirect:/company-page?id="+companyId;
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.GET)
    public String companyDetails(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        Long ownerId = userRepository.findByEmail(principal.getName()).getId();
        Company company = companyRepository.getOne(id);
        if (company.getUser().getId() == ownerId) {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            model.addAttribute("companyOwnerId", companyRepository.getOne(id).getUser().getId());
            model.addAttribute("updateCompany", new Company());
            companyId = id;
            return "/company-page";
        } else if (userRepository.findByEmail(principal.getName()).getWorkingStatus().equals("owner")) {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            return "/company-details-no-application";
        } else if (userRepository.findByEmail(principal.getName()).getWorkingStatus().equals("worker")) {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            return "/company-details-no-application";
        } else {
            User user = userRepository.findByEmail(principal.getName());
            Application application = null;
            List<Application> allApps = applicationRepository.findAll();
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
                    model.addAttribute("companyInfo", companyRepository.getOne(id));
                    companyId = id;
                    return "/company-details-no-application";
                } else {
                    model.addAttribute("companyInfo", companyRepository.getOne(id));
                    companyId = id;
                    return "/company-details";
                }
            } else {
                model.addAttribute("companyInfo", companyRepository.getOne(id));
                companyId = id;
                return "/company-details";
            }
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(Model model, @RequestParam("files") MultipartFile[] files, Company company, Principal principal, Application application) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        for (MultipartFile file : files) {
            Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename());
            Files.write(fileNameAndPath, file.getBytes());
        }
        application.setApproved(false);
        application.setInvited(false);
        application.setCompanyId(companyId);
        application.setCvPath(fileNames + "");
        User user = userRepository.findByEmail(principal.getName());
        application.setUser(user);
        applicationRepository.save(application);
        return "redirect:/company-list?uploadok";
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.POST)
    public String updateUserCompany(Model model, Company company, Principal principal) {
        Company oldCompany = companyRepository.getOne(companyId);
        companyId = null;
        oldCompany.setName(company.getName());
        oldCompany.setDescription(company.getDescription());
        oldCompany.setPhoneNumber(company.getPhoneNumber());
        oldCompany.setPaydayDate(company.getPaydayDate());
        if (company.getInvestment()<0){
            return "redirect:/company-home?noMinusError";
        }
        oldCompany.setInvestment(company.getInvestment());
        companyRepository.save(oldCompany);
        return "redirect:/company-page?id=" + oldCompany.getId();
    }

    @RequestMapping(value = "/company-list", method = RequestMethod.GET)
    public String companyList(Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("allcompanies", companyRepository.findAll());
        return "/company-list";
    }
}
