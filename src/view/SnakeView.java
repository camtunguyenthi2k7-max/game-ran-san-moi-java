package view;

import model.SnakeModel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.*;

public class SnakeView extends JPanel {
    public static final int TILE_SIZE = 25;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    private SnakeModel model;

    private Image headImg, bodyImg, tailImg, foodImg, superFoodImg, poisonImg;
    private final Color NOKIA_BLACK = new Color(20, 20, 20);
    private boolean flash = true; 

    public SnakeView(SnakeModel model) {
        this.model = model;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);

        // Tải ảnh (Nhớ đúng tên file Quỳnh đã lưu nhé)
        headImg = new ImageIcon("images/head.png").getImage();
        bodyImg = new ImageIcon("images/body.png").getImage();
        tailImg = new ImageIcon("images/tail.png").getImage();
        foodImg = new ImageIcon("images/food.png").getImage();
        superFoodImg = new ImageIcon("images/watermelon.png").getImage();
        poisonImg = new ImageIcon("images/mushroom.png").getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        flash = !flash; // Tạo hiệu ứng chớp nháy
        if (!model.isGameOver()) {
            drawGame(g);
        } else {
            drawGameOver(g);
        }
    }

    private void drawGame(Graphics g) {
        // Vẽ biên và chướng ngại vật
        g.setColor(NOKIA_BLACK);
        g.fillRect(0, 0, WIDTH, TILE_SIZE);
        g.fillRect(0, HEIGHT - TILE_SIZE, WIDTH, TILE_SIZE);
        g.fillRect(0, 0, TILE_SIZE, HEIGHT);
        g.fillRect(WIDTH - TILE_SIZE, 0, TILE_SIZE, HEIGHT);
        for (Point obs : model.getObstacles()) {
            g.fillRect(obs.x * TILE_SIZE, obs.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
        
        // 1. VẼ MỒI THƯỜNG (Táo)
        if (model.getFood() != null) {
            g.drawImage(foodImg, model.getFood().x * TILE_SIZE, model.getFood().y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
        }

        // 2. VẼ SIÊU MỒI (Dưa hấu) - Chớp nháy cho nó rực rỡ
        if (model.getSuperFood() != null && flash) {
            g.drawImage(superFoodImg, model.getSuperFood().x * TILE_SIZE, model.getSuperFood().y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
        }

        // 3. VẼ NẤM ĐỘC
        if (model.getPoisonFood() != null) {
            g.drawImage(poisonImg, model.getPoisonFood().x * TILE_SIZE, model.getPoisonFood().y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
        }

        // 4. VẼ RẮN (Có hiệu ứng há miệng và tự động xoay thân/đuôi)
        for (int i = 0; i < model.getSnakeBody().size(); i++) {
            Point p = model.getSnakeBody().get(i);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Đưa tâm xoay về giữa ô
            g2d.translate(p.x * TILE_SIZE + TILE_SIZE / 2.0, p.y * TILE_SIZE + TILE_SIZE / 2.0);

            if (i == 0) {
                // --- VẼ ĐẦU RẮN ---
                int extra = 0;
                if (model.getFood() != null && p.distance(model.getFood()) < 2) extra = 6; // Há miệng
                
                char dir = model.getDirection();
                if (dir == 'D') g2d.rotate(Math.PI / 2);
                else if (dir == 'L') g2d.rotate(Math.PI);
                else if (dir == 'U') g2d.rotate(-Math.PI / 2);
                
                g2d.drawImage(headImg, -(TILE_SIZE + extra) / 2, -(TILE_SIZE + extra) / 2, TILE_SIZE + extra, TILE_SIZE + extra, this);
                
            } else {
                // --- VẼ THÂN VÀ ĐUÔI RẮN ---
                // Lấy vị trí của đốt liền trước nó (đốt nối về phía đầu)
                Point prev = model.getSnakeBody().get(i - 1);
                
                // Tính khoảng cách để xác định hướng của thân
                int dx = prev.x - p.x;
                int dy = prev.y - p.y;
                
                // Xử lý trường hợp rắn đi xuyên tường (wrap around) để không bị lỗi xoay
                if (dx > 1) dx = -1; else if (dx < -1) dx = 1;
                if (dy > 1) dy = -1; else if (dy < -1) dy = 1;

                // Xoay hình ảnh thân/đuôi hướng về phía đốt trước nó
                if (dx == 1) g2d.rotate(0);                 // Hướng Phải
                else if (dx == -1) g2d.rotate(Math.PI);     // Hướng Trái
                else if (dy == 1) g2d.rotate(Math.PI / 2);  // Hướng Xuống
                else if (dy == -1) g2d.rotate(-Math.PI / 2);// Hướng Lên

                if (i == model.getSnakeBody().size() - 1) {
                    // Vẽ đuôi
                    g2d.drawImage(tailImg, -TILE_SIZE / 2, -TILE_SIZE / 2, TILE_SIZE, TILE_SIZE, this);
                } else {
                    // Vẽ thân (cộng thêm 2 pixel như code gốc của bạn)
                    g2d.drawImage(bodyImg, -(TILE_SIZE + 2) / 2, -TILE_SIZE / 2, TILE_SIZE + 2, TILE_SIZE, this);
                }
            }
            g2d.dispose();
        }
        
        g.setColor(NOKIA_BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.drawString("Score: " + model.getScore(), 35, HEIGHT - 35);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(NOKIA_BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        g.drawString("GAME OVER", WIDTH / 2 - 110, HEIGHT / 2);
    }
}