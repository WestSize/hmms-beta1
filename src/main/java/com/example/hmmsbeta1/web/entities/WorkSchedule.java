package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;

@Entity
@Table(name = "WorkSchedules")
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker worker;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    @Column
    private String date;
    @Lob
    @Column(length=512)
    private String workerReport;
    @Column
    private boolean workerDone;
    @Column
    private String endDateAndTime;

    public WorkSchedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWorkerReport() {
        return workerReport;
    }

    public void setWorkerReport(String workerReport) {
        this.workerReport = workerReport;
    }

    public boolean isWorkerDone() {
        return workerDone;
    }

    public void setWorkerDone(boolean workerDone) {
        this.workerDone = workerDone;
    }

    public String getEndDateAndTime() {
        return endDateAndTime;
    }

    public void setEndDateAndTime(String endDateAndTime) {
        this.endDateAndTime = endDateAndTime;
    }
}
