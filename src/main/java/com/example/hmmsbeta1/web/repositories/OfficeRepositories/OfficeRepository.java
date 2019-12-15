package com.example.hmmsbeta1.web.repositories.OfficeRepositories;

import com.example.hmmsbeta1.web.entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeRepository extends JpaRepository<Office, Long>, OfficeCustomRepository {
}
