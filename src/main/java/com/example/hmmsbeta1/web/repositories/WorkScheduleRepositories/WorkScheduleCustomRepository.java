package com.example.hmmsbeta1.web.repositories.WorkScheduleRepositories;

import com.example.hmmsbeta1.web.entities.WorkSchedule;

import java.util.List;

public interface WorkScheduleCustomRepository {
    List<WorkSchedule> showWorkScheduleByCompanyId(Long id);
    List<WorkSchedule> showWorkScheduleOfCompanyByDateAndCompanyId(String date, Long Id);
    List<WorkSchedule> showLast12WorkSchedulesByUserId(Long id);
    List<WorkSchedule> showAllWorkScheduleByUserId(Long id);
}
