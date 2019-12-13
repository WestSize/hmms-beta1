package com.example.hmmsbeta1.web.repositories.ApplicationRepositories;

import com.example.hmmsbeta1.web.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationCustomRepository {
}
