package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.repositories.ApplicationRepositories.ApplicationRepository;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.repositories.IncomeRepositories.IncomeRepository;
import com.example.hmmsbeta1.web.repositories.SalaryRepositories.SalaryRepository;
import com.example.hmmsbeta1.web.repositories.WorkScheduleRepositories.WorkScheduleRepository;
import com.example.hmmsbeta1.web.repositories.WorkerRepositories.WorkerRepository;
import com.example.hmmsbeta1.web.repositories.MessagesRepositories.MessageRepository;
import com.example.hmmsbeta1.web.repositories.UserRepository;
import com.example.hmmsbeta1.web.services.PrivateMessagesService;
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
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class CompaniesController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    private Long companyId = null;
    private Long workerId = null;
    private Long workScheduleId = null;

    @RequestMapping(value = "/company-home", method = RequestMethod.GET)
    public ModelAndView companyHome(Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("newcompany", new Company());
        model.addAttribute("usercompanies", companyRepository.showOnlyUsersCompanies(principal.getName()));
        User me = userRepository.findByEmail(principal.getName());
        model.addAttribute("userInfo", me);
        if (me.getWorkingStatus().equals("worker")) {
            model.addAttribute("workerInfo", workerRepository.showWorkerByUserId(me.getId()));
            model.addAttribute("myWorkSchedulesCounter", workScheduleRepository.showLast12WorkSchedulesByUserId(me.getId()).size());
            model.addAttribute("myWorkSchedule", workScheduleRepository.showLast12WorkSchedulesByUserId(me.getId()));
        }
        return new ModelAndView("company-home");
    }

    @RequestMapping(value = "/company-home", method = RequestMethod.POST)
    public String processCompany(@Valid Company company, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        company.setUser(user);
        company.setNumberOfOffices(0);
        company.setNumberOfWorkers(0);
        company.setProfit((company.getIncome() - company.getInvestment()));
        company.setCircularLetter("Здравейте! Поканен сте на интервю за работата, която сте кандидатствали" +
                " във фирмата '" + company.getName() + "'. Моля напишете ни удобен ден и час за среща в наш офис или " +
                "се свържете с нас на тел: (не е зададен). Поздрави, " + user.getFirstName() + " " + user.getLastName() + " (управител). :)");
        user.setWorkingStatus("owner");
        companyRepository.save(company);
        Income income = new Income();
        income.setCompany(company);
        income.setIncomeDate(formatter.format(date));
        income.setIncomeDescription("Първоначално въведена инвестиция, при създаване на фирмата.");
        int incomeSum = company.getIncome();
        income.setIncomePrice(incomeSum);
        incomeRepository.save(income);
        Long companyId = company.getId();
        return "redirect:/company-page?id="+companyId;
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.GET)
    public String companyDetails(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        Long ownerId = userRepository.findByEmail(principal.getName()).getId();
        Company company = companyRepository.getOne(id);
        if (company.getUser().getId() == ownerId) {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            model.addAttribute("companyOwnerId", companyRepository.getOne(id).getUser().getId());
            model.addAttribute("updateCompany", new Company());
            companyId = id;
            return "/company-page";
        } else if (userRepository.findByEmail(principal.getName()).getWorkingStatus().equals("owner")) {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            return "/company-details-no-application";
        } else if (userRepository.findByEmail(principal.getName()).getWorkingStatus().equals("worker")) {
            model.addAttribute("companyInfo", companyRepository.getOne(id));
            return "/company-details-no-application";
        } else {
            User user = userRepository.findByEmail(principal.getName());
            Application application = null;
            List<Application> allApps = applicationRepository.findAll();
            for (int i = 0; i < allApps.size(); i++) {
                Application nowApp = allApps.get(i);
                Long compId = nowApp.getCompanyId();
                Long candidateId = nowApp.getUser().getId();
                if (compId.equals(company.getId()) && candidateId.equals(user.getId())) {
                    application = nowApp;
                }
            }
            if (application != null) {
                if (application.getCompanyId().equals(company.getId()) && application.getUser().getId().equals(user.getId())) {
                    model.addAttribute("companyInfo", companyRepository.getOne(id));
                    companyId = id;
                    return "/company-details-no-application";
                } else {
                    model.addAttribute("companyInfo", companyRepository.getOne(id));
                    companyId = id;
                    return "/company-details";
                }
            } else {
                model.addAttribute("companyInfo", companyRepository.getOne(id));
                companyId = id;
                return "/company-details";
            }
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(Model model, @RequestParam("files") MultipartFile[] files, Company company, Principal principal, Application application) throws IOException {
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
        User user = userRepository.findByEmail(principal.getName());
        application.setUser(user);
        applicationRepository.save(application);
        return "redirect:/company-list?uploadok";
    }

    @RequestMapping(value = "/company-page", method = RequestMethod.POST)
    public String updateUserCompany(Model model, Company company, Principal principal) {
        Company oldCompany = companyRepository.getOne(companyId);
        companyId = null;
        oldCompany.setName(company.getName());
        oldCompany.setDescription(company.getDescription());
        oldCompany.setPhoneNumber(company.getPhoneNumber());
        oldCompany.setPaydayDate(company.getPaydayDate());
        oldCompany.setInvestment(company.getInvestment());
        companyRepository.save(oldCompany);
        return "redirect:/company-page?id=" + oldCompany.getId();
    }

    @RequestMapping(value = "/company-list", method = RequestMethod.GET)
    public String companyList(Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("allcompanies", companyRepository.findAll());
        return "/company-list";
    }

    @RequestMapping(value = "/workers-list", method = RequestMethod.GET)
    public String workersList(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        Company company = companyRepository.showOneUserCompany(principal.getName());
        model.addAttribute("applicationsCounter", applicationRepository.showOnlyUsersCompanyApplications(id).size());
        model.addAttribute("invitedUsersCounter", applicationRepository.showOnlyUsersCompanyApplicationsInvited(id).size());
        model.addAttribute("myapplications", applicationRepository.showOnlyUsersCompanyApplications(id));
        model.addAttribute("invitedUsers", applicationRepository.showOnlyUsersCompanyApplicationsInvited(id));
        model.addAttribute("userscompany", companyRepository.showOneUserCompany(principal.getName()));
        model.addAttribute("workersCounter", workerRepository.showWorkersByCompanyId(company.getId()).size());
        model.addAttribute("companyWorkers", workerRepository.showWorkersByCompanyId(company.getId()));
        return "/workers-list";
    }

    @RequestMapping(value = "/workers-list", method = RequestMethod.POST)
    public String updateCircularLetter(Principal principal, Company company) {
        Company oldCompany = companyRepository.showOneUserCompany(principal.getName());
        oldCompany.setCircularLetter(company.getCircularLetter());
        Long redirectId = oldCompany.getId();
        companyRepository.save(oldCompany);
        return "redirect:/workers-list?id=" + redirectId;
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {
        File file = new File("uploads/" + param);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + param);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @RequestMapping(value = "/deleteApplication")
    public String deleteAppplication(long id, Principal principal) {
        applicationRepository.deleteById(id);
        Long redirectId = companyRepository.showOneUserCompany(principal.getName()).getId();
        return "redirect:/workers-list?id=" + redirectId;
    }

    @RequestMapping(value = "/approveToWork")
    public String approveToWork(Long id, Model model, Principal principal) {
        User user = userRepository.getOne(id);
        User me = userRepository.findByEmail(principal.getName());
        if (user.getWorkingStatus().equals("worker")) {
            return "redirect:/company-home?alreadyWorker";
        } else if (!me.getWorkingStatus().equals("owner")) {
            return "redirect:/company-home?notOwner";
        } else {
            model.addAttribute("userinfo", userRepository.getOne(id));
            model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
            workerId = id;
            return "/approve-user";
        }
    }

    @RequestMapping(value = "/approveToWork", method = RequestMethod.POST)
    public String createNewWorker(Worker worker, Principal principal) {
        User user = userRepository.getOne(workerId);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd'-'yyyy");
        Date date = new Date(System.currentTimeMillis());
        List<Application> workersApplications = applicationRepository.showApplicationByCandidateId(workerId);
        workerId = null;
        for (int i = 0; i < workersApplications.size(); i++) {
            Application application = workersApplications.get(i);
            applicationRepository.deleteById(application.getId());
        }
        user.setWorkingStatus("worker");
        userRepository.save(user);
        Company company = companyRepository.showOneUserCompany(principal.getName());
        worker.setCompany(company);
        worker.setUser(user);
        worker.setDateOfAppointment(formatter.format(date));
        worker.setOnDuty(false);
        workerRepository.save(worker);
        int companyWorkersCounter = company.getNumberOfWorkers();
        company.setNumberOfWorkers(companyWorkersCounter + 1);
        companyRepository.save(company);
        return "redirect:/company-page?id=" + company.getId();
    }

    @RequestMapping(value = "/company-workschedule", method = RequestMethod.GET)
    public String workSchedule(Model model, Principal principal, Long id, @RequestParam("workScheduleDay") String searchingDay) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        User user = userRepository.findByEmail(principal.getName());
        Company company = companyRepository.showOneUserCompany(principal.getName());
        if (!user.getWorkingStatus().equals("owner")) {
            return "/company-home?notOwner";
        } else if (!company.getUser().getId().equals(user.getId())) {
            return "/company-home?notOwner";
        } else {
            model.addAttribute("companysWorkSchedulesCounter", workScheduleRepository.showWorkScheduleOfCompanyByDateAndCompanyId(searchingDay, company.getId()).size());
            model.addAttribute("companysWorkSchedules", workScheduleRepository.showWorkScheduleOfCompanyByDateAndCompanyId(searchingDay, company.getId()));
            return "/company-workschedule";
        }
    }

    @RequestMapping(value = "/create-work-schedule", method = RequestMethod.GET)
    public String workScheduleCreate(Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        User me = userRepository.findByEmail(principal.getName());
        if (!me.getWorkingStatus().equals("owner")) {
            return "redirect:/company-home?notOwner";
        } else {
            return "/create-work-schedule";
        }
    }

    @RequestMapping(value = "/create-work-schedule", method = RequestMethod.POST)
    public String workSchedulePost(@RequestParam("workersList") String workers, @RequestParam("nowDate") String nowDate, WorkSchedule workScheduleNew, Principal principal) {
        String[] workersArray = workers.split(",\\s+");
        Company company = companyRepository.showOneUserCompany(principal.getName());
        boolean notConfirmedUser = false;
        int confirmedUsersCounter = 0;
        for (int i = 0; i < workersArray.length; i++) {
            boolean nowUserNotConfirmed = false;
            WorkSchedule workSchedule = new WorkSchedule();
            User user = userRepository.findByEmail(workersArray[i]);
            if (user == null || user == userRepository.findByEmail(principal.getName())) {
//                return "redirect:/create-work-schedule?noSuchUser";
                notConfirmedUser=true;
                nowUserNotConfirmed=true;
            }
            if(!nowUserNotConfirmed) {
                Worker worker = null;
                List<Worker> allWorkers = workerRepository.findAll();
                for (int j = 0; j < allWorkers.size(); j++) {
                    Worker nowWorker = allWorkers.get(j);
                    Long nowCompany = nowWorker.getCompany().getId();
                    Long nowUser = nowWorker.getUser().getId();
                    if (nowCompany.equals(company.getId()) && nowUser.equals(user.getId())) {
                        worker = nowWorker;
                    }
                }
                if (worker == null) {
//                return "redirect:/create-work-schedule?noSuchUser";
                    notConfirmedUser = true;
                    nowUserNotConfirmed = true;
                } else if (!worker.getCompany().getId().equals(company.getId())) {
                    notConfirmedUser = true;
                    nowUserNotConfirmed = true;
//                return "redirect:/create-work-schedule?noSuchWorker";
                }
                if (!nowUserNotConfirmed) {
                    confirmedUsersCounter++;
                    String[] workScheduleDateSplittedArray = workScheduleNew.getDate().split("-");
                    String workScheduleDateSplittedString = null;
                    for (int j = 0; j < workScheduleDateSplittedArray.length; j++) {
                        if (j == 0) {
                            workScheduleDateSplittedString = workScheduleDateSplittedArray[j];
                        } else {
                            workScheduleDateSplittedString += workScheduleDateSplittedArray[j];
                        }
                    }
                    String[] nowDateSplittedArray = nowDate.split("-");
                    String nowDateSplittedString = null;
                    for (int k = 0; k < nowDateSplittedArray.length; k++) {
                        if (k == 0) {
                            nowDateSplittedString = nowDateSplittedArray[k];
                        } else {
                            nowDateSplittedString += nowDateSplittedArray[k];
                        }
                    }
                    int workScheduleDate = Integer.parseInt(workScheduleDateSplittedString);
                    int nowDateInt = Integer.parseInt(nowDateSplittedString);
                    if (workScheduleDate < nowDateInt) {
                        return "redirect:/create-work-schedule?noBackDate";
                    }
                    workSchedule.setDate(workScheduleNew.getDate());
                    workSchedule.setWorker(worker);
                    workSchedule.setWorkerDone(false);
                    workSchedule.setCompany(company);
                    workScheduleRepository.save(workSchedule);
                } else {
                    nowUserNotConfirmed = false;
                }
            }
        }
        if (notConfirmedUser) {
            if(confirmedUsersCounter==0) {
                return "redirect:/create-work-schedule?noSuchUser";
            } else {
                return "redirect:/create-work-schedule?missingWorkers";
            }
        }
        return "redirect:/company-page?id=" + company.getId()+"&workScheduleOk";
    }

    @RequestMapping(value = "/work-schedule-report", method = RequestMethod.GET)
    public String workReport(Long id, Principal principal, Model model) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        User user = userRepository.findByEmail(principal.getName());
        Worker worker = workerRepository.showWorkerByUserId(user.getId());
        WorkSchedule workSchedule = workScheduleRepository.getOne(id);
        if (workSchedule == null) {
            return "redirect:/company-home?notYourWorkSchedule";
        } else if (workSchedule.isWorkerDone()) {
            return "redirect:/company-home?cantEditReport";
        } else if (!user.getWorkingStatus().equals("worker")) {
            return "redirect:/company-home?notWorker";
        } else if (!workSchedule.getWorker().getId().equals(worker.getId())) {
            return "redirect:/company-home?notYourWorkSchedule";
        }
        model.addAttribute("workScheduleInfo", workSchedule);
        model.addAttribute("workScheduleFull", new WorkSchedule());
        workScheduleId = id;
        return "work-schedule-report";
    }

    @RequestMapping(value = "/work-schedule-report-post", method = RequestMethod.POST)
    public String workReportPost(@Valid WorkSchedule workScheduleNew) {
        WorkSchedule workScheduleOld = workScheduleRepository.getOne(workScheduleId);
        workScheduleId = null;
        workScheduleOld.setWorkerDone(true);
        workScheduleOld.setEndDateAndTime(workScheduleNew.getEndDateAndTime());
        workScheduleOld.setWorkerReport(workScheduleNew.getWorkerReport());
        int oldWorkedDays = workScheduleOld.getWorker().getMonthWorkedDays();
        workScheduleOld.getWorker().setMonthWorkedDays(oldWorkedDays + 1);
        workScheduleRepository.save(workScheduleOld);
        return "redirect:/company-home?reportOk";
    }

    @RequestMapping(value = "/report-view", method = RequestMethod.GET)
    public String workReportView(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("workScheduleInfo", workScheduleRepository.getOne(id));
        return "work-schedule-report-view";
    }

    @RequestMapping(value = "/view-logs", method = RequestMethod.GET)
    public String viewUserLogs(long id, Model model, Principal principal) {
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        List<WorkSchedule> workSchedules = workScheduleRepository.showAllWorkScheduleByUserId(id);
        model.addAttribute("userLogs", workSchedules);
        List<WorkSchedule> endedWorks = new ArrayList<>();
        List<WorkSchedule> notEndedWorks = new ArrayList<>();
        for (WorkSchedule schedule : workSchedules) {
            if (schedule.isWorkerDone()) {
                endedWorks.add(schedule);
            } else {
                notEndedWorks.add(schedule);
            }
        }
        model.addAttribute("userDetails", userRepository.getOne(id));
        model.addAttribute("userLogsCounter", workSchedules.size());
        model.addAttribute("endedWorks", endedWorks.size());
        model.addAttribute("notEndedWorks", notEndedWorks.size());
        return "view-logs";
    }

    @RequestMapping(value = "/delete-workschedule")
    public String deleteWorkSchedule(Long id) {
        workScheduleRepository.deleteById(id);
        return "redirect:/company-home?deleteok";
    }

    @RequestMapping(value = "/income-page", method = RequestMethod.GET)
    public String companyIncome(Model model, Principal principal, Long id){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String nowDate = formatter.format(date);
        Company company = companyRepository.getOne(id);
        User me = userRepository.findByEmail(principal.getName());
        if(!company.getUser().equals(me)){
            return "redirect:/company-home?notYourCompany";
        } else {
            model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
            model.addAttribute("companyIncomesCounter", incomeRepository.showLast12IncomesByCompanyId(id).size());
            model.addAttribute("companyIncomes", incomeRepository.showLast12IncomesByCompanyId(id));
            model.addAttribute("fullIncomeCash", company.getIncome());
            String[] payDayDateSplittedArray = company.getPaydayDate().split("-");
            String payDayDateSplittedString = null;
            for (int j = 0; j < payDayDateSplittedArray.length; j++) {
                if (j == 0) {
                    payDayDateSplittedString = payDayDateSplittedArray[j];
                } else {
                    payDayDateSplittedString += payDayDateSplittedArray[j];
                }
            }
            String[] nowDateSplittedArray = nowDate.split("-");
            String nowDateSplittedString = null;
            for (int k = 0; k < nowDateSplittedArray.length; k++) {
                if (k == 0) {
                    nowDateSplittedString = nowDateSplittedArray[k];
                } else {
                    nowDateSplittedString += nowDateSplittedArray[k];
                }
            }
            int payDayDate = Integer.parseInt(payDayDateSplittedString);
            int nowDateInt = Integer.parseInt(nowDateSplittedString);
            if(nowDateInt>=payDayDate) {
                model.addAttribute("paydayDate", company.getPaydayDate());
                int paydayFullSum = 0;
                List<Worker> companyWorkers = workerRepository.showWorkersByCompanyId(company.getId());
                String[] nowYearAndMonth = nowDate.split("-");
                int year = Integer.parseInt(nowYearAndMonth[0]);
                int month = Integer.parseInt(nowYearAndMonth[1]);
                for (Worker companyWorker : companyWorkers) {
                    YearMonth yearMonthObject = YearMonth.of(year, month);
                    int daysInMonth = yearMonthObject.lengthOfMonth();
                    int workerPaycheckPerDay = companyWorker.getSalary()/daysInMonth;
                    int workerPaycheckFull = companyWorker.getMonthWorkedDays()*workerPaycheckPerDay;
                    paydayFullSum += workerPaycheckFull;
                }
                model.addAttribute("paydaySum", paydayFullSum);
            } else {
                model.addAttribute("paydayDate", "0");
            }
            return "/income-page";
        }
    }

    @RequestMapping(value = "/income-page", method = RequestMethod.POST)
    public String insertIncome(Model model, Principal principal, Income income){
//        User me = userRepository.findByEmail(principal.getName());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        Company company = companyRepository.showOneUserCompany(principal.getName());
        income.setCompany(company);
        income.setIncomeDate(formatter.format(date));
        incomeRepository.save(income);
        int nowProfit = company.getProfit();
        int nowIncome = company.getIncome();
        company.setIncome(nowIncome+income.getIncomePrice());
        company.setProfit(nowProfit+income.getIncomePrice());
        companyRepository.save(company);
        return "redirect:/income-page?id="+company.getId();
    }

    @RequestMapping(value = "/income-by-date", method = RequestMethod.GET)
    public String getIncomeByDate(Model model, Principal principal, @RequestParam("incomeDay") String incomeDay){
        User me = userRepository.findByEmail(principal.getName());
        if(!me.getWorkingStatus().equals("owner")){
            return "redirect:/comapny-home?notOwner";
        }
        Company company = companyRepository.showOneUserCompany(principal.getName());
        model.addAttribute("userPMs", userRepository.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("incomeDetailsCounter", incomeRepository.showAllIncomesByCompanyIdAndDate(company.getId(), incomeDay).size());
        model.addAttribute("incomeDetails", incomeRepository.showAllIncomesByCompanyIdAndDate(company.getId(), incomeDay));
        model.addAttribute("incomeDay", incomeDay);

        return "/income-by-date";
    }

    @RequestMapping(value = "/salaries-pay", method = RequestMethod.POST)
    public String salariesPay(Model model, Principal principal, Long id){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String nowDate = formatter.format(date);
        User me = userRepository.findByEmail(principal.getName());
        Company company = companyRepository.showOneUserCompany(principal.getName());
        int paydayFullSum = 0;
        List<Worker> companyWorkers = workerRepository.showWorkersByCompanyId(company.getId());
        String[] nowYearAndMonth = nowDate.split("-");
        int year = Integer.parseInt(nowYearAndMonth[0]);
        int month = Integer.parseInt(nowYearAndMonth[1]);
        for (Worker companyWorker : companyWorkers) {
            YearMonth yearMonthObject = YearMonth.of(year, month);
            int daysInMonth = yearMonthObject.lengthOfMonth();
            int workerPaycheckPerDay = companyWorker.getSalary()/daysInMonth;
            int workerPaycheckFull = companyWorker.getMonthWorkedDays()*workerPaycheckPerDay;
            paydayFullSum += workerPaycheckFull;
        }
        if(company.getIncome()<paydayFullSum){
            return "redirect:/income-page?id="+company.getId()+"&notEnoughCash";
        } else {
            String nowPayday = company.getPaydayDate();
            String[] nowPaydayArraySplitted = nowPayday.split("-");
            String newPayDay = "";
            int paydayMonthPlusOne = Integer.parseInt(nowPaydayArraySplitted[1])+1;
            if(Integer.parseInt(nowPaydayArraySplitted[1])<10){
                newPayDay = nowPaydayArraySplitted[0]+"-0"+String.valueOf(paydayMonthPlusOne)+"-"+nowPaydayArraySplitted[2];
            }
            if(paydayMonthPlusOne>12){
                paydayMonthPlusOne=1;
                int paydayYearPlusOne = Integer.parseInt(nowPaydayArraySplitted[0])+1;
                nowPaydayArraySplitted[0] = String.valueOf(paydayYearPlusOne);
                newPayDay = nowPaydayArraySplitted[0]+"-0"+String.valueOf(paydayMonthPlusOne)+"-"+nowPaydayArraySplitted[2];

            } else {
                newPayDay = nowPaydayArraySplitted[0]+"-"+String.valueOf(paydayMonthPlusOne)+"-"+nowPaydayArraySplitted[2];
            }
            company.setPaydayDate(newPayDay);
            int nowProfit = company.getProfit();
            company.setProfit(nowProfit-paydayFullSum);
            for (Worker worker : companyWorkers) {
                Salary salary = new Salary();
                salary.setDate(company.getPaydayDate());
                YearMonth yearMonthObject = YearMonth.of(year, month);
                int daysInMonth = yearMonthObject.lengthOfMonth();
                int workerPaycheckPerDay = worker.getSalary()/daysInMonth;
                salary.setPaycheckPerDay(workerPaycheckPerDay);
                int workerPaycheckFull = worker.getMonthWorkedDays()*workerPaycheckPerDay;
                salary.setSalarySum(workerPaycheckFull);
                salary.setWorkedDays(worker.getMonthWorkedDays());
                salary.setWorker(worker);
                worker.setMonthWorkedDays(0);
                workerRepository.save(worker);
                salaryRepository.save(salary);
            }
            companyRepository.save(company);
        }
        return "redirect:/income-page?id="+company.getId()+"&salaryPayOk";
    }

    @RequestMapping("/deleteIncome")
    public String deleteIncome(Principal principal, Long id){
        Company company = companyRepository.showOneUserCompany(principal.getName());
        Income income = incomeRepository.getOne(id);
        int nowIncome = company.getIncome();
        int nowProfit = company.getProfit();
        company.setIncome(nowIncome-income.getIncomePrice());
        company.setProfit(nowProfit-income.getIncomePrice());
        incomeRepository.deleteById(id);
        companyRepository.save(company);
        return "redirect:/income-page?id="+company.getId()+"&incomeDeleted";
    }

    @RequestMapping("/hideIncome")
    public String hideIncome(Principal principal, Long id){
        Company company = companyRepository.showOneUserCompany(principal.getName());
        incomeRepository.deleteById(id);
        return "redirect:/income-page?id="+company.getId()+"&incomeHided";
    }
}
