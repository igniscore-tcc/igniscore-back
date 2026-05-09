package com.igniscore.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igniscore.api.model.Audit;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.AuditRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditUtils {

    private final AuditRepository repository;
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    public AuditUtils(
            AuditRepository repository,
            HttpServletRequest request,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.request = request;
        this.objectMapper = objectMapper;
    }

    public void newAudit(
            User user,
            Company company,
            String entity,
            String action,
            Object oldData,
            Object newData
    ) {

        try {

            String ip = IpUtils.getClientIp(request);

            Audit audit = new Audit();

            audit.setEntity(entity);
            audit.setAction(action);

            audit.setUser(user);
            audit.setCompany(company);

            audit.setOldData(
                    objectMapper.convertValue(oldData, Map.class)
            );

            audit.setNewData(
                    objectMapper.convertValue(newData, Map.class)
            );

            audit.setIp(ip);

            repository.save(audit);

        } catch (Exception e) {
            throw new RuntimeException("Error creating audit log", e);
        }
    }
}