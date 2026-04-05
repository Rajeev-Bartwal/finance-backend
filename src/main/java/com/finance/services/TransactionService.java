package com.finance.services;

import com.finance.dto.request.CreateTransactionRequest;
import com.finance.dto.request.UpdateTransactionRequest;
import com.finance.dto.response.PagedResponse;
import com.finance.dto.response.TransactionResponse;
import com.finance.enums.TransactionType;
import com.finance.exception.BadRequestException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.models.Transaction;
import com.finance.models.User;
import com.finance.repositories.TransactionRepository;
import com.finance.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public PagedResponse<TransactionResponse> getAll(
            String type,
            String category,
            String startDate,
            String endDate,
            String search,
            int page,
            int size
    ) {
        TransactionType typeEnum = parseType(type);
        LocalDate start = parseDate(startDate, "start_date");
        LocalDate end = parseDate(endDate, "end_date");

        Page<TransactionResponse> result = transactionRepository
                .findAllWithFilters(
                        typeEnum, category, start, end, search,
                        PageRequest.of(page - 1, size, Sort.by("date").descending())
                )
                .map(TransactionResponse::from);

        return new PagedResponse<>(result);
    }

    public TransactionResponse getById(Long id) {
        return TransactionResponse.from(findOrThrow(id));
    }

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory().trim())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(creator)
                .build();

        transactionRepository.save(transaction);
        log.info("Transaction created: {} {} by {}", request.getType(), request.getAmount(), username);
        return TransactionResponse.from(transaction);
    }

    @Transactional
    public TransactionResponse update(Long id, UpdateTransactionRequest request) {
        Transaction transaction = findOrThrow(id);

        if (request.getAmount() != null)   transaction.setAmount(request.getAmount());
        if (request.getType() != null)     transaction.setType(request.getType());
        if (request.getCategory() != null) transaction.setCategory(request.getCategory().trim());
        if (request.getDate() != null)     transaction.setDate(request.getDate());
        if (request.getNotes() != null)    transaction.setNotes(request.getNotes());

        transactionRepository.save(transaction);
        log.info("Transaction {} updated", id);
        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void delete(Long id) {
        Transaction transaction = findOrThrow(id);
        transactionRepository.delete(transaction);
        log.info("Transaction {} soft-deleted", id);
    }

    private Transaction findOrThrow(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    }

    private TransactionType parseType(String type) {
        if (type == null || type.isBlank()) return null;
        try {
            return TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid type '" + type + "' — must be INCOME or EXPENSE");
        }
    }

    private LocalDate parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid " + fieldName + " format — use YYYY-MM-DD");
        }
    }
}
