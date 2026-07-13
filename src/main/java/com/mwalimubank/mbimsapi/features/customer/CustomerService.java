package com.mwalimubank.mbimsapi.features.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mwalimubank.mbimsapi.core.dto.PaginationRequest;
import com.mwalimubank.mbimsapi.features.common.entity.CustomerAddressEntity;
import com.mwalimubank.mbimsapi.features.common.entity.CustomerCategoryEntity;
import com.mwalimubank.mbimsapi.features.common.entity.GenericDetailEntity;
import com.mwalimubank.mbimsapi.features.common.entity.OtherIdEntity;
import com.mwalimubank.mbimsapi.features.common.repository.CustomerAddressRepository;
import com.mwalimubank.mbimsapi.features.common.repository.CustomerCategoryRepository;
import com.mwalimubank.mbimsapi.features.common.repository.GenericDetailRepository;
import com.mwalimubank.mbimsapi.features.common.repository.OtherIdRepository;
import com.mwalimubank.mbimsapi.features.customer.dto.CreateCustomerDTO;
import com.mwalimubank.mbimsapi.features.customer.dto.CustomerResponseDTO;
import com.mwalimubank.mbimsapi.features.customer.CustomerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mwalimubank.mbimsapi.core.dto.PagedResponse;
import com.mwalimubank.mbimsapi.core.dto.PaginationDto;
import com.mwalimubank.mbimsapi.features.approval.util.ApprovalStatusUtil;
import com.mwalimubank.mbimsapi.core.services.CurrentUserService;
import com.mwalimubank.mbimsapi.features.approval.dto.ApprovalAwareDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerAddressRepository customerAddressRepository;
    private final CustomerCategoryRepository customerCategoryRepository;
    private final GenericDetailRepository genericDetailRepository;
    private final ApprovalStatusUtil approvalStatusUtil;
    private final CurrentUserService currentUserService;
    private final ObjectMapper objectMapper;
    private final OtherIdRepository otherIdRepository;


    public PagedResponse<CustomerResponseDTO> findAll(PaginationRequest pagination, String search) {
//        Specification<CustomerEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        // Add search logic here if needed

        Page<CustomerEntity> page = repository.findAll( pagination.toPageable());

        List<CustomerResponseDTO> result = page.getContent().stream()
                .map(CustomerResponseDTO::fromEntity)
                .toList();

        return new PagedResponse<>(
                result,
                new PaginationDto(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getTotalPages()),
                false
        );
    }

    @Transactional
    public CustomerResponseDTO create(CreateCustomerDTO request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setFirstName(request.getName());
        CustomerEntity saved = repository.save(entity);
        return CustomerResponseDTO.fromEntity(saved);
    }

    public ApprovalAwareDTO<CustomerResponseDTO> findOne(Long id) {
        CustomerEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));

        CustomerResponseDTO dto = CustomerResponseDTO.fromEntity(entity);


        CustomerAddressEntity address = customerAddressRepository
                .findFirstByIdFkCustomerCustIdAndCommunicationAddrAndEntryStatus(
                        entity.getId().intValue(),
                        "1",
                        "1"
                )
                .orElse(null);

        if (address != null) {
            dto.setRegion( address.getCity() );
            dto.setDistrict(address.getRegion());
            dto.setWard(address.getAddress1());
        }

        GenericDetailEntity nationality = getGenericDetail(
                entity.getId().intValue(),
                "NATIONAL",
                "NATIO"
        );

        if (nationality != null) {
            dto.setNationality(nationality.getDescription());
            dto.setCitizenship(nationality.getDescription());
        }
        GenericDetailEntity profession = getGenericDetail(
                entity.getId().intValue(),
                "PROFES",
                "PROFF"
        );

        if (profession != null) {
            dto.setProfession(profession.getDescription());
        }

        GenericDetailEntity employment = getGenericDetail(
                entity.getId().intValue(),
                "PROFLEVL",
                "PRFST"
        );

        if (employment != null) {
            dto.setEmploymentStatus(
                    switch (employment.getDescription()) {
                        case "EMPLOYED", "SALARIED" -> "Employed";
                        case "CUSTOMER SERVICE" -> "Self - Employed";
                        default -> "Unemployed";
                    }
            );
        }

        GenericDetailEntity education = getGenericDetail(
                entity.getId().intValue(),
                "PROFES",
                "PROFF"
        );

        if (education != null) {
            dto.setEducationLevel(education.getDescription());
        }

        OtherIdEntity otherId = otherIdRepository
                .findMainIdByCustomerId(entity.getId().intValue())
                .orElse(null);

        if (otherId != null) {
            dto.setIdentificationNumber(otherId.getIdNo());

            dto.setIdIssuanceDate(formatDate(otherId.getIssueDate()));
            dto.setIdExpirationDate(formatDate(otherId.getExpiryDate()));


            GenericDetailEntity identificationTypeDetail = genericDetailRepository.findFirstByIdAndSerialNumber(otherId.getFkGhHasType(), otherId.getFkGdHasType()) .orElse(null);

            if (identificationTypeDetail != null) {
                dto.setIdentificationType(identificationTypeDetail.getDescription() );
            }
        }



        return approvalStatusUtil.attachApprovalInfo(
                dto,
                entity.getId(),
                CustomerEntity.class.getSimpleName(),
                currentUserService.getCurrentUserRoleId()
        );
    }


    private GenericDetailEntity getGenericDetail(
            Integer customerId,
            String category,
            String genericDetailCode
    ) {
        return customerCategoryRepository
                .findFirstByIdFkCustomerCustIdAndIdCategoryAndGenericDetail(
                        customerId,
                        category,
                        genericDetailCode
                )
                .flatMap(customerCategory ->
                        genericDetailRepository.findFirstByIdAndSerialNumber(
                                customerCategory.getGenericDetail(),
                                customerCategory.getFkGenericDetaSer()
                        )
                )
                .orElse(null);
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String formatDate(LocalDate date) {
        if (date == null || date.equals(LocalDate.of(1, 1, 1))) {
            return "---";
        }

        return date.format(FORMATTER);
    }




    @Transactional
    public CustomerResponseDTO update(Long id, CreateCustomerDTO request) {
        CustomerEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));

        entity.setFirstName(request.getName());

        CustomerEntity updated = repository.save(entity);
        return CustomerResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id, boolean soft) {
        CustomerEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
        if (soft) {
            repository.delete(entity);
            repository.save(entity);
        } else {
            repository.delete(entity);
        }
    }
}
