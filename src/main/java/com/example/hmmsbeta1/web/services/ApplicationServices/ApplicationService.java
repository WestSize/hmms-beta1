package com.example.hmmsbeta1.web.services.ApplicationServices;

import com.example.hmmsbeta1.web.entities.Application;
import com.example.hmmsbeta1.web.entities.Company;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface ApplicationService {
    List showOnlyUsersCompanyApplications(Long id);
    List showApplicationByCandidateId(Long id);
    List showOnlyUsersCompanyApplicationsInvited(Long id);
    Application showApplicationByOwnerCompanyId(Long id);
    Application showApplicationByCompanyId(Long id);
    Application showOneApplicationByUserIdAndCompanyId(Long UserId, Long CompanyID);
//    void save(Application application);
    void save(MultipartFile[] files, Company company, Principal principal, Application application, String uploadDirectory, Long companyId) throws IOException;
    Application getOne(Long id);
    List<Application> findAll();
    void deleteById(Long id);
    void updateToInvited(Application application, Principal principal, Long id);

//    void save(Application application);
}
