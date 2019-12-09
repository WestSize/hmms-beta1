package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.Application;
import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class CompaniesController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public static String uploadDirectory = System.getProperty("user.dir")+"/uploads";

    private Long companyId = null;

    @RequestMapping(value = "/company-home", method = RequestMethod.GET)
    public ModelAndView companyHome(Model model, Principal principal){
        model.addAttribute("newcompany", new Company());
        model.addAttribute("usercompanies", companyRepository.showOnlyUsersCompanies(principal.getName()));
        return new ModelAndView("company-home");
    }

    @RequestMapping(value = "/company-home", method = RequestMethod.POST)
    public String processCompany(@Valid Company company, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        company.setUser(user);
        company.setNumberOfOffices(0);
        company.setNumberOfWorkers(0);
        company.setProfit((company.getIncome()-company.getInvestment()));
        user.setWorkingStatus("owner");
        companyRepository.save(company);
        return "/company-home";
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.GET)
    public String companyDetails(long id, Model model, Principal principal){
        Long ownerId = userRepository.findByEmail(principal.getName()).getId();
        Company company = companyRepository.getOne(id);
        if(company.getUser().getId()==ownerId) {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            model.addAttribute("companyOwnerId", companyRepository.getOne(id).getUser().getId());
            model.addAttribute("updateCompany", new Company());
            companyId = id;
            return "/company-page";
        } else {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            companyId = id;
            return "/company-details";
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(Model model, @RequestParam("files")MultipartFile[] files, Company company, Principal principal, Application application) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        for(MultipartFile file:files){
            Path fileNameAndPath = Paths.get(uploadDirectory,file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename());
            Files.write(fileNameAndPath, file.getBytes());
        }
        application.setApproved(false);
        application.setInvited(false);
        application.setCompanyId(companyId);
        application.setCvPath(fileNames+"");
        User user = userRepository.findByEmail(principal.getName());
        application.setUser(user);
        applicationRepository.save(application);
        return "redirect:/company-list?uploadok";
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.POST)
    public String updateUserCompany(Model model, Company company, Principal principal){
        Company oldCompany = companyRepository.getOne(companyId);
        companyId = null;
        oldCompany.setName(company.getName());
        oldCompany.setDescription(company.getDescription());
        companyRepository.save(oldCompany);
        return "redirect:/company-page?id="+oldCompany.getId();
    }

    @RequestMapping(value = "/company-list", method = RequestMethod.GET)
    public String companyList(Model model){
        model.addAttribute("allcompanies", companyRepository.findAll());
        return "/company-list";
    }

    @RequestMapping(value = "/workers-list", method = RequestMethod.GET)
    public String workersList(Model model, Principal principal){
        Long userId = userRepository.findByEmail(principal.getName()).getId();
//        Company company = companyRepository.showOnlyUsersCompanies(principal.getName());
        model.addAttribute("myapplications", applicationRepository.showOnlyUsersCompanyApplications((long)272));
        return "/workers-list";
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {

        // ...

        File file = new File("uploads/"+param);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+param);
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
