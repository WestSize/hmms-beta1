package com.example.hmmsbeta1.web.repositories.CompanyRepositories;

import com.example.hmmsbeta1.web.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Long>, WorkerCustomRepository {
}
