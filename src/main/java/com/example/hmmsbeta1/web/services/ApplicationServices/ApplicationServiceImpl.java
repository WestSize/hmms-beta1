package com.example.hmmsbeta1.web.services.ApplicationServices;

import com.example.hmmsbeta1.web.entities.Application;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public List showOnlyUsersCompanyApplications(Long id) {
        List<Application> applications = applicationRepository.showOnlyUsersCompanyApplications(id);
        return applications;
    }

    @Override
    public List showApplicationByCandidateId(Long id) {
        List<Application> applications = applicationRepository.showApplicationByCandidateId(id);
        return applications;
    }

    @Override
    public List showOnlyUsersCompanyApplicationsInvited(Long id) {
        List<Application> applications = applicationRepository.showOnlyUsersCompanyApplicationsInvited(id);
        return applications;
    }

    @Override
    public Application showApplicationByOwnerCompanyId(Long id) {
        Application application = applicationRepository.showApplicationByOwnerCompanyId(id);
        return application;
    }

    @Override
    public Application showApplicationByCompanyId(Long id) {
        Application application = applicationRepository.showApplicationByCompanyId(id);
        return application;
    }

    @Override
    public Application showOneApplicationByUserIdAndCompanyId(Long UserId, Long CompanyID) {
        Application application = applicationRepository.showOneApplicationByUserIdAndCompanyId(UserId, CompanyID);
        return application;
    }

    @Override
    public void save(Application application) {
        applicationRepository.save(application);
    }

    @Override
    public Application getOne(Long id) {
        Application application = applicationRepository.getOne(id);
        return application;
    }

    @Override
    public List<Application> findAll() {
        List<Application> applications = applicationRepository.findAll();
        return applications;
    }

    @Override
    public void deleteById(Long id) {
        applicationRepository.deleteById(id);
    }
}
