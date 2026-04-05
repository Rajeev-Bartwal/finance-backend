package com.finance.config;

import com.finance.enums.Role;
import com.finance.enums.TransactionType;
import com.finance.models.Transaction;
import com.finance.models.User;
import com.finance.repositories.TransactionRepository;
import com.finance.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedTransactions();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        String hashed = passwordEncoder.encode("password123");

        userRepository.saveAll(List.of(
            User.builder().username("admin").email("admin@finance.local")
                .password(hashed).role(Role.ADMIN).active(true).build(),
            User.builder().username("analyst").email("analyst@finance.local")
                .password(hashed).role(Role.ANALYST).active(true).build(),
            User.builder().username("viewer").email("viewer@finance.local")
                .password(hashed).role(Role.VIEWER).active(true).build()
        ));

        log.info("Seeded users — admin / analyst / viewer (password: password123)");
    }

    private void seedTransactions() {
        if (transactionRepository.count() > 0) return;

        User admin = userRepository.findByUsername("admin").orElseThrow();
        LocalDate today = LocalDate.now();

        transactionRepository.saveAll(List.of(
            tx(52000, TransactionType.INCOME,  "Salary",      today.minusDays(2),           "Monthly salary",             admin),
            tx(8500,  TransactionType.INCOME,  "Freelance",   today.minusDays(10),          "Website project payment",    admin),
            tx(3200,  TransactionType.INCOME,  "Investment",  today.minusDays(15),          "Mutual fund returns",        admin),
            tx(1500,  TransactionType.INCOME,  "Freelance",   today.minusDays(22),          "Logo design project",        admin),
            tx(52000, TransactionType.INCOME,  "Salary",      today.minusMonths(1).minusDays(2),  "Monthly salary",       admin),
            tx(4200,  TransactionType.INCOME,  "Investment",  today.minusMonths(1).minusDays(18), "Dividend payout",      admin),
            tx(52000, TransactionType.INCOME,  "Salary",      today.minusMonths(2).minusDays(2),  "Monthly salary",       admin),
            tx(6000,  TransactionType.INCOME,  "Freelance",   today.minusMonths(2).minusDays(8),  "Mobile app UI work",   admin),
            tx(18500, TransactionType.EXPENSE, "Rent",        today.minusDays(1),           "Monthly apartment rent",     admin),
            tx(3200,  TransactionType.EXPENSE, "Groceries",   today.minusDays(3),           "Supermarket weekly shop",    admin),
            tx(1200,  TransactionType.EXPENSE, "Utilities",   today.minusDays(5),           "Electricity and water bill", admin),
            tx(850,   TransactionType.EXPENSE, "Transport",   today.minusDays(7),           "Metro pass + Uber",          admin),
            tx(2400,  TransactionType.EXPENSE, "Dining",      today.minusDays(9),           "Restaurants and delivery",   admin),
            tx(5999,  TransactionType.EXPENSE, "Electronics", today.minusDays(12),          "Headphones",                 admin),
            tx(800,   TransactionType.EXPENSE, "Healthcare",  today.minusDays(16),          "Doctor + medicine",          admin),
            tx(18500, TransactionType.EXPENSE, "Rent",        today.minusMonths(1).minusDays(1),  "Monthly apartment rent", admin),
            tx(2900,  TransactionType.EXPENSE, "Groceries",   today.minusMonths(1).minusDays(5),  "Supermarket",          admin),
            tx(1200,  TransactionType.EXPENSE, "Utilities",   today.minusMonths(1).minusDays(6),  "Monthly bills",        admin),
            tx(18500, TransactionType.EXPENSE, "Rent",        today.minusMonths(2).minusDays(1),  "Monthly apartment rent", admin),
            tx(3100,  TransactionType.EXPENSE, "Groceries",   today.minusMonths(2).minusDays(4),  "Weekly shops",         admin)
        ));

        log.info("Seeded 20 sample transactions");
    }

    private Transaction tx(double amount, TransactionType type, String category,
                            LocalDate date, String notes, User creator) {
        return Transaction.builder()
                .amount(BigDecimal.valueOf(amount))
                .type(type)
                .category(category)
                .date(date)
                .notes(notes)
                .createdBy(creator)
                .build();
    }
}
