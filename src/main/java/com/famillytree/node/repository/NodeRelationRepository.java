package com.famillytree.node.repository;

import com.famillytree.node.model.Node;
import com.famillytree.node.model.NodeRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRelationRepository extends JpaRepository<NodeRelation, Long> {
    List<NodeRelation> findByNode1AndRelation(Node node1, NodeRelation.RelationType relation);
    List<NodeRelation> findByNode2AndRelation(Node node2, NodeRelation.RelationType relation);
} 