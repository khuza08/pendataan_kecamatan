// src/pendataankecamatan/Main.java
package pendataankecamatan;

import pendataankecamatan.view.HomeView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomeView().setVisible(true);
        });
    }
}