package com.example.hmmsbeta1.web.services.OfficeServices;

import com.example.hmmsbeta1.web.entities.Office;
import com.example.hmmsbeta1.web.repositories.OfficeRepositories.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeServiceImpl implements OfficeService {

    @Autowired
    private OfficeRepository officeRepository;

    @Override
    public List<Office> showOfficesByCompanyId(Long id) {
        List<Office> offices = officeRepository.showOfficesByCompanyId(id);
        return offices;
    }

    @Override
    public void save(Office office) {
        officeRepository.save(office);
    }

    @Override
    public Office getOne(Long id) {
        Office office = officeRepository.getOne(id);
        return office;
    }

    @Override
    public List<Office> findAll() {
        List offices = officeRepository.findAll();
        if(offices.size()==0 || offices.isEmpty() || offices == null) {
            return null;
        } else {
            return  offices;
        }
    }

    @Override
    public void deleteById(Long id) {
        officeRepository.deleteById(id);
    }
}
