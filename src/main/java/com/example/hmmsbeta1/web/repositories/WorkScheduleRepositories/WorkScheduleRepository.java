package com.example.hmmsbeta1.web.repositories.WorkScheduleRepositories;

import com.example.hmmsbeta1.web.entities.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long>, WorkScheduleCustomRepository {
}
