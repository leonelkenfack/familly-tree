package com.famillytree.node.repository;

import com.famillytree.node.model.Node;
import com.famillytree.node.model.NodeRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRelationRepository extends JpaRepository<NodeRelation, Long> {
    List<NodeRelation> findByNode1AndRelation(Node node1, NodeRelation.RelationType relation);
} 