package com.kecamatan.util;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Utility class for Role-Based Access Control (RBAC) operations.
 * Centralizes RBAC logic to avoid code duplication across controllers.
 */
public class RBACUtil {
    
    /**
     * Sets up the user name and role labels in the sidebar.
     * Automatically applies the correct style class based on role.
     * 
     * @param nameLabel Label to display username
     * @param roleLabel Label to display role badge
     */
    public static void setupUserLabels(Label nameLabel, Label roleLabel) {
        if (nameLabel != null) {
            nameLabel.setText(UserSession.getUsername());
        }
        
        if (roleLabel != null) {
            String role = UserSession.getRole();
            roleLabel.setText(role);
            roleLabel.getStyleClass().clear();
            
            if ("ADMIN".equals(role)) {
                roleLabel.getStyleClass().add("role-badge-admin");
            } else {
                roleLabel.getStyleClass().add("role-badge-warga");
            }
        }
    }
    
    /**
     * Hides navigation buttons that should only be visible to admins.
     * Call this method for non-admin users.
     * 
     * @param buttons Variable number of buttons to hide
     */
    public static void hideAdminButtons(Button... buttons) {
        for (Button btn : buttons) {
            if (btn != null) {
                btn.setManaged(false);
                btn.setVisible(false);
            }
        }
    }
    
    /**
     * Applies full RBAC settings - sets up labels and hides buttons for non-admins.
     * This is a convenience method that combines setupUserLabels and hideAdminButtons.
     * 
     * @param nameLabel User name label
     * @param roleLabel Role badge label
     * @param adminButtons Buttons to hide if user is not admin
     */
    public static void applyRBAC(Label nameLabel, Label roleLabel, Button... adminButtons) {
        setupUserLabels(nameLabel, roleLabel);
        
        if (!UserSession.isAdmin()) {
            hideAdminButtons(adminButtons);
        }
    }
}
