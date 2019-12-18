package com.example.hmmsbeta1.web.services.WorkerServices;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.repositories.WorkerRepositories.WorkerRepository;
import com.example.hmmsbeta1.web.services.SalaryServices.SalaryService;
import com.example.hmmsbeta1.web.services.WorkScheduleServices.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class WorkerServiceImpl implements WorkerService {
    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private WorkScheduleService workScheduleService;

    @Override
    public List<Worker> showWorkersByCompanyId(Long id) {
        List<Worker> workers = workerRepository.showWorkersByCompanyId(id);
        return workers;
    }

    @Override
    public Worker showWorkerByCompanyIdAndUserId(Long companyId, Long userId) {
        Worker worker = workerRepository.showWorkerByCompanyIdAndUserId(companyId, userId);
        return worker;
    }

    @Override
    public Worker showWorkerByUserId(Long id) {
        Worker worker = workerRepository.showWorkerByUserId(id);
        return worker;
    }

    @Override
    public void save(Worker worker, Company company, User user) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd'-'yyyy");
        Date date = new Date(System.currentTimeMillis());
        worker.setCompany(company);
        worker.setUser(user);
        worker.setDateOfAppointment(formatter.format(date));
        worker.setOnDuty(false);
        workerRepository.save(worker);
    }

    @Override
    public Worker getOne(Long id) {
        Worker worker = workerRepository.getOne(id);
        return worker;
    }

    @Override
    public List<Worker> findAll() {
        List<Worker> workers = workerRepository.findAll();
        return workers;
    }

    @Override
    public void deleteById(Long id) {
        workerRepository.deleteById(id);
    }

    @Override
    public void updateWorkerMonthWorkedDays(Worker worker, int days) {
        worker.setMonthWorkedDays(days);
        workerRepository.save(worker);
    }

    @Override
    public void fire(Worker worker, User user, Long id) {
        List<Salary> allWorkerSalaries = salaryService.showWorkerSalariesByWorkerId(worker.getId());
        if (!allWorkerSalaries.isEmpty()) {
            for (Salary salary : allWorkerSalaries) {
                salaryService.deleteById(salary.getId());
            }
        }
        List<WorkSchedule> workerSchedules = workScheduleService.showAllWorkScheduleByUserId(user.getId());
        if(!workerSchedules.isEmpty()) {
            for (WorkSchedule workerSchedule : workerSchedules) {
                workScheduleService.deleteById(workerSchedule.getId());
            }
        }
        workerRepository.deleteById(id);
    }

    @Override
    public void updateFromUserPage(Worker worker, Worker workerOld) {
        workerOld.setPosition(worker.getPosition());
        workerOld.setSalary(worker.getSalary());
        workerRepository.save(workerOld);
    }

    @Override
    public void addDayOff(Worker worker, Worker workerNew) {
        int nowWorkedDays = worker.getMonthWorkedDays();
        worker.setMonthWorkedDays(nowWorkedDays+workerNew.getMonthWorkedDays());
        workerRepository.save(worker);
    }
}
