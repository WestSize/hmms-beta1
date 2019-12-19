package com.example.hmmsbeta1.web.services.CompanyServices;

import com.example.hmmsbeta1.web.entities.Company;
import com.example.hmmsbeta1.web.entities.Income;
import com.example.hmmsbeta1.web.entities.Outcome;
import com.example.hmmsbeta1.web.entities.User;
import com.example.hmmsbeta1.web.repositories.CompanyRepositories.CompanyRepository;
import com.example.hmmsbeta1.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

    public CompanyServiceImpl(CompanyRepository companyRepository, UserService userService) {
        this.companyRepository = companyRepository;
        this.userService = userService;
    }

    @Override
    public List showOnlyUsersCompanies(String email) {
        List<Company> companies = companyRepository.showOnlyUsersCompanies(email);
        return companies;
    }

    @Override
    public Company showOneUserCompany(String email) {
        Company company = companyRepository.showOneUserCompany(email);
        return company;
    }

    @Override
    public Company showCompanyByUserId(Long id) {
        Company company = showCompanyByUserId(id);
        return company;
    }

    @Override
    public void save(Company company, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM-dd");
        Date date = new Date(System.currentTimeMillis());
        company.setUser(user);
        company.setNumberOfOffices(0);
        company.setNumberOfWorkers(0);
        company.setPaydayDate(formatter.format(date));
        company.setProfit((company.getIncome() - company.getInvestment()));
        company.setCircularLetter("Здравейте! Поканен сте на интервю за работата, която сте кандидатствали" +
                " във фирмата '" + company.getName() + "'. Моля напишете ни удобен ден и час за среща в наш офис или " +
                "се свържете с нас на тел: (не е зададен). Поздрави, " + user.getFirstName() + " " + user.getLastName() + " (управител). :)");
        company.setOutcome(0);
        user.setWorkingStatus("owner");
        companyRepository.save(company);
    }

    @Override
    public Company getOne(Long id) {
        Company company = companyRepository.getOne(id);
        return company;
    }

    @Override
    public List<Company> findAll() {
        List<Company> companies = companyRepository.findAll();
        return companies;
    }

    @Override
    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public void update(Company company, Long companyId) {
        Company oldCompany = companyRepository.getOne(companyId);
        oldCompany.setName(company.getName());
        oldCompany.setDescription(company.getDescription());
        oldCompany.setPhoneNumber(company.getPhoneNumber());
        oldCompany.setPaydayDate(company.getPaydayDate());
        companyRepository.save(oldCompany);
    }

    @Override
    public void fireWorker(Company company) {
        int companyNowWorkers = company.getNumberOfWorkers();
        company.setNumberOfWorkers(companyNowWorkers-1);
        companyRepository.save(company);
    }

    @Override
    public void updateCircullarLetter(Company company, Principal principal) {
        Company oldCompany = companyRepository.showOneUserCompany(principal.getName());
        oldCompany.setCircularLetter(company.getCircularLetter());
        Long redirectId = oldCompany.getId();
        companyRepository.save(oldCompany);
    }

    @Override
    public void setCompanyWorkersPlusOne(Company company) {
        int companyWorkersCounter = company.getNumberOfWorkers();
        company.setNumberOfWorkers(companyWorkersCounter + 1);
        companyRepository.save(company);
    }

    @Override
    public void setCompanyIncomeAndProfitPlusOne(Company company, Income income) {
        int nowProfit = company.getProfit();
        int nowIncome = company.getIncome();
        company.setIncome(nowIncome+income.getIncomePrice());
        company.setProfit(nowProfit+income.getIncomePrice());
        companyRepository.save(company);
    }

    @Override
    public void setCompanyIncomeAndProfitMinus(Company company, Income income) {
        int nowIncome = company.getIncome();
        int nowProfit = company.getProfit();
        company.setIncome(nowIncome-income.getIncomePrice());
        company.setProfit(nowProfit-income.getIncomePrice());
        companyRepository.save(company);
    }

    @Override
    public void addCompanyOutcome(Company company, Outcome outcome) {
        int companyNowProfit = company.getProfit();
        company.setProfit(companyNowProfit-outcome.getOutcomePrice());
        int companyNowOutcome = company.getOutcome();
        company.setOutcome(companyNowOutcome+outcome.getOutcomePrice());
        companyRepository.save(company);
    }

    @Override
    public void companyRemoveOutcome(Company company, Outcome outcome) {
        int nowOutcome = company.getOutcome();
        int nowProfit = company.getProfit();
        company.setOutcome(nowOutcome-outcome.getOutcomePrice());
        company.setProfit(nowProfit+outcome.getOutcomePrice());
        companyRepository.save(company);
    }

    @Override
    public void companyPaySalaries(Company company, int SalarySum) {
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
        company.setProfit(nowProfit-SalarySum);
        companyRepository.save(company);
    }
}
