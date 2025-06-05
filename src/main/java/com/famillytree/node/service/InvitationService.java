package com.famillytree.node.service;

import com.famillytree.node.dto.InvitationRequest;
import com.famillytree.node.dto.InvitationResponse;
import com.famillytree.node.exception.NodeException;
import com.famillytree.node.model.Invitation;
import com.famillytree.node.model.Node;
import com.famillytree.node.repository.InvitationRepository;
import com.famillytree.node.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final NodeService nodeService;
    private final NodeRepository nodeRepository;
    private static final Random random = new Random();

    @Transactional
    public InvitationResponse createInvitation(InvitationRequest request) {
        // Vérifier que le nœud existe et que l'utilisateur est le propriétaire
        Node node = nodeService.getNodeById(request.getNodeId());
        Long currentUserId = nodeService.getCurrentUserId();
        if (!node.getUserId().equals(currentUserId)) {
            throw NodeException.unauthorized("Vous n'êtes pas autorisé à créer une invitation pour ce nœud");
        }

        // Générer un code d'invitation unique à 6 chiffres
        String invitationCode = generateUniqueInvitationCode();

        // Créer l'invitation
        Invitation invitation = Invitation.builder()
                .nodeId(request.getNodeId())
                .invitationCode(invitationCode)
                .used(false)
                .build();

        invitation = invitationRepository.save(invitation);

        return mapToResponse(invitation);
    }

    @Transactional
    public InvitationResponse useInvitation(String invitationCode) {
        // Récupérer l'ID de l'utilisateur actuel
        Long currentUserId = nodeService.getCurrentUserId();

        // Récupérer l'invitation
        Invitation invitation = invitationRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> NodeException.invalidInput("Code d'invitation non trouvé"));

        if (invitation.isUsed()) {
            throw NodeException.invalidInput("Cette invitation a déjà été utilisée");
        }

        // Récupérer les nœuds
        Node invitationNode = nodeService.getNodeById(invitation.getNodeId());
        Node userNode = nodeRepository.findByUserId(currentUserId)
                .orElseThrow(() -> NodeException.invalidInput("Nœud utilisateur non trouvé"));

        // Copier les données du nœud utilisateur vers le nœud d'invitation
        invitationNode.setTitle(userNode.getTitle());
        invitationNode.setFirstName(userNode.getFirstName());
        invitationNode.setLastName(userNode.getLastName());
        invitationNode.setBirthDate(userNode.getBirthDate());
        invitationNode.setGender(userNode.getGender());
        invitationNode.setAddress(userNode.getAddress());
        invitationNode.setPhone(userNode.getPhone());
        invitationNode.setInterests(userNode.getInterests());
        invitationNode.setUserId(userNode.getUserId());
        invitationNode.setBaseNode(userNode.isBaseNode());

        // Supprimer le nœud utilisateur
        nodeRepository.delete(userNode);

        // Sauvegarder le nœud d'invitation mis à jour
        nodeRepository.save(invitationNode);


        // Marquer l'invitation comme utilisée
        invitation.setUsed(true);
        invitation.setUsedDate(LocalDateTime.now());
        invitation = invitationRepository.save(invitation);

        return mapToResponse(invitation);
    }

    private String generateUniqueInvitationCode() {
        String code;
        do {
            // Générer un code à 6 chiffres
            code = String.format("%06d", random.nextInt(1000000));
        } while (invitationRepository.existsByInvitationCode(code));

        return code;
    }

    private InvitationResponse mapToResponse(Invitation invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .nodeId(invitation.getNodeId())
                .invitationCode(invitation.getInvitationCode())
                .used(invitation.isUsed())
                .createdDate(invitation.getCreatedDate())
                .usedDate(invitation.getUsedDate())
                .build();
    }
} 