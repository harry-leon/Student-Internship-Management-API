package com.se191116.studymanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityCheckResponse {
    private Integer profileId;
    private String overallStatus;
    private Boolean isEligible;
    private List<ChecklistItemResult> items;
    private List<String> missingRequiredItems;
    private Integer totalItems;
    private Integer passedItems;
    private Integer failedItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChecklistItemResult {
        private Integer checklistId;
        private String itemCode;
        private String itemName;
        private String itemType;
        private Boolean isRequired;
        private String status;
        private String value;
        private String errorMessage;
    }
}
