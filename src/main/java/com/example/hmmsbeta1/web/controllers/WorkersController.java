package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.repositories.SalaryRepositories.SalaryRepository;
import com.example.hmmsbeta1.web.services.ApplicationServices.ApplicationService;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import com.example.hmmsbeta1.web.services.WorkScheduleServices.WorkScheduleService;
import com.example.hmmsbeta1.web.services.WorkerServices.WorkerService;
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
    private SalaryRepository salaryRepository;

    private Long workerId = null;

    @RequestMapping(value = "/workers-list", method = RequestMethod.GET)
    public String workersList(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        Company company = companyService.showOneUserCompany(principal.getName());
        model.addAttribute("applicationsCounter", applicationService.showOnlyUsersCompanyApplications(id).size());
        model.addAttribute("invitedUsersCounter", applicationService.showOnlyUsersCompanyApplicationsInvited(id).size());
        model.addAttribute("myapplications", applicationService.showOnlyUsersCompanyApplications(id));
        model.addAttribute("invitedUsers", applicationService.showOnlyUsersCompanyApplicationsInvited(id));
        model.addAttribute("userscompany", companyService.showOneUserCompany(principal.getName()));
        model.addAttribute("workersCounter", workerService.showWorkersByCompanyId(company.getId()).size());
        model.addAttribute("companyWorkers", workerService.showWorkersByCompanyId(company.getId()));
        return "/workers-list";
    }

    @RequestMapping(value = "/workers-list", method = RequestMethod.POST)
    public String updateCircularLetter(Principal principal, Company company) {
        Company oldCompany = companyService.showOneUserCompany(principal.getName());
        oldCompany.setCircularLetter(company.getCircularLetter());
        Long redirectId = oldCompany.getId();
        companyService.save(oldCompany);
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
        applicationService.deleteById(id);
        Long redirectId = companyService.showOneUserCompany(principal.getName()).getId();
        return "redirect:/workers-list?id=" + redirectId;
    }

    @RequestMapping(value = "/approveToWork")
    public String approveToWork(Long id, Model model, Principal principal) {
        User user = userService.getOne(id);
        User me = userService.findByEmail(principal.getName());
        if (user.getWorkingStatus().equals("worker")) {
            return "redirect:/company-home?alreadyWorker";
        } else if (!me.getWorkingStatus().equals("owner")) {
            return "redirect:/company-home?notOwner";
        } else {
            model.addAttribute("userinfo", userService.getOne(id));
            model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
            workerId = id;
            return "/approve-user";
        }
    }

    @RequestMapping(value = "/approveToWork", method = RequestMethod.POST)
    public String createNewWorker(Worker worker, Principal principal) {
        User user = userService.getOne(workerId);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd'-'yyyy");
        Date date = new Date(System.currentTimeMillis());
        List<Application> workersApplications = applicationService.showApplicationByCandidateId(workerId);
        workerId = null;
        for (int i = 0; i < workersApplications.size(); i++) {
            Application application = workersApplications.get(i);
            applicationService.deleteById(application.getId());
        }
        user.setWorkingStatus("worker");
        userService.update(user);
        Company company = companyService.showOneUserCompany(principal.getName());
        worker.setCompany(company);
        worker.setUser(user);
        worker.setDateOfAppointment(formatter.format(date));
        worker.setOnDuty(false);
        workerService.save(worker);
        int companyWorkersCounter = company.getNumberOfWorkers();
        company.setNumberOfWorkers(companyWorkersCounter + 1);
        companyService.save(company);
        return "redirect:/company-page?id=" + company.getId();
    }

    @RequestMapping(value = "/fire")
    public String fireWorker(Long id, Principal principal){
        User me = userService.findByEmail(principal.getName());
        Company company = companyService.showOneUserCompany(principal.getName());
        Worker worker = workerService.getOne(id);
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
        List<WorkSchedule> workerSchedules = workScheduleService.showAllWorkScheduleByUserId(workerUser.getId());
        if(!workerSchedules.isEmpty()) {
            for (WorkSchedule workerSchedule : workerSchedules) {
                workScheduleService.deleteById(workerSchedule.getId());
            }
        }
        workerService.deleteById(id);
        workerUser.setWorkingStatus("guest");
        userService.update(workerUser);
        int companyNowWorkers = company.getNumberOfWorkers();
        company.setNumberOfWorkers(companyNowWorkers-1);
        companyService.save(company);
        return "redirect:/company-page?id="+company.getId()+"&workerDeleted";
    }
}
