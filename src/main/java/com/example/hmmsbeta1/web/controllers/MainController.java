package com.example.hmmsbeta1.web.controllers;


import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.entities.Worker;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.repositories.WorkerRepositories.WorkerRepository;
import com.example.hmmsbeta1.web.services.UserService;
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
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkerRepository workerRepository;

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads/avatars";

    private Long workerUserId = null;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home(Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
//        int userUnreededMsgs = userService.countUnreededMessages(principal.getName());
        model.addAttribute("updateuser", new User());
        //        ot tuka pochva reloada na principalite
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER")); //add your role here [e.g., new SimpleGrantedAuthority("ROLE_NEW_ROLE")]

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
        User oldUserInfo = userRepository.findByEmail(principal.getName());
        oldUserInfo.setFirstName(newUserInfo.getFirstName());
        oldUserInfo.setLastName(newUserInfo.getLastName());
        oldUserInfo.setEmail(newUserInfo.getEmail());
        if (!oldUserInfo.getPassword().equals(newUserInfo.getPassword()) && !newUserInfo.getPassword().equals("")) {
            oldUserInfo.setPassword(passwordEncoder.encode(newUserInfo.getPassword()));
        }
        oldUserInfo.setAge(newUserInfo.getAge());
        oldUserInfo.setFacebookPage(newUserInfo.getFacebookPage());
        oldUserInfo.setDescription(newUserInfo.getDescription());
        userRepository.save(oldUserInfo);
        return "redirect:/?updateok";
    }

    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    public String uploadAvatar(@RequestParam("avatarPath") String avatarPath, Principal principal) throws IOException {
        User user = userService.findByEmail(principal.getName());
        user.setAvatarPath(avatarPath);
        userRepository.save(user);
        return "redirect:/";
    }

    @RequestMapping(value = "/user-page", method = RequestMethod.GET)
    public String viewUserPage(Long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        User user = userRepository.getOne(id);
        Worker worker = null; //worker
        List<Worker> allWorkers = workerRepository.findAll();
        for (int i = 0; i < allWorkers.size(); i++) {
            Worker nowWorker = allWorkers.get(i);
            if (nowWorker.getUser() == user) {
                worker = nowWorker;
            }
        }
        if (worker != null) {
            User me = userRepository.findByEmail(principal.getName());
            Company userCompany = worker.getCompany();
            if (me.getWorkingStatus().equals("owner") && userCompany.equals(companyRepository.showOneUserCompany(principal.getName()))) {
                model.addAttribute("userinfo", userRepository.getOne(id));
                model.addAttribute("companyDetails", userCompany);
                model.addAttribute("workerDetails", worker);
                workerUserId = worker.getUser().getId();
                return "/user-page-owner";
            } else {
                model.addAttribute("userinfo", userRepository.getOne(id));
                model.addAttribute("companyDetails", userCompany);
                return "/user-page";
            }
        } else {
            if(user.getWorkingStatus().equals("owner")){
                Company userCompany = companyRepository.showOneUserCompany(user.getEmail());
                model.addAttribute("userinfo", userRepository.getOne(id));
                model.addAttribute("companyDetails", userCompany);
                return "/user-page";
            } else {
                model.addAttribute("userinfo", userRepository.getOne(id));
                return "/user-page";
            }
        }
    }
    @RequestMapping(value = "/user-page", method = RequestMethod.POST)
    public String editWorkerData(Worker worker){
        Long nowId = workerUserId;
        Worker workerOld = workerRepository.showWorkerByUserId(workerUserId);
        workerUserId = null;
        workerOld.setPosition(worker.getPosition());
        workerOld.setSalary(worker.getSalary());
        workerRepository.save(workerOld);
        return "redirect:/user-page?id="+nowId+"&workerUpdated";
    }

    @RequestMapping(value = "/add-dayoff", method = RequestMethod.POST)
    public String addDayOff(Principal principal, Worker workerNew){
        Worker worker = workerRepository.showWorkerByUserId(workerUserId);
        Long nowId = workerUserId;
        workerUserId = null;
        int nowWorkedDays = worker.getMonthWorkedDays();
        worker.setMonthWorkedDays(nowWorkedDays+workerNew.getMonthWorkedDays());
        workerRepository.save(worker);
        return "redirect:/user-page?id="+nowId+"&workerDayoffAdded";
    }
}