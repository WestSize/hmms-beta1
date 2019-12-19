package com.example.hmmsbeta1.web.services.OutcomeServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Message;
import com.example.hmmsbeta1.web.entities.Outcome;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.MessageRepository;
import com.example.hmmsbeta1.web.repositories.OutcomeRepositories.OutcomeRepository;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutcomeServicesImplTest {

    OutcomeRepository outcomeRepository;

    @BeforeEach
    private void setupTest(){
        outcomeRepository = Mockito.mock(OutcomeRepository.class);
    }

    @Test
    void findLast12OutcomesByCompanyId() {
        List<Outcome> outcomes = new ArrayList<>();
        Company company = new Company();
        company.setId((long)1);
        for (int i = 0; i < 13; i++) {
            Outcome outcome = new Outcome();
            outcome.setCompany(company);
            outcomes.add(outcome);
        }
        Mockito.when(outcomeRepository.findLast12OutcomesByCompanyId((long)1)).thenReturn(outcomes);
        assertEquals(12, outcomeRepository.findLast12OutcomesByCompanyId((long)1).size()-1);

    }

    @Test
    void findAllOutcomesByCompanyId() {
        List<Outcome> outcomes = new ArrayList<>();
        Company company = new Company();
        company.setId((long)1);
        for (int i = 0; i < 13; i++) {
            Outcome outcome = new Outcome();
            outcome.setCompany(company);
            outcomes.add(outcome);
        }
        Mockito.when(outcomeRepository.findAllOutcomesByCompanyId((long)1)).thenReturn(outcomes);
        assertEquals(outcomeRepository.findAllOutcomesByCompanyId((long)1), outcomes);
    }

    @Test
    void findOutcomesByCompanyIdAndDate() {
    }

    @Test
    void getOne() {
        Outcome outcome = new Outcome();
        outcome.setId((long)1);
        Mockito.when(outcomeRepository.getOne((long)1)).thenReturn(outcome);
        assertEquals(outcomeRepository.getOne((long)1), outcome);
    }

    @Test
    void findAll() {
        List<Outcome> outcomes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Outcome outcome = new Outcome();
            outcomes.add(outcome);
        }
        Mockito.when(outcomeRepository.findAll()).thenReturn(outcomes);
        assertEquals(outcomeRepository.findAll(), outcomes);
    }
}