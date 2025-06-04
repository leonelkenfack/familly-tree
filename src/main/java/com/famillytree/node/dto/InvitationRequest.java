package com.famillytree.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating an invitation")
public class InvitationRequest {
    @NotNull(message = "Node ID is required")
    @Schema(description = "ID of the node to create invitation for", example = "1", required = true)
    private Long nodeId;
} 