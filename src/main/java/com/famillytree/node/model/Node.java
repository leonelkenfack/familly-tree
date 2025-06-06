package com.famillytree.node.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nodes")
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = true)
    private String title;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private String phone;

    @ElementCollection
    @CollectionTable(name = "node_interests", joinColumns = @JoinColumn(name = "node_id"))
    @Column(name = "interest")
    private List<String> interests;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "base_node", nullable = false, columnDefinition = "boolean default false")
    private boolean baseNode;
} 