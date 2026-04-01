package view;

import javax.swing.*;
import java.awt.*;

public class WelcomeView extends JFrame {
    public WelcomeView() {
        setTitle("Game Rắn Săn Mồi - Nhóm 8");
        setSize(750, 500); // Kích thước bằng với trang Quản lý
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- TẢI ẢNH NỀN VÀ VẼ LÊN PANEL ---
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            // Nhớ gõ đúng tên file ảnh Sương vừa lưu nha
            Image bg = new ImageIcon("images/bg_welcome.png").getImage(); 
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        // --- TẠO NÚT BẮT ĐẦU ---
        JButton btnStart = new JButton("BẮT ĐẦU");
        btnStart.setFont(new Font("Arial", Font.BOLD, 24));
        btnStart.setBackground(new Color(46, 204, 113)); // Xanh lá cây
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // SỰ KIỆN: Bấm nút thì TẮT trang này, MỞ trang Quản lý
        btnStart.addActionListener(e -> {
            this.dispose(); // Đóng màn hình hình con rắn
            new MainMenuView().setVisible(true); // Mở cái bảng nền trắng lên
        });

        // Căn chỉnh vị trí nút nằm ở dưới cùng
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false); // Trong suốt để không che ảnh
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0)); // Đẩy lên 1 xíu cho đẹp
        bottomPanel.add(btnStart);

        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(backgroundPanel);
    }
}