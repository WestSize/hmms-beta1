package com.example.hmmsbeta1.web.services.OfficeServices;

import com.example.hmmsbeta1.web.entities.Office;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OfficeService {
    List<Office> showOfficesByCompanyId(Long id);
    void save(Office office);
    Office getOne(Long id);
    List<Office> findAll();
    void deleteById(Long id);
}
