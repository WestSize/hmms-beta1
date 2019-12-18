package com.example.hmmsbeta1.web.services.WorkScheduleServices;

import com.example.hmmsbeta1.web.entities.WorkSchedule;
import com.example.hmmsbeta1.web.repositories.WorkScheduleRepositories.WorkScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {
    @Autowired
    private WorkScheduleRepository workScheduleRepository;

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
}
