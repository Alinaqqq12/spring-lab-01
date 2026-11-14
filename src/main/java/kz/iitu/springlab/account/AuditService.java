
package kz.iitu.springlab.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRequired() {
        auditRepository.save(
                new AuditRecord("Required audit", "REQUIRED")
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRequiresNew() {
        auditRepository.save(
                new AuditRecord("Requires new audit", "REQUIRES_NEW")
        );
    }
}
