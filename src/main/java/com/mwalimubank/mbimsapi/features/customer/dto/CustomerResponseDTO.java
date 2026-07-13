package com.mwalimubank.mbimsapi.features.customer.dto;

import com.mwalimubank.mbimsapi.features.customer.CustomerEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String dateOfBirth;
    private String customerType;
    private String sex;
    private String maritalStatus;
    private String region;
    private String district;
    private String ward;

    private String nationality;
    private String citizenship;
    private String residency;
    private String profession;
    private String employmentStatus;
    private String numberDependants;
    private String educationLevel;
    private String birthRegion;
    private String idIssuanceDate;
    private String idExpirationDate;
    private String identificationType;
    private String identificationNumber;
    private String location;
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static CustomerResponseDTO fromEntity(CustomerEntity entity) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getFirstName() + " " + entity.getMiddleName() + " " + entity.getLastName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setEmail(entity.getEmail());
        dto.setBirthRegion(entity.getBirthRegion());

//        dto.setCitizenship("Tanzania");
//        dto.setResidency("Tanzania");
//        dto.setProfession("Tanzania");
//        dto.setEmploymentStatus("Tanzania");
//        dto.setNumberDependants("Tanzania");
//        dto.setEducationLevel("Tanzania");
//        dto.setIdentificationType("Tanzania");
//        dto.setIdentificationNumber("Tanzania");
//        dto.setNationality("Tanzania");


        dto.setSex(
                switch (entity.getSex()) {
                    case "M" -> "Male";
                    case "F" -> "Female";
                    default -> "Not Applicable";
                }
        );
        dto.setMaritalStatus(
                switch (entity.getSex()) {
                    case "MARRIED" -> "'Married'";
                    case "'DIVORCED'" -> "''Divorced''";
                    case "'WIDOWED'" -> "''Widowed''";
                    default -> "Single";
                }
        );
        dto.setCreatedAt(
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().format(FORMATTER)
                        : null
        );

        dto.setResidency(
                Objects.equals(entity.getNonResident(), "0")
                        ? "Resident"
                        : "Non-Resident"
        );
        dto.setDateOfBirth(
                entity.getDateOfBirth() != null
                        ? entity.getDateOfBirth().format(FORMATTER)
                        : null
        );
        dto.setCustomerType(Objects.equals(entity.getCustType(), "1") ? "Individual": "Corporate");
        dto.setNumberDependants(
                entity.getNumberOfChildren() != null
                        ? entity.getNumberOfChildren().toString()
                        : null
        );
        return dto;
    }
}
