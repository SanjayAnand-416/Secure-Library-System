package com.SecureLibrarySystem.webapp.authorization;

public class AccessControl {

    public static boolean canViewBooks(Role role) {
        return role == Role.ADMIN || role == Role.LIBRARIAN || role == Role.STUDENT;
    }

    public static boolean canAddBook(Role role) {
        return role == Role.ADMIN || role == Role.LIBRARIAN;
    }

    public static boolean canViewTransactions(Role role) {
        return role == Role.ADMIN || role == Role.LIBRARIAN;
    }
}
