
package kz.iitu.springlab.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = Lab9Application.class,
        properties = {
                "spring.datasource.url=jdbc:postgresql://localhost:5432/springlab9"
        }
)
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Long aliceId;
    private Long bobId;

    @BeforeEach
    void setUp() {

        // Reset only the accounts used by these tests.
        accountRepository.deleteAll();

        Account alice = accountRepository.save(
                new Account("Alice", new BigDecimal("1000.00"))
        );

        Account bob = accountRepository.save(
                new Account("Bob", new BigDecimal("500.00"))
        );

        aliceId = alice.getId();
        bobId = bob.getId();
    }

    @Test
    void successfulTransfer() {

        accountService.transfer(
                aliceId,
                bobId,
                new BigDecimal("100.00")
        );

        assertEquals(
                0,
                accountService.getAccount(aliceId)
                        .getBalance()
                        .compareTo(new BigDecimal("900.00"))
        );

        assertEquals(
                0,
                accountService.getAccount(bobId)
                        .getBalance()
                        .compareTo(new BigDecimal("600.00"))
        );

        System.out.println(
                "TEST 1 PASSED: Alice=900.00, Bob=600.00"
        );
    }

    @Test
    void insufficientFundsDoesNotChangeBalances() {

        assertThrows(
                IllegalStateException.class,
                () -> accountService.transfer(
                        aliceId,
                        bobId,
                        new BigDecimal("2000.00")
                )
        );

        assertBalancesUnchanged();

        System.out.println(
                "TEST 2 PASSED: Insufficient funds, balances unchanged"
        );
    }

    @Test
    void runtimeExceptionRollsBackTransfer() {

        assertThrows(
                IllegalStateException.class,
                () -> accountService.transferWithFailure(
                        aliceId,
                        bobId,
                        new BigDecimal("100.00")
                )
        );

        assertBalancesUnchanged();

        System.out.println(
                "TEST 3 PASSED: Rollback successful"
        );
    }

    private void assertBalancesUnchanged() {

        assertEquals(
                0,
                accountService.getAccount(aliceId)
                        .getBalance()
                        .compareTo(new BigDecimal("1000.00"))
        );

        assertEquals(
                0,
                accountService.getAccount(bobId)
                        .getBalance()
                        .compareTo(new BigDecimal("500.00"))
        );
    }
}
