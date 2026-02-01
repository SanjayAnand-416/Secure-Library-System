package com.SecureLibrarySystem.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SecureLibrarySystem.webapp.authorization.Role;
import com.SecureLibrarySystem.webapp.dao.NotificationDAO;
import com.SecureLibrarySystem.webapp.model.Notification;
import com.SecureLibrarySystem.webapp.model.NotificationType;

@Service
public class NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;

    public List<Notification> getNotificationsFor(Role role) {
        return notificationDAO.findByTargetRoleOrderByCreatedAtDesc(role);
    }

    public List<Notification> getNotificationsForUser(String username) {
        return notificationDAO.findByTargetUsernameOrderByCreatedAtDesc(username);
    }

    public void createIfMissing(Role role, NotificationType type, Long referenceId, String message) {
        if (referenceId == null) {
            return;
        }
        boolean exists = notificationDAO.existsByTypeAndReferenceIdAndTargetRole(type, referenceId, role);
        if (exists) {
            return;
        }
        Notification notification = new Notification();
        notification.setTargetRole(role);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setMessage(message);
        notificationDAO.save(notification);
    }

    public void createForUser(String username, NotificationType type, Long referenceId, String message) {
        if (referenceId == null || username == null || username.isBlank()) {
            return;
        }
        boolean exists = notificationDAO.existsByTypeAndReferenceIdAndTargetUsername(type, referenceId, username);
        if (exists) {
            return;
        }
        Notification notification = new Notification();
        notification.setTargetRole(Role.STUDENT);
        notification.setTargetUsername(username);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setMessage(message);
        notificationDAO.save(notification);
    }
}
