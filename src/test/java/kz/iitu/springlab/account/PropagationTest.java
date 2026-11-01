
package kz.iitu.springlab.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Lab9Application.class)
class PropagationTest {

    @Autowired
    private PropagationService propagationService;

    @Autowired
    private AuditRepository auditRepository;

    @Test
    void requiredRollsBack() {
        long before = auditRepository
                .countByPropagationType("REQUIRED");

        assertThrows(
                IllegalStateException.class,
                () -> propagationService.testRequired()
        );

        long after = auditRepository
                .countByPropagationType("REQUIRED");

        assertEquals(before, after);

        System.out.println(
                "TASK B REQUIRED PASSED: audit rolled back"
        );
    }

    @Test
    void requiresNewSurvivesRollback() {
        long before = auditRepository
                .countByPropagationType("REQUIRES_NEW");

        assertThrows(
                IllegalStateException.class,
                () -> propagationService.testRequiresNew()
        );

        long after = auditRepository
                .countByPropagationType("REQUIRES_NEW");

        assertEquals(before + 1, after);

        System.out.println(
                "TASK B REQUIRES_NEW PASSED: audit committed"
        );
    }
}
