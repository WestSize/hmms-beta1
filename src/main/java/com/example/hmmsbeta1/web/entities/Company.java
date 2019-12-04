package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;

@Entity()
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column
    private int numberOfWorkers;
    @Column
    private int numberOfOffices;
    @Column
    private int income;
    @Column
    private int investment;
    @Column
    private int profit;
    @OneToOne
    @JoinColumn(name="owner_id", nullable = false)
    private User user;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
//    private List<Worker> workers;

    public Company() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfWorkers() {
        return numberOfWorkers;
    }

    public void setNumberOfWorkers(int numberOfWorkers) {
        this.numberOfWorkers = numberOfWorkers;
    }

    public int getNumberOfOffices() {
        return numberOfOffices;
    }

    public void setNumberOfOffices(int numberOfOffices) {
        this.numberOfOffices = numberOfOffices;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getInvestment() {
        return investment;
    }

    public void setInvestment(int investment) {
        this.investment = investment;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //    public List<Worker> getWorkers() {
//        return workers;
//    }
//
//    public void setWorkers(List<Worker> workers) {
//        this.workers = workers;
//    }
}
