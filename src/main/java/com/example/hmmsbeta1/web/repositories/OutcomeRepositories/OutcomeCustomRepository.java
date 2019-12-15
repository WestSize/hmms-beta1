package com.example.hmmsbeta1.web.repositories.OutcomeRepositories;

import com.example.hmmsbeta1.web.entities.Outcome;

import java.util.List;

public interface OutcomeCustomRepository {
    List<Outcome> findLast12OutcomesByCompanyId(Long id);
    List<Outcome> findAllOutcomesByCompanyId(Long id);
    List<Outcome> findOutcomesByCompanyIdAndDate(Long id, String date);
}
