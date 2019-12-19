package com.example.hmmsbeta1.web.services.ApplicationServices;

import com.example.hmmsbeta1.web.entities.Application;
import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

//    private static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

//    private Long companyId = null;


    public ApplicationServiceImpl(ApplicationRepository applicationRepository, UserService userService, CompanyService companyService) {
        this.applicationRepository = applicationRepository;
        this.userService = userService;
        this.companyService = companyService;
    }

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
    public void save(MultipartFile[] files, Company company, Principal principal, Application application, String uploadDirectory, Long companyId) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        for (MultipartFile file : files) {
            Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename());
            Files.write(fileNameAndPath, file.getBytes());
        }
        application.setApproved(false);
        application.setInvited(false);
        application.setCompanyId(companyId);
        application.setCvPath(fileNames + "");
        User user = userService.findByEmail(principal.getName());
        application.setUser(user);
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
        if (applications == null){
            return null;
        } else {
            return applications;
        }
    }

    @Override
    public void deleteById(Long id) {
        applicationRepository.deleteById(id);
    }

    @Override
    public void updateToInvited(Application application, Principal principal, Long id) {
        List <Application> applications = applicationRepository.showApplicationByCandidateId(id);
        for (int i = 0; i < applications.size(); i++) {
            Application nowApp = applications.get(i);
            if(nowApp.getCompanyId().equals(companyService.showOneUserCompany(principal.getName()).getId())){
                application = nowApp;
            }
        }
        application.setInvited(true);
        applicationRepository.save(application);
    }
}
