
package kz.iitu.springlab.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Lab9Application.class)
class OptimisticLockingTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void staleUpdateMustFail() {

        Account created = accountRepository.save(
                new Account(
                        "OptimisticLockTest",
                        new BigDecimal("1000.00")
                )
        );

        Long id = created.getId();

        try {
            // Two independent reads produce two copies.
            Account first = accountRepository.findById(id)
                    .orElseThrow();

            Account second = accountRepository.findById(id)
                    .orElseThrow();

            assertEquals(
                    first.getVersion(),
                    second.getVersion()
            );

            // First user updates and commits.
            first.credit(new BigDecimal("100.00"));
            accountRepository.saveAndFlush(first);

            Account afterFirst = accountRepository.findById(id)
                    .orElseThrow();

            assertEquals(
                    0,
                    afterFirst.getBalance().compareTo(
                            new BigDecimal("1100.00")
                    )
            );

            System.out.println(
                    "FIRST UPDATE PASSED: balance=1100.00"
            );

            // Second user modifies the stale copy.
            second.credit(new BigDecimal("200.00"));

            assertThrows(
                    ObjectOptimisticLockingFailureException.class,
                    () -> accountRepository.saveAndFlush(second)
            );

            System.out.println(
                    "OPTIMISTIC LOCK CONFLICT DETECTED"
            );

            // Confirm first update was preserved.
            Account finalAccount = accountRepository.findById(id)
                    .orElseThrow();

            assertEquals(
                    0,
                    finalAccount.getBalance().compareTo(
                            new BigDecimal("1100.00")
                    )
            );

            System.out.println(
                    "TASK D PASSED: stale update rejected"
            );

        } finally {
            accountRepository.deleteById(id);
        }
    }
}
