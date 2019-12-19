package com.example.hmmsbeta1.web.services.ApplicationServices;

import com.example.hmmsbeta1.web.entities.Application;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationServiceImplTest {

    ApplicationServiceImpl service;

    ApplicationRepository applicationRepository;

    UserService userService;

    CompanyService companyService;

    @BeforeEach
    private void setupTest(){
        applicationRepository = Mockito.mock(ApplicationRepository.class);
        userService = Mockito.mock(UserService.class);
        companyService = Mockito.mock(CompanyService.class);
        service = new ApplicationServiceImpl(applicationRepository, userService, companyService);

    }

    @Test
    void showOnlyUsersCompanyApplicationsIfEverythingIsOk() {
        List<Application> applications = new ArrayList<>();
        User user = new User();
        user.setId((long)1);
        for (int i = 0; i < 5; i++) {
            Application application = new Application();
            application.setId((long)i);
            application.setUser(user);
            applications.add(application);
        }
        Mockito.when(applicationRepository.showOnlyUsersCompanyApplications((long)1)).thenReturn(applications);
        List<Application> appsFromRepository = applicationRepository.showOnlyUsersCompanyApplications((long)1);
        assertEquals(applications, appsFromRepository);
    }

    @Test
    void showApplicationByCandidateId() {
        List<Application> applications = new ArrayList<>();
        User user = new User();
        user.setId((long)1);
        for (int i = 0; i < 5; i++) {
            Application application = new Application();
            application.setId((long)i);
            application.setUser(user);
            applications.add(application);
        }
        Mockito.when(applicationRepository.showApplicationByCandidateId((long)1)).thenReturn(applications);
        List<Application> appsFromRepository = applicationRepository.showApplicationByCandidateId((long)1);
        assertEquals(applications, appsFromRepository);

    }

    @Test
    void showOnlyUsersCompanyApplicationsInvitedIfNoOneIsInvited() {
        List<Application> applications = new ArrayList<>();
        User user = new User();
        user.setId((long)1);
        for (int i = 0; i < 5; i++) {
            Application application = new Application();
            application.setId((long)i);
            application.setUser(user);
            applications.add(application);
        }
        Mockito.when(applicationRepository.showApplicationByCandidateId((long)1)).thenReturn(applications);
        List<Application> appsFromRepository = applicationRepository.showOnlyUsersCompanyApplicationsInvited((long)1);
        assertEquals(0, appsFromRepository.size());

    }

    @Test
    void showOnlyUsersCompanyApplicationsInvitedIfIsInvited() {
        List<Application> applications = new ArrayList<>();
        User user = new User();
        user.setId((long)1);
        for (int i = 0; i < 5; i++) {
            Application application = new Application();
            application.setId((long)i);
            application.setUser(user);
            application.setInvited(true);
            applications.add(application);
        }
        Mockito.when(applicationRepository.showApplicationByCandidateId((long)1)).thenReturn(applications);
        List<Application> appsFromRepository = applicationRepository.showOnlyUsersCompanyApplicationsInvited((long)1);
        assertEquals(0, appsFromRepository.size());

    }

    @Test
    void showApplicationByOwnerCompanyId() {
        User user = new User();
        user.setId((long)1);
        Application application = new Application();
        application.setUser(user);
        Mockito.when(applicationRepository.showApplicationByOwnerCompanyId((long)1)).thenReturn(application);
        Application expectedApp = applicationRepository.showApplicationByOwnerCompanyId((long)1);
        assertEquals(expectedApp, application);
    }

    @Test
    void showApplicationByCompanyId() {
        Application application = new Application();
        application.setCompanyId((long)1);
        Mockito.when(applicationRepository.showApplicationByCompanyId((long)1)).thenReturn(application);
        Application expectedApp = applicationRepository.showApplicationByCompanyId((long)1);
        assertEquals(expectedApp, application);
    }

    @Test
    void showOneApplicationByUserIdAndCompanyId() {
        Application application = new Application();
        application.setCompanyId((long)2);
        User user = new User();
        user.setId((long)1);
        application.setUser(user);
        Mockito.when(applicationRepository.showOneApplicationByUserIdAndCompanyId((long)1, (long)2)).thenReturn(application);
        Application expectedApp = applicationRepository.showOneApplicationByUserIdAndCompanyId((long)1, (long)2);
        assertEquals(expectedApp, application);

    }

    @Test
    void getOne() {
        Application application = new Application();
        application.setId((long)1);
        Mockito.when(applicationRepository.getOne((long)1)).thenReturn(application);
        Application expectedApp = applicationRepository.getOne((long)1);
        assertEquals(expectedApp, application);
    }

    @Test
    void getOneIfNotExist() {
        Application application = new Application();
        application.setId((long)1);
        Mockito.when(applicationRepository.getOne((long)1)).thenReturn(application);
        Application expectedApp = applicationRepository.getOne((long)2);
        assertEquals(null, expectedApp);
    }

    @Test
    void findAll() {
        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Application application = new Application();
            application.setId((long)i);
            applications.add(application);
        }
        Mockito.when(applicationRepository.findAll()).thenReturn(applications);
        assertEquals(applicationRepository.findAll(), applications);
    }

//    @Test
//    void deleteById() {
//        List<Application> applications = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            Application application = new Application();
//            application.setId((long)i);
//            applications.add(application);
//        }
//        Mockito.when(applicationRepository.deleteById((long)1)).thenReturn(applications.remove(applications.get(1)));
//
//    }

}