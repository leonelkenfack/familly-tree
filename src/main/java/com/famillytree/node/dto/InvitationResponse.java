package com.famillytree.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for invitation operations")
public class InvitationResponse {
    @Schema(description = "ID of the invitation", example = "1")
    private Long id;

    @Schema(description = "ID of the node", example = "1")
    private Long nodeId;

    @Schema(description = "Unique invitation key", example = "abc123def456")
    private String invitationKey;

    @Schema(description = "Whether the invitation has been used", example = "false")
    private boolean used;

    @Schema(description = "Date when the invitation was created", example = "2024-03-15T10:30:00")
    private LocalDateTime createdDate;

    @Schema(description = "Date when the invitation was used (if applicable)", example = "2024-03-16T15:45:00")
    private LocalDateTime usedDate;
} 