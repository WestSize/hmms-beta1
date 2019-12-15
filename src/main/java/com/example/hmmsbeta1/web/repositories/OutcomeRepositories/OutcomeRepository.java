package com.example.hmmsbeta1.web.repositories.OutcomeRepositories;

import com.example.hmmsbeta1.web.entities.Outcome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutcomeRepository extends JpaRepository<Outcome, Long>, OutcomeCustomRepository {
}
