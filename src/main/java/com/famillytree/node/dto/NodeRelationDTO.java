package com.famillytree.node.dto;

import com.famillytree.node.model.NodeRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeRelationDTO {
    private NodeDTO node1;
    private NodeDTO node2;
    private NodeRelation.RelationType relationType;
} 