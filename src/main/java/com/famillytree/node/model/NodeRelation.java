package com.famillytree.node.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "node_relations")
public class NodeRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node1_id", nullable = false)
    private Node node1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationType relation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node2_id", nullable = false)
    private Node node2;

    public enum RelationType {
        SIBLING("Frère/Soeur"),
        CHILD("Fils/Fille"),
        PARENT("Père/Mère"),
        SPOUSE("Époux/Épouse");

        private final String label;

        RelationType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
} 