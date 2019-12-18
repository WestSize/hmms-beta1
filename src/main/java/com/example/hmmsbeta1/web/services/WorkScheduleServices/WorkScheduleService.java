package com.example.hmmsbeta1.web.services.WorkScheduleServices;

import com.example.hmmsbeta1.web.entities.WorkSchedule;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

public interface WorkScheduleService {
    List<WorkSchedule> showWorkScheduleByCompanyId(Long id);
    List<WorkSchedule> showWorkScheduleOfCompanyByDateAndCompanyId(String date, Long Id);
    List<WorkSchedule> showLast12WorkSchedulesByUserId(Long id);
    List<WorkSchedule> showAllWorkScheduleByUserId(Long id);
    void save(WorkSchedule workSchedule);
    WorkSchedule getOne(Long id);
    List<WorkSchedule> findAll();
    void deleteById(Long id);
    String createWorkSchedule(String workers, String nowDate, WorkSchedule workScheduleNew, Principal principal);
    String workReportPost(WorkSchedule workScheduleNew, WorkSchedule workScheduleOld);
}
