package com.finance;

import com.finance.dto.request.CreateTransactionRequest;
import com.finance.dto.request.UpdateTransactionRequest;
import com.finance.dto.response.TransactionResponse;
import com.finance.enums.TransactionType;
import com.finance.exception.BadRequestException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.models.Transaction;
import com.finance.models.User;
import com.finance.repositories.TransactionRepository;
import com.finance.repositories.UserRepository;
import com.finance.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private TransactionService transactionService;

    private User mockUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("analyst");
        return u;
    }

    private CreateTransactionRequest validCreateRequest() {
        CreateTransactionRequest req = mock(CreateTransactionRequest.class);
        when(req.getAmount()).thenReturn(BigDecimal.valueOf(1500));
        when(req.getType()).thenReturn(TransactionType.INCOME);
        when(req.getCategory()).thenReturn("Salary");
        when(req.getDate()).thenReturn(LocalDate.now());
        when(req.getNotes()).thenReturn("Monthly salary");
        return req;
    }

    @Test
    void create_success() {
        CreateTransactionRequest req = validCreateRequest();
        User user = mockUser();

        when(userRepository.findByUsername("analyst")).thenReturn(Optional.of(user));
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(
            Transaction.builder()
                .id(1L).amount(req.getAmount()).type(req.getType())
                .category(req.getCategory()).date(req.getDate())
                .notes(req.getNotes()).createdBy(user).build()
        ));

        TransactionResponse response = transactionService.create(req, "analyst");

        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1500));
        assertThat(response.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(response.getCategory()).isEqualTo("Salary");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(transactionRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.delete(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_onlyChangesProvidedFields() {
        Transaction existing = Transaction.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(1000))
                .type(TransactionType.INCOME)
                .category("Salary")
                .date(LocalDate.now().minusDays(5))
                .notes("Old notes")
                .createdBy(mockUser())
                .build();

        UpdateTransactionRequest req = mock(UpdateTransactionRequest.class);
        when(req.getAmount()).thenReturn(BigDecimal.valueOf(2000));
        when(req.getType()).thenReturn(null);
        when(req.getCategory()).thenReturn(null);
        when(req.getDate()).thenReturn(null);
        when(req.getNotes()).thenReturn(null);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(transactionRepository.save(any())).thenReturn(existing);

        transactionService.update(1L, req);

        assertThat(existing.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(2000));
        assertThat(existing.getCategory()).isEqualTo("Salary");
        assertThat(existing.getNotes()).isEqualTo("Old notes");
    }

    @Test
    void getAll_throwsBadRequest_onInvalidType() {
        assertThatThrownBy(() ->
                transactionService.getAll("INVALID", null, null, null, null, 1, 20))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("INCOME or EXPENSE");
    }

    @Test
    void getAll_throwsBadRequest_onInvalidDateFormat() {
        assertThatThrownBy(() ->
                transactionService.getAll(null, null, "15-03-2024", null, null, 1, 20))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("YYYY-MM-DD");
    }
}
