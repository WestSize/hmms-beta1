package com.example.hmmsbeta1;

import com.example.hmmsbeta1.web.controllers.CompaniesController;
import com.example.hmmsbeta1.web.controllers.MainController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;

@SpringBootApplication
@ComponentScan("com")
public class HmmsBeta1Application {

    public static void main(String[] args) {
        new File(CompaniesController.uploadDirectory).mkdir();
        new File(MainController.uploadDirectory).mkdir();
        SpringApplication.run(HmmsBeta1Application.class, args);
    }

}
