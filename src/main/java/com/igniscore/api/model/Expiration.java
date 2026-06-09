package com.igniscore.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "expirations")
public class Expiration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_expiration")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_sale", nullable = false)
    @JsonIgnore
    private Sale sale;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_expiration")
    private ExpirationStatus status;


    public Expiration() {
    }

    public Expiration(Sale sale, ExpirationStatus status) {
        this.sale = sale;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public Sale getSale() {
        return sale;
    }

    public ExpirationStatus getStatus() {
        return status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public void setStatus(ExpirationStatus status) {
        this.status = status;
    }
}
