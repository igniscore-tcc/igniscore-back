package com.igniscore.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_audit")
    private Integer id;

    @Column(name = "entity_name_audit")
    private String entity;

    @Column(name = "action_audit")
    private String action;

    @Column(name = "old_data_audit")
    private String old_data;

    @Column(name = "new_data_audit")
    private String new_data;

    @Column(name = "ip_address_audit")
    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company", nullable = false)
    private Company company;

    public Integer getId() {
        return id;
    }

    public String getEntity() {
        return entity;
    }

    public String getAction() {
        return action;
    }

    public String getOldData() {
        return old_data;
    }

    public String getNewData() {
        return new_data;
    }

    public String getIp() {
        return ip;
    }

    public User getUser() {
        return user;
    }

    public Company getCompany() {
        return company;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setOldData(String old_data) {
        this.old_data = old_data;
    }

    public void setNewData(String new_data) {
        this.new_data = new_data;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
