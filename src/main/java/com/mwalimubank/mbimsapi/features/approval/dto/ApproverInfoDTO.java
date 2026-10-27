// features/approval/dto/ApproverInfoDTO.java
package com.mwalimubank.mbimsapi.features.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproverInfoDTO {
    private String name;       // usually the person's full name
    private String position;   // role / job title
    // optional extras if you need them later
    // private Long levelId;
    // private Integer levelOrder;
}