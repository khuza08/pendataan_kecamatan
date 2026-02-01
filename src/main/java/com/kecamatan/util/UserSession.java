package com.kecamatan.util;

public class UserSession {
    private static int userId;
    private static String username;
    private static String role; // "ADMIN" or "WARGA"
    private static String nik; // Only for WARGA

    public static void login(int id, String name, String userRole) {
        userId = id;
        username = name;
        role = userRole;
    }

    public static void loginWarga(int id, String name, String citizenNik) {
        userId = id;
        // Store only the first name for display purposes
        if (name != null && name.contains(" ")) {
            username = name.split(" ")[0];
        } else {
            username = name;
        }
        role = "WARGA";
        nik = citizenNik;
    }

    public static void logout() {
        userId = -1;
        username = null;
        role = null;
        nik = null;
    }

    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }
    public static String getRole() { return role; }
    public static String getNik() { return nik; }
    public static boolean isAdmin() { return "ADMIN".equals(role); }
}
