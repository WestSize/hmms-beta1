package com.example.hmmsbeta1.web.repositories.ApplicationRepositories;

import com.example.hmmsbeta1.web.entities.Application;
import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationCustomRepositoryTest {
    private final User user = new User();
    private final Application application = new Application();

    @Test
    void showOnlyUsersCompanyApplications() {
        Company company = new Company();
        company.setId((long)1);
        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Application application = new Application();
            application.setId((long)i);
            application.setCompanyId((long)1);
            applications.add(application);
        }
        assertEquals(5, applications.size());
    }

    @Test
    void showApplicationByCandidateId() {
        user.setId((long) 1);
        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Application application = new Application();
            application.setId((long)i);
            application.setUser(user);
            applications.add(application);
        }
        assertEquals(5, applications.size());

    }

    @Test
    void showOnlyUsersCompanyApplicationsInvited() {
    }

    @Test
    void showApplicationByOwnerCompanyId() {
    }

    @Test
    void showApplicationByCompanyId() {
    }

    @Test
    void showOneApplicationByUserIdAndCompanyId() {
    }
}