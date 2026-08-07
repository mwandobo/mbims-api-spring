package com.mwalimubank.mbimsapi.features.customer.services;

import com.mwalimubank.mbimsapi.features.customer.entity.CustomerEntity;
import com.mwalimubank.mbimsapi.features.customer.entity.CustomerFromBankEntity;
import com.mwalimubank.mbimsapi.features.customer.repository.CustomerFromBankRepository;
import com.mwalimubank.mbimsapi.features.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerSyncService {

    private final CustomerFromBankRepository bankCustomerRepository;
    private final CustomerRepository customerRepository;

    private static final int BATCH_SIZE = 500;

    /**
     * Sync customers from profits.customer → mbims.customer
     * Only inserts new or updates existing records (matched by custId)
     */
//    @Transactional
//    public int syncCustomers() {
//        log.info("Starting customer sync from PROFITS.CUSTOMER → MBIMS.CUSTOMER...");
//
//        int totalProcessed = 0;
//        int pageNumber = 0;
//        Page<CustomerFromBankEntity> page;
//
//        do {
//            page = bankCustomerRepository.findAll(PageRequest.of(pageNumber, BATCH_SIZE));
//            List<CustomerFromBankEntity> bankCustomers = page.getContent();
//
//            for (CustomerFromBankEntity bankCust : bankCustomers) {
//                if (bankCust.getId() == null) {
//                    continue;
//                }
//
//                Optional<CustomerEntity> existingOpt = customerRepository.findByCustId(bankCust.getId());
//
//                CustomerEntity target;
//                if (existingOpt.isPresent()) {
//                    // Update existing
//                    target = existingOpt.get();
//                    mapBankToLocal(bankCust, target);
//                } else {
//                    // Insert new
//                    target = new CustomerEntity();
//                    target.setCustId(bankCust.getId());
//                    mapBankToLocal(bankCust, target);
//                }
//
//                customerRepository.save(target);
//                totalProcessed++;
//            }
//
//            pageNumber++;
//            log.info("Processed page {} ({} customers)", pageNumber, bankCustomers.size());
//
//        } while (page.hasNext());
//
//        log.info("Customer sync completed. Total processed: {}", totalProcessed);
//        return totalProcessed;
//    }

    @Async
    public void syncCustomersAsync() {
        log.info("Async customer sync started...");
        try {
            int processed = syncCustomers();
            log.info("Async customer sync finished. Processed: {}", processed);
        } catch (Exception e) {
            log.error("Async customer sync failed", e);
        }
    }


    @Transactional
    public int syncCustomers() {
        log.info("Starting customer sync from PROFITS.CUSTOMER → MBIMS.CUSTOMER...");

        int totalProcessed = 0;
        int pageNumber = 0;
        Page<CustomerFromBankEntity> page;

        do {
            page = bankCustomerRepository.findAll(PageRequest.of(pageNumber, BATCH_SIZE));
            List<CustomerFromBankEntity> bankCustomers = page.getContent();

            for (CustomerFromBankEntity bankCust : bankCustomers) {
                if (bankCust.getId() == null) continue;

                Optional<CustomerEntity> existingOpt = customerRepository.findByCustId(bankCust.getId());

                CustomerEntity target = existingOpt.orElseGet(CustomerEntity::new);
                if (target.getCustId() == null) {
                    target.setCustId(bankCust.getId());
                }

                mapBankToLocal(bankCust, target);
                customerRepository.save(target);
                totalProcessed++;
            }

            pageNumber++;
            log.info("Processed page {} ({} customers)", pageNumber, bankCustomers.size());

            // Optional: clear persistence context to avoid memory issues
            // entityManager.clear();  // if you inject EntityManager

        } while (page.hasNext());

        log.info("Customer sync completed. Total processed: {}", totalProcessed);
        return totalProcessed;
    }

    /**
     * Maps fields from bank customer to local customer
     */
    private void mapBankToLocal(CustomerFromBankEntity source, CustomerEntity target) {
        target.setFirstName(source.getFirstName());
        target.setMiddleName(source.getMiddleName());
        target.setLastName(source.getLastName());
        target.setName(buildFullName(source));
        target.setSex(source.getSex());
        target.setDateOfBirth(source.getDateOfBirth());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setEmail(source.getEmail());
        target.setChildrenAbove18(source.getChildrenAbove18());
        target.setNumberOfChildren(source.getNumberOfChildren());
        target.setFamilyMembers(source.getFamilyMembers());
        target.setBirthRegion(source.getBirthRegion());
        target.setEmployer(source.getEmployer());
        target.setEmployerAddress(source.getEmployerAddress());
        target.setIdentificationNumber(source.getIdentificationNumber());
        target.setIdentificationType(source.getIdentificationType());
        target.setNonResident(source.getNonResident());
        target.setVipIndicator(source.getVipIndicator());
        target.setBlacklisted(source.getBlacklisted());
        target.setCustomerBeginDate(source.getCustomerBeginDate());
        target.setCustomerOpenDate(source.getCustomerOpenDate());
        target.setTitle(source.getTitle());
        if (source.getCreatedAt() != null) {
            target.setCreatedAt(
                    source.getCreatedAt()
                            .atZone(java.time.ZoneId.of("Africa/Nairobi"))  // or ZoneId.systemDefault()
                            .toInstant()
            );
        } else {
            target.setCreatedAt(null);
        }
        target.setCustType(source.getCustType());
        target.setCustStatus(source.getCustStatus());
    }

    /**
     * Builds full name safely (handles null/empty values)
     */
    private String buildFullName(CustomerFromBankEntity source) {
        return Stream.of(source.getFirstName(), source.getMiddleName(), source.getLastName())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }

    /**
     * Scheduled job – runs every day at 9:00 PM
     */
    @Scheduled(cron = "0 0 21 * * *")
    public void scheduledCustomerSync() {
        log.info("=== Scheduled Customer Sync started at 9:00 PM ===");
        try {
            int processed = syncCustomers();
            log.info("=== Scheduled Customer Sync finished. Processed: {} ===", processed);
        } catch (Exception e) {
            log.error("=== Scheduled Customer Sync failed ===", e);
        }
    }
}