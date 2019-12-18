package com.example.hmmsbeta1.web.controllers;


import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.entities.Worker;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.UserService;
import com.example.hmmsbeta1.web.services.WorkerServices.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private WorkerService workerService;

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads/avatars";

    private Long workerUserId = null;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home(Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("updateuser", new User());
        //        ot tuka pochva reloada na principalite
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
        // do tuka
        return new ModelAndView("home.html");
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String updateUser(Principal principal, User newUserInfo) {
        User oldUserInfo = userService.findByEmail(principal.getName());
        oldUserInfo.setFirstName(newUserInfo.getFirstName());
        oldUserInfo.setLastName(newUserInfo.getLastName());
        oldUserInfo.setEmail(newUserInfo.getEmail());
        if (!oldUserInfo.getPassword().equals(newUserInfo.getPassword()) && !newUserInfo.getPassword().equals("")) {
            oldUserInfo.setPassword(passwordEncoder.encode(newUserInfo.getPassword()));
        }
        oldUserInfo.setAge(newUserInfo.getAge());
        oldUserInfo.setFacebookPage(newUserInfo.getFacebookPage());
        oldUserInfo.setDescription(newUserInfo.getDescription());
        userService.update(oldUserInfo);
        return "redirect:/?updateok";
    }

    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    public String uploadAvatar(@RequestParam("avatarPath") String avatarPath, Principal principal) throws IOException {
        User user = userService.findByEmail(principal.getName());
        user.setAvatarPath(avatarPath);
        userService.update(user);
        return "redirect:/";
    }

    @RequestMapping(value = "/user-page", method = RequestMethod.GET)
    public String viewUserPage(Long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        User user = userService.getOne(id);
        Worker worker = null; //worker
        List<Worker> allWorkers = workerService.findAll();
        for (int i = 0; i < allWorkers.size(); i++) {
            Worker nowWorker = allWorkers.get(i);
            if (nowWorker.getUser() == user) {
                worker = nowWorker;
            }
        }
        if (worker != null) {
            User me = userService.findByEmail(principal.getName());
            Company userCompany = worker.getCompany();
            if (me.getWorkingStatus().equals("owner") && userCompany.equals(companyService.showOneUserCompany(principal.getName()))) {
                model.addAttribute("userinfo", userService.getOne(id));
                model.addAttribute("companyDetails", userCompany);
                model.addAttribute("workerDetails", worker);
                workerUserId = worker.getUser().getId();
                return "/user-page-owner";
            } else {
                model.addAttribute("userinfo", userService.getOne(id));
                model.addAttribute("companyDetails", userCompany);
                return "/user-page";
            }
        } else {
            if(user.getWorkingStatus().equals("owner")){
                Company userCompany = companyService.showOneUserCompany(user.getEmail());
                model.addAttribute("userinfo", userService.getOne(id));
                model.addAttribute("companyDetails", userCompany);
                return "/user-page";
            } else {
                model.addAttribute("userinfo", userService.getOne(id));
                return "/user-page";
            }
        }
    }
    @RequestMapping(value = "/user-page", method = RequestMethod.POST)
    public String editWorkerData(Worker worker){
        Long nowId = workerUserId;
        Worker workerOld = workerService.showWorkerByUserId(workerUserId);
        workerUserId = null;
        workerOld.setPosition(worker.getPosition());
        workerOld.setSalary(worker.getSalary());
        workerService.save(workerOld);
        return "redirect:/user-page?id="+nowId+"&workerUpdated";
    }

    @RequestMapping(value = "/add-dayoff", method = RequestMethod.POST)
    public String addDayOff(Principal principal, Worker workerNew){
        Worker worker = workerService.showWorkerByUserId(workerUserId);
        Long nowId = workerUserId;
        workerUserId = null;
        int nowWorkedDays = worker.getMonthWorkedDays();
        worker.setMonthWorkedDays(nowWorkedDays+workerNew.getMonthWorkedDays());
        workerService.save(worker);
        return "redirect:/user-page?id="+nowId+"&workerDayoffAdded";
    }
}