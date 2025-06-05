package com.famillytree.node.service;

import com.famillytree.auth.repository.UserRepository;
import com.famillytree.node.dto.NodeRelationDTO;
import com.famillytree.node.dto.NodeRequest;
import com.famillytree.node.dto.NodeUpdateRequest;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final UserRepository userRepository;
    private final NodeRelationRepository nodeRelationRepository;

    @Transactional
    public Node createNode(NodeRequest request) {
        validateCreateNodeRequest(request);

        // Récupérer l'ID de l'utilisateur à partir du contexte de sécurité
        Long userId = getCurrentUserId();

        // Vérifier la contrainte de baseNode
        validateBaseNodeConstraint(userId, request.getBaseNode());

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
    public Node updateNode(Long id, NodeUpdateRequest request) {
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

        // Vérifier la contrainte de baseNode
        // validateBaseNodeConstraint(currentUserId, request.getBaseNode(), id);
        
        // Mettre à jour les champs du nœud
        node.setTitle(request.getTitle());
        node.setFirstName(request.getFirstName());
        node.setLastName(request.getLastName());
        node.setBirthDate(request.getBirthDate());
        node.setGender(request.getGender());
        node.setAddress(request.getAddress());
        node.setPhone(request.getPhone());
        node.setInterests(request.getInterests());
        // node.setBaseNode(request.getBaseNode() != null ? request.getBaseNode() : node.isBaseNode());

        return nodeRepository.save(node);
    }

    private void validateNodeRequest(NodeUpdateRequest request) {
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

    private void validateBaseNodeConstraint(Long userId, Boolean newBaseNodeValue) {
        validateBaseNodeConstraint(userId, newBaseNodeValue, null);
    }

    private void validateBaseNodeConstraint(Long userId, Boolean newBaseNodeValue, Long currentNodeId) {
        if (newBaseNodeValue != null && newBaseNodeValue) {
            // Vérifier s'il existe déjà un nœud base pour cet utilisateur
            List<Node> existingBaseNodes = nodeRepository.findByUserIdAndBaseNodeIsTrue(userId);
            if (!existingBaseNodes.isEmpty() && (currentNodeId == null || !existingBaseNodes.get(0).getId().equals(currentNodeId))) {
                throw NodeException.invalidInput("Un utilisateur ne peut avoir qu'un seul nœud de base");
            }
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

    public List<Node> getDirectChildren(Node node) {
        return nodeRelationRepository.findByNode1AndRelation(node, NodeRelation.RelationType.CHILD)
                .stream()
                .map(NodeRelation::getNode2)
                .collect(Collectors.toList());
    }

    public List<Node> getDirectParents(Node node) {
        return nodeRelationRepository.findByNode2AndRelation(node, NodeRelation.RelationType.CHILD)
                .stream()
                .map(NodeRelation::getNode1)
                .collect(Collectors.toList());
    }

    public List<Node> getSiblings(Node node) {
        // Récupérer d'abord les parents
        List<Node> parents = getDirectParents(node);
        if (parents.isEmpty()) {
            return Collections.emptyList();
        }

        // Pour chaque parent, récupérer tous les enfants
        return parents.stream()
                .flatMap(parent -> getDirectChildren(parent).stream())
                .filter(sibling -> !sibling.getId().equals(node.getId())) // Exclure le nœud lui-même
                .distinct() // Éviter les doublons si les deux parents sont connus
                .collect(Collectors.toList());
    }

    public List<Node> getSpouses(Node node) {
        return nodeRelationRepository.findByNode1AndRelation(node, NodeRelation.RelationType.SPOUSE)
                .stream()
                .map(NodeRelation::getNode2)
                .collect(Collectors.toList());
    }

    public Set<NodeRelationDTO> getAllAncestorRelations(Node node) {
        Set<NodeRelationDTO> relations = new HashSet<>();
        getAllAncestorRelationsRecursive(node, relations);
        return relations;
    }

    private void getAllAncestorRelationsRecursive(Node node, Set<NodeRelationDTO> relations) {
        List<NodeRelation> parentRelations = nodeRelationRepository.findByNode2AndRelation(node, NodeRelation.RelationType.CHILD);
        for (NodeRelation relation : parentRelations) {
            NodeRelationDTO dto = NodeRelationDTO.builder()
                    .node1(relation.getNode1())
                    .node2(relation.getNode2())
                    .relationType(relation.getRelation())
                    .build();
            if (relations.add(dto)) {
                getAllAncestorRelationsRecursive(relation.getNode1(), relations);
            }
        }
    }

    public Set<NodeRelationDTO> getAllDescendantRelations(Node node) {
        Set<NodeRelationDTO> relations = new HashSet<>();
        getAllDescendantRelationsRecursive(node, relations);
        return relations;
    }

    private void getAllDescendantRelationsRecursive(Node node, Set<NodeRelationDTO> relations) {
        List<NodeRelation> childRelations = nodeRelationRepository.findByNode1AndRelation(node, NodeRelation.RelationType.CHILD);
        for (NodeRelation relation : childRelations) {
            NodeRelationDTO dto = NodeRelationDTO.builder()
                    .node1(relation.getNode1())
                    .node2(relation.getNode2())
                    .relationType(relation.getRelation())
                    .build();
            if (relations.add(dto)) {
                getAllDescendantRelationsRecursive(relation.getNode2(), relations);
            }
        }
    }

    public Set<NodeRelationDTO> getUncleAndAuntRelations(Node node) {
        Set<NodeRelationDTO> relations = new HashSet<>();
        List<Node> parents = getDirectParents(node);
        
        for (Node parent : parents) {
            List<Node> siblings = getSiblings(parent);
            for (Node sibling : siblings) {
                relations.add(NodeRelationDTO.builder()
                        .node1(parent)
                        .node2(sibling)
                        .relationType(NodeRelation.RelationType.SIBLING)
                        .build());
            }
        }
        
        return relations;
    }

    public Set<NodeRelationDTO> getCousinRelations(Node node) {
        Set<NodeRelationDTO> relations = new HashSet<>();
        List<Node> parents = getDirectParents(node);
        
        for (Node parent : parents) {
            List<Node> parentSiblings = getSiblings(parent);
            for (Node parentSibling : parentSiblings) {
                relations.addAll(getAllDescendantRelations(parentSibling));
            }
        }
        
        return relations;
    }

    public Set<NodeRelationDTO> getAllFamilyRelations(Node node) {
        Set<NodeRelationDTO> allRelations = new HashSet<>();
        
        // Ajouter toutes les relations
        allRelations.addAll(getAllAncestorRelations(node));
        allRelations.addAll(getAllDescendantRelations(node));
        allRelations.addAll(getUncleAndAuntRelations(node));
        allRelations.addAll(getCousinRelations(node));
        
        // Ajouter les relations de conjoints
        List<NodeRelation> spouseRelations = nodeRelationRepository.findByNode1AndRelation(node, NodeRelation.RelationType.SPOUSE);
        spouseRelations.addAll(nodeRelationRepository.findByNode2AndRelation(node, NodeRelation.RelationType.SPOUSE));
        
        for (NodeRelation relation : spouseRelations) {
            allRelations.add(NodeRelationDTO.builder()
                    .node1(relation.getNode1())
                    .node2(relation.getNode2())
                    .relationType(relation.getRelation())
                    .build());
        }
        
        return allRelations;
    }

    public Node getBaseNode() {
        Long userId = getCurrentUserId();
        List<Node> baseNodes = nodeRepository.findByUserIdAndBaseNodeIsTrue(userId);
        if (baseNodes.isEmpty()) {
            throw NodeException.invalidInput("Nœud de base non trouvé pour l'utilisateur");
        }
        return baseNodes.get(0);
    }

    private void validateCreateNodeRequest(NodeRequest request) {
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

} 