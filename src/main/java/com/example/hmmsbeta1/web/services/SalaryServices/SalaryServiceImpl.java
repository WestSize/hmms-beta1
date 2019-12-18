package com.example.hmmsbeta1.web.services.SalaryServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Salary;
import com.example.hmmsbeta1.web.entities.Worker;
import com.example.hmmsbeta1.web.repositories.SalaryRepositories.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {
    @Autowired
    private SalaryRepository salaryRepository;

    @Override
    public List<Salary> showWorkerSalariesByWorkerId(Long id) {
        List<Salary> salaries = salaryRepository.showWorkerSalariesByWorkerId(id);
        if(salaries == null){
            return null;
        } else {
            return salaries;
        }
    }

    @Override
    public List<Salary> showLast12WorkerSalariesByWorkerId(Long id) {
        List<Salary> salaries = salaryRepository.showLast12WorkerSalariesByWorkerId(id);
        if(salaries == null){
            return null;
        } else {
            return salaries;
        }
    }

    @Override
    public void save(Company company, Worker worker, int year, int month) {
        Salary salary = new Salary();
        salary.setDate(company.getPaydayDate());
        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();
        int workerPaycheckPerDay = worker.getSalary()/daysInMonth;
        salary.setPaycheckPerDay(workerPaycheckPerDay);
        int workerPaycheckFull = worker.getMonthWorkedDays()*workerPaycheckPerDay;
        salary.setSalarySum(workerPaycheckFull);
        salary.setWorkedDays(worker.getMonthWorkedDays());
        salary.setWorker(worker);
        salaryRepository.save(salary);
    }

    @Override
    public Salary getOne(Long id) {
        Salary salary = salaryRepository.getOne(id);
        return salary;
    }

    @Override
    public List<Salary> findAll() {
        List<Salary> salaries = salaryRepository.findAll();
        return salaries;
    }

    @Override
    public void deleteById(Long id) {
        salaryRepository.deleteById(id);
    }
}
