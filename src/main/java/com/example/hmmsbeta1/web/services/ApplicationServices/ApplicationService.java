package com.example.hmmsbeta1.web.services.ApplicationServices;

import com.example.hmmsbeta1.web.entities.Application;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ApplicationService {
    List showOnlyUsersCompanyApplications(Long id);
    List showApplicationByCandidateId(Long id);
    List showOnlyUsersCompanyApplicationsInvited(Long id);
    Application showApplicationByOwnerCompanyId(Long id);
    Application showApplicationByCompanyId(Long id);
    Application showOneApplicationByUserIdAndCompanyId(Long UserId, Long CompanyID);
    void save(Application application);
    Application getOne(Long id);
    List<Application> findAll();
    void deleteById(Long id);
}
