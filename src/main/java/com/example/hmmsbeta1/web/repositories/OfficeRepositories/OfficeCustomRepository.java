package com.example.hmmsbeta1.web.repositories.OfficeRepositories;

import com.example.hmmsbeta1.web.entities.Office;

import java.util.List;

public interface OfficeCustomRepository {
    List<Office> showOfficesByCompanyId(Long id);
}
