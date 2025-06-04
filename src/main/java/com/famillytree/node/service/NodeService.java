package com.famillytree.node.service;

import com.famillytree.auth.repository.UserRepository;
import com.famillytree.node.dto.NodeRequest;
import com.famillytree.node.exception.NodeException;
import com.famillytree.node.model.Node;
import com.famillytree.node.model.NodeRelation;
import com.famillytree.node.repository.NodeRelationRepository;
import com.famillytree.node.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final UserRepository userRepository;
    private final NodeRelationRepository nodeRelationRepository;

    @Transactional
    public Node createNode(NodeRequest request) {
        validateNodeRequest(request);

        // Récupérer l'ID de l'utilisateur à partir du contexte de sécurité
        Long userId = getCurrentUserId();

        Node node = Node.builder()
                .title(request.getTitle())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .address(request.getAddress())
                .phone(request.getPhone())
                .interests(request.getInterests())
                .userId(userId)
                .baseNode(request.getBaseNode() != null ? request.getBaseNode() : false)
                .build();

        node = nodeRepository.save(node);

        // Si une relation est spécifiée, la créer
        if (request.getRelatedNodeId() != null && request.getRelationType() != null) {
            Node relatedNode = getNodeById(request.getRelatedNodeId());

            NodeRelation relation = NodeRelation.builder()
                    .node1(relatedNode)
                    .node2(node)
                    .relation(request.getRelationType())
                    .build();

            nodeRelationRepository.save(relation);
        }

        return node;
    }

    public List<Node> getAllNodes() {
        return nodeRepository.findAll();
    }

    public Node getNodeById(Long id) {
        if (id == null) {
            throw NodeException.invalidInput("Node ID cannot be null");
        }
        return nodeRepository.findById(id)
                .orElseThrow(() -> NodeException.notFound(id));
    }

    @Transactional
    public Node updateNode(Long id, NodeRequest request) {
        if (id == null) {
            throw NodeException.invalidInput("Node ID cannot be null");
        }
        validateNodeRequest(request);

        // Récupérer le nœud et vérifier qu'il existe
        Node node = getNodeById(id);
        
        // Récupérer l'ID de l'utilisateur actuel et vérifier qu'il est le propriétaire du nœud
        Long currentUserId = getCurrentUserId();
        if (!node.getUserId().equals(currentUserId)) {
            throw NodeException.unauthorized("Vous n'êtes pas autorisé à modifier ce nœud");
        }
        
        // Vérifier que l'ID du nœud n'a pas été modifié
        if (!node.getId().equals(id)) {
            throw NodeException.invalidInput("L'ID du nœud ne peut pas être modifié");
        }
        
        // Mettre à jour les champs du nœud
        node.setTitle(request.getTitle());
        node.setFirstName(request.getFirstName());
        node.setLastName(request.getLastName());
        node.setBirthDate(request.getBirthDate());
        node.setGender(request.getGender());
        node.setAddress(request.getAddress());
        node.setPhone(request.getPhone());
        node.setInterests(request.getInterests());

        return nodeRepository.save(node);
    }

    private void validateNodeRequest(NodeRequest request) {
        if (request == null) {
            throw NodeException.invalidInput("Request cannot be null");
        }
        if (!StringUtils.hasText(request.getFirstName())) {
            throw NodeException.invalidInput("First name is required");
        }
        if (!StringUtils.hasText(request.getLastName())) {
            throw NodeException.invalidInput("Last name is required");
        }
        if (request.getBirthDate() == null) {
            throw NodeException.invalidInput("Birth date is required");
        }
        if (request.getGender() == null) {
            throw NodeException.invalidInput("Gender is required");
        }
    }

    public Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) {
            throw NodeException.unauthorized("Utilisateur non authentifié");
        }

        // Le username est l'email de l'utilisateur
        return userRepository.findByEmail(username)
                .map(user -> user.getId())
                .orElseThrow(() -> NodeException.unauthorized("Utilisateur non trouvé"));
    }
    
} 