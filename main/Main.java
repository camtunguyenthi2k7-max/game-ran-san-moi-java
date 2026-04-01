package main;

import view.WelcomeView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Khởi động vào trang có hình con rắn trước!
            new WelcomeView().setVisible(true); 
        });
    }
}