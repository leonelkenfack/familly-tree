package com.famillytree.node.repository;

import com.famillytree.node.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Optional<Node> findByUserId(Long userId);
    List<Node> findByUserIdAndBaseNodeIsTrue(Long userId);
} 