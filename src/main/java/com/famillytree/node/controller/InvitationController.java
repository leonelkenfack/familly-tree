package com.famillytree.node.controller;

import com.famillytree.node.dto.InvitationRequest;
import com.famillytree.node.dto.InvitationResponse;
import com.famillytree.node.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitation", description = "API de gestion des invitations")
@SecurityRequirement(name = "bearerAuth")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une invitation", description = "Crée une nouvelle invitation pour un nœud. Seul le propriétaire du nœud peut créer une invitation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Invitation créée avec succès",
            content = @Content(schema = @Schema(implementation = InvitationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé - Seul le propriétaire peut créer une invitation"),
        @ApiResponse(responseCode = "404", description = "Nœud non trouvé")
    })
    public ResponseEntity<InvitationResponse> createInvitation(
            @Parameter(description = "Données de l'invitation à créer", required = true)
            @Valid @RequestBody InvitationRequest request) {
        return ResponseEntity.ok(invitationService.createInvitation(request));
    }

    @PutMapping(value = "/{invitationKey}/use", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Utiliser une invitation", description = "Marque une invitation comme utilisée. Une invitation ne peut être utilisée qu'une seule fois.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitation utilisée avec succès",
            content = @Content(schema = @Schema(implementation = InvitationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invitation déjà utilisée"),
        @ApiResponse(responseCode = "404", description = "Invitation non trouvée")
    })
    public ResponseEntity<InvitationResponse> useInvitation(
            @Parameter(description = "Clé d'invitation à utiliser", required = true)
            @PathVariable String invitationKey) {
        return ResponseEntity.ok(invitationService.useInvitation(invitationKey));
    }
} 