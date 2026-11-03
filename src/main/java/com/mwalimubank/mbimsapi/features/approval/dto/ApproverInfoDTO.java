// features/approval/dto/ApproverInfoDTO.java
package com.mwalimubank.mbimsapi.features.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproverInfoDTO {
    private String position;
    private List<String> users;        // List of user names under this position
}