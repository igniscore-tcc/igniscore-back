package com.igniscore.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_plan")
    private Integer id;

    @Column(name = "name_plan", nullable = false, length = 50)
    private String name;

    @Column(name = "code_plan", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "max_users_plan")
    private Integer maxUsers;

    @Column(name = "max_products_plan")
    private Integer maxProducts;

    @Column(name = "max_clients_plan")
    private Integer maxClients;

    @Column(name = "max_sales_month_plan")
    private Integer maxSalesPerMonth;

    @Column(name = "advanced_reports_plan", nullable = false)
    private Boolean advancedReports = false;

    @Column(name = "financial_management_plan", nullable = false)
    private Boolean financialManagement = false;

    @Column(name = "pdf_documents_plan", nullable = false)
    private Boolean pdfDocuments = false;

    @Column(name = "automation_plan", nullable = false)
    private Boolean automation = false;

    @Column(name = "integrations_plan", nullable = false)
    private Boolean integrations = false;

    @Column(name = "priority_support_plan", nullable = false)
    private Boolean prioritySupport = false;

    @Column(name = "active_plan", nullable = false)
    private Boolean active = true;
}