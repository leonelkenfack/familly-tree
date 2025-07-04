package com.famillytree.node.repository;

import com.famillytree.node.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByInvitationCode(String invitationCode);
    boolean existsByInvitationCode(String invitationCode);
} 