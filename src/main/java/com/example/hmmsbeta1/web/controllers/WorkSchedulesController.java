package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.entities.WorkSchedule;
import com.example.hmmsbeta1.web.entities.Worker;
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
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    private Long workScheduleId = null;

    @RequestMapping(value = "/company-workschedule", method = RequestMethod.GET)
    public String workSchedule(Model model, Principal principal, Long id, @RequestParam("workScheduleDay") String searchingDay) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        User user = userRepository.findByEmail(principal.getName());
        Company company = companyRepository.showOneUserCompany(principal.getName());
        if (!user.getWorkingStatus().equals("owner")) {
            return "/company-home?notOwner";
        } else if (!company.getUser().getId().equals(user.getId())) {
            return "/company-home?notOwner";
        } else {
            model.addAttribute("companysWorkSchedulesCounter", workScheduleRepository.showWorkScheduleOfCompanyByDateAndCompanyId(searchingDay, company.getId()).size());
            model.addAttribute("companysWorkSchedules", workScheduleRepository.showWorkScheduleOfCompanyByDateAndCompanyId(searchingDay, company.getId()));
            return "/company-workschedule";
        }
    }

    @RequestMapping(value = "/create-work-schedule", method = RequestMethod.GET)
    public String workScheduleCreate(Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        User me = userRepository.findByEmail(principal.getName());
        if (!me.getWorkingStatus().equals("owner")) {
            return "redirect:/company-home?notOwner";
        } else {
            return "/create-work-schedule";
        }
    }

    @RequestMapping(value = "/create-work-schedule", method = RequestMethod.POST)
    public String workSchedulePost(@RequestParam("workersList") String workers, @RequestParam("nowDate") String nowDate, WorkSchedule workScheduleNew, Principal principal) {
        String[] workersArray = workers.split(",\\s+");
        Company company = companyRepository.showOneUserCompany(principal.getName());
        boolean notConfirmedUser = false;
        int confirmedUsersCounter = 0;
        for (int i = 0; i < workersArray.length; i++) {
            boolean nowUserNotConfirmed = false;
            WorkSchedule workSchedule = new WorkSchedule();
            User user = userRepository.findByEmail(workersArray[i]);
            if (user == null || user == userRepository.findByEmail(principal.getName())) {
//                return "redirect:/create-work-schedule?noSuchUser";
                notConfirmedUser=true;
                nowUserNotConfirmed=true;
            }
            if(!nowUserNotConfirmed) {
                Worker worker = null;
                List<Worker> allWorkers = workerRepository.findAll();
                for (int j = 0; j < allWorkers.size(); j++) {
                    Worker nowWorker = allWorkers.get(j);
                    Long nowCompany = nowWorker.getCompany().getId();
                    Long nowUser = nowWorker.getUser().getId();
                    if (nowCompany.equals(company.getId()) && nowUser.equals(user.getId())) {
                        worker = nowWorker;
                    }
                }
                if (worker == null) {
                    notConfirmedUser = true;
                    nowUserNotConfirmed = true;
                } else if (!worker.getCompany().getId().equals(company.getId())) {
                    notConfirmedUser = true;
                    nowUserNotConfirmed = true;
                }
                if (!nowUserNotConfirmed) {
                    confirmedUsersCounter++;
                    String[] workScheduleDateSplittedArray = workScheduleNew.getDate().split("-");
                    String workScheduleDateSplittedString = null;
                    for (int j = 0; j < workScheduleDateSplittedArray.length; j++) {
                        if (j == 0) {
                            workScheduleDateSplittedString = workScheduleDateSplittedArray[j];
                        } else {
                            workScheduleDateSplittedString += workScheduleDateSplittedArray[j];
                        }
                    }
                    String[] nowDateSplittedArray = nowDate.split("-");
                    String nowDateSplittedString = null;
                    for (int k = 0; k < nowDateSplittedArray.length; k++) {
                        if (k == 0) {
                            nowDateSplittedString = nowDateSplittedArray[k];
                        } else {
                            nowDateSplittedString += nowDateSplittedArray[k];
                        }
                    }
                    int workScheduleDate = Integer.parseInt(workScheduleDateSplittedString);
                    int nowDateInt = Integer.parseInt(nowDateSplittedString);
                    if (workScheduleDate < nowDateInt) {
                        return "redirect:/create-work-schedule?noBackDate";
                    }
                    workSchedule.setDate(workScheduleNew.getDate());
                    workSchedule.setWorker(worker);
                    workSchedule.setWorkerDone(false);
                    workSchedule.setCompany(company);
                    workScheduleRepository.save(workSchedule);
                } else {
                    nowUserNotConfirmed = false;
                }
            }
        }
        if (notConfirmedUser) {
            if(confirmedUsersCounter==0) {
                return "redirect:/create-work-schedule?noSuchUser";
            } else {
                return "redirect:/create-work-schedule?missingWorkers";
            }
        }
        return "redirect:/company-page?id=" + company.getId()+"&workScheduleOk";
    }

    @RequestMapping(value = "/work-schedule-report", method = RequestMethod.GET)
    public String workReport(Long id, Principal principal, Model model) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        User user = userRepository.findByEmail(principal.getName());
        Worker worker = workerRepository.showWorkerByUserId(user.getId());
        WorkSchedule workSchedule = workScheduleRepository.getOne(id);
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
        WorkSchedule workScheduleOld = workScheduleRepository.getOne(workScheduleId);
        workScheduleId = null;
        workScheduleOld.setWorkerDone(true);
        workScheduleOld.setEndDateAndTime(workScheduleNew.getEndDateAndTime());
        workScheduleOld.setWorkerReport(workScheduleNew.getWorkerReport());
        int oldWorkedDays = workScheduleOld.getWorker().getMonthWorkedDays();
        workScheduleOld.getWorker().setMonthWorkedDays(oldWorkedDays + 1);
        workScheduleRepository.save(workScheduleOld);
        return "redirect:/company-home?reportOk";
    }

    @RequestMapping(value = "/report-view", method = RequestMethod.GET)
    public String workReportView(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        WorkSchedule workSchedule = workScheduleRepository.getOne(id);
        User me = userRepository.findByEmail(principal.getName());
        if(!me.getWorkingStatus().equals("owner")) {
            Worker worker = workerRepository.showWorkerByUserId(me.getId());
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
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        Worker worker = workerRepository.showWorkerByUserId(id);
        User me = userRepository.findByEmail(principal.getName());
        if(!me.getWorkingStatus().equals("owner")){
            return "redirect:/company-home?notOwner";
        } else {
            Company company = companyRepository.showOneUserCompany(principal.getName());
            if(!worker.getCompany().equals(company)){
                return "redirect:/company-home?param.notYourCompany2";
            }
        }
        List<WorkSchedule> workSchedules = workScheduleRepository.showAllWorkScheduleByUserId(id);
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
        model.addAttribute("userDetails", userRepository.getOne(id));
        model.addAttribute("userLogsCounter", workSchedules.size());
        model.addAttribute("endedWorks", endedWorks.size());
        model.addAttribute("notEndedWorks", notEndedWorks.size());
        model.addAttribute("monthWorkedDays", worker.getMonthWorkedDays());
        return "view-logs";
    }

    @RequestMapping(value = "/delete-workschedule")
    public String deleteWorkSchedule(Long id) {
        workScheduleRepository.deleteById(id);
        return "redirect:/company-home?deleteok";
    }
}
