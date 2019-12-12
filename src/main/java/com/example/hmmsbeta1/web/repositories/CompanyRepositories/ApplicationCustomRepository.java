package com.example.hmmsbeta1.web.repositories.CompanyRepositories;

import com.example.hmmsbeta1.web.entities.Application;

import java.util.List;

public interface ApplicationCustomRepository {
    List showOnlyUsersCompanyApplications(Long id);
    List showApplicationByCandidateId(Long id);
    List showOnlyUsersCompanyApplicationsInvited(Long id);
    Application showApplicationByOwnerCompanyId(Long id);
    Application showApplicationByCompanyId(Long id);
    Application showOneApplicationByUserIdAndCompanyId(Long UserId, Long CompanyID);
}
