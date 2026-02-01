package com.SecureLibrarySystem.webapp.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.SecureLibrarySystem.webapp.authorization.Role;
import com.SecureLibrarySystem.webapp.model.Notification;
import com.SecureLibrarySystem.webapp.model.NotificationType;

public interface NotificationDAO extends JpaRepository<Notification, Long> {
    List<Notification> findByTargetRoleOrderByCreatedAtDesc(Role targetRole);
    List<Notification> findByTargetUsernameOrderByCreatedAtDesc(String targetUsername);
    boolean existsByTypeAndReferenceIdAndTargetRole(NotificationType type, Long referenceId, Role targetRole);
    boolean existsByTypeAndReferenceIdAndTargetUsername(NotificationType type, Long referenceId, String targetUsername);
}
