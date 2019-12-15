package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;

@Entity
@Table(name = "salaries")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    @JoinColumn(name="worker_id", nullable = false)
    private Worker worker;
    @Column
    private String date;
    @Column
    private int workedDays;
    @Column
    private int paycheckPerDay;
    @Column
    private int salarySum;

    public Salary() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWorkedDays() {
        return workedDays;
    }

    public void setWorkedDays(int workedDays) {
        this.workedDays = workedDays;
    }

    public int getPaycheckPerDay() {
        return paycheckPerDay;
    }

    public void setPaycheckPerDay(int paycheckPerDay) {
        this.paycheckPerDay = paycheckPerDay;
    }

    public int getSalarySum() {
        return salarySum;
    }

    public void setSalarySum(int salarySum) {
        this.salarySum = salarySum;
    }
}
