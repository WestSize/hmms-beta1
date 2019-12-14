package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;

@Entity
@Table(name = "incomes")
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    @JoinColumn(name="company_id", nullable = false)
    private Company company;
    @Column
    private String incomeDescription;
    @Column int incomePrice;
    @Column
    private String incomeDate;

    public Income() {
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

    public String getIncomeDescription() {
        return incomeDescription;
    }

    public void setIncomeDescription(String incomeDescription) {
        this.incomeDescription = incomeDescription;
    }

    public int getIncomePrice() {
        return incomePrice;
    }

    public void setIncomePrice(int incomePrice) {
        this.incomePrice = incomePrice;
    }

    public String getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(String incomeDate) {
        this.incomeDate = incomeDate;
    }
}
