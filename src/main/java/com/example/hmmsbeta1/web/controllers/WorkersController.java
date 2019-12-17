package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.IncomeRepositories.IncomeRepository;
import com.example.hmmsbeta1.web.repositories.OfficeRepositories.OfficeRepository;
import com.example.hmmsbeta1.web.repositories.OutcomeRepositories.OutcomeRepository;
import com.example.hmmsbeta1.web.repositories.SalaryRepositories.SalaryRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.repositories.WorkScheduleRepositories.WorkScheduleRepository;
import com.example.hmmsbeta1.web.repositories.WorkerRepositories.WorkerRepository;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class WorkersController {

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

    @Autowired
    private OutcomeRepository outcomeRepository;

    @Autowired
    private OfficeRepository officeRepository;

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    private Long companyId = null;
    private Long workerId = null;
    private Long workScheduleId = null;

    @RequestMapping(value = "/workers-list", method = RequestMethod.GET)
    public String workersList(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
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
        } else if (!me.getWorkingStatus().equals("owner")) {
            return "redirect:/company-home?notOwner";
        } else {
            model.addAttribute("userinfo", userRepository.getOne(id));
            model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
            workerId = id;
            return "/approve-user";
        }
    }

    @RequestMapping(value = "/approveToWork", method = RequestMethod.POST)
    public String createNewWorker(Worker worker, Principal principal) {
        User user = userRepository.getOne(workerId);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd'-'yyyy");
        Date date = new Date(System.currentTimeMillis());
        List<Application> workersApplications = applicationRepository.showApplicationByCandidateId(workerId);
        workerId = null;
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
        worker.setOnDuty(false);
        workerRepository.save(worker);
        int companyWorkersCounter = company.getNumberOfWorkers();
        company.setNumberOfWorkers(companyWorkersCounter + 1);
        companyRepository.save(company);
        return "redirect:/company-page?id=" + company.getId();
    }

    @RequestMapping(value = "/fire")
    public String fireWorker(Long id, Principal principal){
        User me = userRepository.findByEmail(principal.getName());
        Company company = companyRepository.showOneUserCompany(principal.getName());
        Worker worker = workerRepository.getOne(id);
        if(!me.getWorkingStatus().equals("owner")){
            return "redirect:/comapny-home?notOwner";
        } else if (!worker.getCompany().equals(company)){
            return "redirect:/company-home?notYourCompany";
        }
        User workerUser = worker.getUser();
        List<Salary> allWorkerSalaries = salaryRepository.showWorkerSalariesByWorkerId(worker.getId());
        if (!allWorkerSalaries.isEmpty()) {
            for (Salary salary : allWorkerSalaries) {
                salaryRepository.deleteById(salary.getId());
            }
        }
        List<WorkSchedule> workerSchedules = workScheduleRepository.showAllWorkScheduleByUserId(workerUser.getId());
        if(!workerSchedules.isEmpty()) {
            for (WorkSchedule workerSchedule : workerSchedules) {
                workScheduleRepository.deleteById(workerSchedule.getId());
            }
        }
        workerRepository.deleteById(id);
        workerUser.setWorkingStatus("guest");
        userRepository.save(workerUser);
        int companyNowWorkers = company.getNumberOfWorkers();
        company.setNumberOfWorkers(companyNowWorkers-1);
        companyRepository.save(company);
        return "redirect:/company-page?id="+company.getId()+"&workerDeleted";
    }
}
