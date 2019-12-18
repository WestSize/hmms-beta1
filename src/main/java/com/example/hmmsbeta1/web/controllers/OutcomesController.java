package com.example.hmmsbeta1.web.controllers;

import com.example.hmmsbeta1.web.entities.*;
import com.example.hmmsbeta1.web.services.CompanyServices.CompanyService;
import com.example.hmmsbeta1.web.services.OutcomeServices.OutcomeService;
import com.example.hmmsbeta1.web.services.SalaryServices.SalaryService;
import com.example.hmmsbeta1.web.services.UserService;
import com.example.hmmsbeta1.web.services.WorkerServices.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

@Controller
public class OutcomesController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private OutcomeService outcomeService;
    @Autowired
    private SalaryService salaryService;

    @RequestMapping(value = "/outcomes", method = RequestMethod.GET)
    public String getOutcomesPage(Principal principal, Model model, Long id){
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        User me = userService.findByEmail(principal.getName());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String nowDate = formatter.format(date);
        Company company = companyService.showOneUserCompany(principal.getName());
        if(!company.getUser().equals(me)){
            return "redirect:/company-home?notYourCompany";
        }
        model.addAttribute("outcomesCounter", outcomeService.findAllOutcomesByCompanyId(id).size());
        model.addAttribute("CompanyOutcomes", outcomeService.findLast12OutcomesByCompanyId(id));
        model.addAttribute("fullOutcomeCash", company.getOutcome());
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
            List<Worker> companyWorkers = workerService.showWorkersByCompanyId(company.getId());
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
        return "outcome-page";
    }

    @RequestMapping(value = "/outcomes", method = RequestMethod.POST)
    public String addOutCome(Principal principal, Model model, Outcome outcome){
        Company company = companyService.showOneUserCompany(principal.getName());
        if(outcome.getOutcomePrice()<0){
            return "redirect:/outcomes?id="+company.getId()+"&noMinusError";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        outcome.setCompany(company);
        outcome.setOutcomeDate(formatter.format(date));
        outcomeService.save(outcome);
        int companyNowProfit = company.getProfit();
        company.setProfit(companyNowProfit-outcome.getOutcomePrice());
        int companyNowOutcome = company.getOutcome();
        company.setOutcome(companyNowOutcome+outcome.getOutcomePrice());
        companyService.save(company);
        return "redirect:/outcomes?id="+company.getId()+"&outcomeAdded";
    }
    @RequestMapping(value = "/outcomes-by-date", method = RequestMethod.GET)
    public String findOutcomesByDate(Principal principal, Model model, @RequestParam("outcomeDay") String outcomeDay){
        User me = userService.findByEmail(principal.getName());
        if (!me.getWorkingStatus().equals("owner")){
            return "redirect:/company-home?notOwner";
        }
        Company company = companyService.showOneUserCompany(principal.getName());
        model.addAttribute("userPMs", userService.findByEmail(principal.getName()).getUnreadedMessages());
        model.addAttribute("outcomesCounter", outcomeService.findOutcomesByCompanyIdAndDate(company.getId(), outcomeDay).size());
        model.addAttribute("outcomesList", outcomeService.findOutcomesByCompanyIdAndDate(company.getId(), outcomeDay));
        model.addAttribute("outcomeDay", outcomeDay);
        return "outcomes-by-date";
    }

    @RequestMapping("/deleteoutcome")
    public String deleteOutcome(Principal principal, Long id){
        Company company = companyService.showOneUserCompany(principal.getName());
        Outcome outcome = outcomeService.getOne(id);
        int nowOutcome = company.getOutcome();
        int nowProfit = company.getProfit();
        company.setOutcome(nowOutcome-outcome.getOutcomePrice());
        company.setProfit(nowProfit+outcome.getOutcomePrice());
        outcomeService.deleteById(id);
        companyService.save(company);
        return "redirect:/outcomes?id="+company.getId()+"&outcomeDeleted";
    }

    @RequestMapping("/hideoutcome")
    public String hideOutcome(Principal principal, Long id){
        Company company = companyService.showOneUserCompany(principal.getName());
        outcomeService.deleteById(id);
        return "redirect:/outcomes?id="+company.getId()+"&outcomeHided";
    }

    @RequestMapping(value = "/salaries-pay", method = RequestMethod.POST)
    public String salariesPay(Model model, Principal principal, Long id){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String nowDate = formatter.format(date);
        User me = userService.findByEmail(principal.getName());
        Company company = companyService.showOneUserCompany(principal.getName());
        int paydayFullSum = 0;
        List<Worker> companyWorkers = workerService.showWorkersByCompanyId(company.getId());
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
                workerService.save(worker);
                salaryService.save(salary);
            }
            companyService.save(company);
            Outcome outcome = new Outcome();
            outcome.setOutcomePrice(paydayFullSum);
            outcome.setOutcomeDate(nowDate);
            outcome.setCompany(company);
            outcome.setOutcomeDescription("Изплащане на заплати в размер на "+paydayFullSum+" лв.");
            outcomeService.save(outcome);
        }
        return "redirect:/outcomes?id="+company.getId()+"&salaryPayOk";
    }
}
