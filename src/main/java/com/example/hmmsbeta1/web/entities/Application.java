package com.example.hmmsbeta1.web.entities;

import javax.persistence.*;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn(name="candidate_id", nullable = false)
    private User user;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "cv_path")
    private String cvPath;
    @Column(name = "approved")
    private boolean approved;
    @Column(name="invited")
    private boolean invited;
    @Column(name="candidate_tel")
    private int candidateTelephoneNum;

    public Application() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCvPath() {
        return cvPath;
    }

    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }

    public int getCandidateTelephoneNum() {
        return candidateTelephoneNum;
    }

    public void setCandidateTelephoneNum(int candidateTelephoneNum) {
        this.candidateTelephoneNum = candidateTelephoneNum;
    }
}
