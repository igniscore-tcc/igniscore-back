package com.igniscore.api.service;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.utils.CompanyUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private CompanyUtils companyUtils;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User updateUserCompany(Integer id, Integer company_id) {
        User user = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Company company = companyUtils.existsCompany(company_id);

        user.setCompany(company);
        return repository.save(user);
    }

    public User finUserId(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public User editUser(String email, String name) {
        User user = repository.findByEmail(email);
        user.setName(name);
        user.setEmail(email);


        return user;
    }


}