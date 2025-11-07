package pendataankecamatan;

import javax.swing.SwingUtilities;
import pendataankecamatan.view.LoginView;


public class Main {

    public static void main(String[] args) {
        // Pastikan UI dibuat dan diakses dari Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginView().setVisible(true);
            }
        });
    }
}