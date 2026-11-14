
package kz.iitu.springlab.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationService {

    private final AuditService auditService;

    public PropagationService(AuditService auditService) {
        this.auditService = auditService;
    }

    @Transactional
    public void testRequired() {
        auditService.saveRequired();

        throw new IllegalStateException(
                "Outer transaction failed: REQUIRED"
        );
    }

    @Transactional
    public void testRequiresNew() {
        auditService.saveRequiresNew();

        throw new IllegalStateException(
                "Outer transaction failed: REQUIRES_NEW"
        );
    }
}
