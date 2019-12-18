package com.example.hmmsbeta1.web.services.WorkScheduleServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.entities.WorkSchedule;
import com.example.hmmsbeta1.web.entities.Worker;
import com.example.hmmsbeta1.web.repositories.WorkScheduleRepositories.WorkScheduleRepository;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import com.example.hmmsbeta1.web.services.WorkerServices.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {
    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private WorkScheduleService workScheduleService;

    @Override
    public List<WorkSchedule> showWorkScheduleByCompanyId(Long id) {
        List<WorkSchedule> workSchedules = workScheduleRepository.showWorkScheduleByCompanyId(id);
        return workSchedules;
    }

    @Override
    public List<WorkSchedule> showWorkScheduleOfCompanyByDateAndCompanyId(String date, Long Id) {
        List<WorkSchedule> workSchedules = workScheduleRepository.showWorkScheduleOfCompanyByDateAndCompanyId(date, Id);
        return workSchedules;
    }

    @Override
    public List<WorkSchedule> showLast12WorkSchedulesByUserId(Long id) {
        List<WorkSchedule> workSchedules = workScheduleRepository.showLast12WorkSchedulesByUserId(id);
        return workSchedules;
    }

    @Override
    public List<WorkSchedule> showAllWorkScheduleByUserId(Long id) {
        List<WorkSchedule> workSchedules = workScheduleRepository.showAllWorkScheduleByUserId(id);
        return workSchedules;
    }

    @Override
    public void save(WorkSchedule workSchedule) {
        workScheduleRepository.save(workSchedule);
    }

    @Override
    public WorkSchedule getOne(Long id) {
        WorkSchedule workSchedule = workScheduleRepository.getOne(id);
        return workSchedule;
    }

    @Override
    public List<WorkSchedule> findAll() {
        List<WorkSchedule> workSchedules = workScheduleRepository.findAll();
        return workSchedules;
    }

    @Override
    public void deleteById(Long id) {
        workScheduleRepository.deleteById(id);
    }

    @Override
    public String createWorkSchedule(String workers, String nowDate, WorkSchedule workScheduleNew, Principal principal) {
        String[] workersArray = workers.split(",\\s+");
        Company company = companyService.showOneUserCompany(principal.getName());
        boolean notConfirmedUser = false;
        int confirmedUsersCounter = 0;
        for (int i = 0; i < workersArray.length; i++) {
            boolean nowUserNotConfirmed = false;
            WorkSchedule workSchedule = new WorkSchedule();
            User user = userService.findByEmail(workersArray[i]);
            if (user == null || user == userService.findByEmail(principal.getName())) {
//                return "redirect:/create-work-schedule?noSuchUser";
                notConfirmedUser=true;
                nowUserNotConfirmed=true;
            }
            if(!nowUserNotConfirmed) {
                Worker worker = null;
                List<Worker> allWorkers = workerService.findAll();
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
                    workScheduleService.save(workSchedule);
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

    @Override
    public String workReportPost(WorkSchedule workScheduleNew, WorkSchedule workScheduleOld) {
        workScheduleOld.setWorkerDone(true);
        workScheduleOld.setEndDateAndTime(workScheduleNew.getEndDateAndTime());
        workScheduleOld.setWorkerReport(workScheduleNew.getWorkerReport());
        int oldWorkedDays = workScheduleOld.getWorker().getMonthWorkedDays();
        workScheduleOld.getWorker().setMonthWorkedDays(oldWorkedDays + 1);
        workScheduleService.save(workScheduleOld);
        return "redirect:/company-home?reportOk";
    }
}
