package com.igniscore.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.igniscore.api.dto.client.ClientRegisterDTO;
import com.igniscore.api.dto.client.ClientUpdateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a client within the system.
 *
 * <p>This class models a client associated with a specific {@link Company},
 * supporting a multi-tenant architecture where each company manages its own clients.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Store client identification and contact information</li>
 *     <li>Maintain association with a company (tenant isolation)</li>
 *     <li>Support unique client numbering within a company</li>
 * </ul>
 *
 * <p>Database constraints:
 * <ul>
 *     <li>Each client has a unique "number" within its company</li>
 *     <li>Enforced via composite unique constraint (company + number)</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})
@Entity
@Table(
        name = "clients",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_client_per_company",
                        columnNames = {"fk_id_company", "number_client"}
                )
        }
)
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_client")
    private Integer id;

    /**
     * Client name entity name.
     */
    @Column(name = "name_client")
    private String name;

    /**
     * Client legal entity name.
     */
    @Column(name = "legal_name")
    private String legal;

    /**
     * Client CNPJ (Brazilian business identifier).
     */
    @Column(name = "cnpj_client")
    private String cnpj;

    /**
     * Client contact email.
     */
    @Column(name = "email_client")
    private String email;

    /**
     * Client phone number.
     */
    @Column(name = "phone_client")
    private String phone;

    /**
     * Sequential client number within a company.
     *
     * <p>This field is managed at the database level
     * (not insertable or updatable from application code).
     */
    @Column(name = "number_client", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private Integer number;

    /**
     * State registration (Inscrição Estadual).
     */
    @Column(name = "ie_client")
    private String ie;

    /**
     * State abbreviation associated with IE.
     */
    @Column(name = "uf_ie_client")
    private String uf_ie;

    /**
     * Additional notes or observations about the client.
     */
    @Column(name = "obs_client")
    private String obs;

    /**
     * Client CPF (for individuals).
     */
    @Column(name = "cpf_client")
    private String cpf;

    /**
     * Associated company (multi-tenant relationship).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company", nullable = false)
    @JsonIgnore
    private Company company;

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Address> addresses = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    /**
     * Constructs a client from registration data.
     *
     * @param dto client registration data
     * @param company associated company
     */
    public Client(ClientRegisterDTO dto, Company company) {
        this.name = dto.getName();
        this.legal = dto.getLegal();
        this.cnpj = dto.getCnpj();
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
        this.ie = dto.getIe();
        this.uf_ie = dto.getUfIe();
        this.obs = dto.getObs();
        this.cpf = dto.getCpf();
        this.company = company;
    }

    /**
     * Copy constructor.
     *
     * @param client client to copy
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Client(Client client) {
        this.id = client.id;
        this.name = client.name;
        this.legal = client.legal;
        this.cnpj = client.cnpj;
        this.email = client.email;
        this.phone = client.phone;
        this.ie = client.ie;
        this.uf_ie = client.uf_ie;
        this.obs = client.obs;
        this.cpf = client.cpf;
    }

    /**
     * Updates the client fields with non-null values from the DTO.
     *
     * @param dto client update data
     */
    public void update(ClientUpdateDTO dto) {
        if (dto.getName() != null) this.name = dto.getName();
        if (dto.getLegal() != null) this.legal = dto.getLegal();
        if (dto.getCnpj() != null) this.cnpj = dto.getCnpj();
        if (dto.getEmail() != null) this.email = dto.getEmail();
        if (dto.getPhone() != null) this.phone = dto.getPhone();
        if (dto.getIe() != null) this.ie = dto.getIe();
        if (dto.getUfIe() != null) this.uf_ie = dto.getUfIe();
        if (dto.getObs() != null) this.obs = dto.getObs();
        if (dto.getCpf() != null) this.cpf = dto.getCpf();
    }
}