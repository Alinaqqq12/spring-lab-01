
package kz.iitu.springlab.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository
        extends JpaRepository<AuditRecord, Long> {

    long countByPropagationType(String propagationType);
}
