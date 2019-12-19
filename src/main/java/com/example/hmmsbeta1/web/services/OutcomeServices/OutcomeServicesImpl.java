package com.example.hmmsbeta1.web.services.OutcomeServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Outcome;
import com.example.hmmsbeta1.web.repositories.OutcomeRepositories.OutcomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OutcomeServicesImpl implements OutcomeService {
    @Autowired
    private OutcomeRepository outcomeRepository;

    public OutcomeServicesImpl(OutcomeRepository outcomeRepository) {
        this.outcomeRepository=outcomeRepository;
    }

    @Override
    public List<Outcome> findLast12OutcomesByCompanyId(Long id) {
        List<Outcome> outcomes = outcomeRepository.findLast12OutcomesByCompanyId(id);
        return outcomes;
    }

    @Override
    public List<Outcome> findAllOutcomesByCompanyId(Long id) {
        List<Outcome> outcomes = outcomeRepository.findAllOutcomesByCompanyId(id);
        return outcomes;
    }

    @Override
    public List<Outcome> findOutcomesByCompanyIdAndDate(Long id, String date) {
        List<Outcome> outcomes = outcomeRepository.findOutcomesByCompanyIdAndDate(id, date);
        return outcomes;
    }

    @Override
    public void save(Outcome outcome, Company company) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        outcome.setCompany(company);
        outcome.setOutcomeDate(formatter.format(date));
        outcomeRepository.save(outcome);
    }

    @Override
    public Outcome getOne(Long id) {
        Outcome outcome = outcomeRepository.getOne(id);
        return outcome;
    }

    @Override
    public List<Outcome> findAll() {
        List<Outcome> outcomes = outcomeRepository.findAll();
        return outcomes;
    }

    @Override
    public void deleteById(Long id) {
        outcomeRepository.deleteById(id);
    }

    @Override
    public void salariesPay(int SalariesSum, Company company, String date) {
        Outcome outcome = new Outcome();
        outcome.setOutcomePrice(SalariesSum);
        outcome.setOutcomeDate(date);
        outcome.setCompany(company);
        outcome.setOutcomeDescription("Изплащане на заплати в размер на "+SalariesSum+" лв.");
        outcomeRepository.save(outcome);
    }
}
