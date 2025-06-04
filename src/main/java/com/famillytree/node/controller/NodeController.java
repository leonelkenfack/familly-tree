package com.famillytree.node.controller;

import com.famillytree.node.dto.NodeRequest;
import com.famillytree.node.model.Node;
import com.famillytree.node.service.NodeService;
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

import java.util.List;

@RestController
@RequestMapping("/api/nodes")
@RequiredArgsConstructor
@Tag(name = "Node", description = "API de gestion des nœuds de l'arbre généalogique")
@SecurityRequirement(name = "bearerAuth")
public class NodeController {

    private final NodeService nodeService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouveau nœud", description = "Crée un nouveau nœud dans l'arbre généalogique pour l'utilisateur authentifié")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Nœud créé avec succès",
            content = @Content(schema = @Schema(implementation = Node.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<Node> createNode(
            @Parameter(description = "Données du nœud à créer", required = true)
            @Valid @RequestBody NodeRequest request) {
        return ResponseEntity.ok(nodeService.createNode(request));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer tous les nœuds", description = "Récupère la liste de tous les nœuds de l'arbre généalogique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des nœuds récupérée avec succès",
            content = @Content(schema = @Schema(implementation = Node.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<Node>> getAllNodes() {
        return ResponseEntity.ok(nodeService.getAllNodes());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer un nœud par son ID", description = "Récupère les détails d'un nœud spécifique de l'arbre généalogique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nœud récupéré avec succès",
            content = @Content(schema = @Schema(implementation = Node.class))),
        @ApiResponse(responseCode = "404", description = "Nœud non trouvé"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<Node> getNodeById(
            @Parameter(description = "ID du nœud à récupérer", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(nodeService.getNodeById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un nœud", description = "Met à jour les informations d'un nœud existant. Seul le propriétaire du nœud peut le modifier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nœud mis à jour avec succès",
            content = @Content(schema = @Schema(implementation = Node.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Non autorisé - Seul le propriétaire peut modifier le nœud"),
        @ApiResponse(responseCode = "404", description = "Nœud non trouvé")
    })
    public ResponseEntity<Node> updateNode(
            @Parameter(description = "ID du nœud à mettre à jour", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du nœud", required = true)
            @Valid @RequestBody NodeRequest request) {
        return ResponseEntity.ok(nodeService.updateNode(id, request));
    }
} 