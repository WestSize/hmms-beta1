package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;

@Entity
@Table(name = "outcomes")
public class Outcome {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    @JoinColumn(name="company_id", nullable = false)
    private Company company;
    @Column
    private String outcomeDescription;
    @Column
    int outcomePrice;
    @Column
    private String outcomeDate;

    public Outcome() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getOutcomeDescription() {
        return outcomeDescription;
    }

    public void setOutcomeDescription(String outcomeDescription) {
        this.outcomeDescription = outcomeDescription;
    }

    public int getOutcomePrice() {
        return outcomePrice;
    }

    public void setOutcomePrice(int outcomePrice) {
        this.outcomePrice = outcomePrice;
    }

    public String getOutcomeDate() {
        return outcomeDate;
    }

    public void setOutcomeDate(String outcomeDate) {
        this.outcomeDate = outcomeDate;
    }
}
