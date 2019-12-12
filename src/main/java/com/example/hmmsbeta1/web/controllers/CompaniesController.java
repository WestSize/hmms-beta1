package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.WorkerRepository;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.MessageRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.services.PrivateMessagesService;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
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
    private PrivateMessagesService privateMessagesService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private WorkerRepository workerRepository;

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    private Long companyId = null;
    private Long workerId = null;

    @RequestMapping(value = "/company-home", method = RequestMethod.GET)
    public ModelAndView companyHome(Model model, Principal principal) {
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
        company.setProfit((company.getIncome() - company.getInvestment()));
        company.setCircularLetter("Здравейте! Поканен сте на интервю за работата, която сте кандидатствали" +
                " във фирмата '" + company.getName() + "'. Моля напишете ни удобен ден и час за среща в наш офис или " +
                "се свържете с нас на тел: (не е зададен). Поздрави, " + user.getFirstName() + " " + user.getLastName() + " (управител). :)");
        user.setWorkingStatus("owner");
        companyRepository.save(company);
        return "/company-home";
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.GET)
    public String companyDetails(long id, Model model, Principal principal) {
        Long ownerId = userRepository.findByEmail(principal.getName()).getId();
        Company company = companyRepository.getOne(id);
        if (company.getUser().getId() == ownerId) {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            model.addAttribute("companyOwnerId", companyRepository.getOne(id).getUser().getId());
            model.addAttribute("updateCompany", new Company());
            companyId = id;
            return "/company-page";
        } else if (userRepository.findByEmail(principal.getName()).getWorkingStatus().equals("owner")){
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            return "/company-details-no-application";
        } else if (userRepository.findByEmail(principal.getName()).getWorkingStatus().equals("worker")){
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
        companyRepository.save(oldCompany);
        return "redirect:/company-page?id=" + oldCompany.getId();
    }

    @RequestMapping(value = "/company-list", method = RequestMethod.GET)
    public String companyList(Model model) {
        model.addAttribute("allcompanies", companyRepository.findAll());
        return "/company-list";
    }

    @RequestMapping(value = "/workers-list", method = RequestMethod.GET)
    public String workersList(long id, Model model, Principal principal) {
        Company company = companyRepository.showOneUserCompany(principal.getName());
        model.addAttribute("applicationsCounter", applicationRepository.showOnlyUsersCompanyApplications(id).size());
        model.addAttribute("invitedUsersCounter", applicationRepository.showOnlyUsersCompanyApplicationsInvited(id).size());
        model.addAttribute("myapplications", applicationRepository.showOnlyUsersCompanyApplications(id));
        model.addAttribute("invitedUsers", applicationRepository.showOnlyUsersCompanyApplicationsInvited(id));
        model.addAttribute("userscompany", companyRepository.showOneUserCompany(principal.getName()));
        model.addAttribute("workersCounter", workerRepository.showWorkersByCompanyId(company.getId()).size());
        model.addAttribute("companyWorkers", workerRepository.showWorkersByCompanyId(company.getId()));
        return "/workers-list";
    }

    @RequestMapping(value = "/workers-list", method = RequestMethod.POST)
    public String updateCircularLetter(Principal principal, Company company) {
        Company oldCompany = companyRepository.showOneUserCompany(principal.getName());
        oldCompany.setCircularLetter(company.getCircularLetter());
        Long redirectId = oldCompany.getId();
        companyRepository.save(oldCompany);
        return "redirect:/workers-list?id=" + redirectId;
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {
        File file = new File("uploads/" + param);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + param);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @RequestMapping(value = "/deleteApplication")
    public String deleteAppplication(long id, Principal principal) {
        applicationRepository.deleteById(id);
        Long redirectId = companyRepository.showOneUserCompany(principal.getName()).getId();
        return "redirect:/workers-list?id=" + redirectId;
    }

    @RequestMapping(value = "/approveToWork")
    public String approveToWork(Long id, Model model, Principal principal) {
        User user = userRepository.getOne(id);
        User me = userRepository.findByEmail(principal.getName());
        if (user.getWorkingStatus().equals("worker")) {
            return "redirect:/company-home?alreadyWorker";
        } else if(!me.getWorkingStatus().equals("owner")){
            return "redirect:/company-home?notOwner";
        } else {
            model.addAttribute("userinfo", userRepository.getOne(id));
            workerId = id;
            return "/approve-user";
        }
    }

    @RequestMapping(value = "/approveToWork", method = RequestMethod.POST)
    public String createNewWorker(Worker worker, Principal principal) {
        User user = userRepository.getOne(workerId);
        workerId = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy'");
        Date date = new Date(System.currentTimeMillis());
        List<Application> workersApplications = applicationRepository.showApplicationByCandidateId(workerId);
        for (int i = 0; i < workersApplications.size(); i++) {
            Application application = workersApplications.get(i);
            applicationRepository.deleteById(application.getId());
        }
        user.setWorkingStatus("worker");
        userRepository.save(user);
        Company company = companyRepository.showOneUserCompany(principal.getName());
        worker.setCompany(company);
        worker.setUser(user);
        worker.setDateOfAppointment(formatter.format(date));
        workerRepository.save(worker);
        int companyWorkersCounter = company.getNumberOfWorkers();
        company.setNumberOfWorkers(companyWorkersCounter + 1);
        companyRepository.save(company);
        return "redirect:/company-page?id=" + company.getId();
    }
}
