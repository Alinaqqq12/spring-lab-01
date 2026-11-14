
package kz.iitu.springlab.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_records")
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(name = "propagation_type", nullable = false)
    private String propagationType;

    protected AuditRecord() {
    }

    public AuditRecord(String message, String propagationType) {
        this.message = message;
        this.propagationType = propagationType;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getPropagationType() {
        return propagationType;
    }
}
