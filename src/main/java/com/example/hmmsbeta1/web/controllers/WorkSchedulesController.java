package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.entities.WorkSchedule;
import com.example.hmmsbeta1.web.entities.Worker;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import com.example.hmmsbeta1.web.services.WorkScheduleServices.WorkScheduleService;
import com.example.hmmsbeta1.web.services.WorkerServices.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WorkSchedulesController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private WorkScheduleService workScheduleService;

    private Long workScheduleId = null;

    @RequestMapping(value = "/company-workschedule", method = RequestMethod.GET)
    public String workSchedule(Model model, Principal principal, Long id, @RequestParam("workScheduleDay") String searchingDay) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        User user = userService.findByEmail(principal.getName());
        Company company = companyService.showOneUserCompany(principal.getName());
        if (!user.getWorkingStatus().equals("owner")) {
            return "/company-home?notOwner";
        } else if (!company.getUser().getId().equals(user.getId())) {
            return "/company-home?notOwner";
        } else {
            model.addAttribute("companysWorkSchedulesCounter", workScheduleService.showWorkScheduleOfCompanyByDateAndCompanyId(searchingDay, company.getId()).size());
            model.addAttribute("companysWorkSchedules", workScheduleService.showWorkScheduleOfCompanyByDateAndCompanyId(searchingDay, company.getId()));
            return "/company-workschedule";
        }
    }

    @RequestMapping(value = "/create-work-schedule", method = RequestMethod.GET)
    public String workScheduleCreate(Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        User me = userService.findByEmail(principal.getName());
        if (!me.getWorkingStatus().equals("owner")) {
            return "redirect:/company-home?notOwner";
        } else {
            return "/create-work-schedule";
        }
    }

    @RequestMapping(value = "/create-work-schedule", method = RequestMethod.POST)
    public String workSchedulePost(@RequestParam("workersList") String workers, @RequestParam("nowDate") String nowDate, WorkSchedule workScheduleNew, Principal principal) {

        return workScheduleService.createWorkSchedule(workers, nowDate, workScheduleNew, principal);
    }

    @RequestMapping(value = "/work-schedule-report", method = RequestMethod.GET)
    public String workReport(Long id, Principal principal, Model model) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        User user = userService.findByEmail(principal.getName());
        Worker worker = workerService.showWorkerByUserId(user.getId());
        WorkSchedule workSchedule = workScheduleService.getOne(id);
        if (workSchedule == null) {
            return "redirect:/company-home?notYourWorkSchedule";
        } else if (workSchedule.isWorkerDone()) {
            return "redirect:/company-home?cantEditReport";
        } else if (!user.getWorkingStatus().equals("worker")) {
            return "redirect:/company-home?notWorker";
        } else if (!workSchedule.getWorker().getId().equals(worker.getId())) {
            return "redirect:/company-home?notYourWorkSchedule";
        }
        model.addAttribute("workScheduleInfo", workSchedule);
        model.addAttribute("workScheduleFull", new WorkSchedule());
        workScheduleId = id;
        return "work-schedule-report";
    }

    @RequestMapping(value = "/work-schedule-report-post", method = RequestMethod.POST)
    public String workReportPost(@Valid WorkSchedule workScheduleNew) {
        WorkSchedule workScheduleOld = workScheduleService.getOne(workScheduleId);
        workScheduleId = null;

        return workScheduleService.workReportPost(workScheduleNew, workScheduleOld);
    }

    @RequestMapping(value = "/report-view", method = RequestMethod.GET)
    public String workReportView(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        WorkSchedule workSchedule = workScheduleService.getOne(id);
        User me = userService.findByEmail(principal.getName());
        if(!me.getWorkingStatus().equals("owner")) {
            Worker worker = workerService.showWorkerByUserId(me.getId());
            if (workSchedule.getWorker().equals(worker)) {
                model.addAttribute("workScheduleInfo", workSchedule);
                return "work-schedule-report-view";
            } else {
                return "redirect:/company-home?notYourWorkSchedule";
            }
        }
        model.addAttribute("workScheduleInfo", workSchedule);
        return "work-schedule-report-view";
    }

    @RequestMapping(value = "/view-logs", method = RequestMethod.GET)
    public String viewUserLogs(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        Worker worker = workerService.showWorkerByUserId(id);
        User me = userService.findByEmail(principal.getName());
        if(!me.getWorkingStatus().equals("owner")){
            return "redirect:/company-home?notOwner";
        } else {
            Company company = companyService.showOneUserCompany(principal.getName());
            if(!worker.getCompany().equals(company)){
                return "redirect:/company-home?param.notYourCompany2";
            }
        }
        List<WorkSchedule> workSchedules = workScheduleService.showAllWorkScheduleByUserId(id);
        model.addAttribute("userLogs", workSchedules);
        List<WorkSchedule> endedWorks = new ArrayList<>();
        List<WorkSchedule> notEndedWorks = new ArrayList<>();
        for (WorkSchedule schedule : workSchedules) {
            if (schedule.isWorkerDone()) {
                endedWorks.add(schedule);
            } else {
                notEndedWorks.add(schedule);
            }
        }
        model.addAttribute("userDetails", userService.getOne(id));
        model.addAttribute("userLogsCounter", workSchedules.size());
        model.addAttribute("endedWorks", endedWorks.size());
        model.addAttribute("notEndedWorks", notEndedWorks.size());
        model.addAttribute("monthWorkedDays", worker.getMonthWorkedDays());
        return "view-logs";
    }

    @RequestMapping(value = "/delete-workschedule")
    public String deleteWorkSchedule(Long id) {
        workScheduleService.deleteById(id);
        return "redirect:/company-home?deleteok";
    }
}
