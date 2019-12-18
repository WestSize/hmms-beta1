package com.example.hmmsbeta1.web.services.OutcomeServices;

import com.example.hmmsbeta1.web.entities.Outcome;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OutcomeService {
    List<Outcome> findLast12OutcomesByCompanyId(Long id);
    List<Outcome> findAllOutcomesByCompanyId(Long id);
    List<Outcome> findOutcomesByCompanyIdAndDate(Long id, String date);
    void save(Outcome outcome);
    Outcome getOne(Long id);
    List<Outcome> findAll();
    void deleteById(Long id);
}
