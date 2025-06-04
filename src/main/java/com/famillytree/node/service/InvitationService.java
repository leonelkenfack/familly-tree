package com.famillytree.node.service;

import com.famillytree.node.dto.InvitationRequest;
import com.famillytree.node.dto.InvitationResponse;
import com.famillytree.node.exception.NodeException;
import com.famillytree.node.model.Invitation;
import com.famillytree.node.model.Node;
import com.famillytree.node.model.NodeRelation;
import com.famillytree.node.repository.InvitationRepository;
import com.famillytree.node.repository.NodeRepository;
import com.famillytree.node.repository.NodeRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final NodeService nodeService;
    private final NodeRepository nodeRepository;
    private final NodeRelationRepository nodeRelationRepository;

    @Transactional
    public InvitationResponse createInvitation(InvitationRequest request) {
        // Vérifier que le nœud existe et que l'utilisateur est le propriétaire
        Node node = nodeService.getNodeById(request.getNodeId());
        Long currentUserId = nodeService.getCurrentUserId();
        if (!node.getUserId().equals(currentUserId)) {
            throw NodeException.unauthorized("Vous n'êtes pas autorisé à créer une invitation pour ce nœud");
        }

        // Générer une clé d'invitation unique
        String invitationKey = generateUniqueInvitationKey();

        // Créer l'invitation
        Invitation invitation = Invitation.builder()
                .nodeId(request.getNodeId())
                .invitationKey(invitationKey)
                .used(false)
                .build();

        invitation = invitationRepository.save(invitation);

        return mapToResponse(invitation);
    }

    @Transactional
    public InvitationResponse useInvitation(String invitationKey) {


        Invitation invitation = invitationRepository.findByInvitationKey(invitationKey)
                .orElseThrow(() -> NodeException.invalidInput("Invitation non trouvée"));

        if (invitation.isUsed()) {
            throw NodeException.invalidInput("Cette invitation a déjà été utilisée");
        }

        invitation.setUsed(true);
        invitation.setUsedDate(LocalDateTime.now());
        invitation = invitationRepository.save(invitation);

        return mapToResponse(invitation);
    }

    private String generateUniqueInvitationKey() {
        String key;
        do {
            // Générer une clé unique en utilisant UUID
            key = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(UUID.randomUUID().toString().getBytes())
                    .substring(0, 12); // Prendre les 12 premiers caractères
        } while (invitationRepository.existsByInvitationKey(key));

        return key;
    }

    private InvitationResponse mapToResponse(Invitation invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .nodeId(invitation.getNodeId())
                .invitationKey(invitation.getInvitationKey())
                .used(invitation.isUsed())
                .createdDate(invitation.getCreatedDate())
                .usedDate(invitation.getUsedDate())
                .build();
    }

    public int getFilledFieldsCount(Long nodeId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> NodeException.notFound(nodeId));

        int count = 0;

        if (StringUtils.hasText(node.getTitle())) count++;
        if (StringUtils.hasText(node.getFirstName())) count++;
        if (StringUtils.hasText(node.getLastName())) count++;
        if (node.getBirthDate() != null) count++;
        if (node.getGender() != null) count++;
        if (StringUtils.hasText(node.getAddress())) count++;
        if (StringUtils.hasText(node.getPhone())) count++;
        if (node.getInterests() != null && !node.getInterests().isEmpty()) count++;

        return count;
    }

    public boolean isBaseNode(Long nodeId) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> NodeException.notFound(nodeId));
        return node.isBaseNode();
    }

    public List<NodeRelation> findRelationsForNodeAndType(Node node, NodeRelation.RelationType relationType) {
        if (node == null) {
            throw NodeException.invalidInput("Node cannot be null");
        }
        if (relationType == null) {
            throw NodeException.invalidInput("Relation type cannot be null"); 
        }

        return nodeRelationRepository.findByNode1AndRelation(node, relationType);
    }

    


} 