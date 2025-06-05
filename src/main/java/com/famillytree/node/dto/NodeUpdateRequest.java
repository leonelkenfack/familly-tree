package com.famillytree.node.dto;

import com.famillytree.node.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for updating a node")
public class NodeUpdateRequest {
    @Schema(description = "Title of the person (optional)", example = "Famille Talla")
    private String title;

    @NotNull(message = "First name is required")
    @Schema(description = "First name of the person", example = "John", required = true)
    private String firstName;

    @NotNull(message = "Last name is required")
    @Schema(description = "Last name of the person", example = "Doe", required = true)
    private String lastName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @Schema(description = "Birth date of the person", example = "1990-01-01", required = true)
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    @Schema(description = "Gender of the person", example = "MALE", required = true, allowableValues = {"MALE", "FEMALE"})
    private Gender gender;

    @Schema(description = "Address of the person", example = "123 Main St, City")
    private String address;

    @Schema(description = "Phone number of the person", example = "+33612345678")
    private String phone;

    @Schema(description = "List of interests of the person", example = "[\"Reading\", \"Sports\"]")
    private List<String> interests;

    // @Schema(description = "Indique si l'utilisateur est propriétaire du nœud. Si non spécifié, la valeur par défaut est false.", example = "false")
    // private Boolean baseNode;
} 