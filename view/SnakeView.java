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

    private Image headImg, bodyImg, tailImg, foodImg, superFoodImg, poisonImg, enemyImg;
    private final Color NOKIA_BLACK = new Color(20, 20, 20);
    private boolean flash = true; 

    public SnakeView(SnakeModel model) {
        this.model = model;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);

        headImg = new ImageIcon("images/head.png").getImage();
        bodyImg = new ImageIcon("images/body.png").getImage();
        tailImg = new ImageIcon("images/tail.png").getImage();
        foodImg = new ImageIcon("images/food.png").getImage();
        superFoodImg = new ImageIcon("images/watermelon.png").getImage();
        poisonImg = new ImageIcon("images/mushroom.png").getImage();
        enemyImg = new ImageIcon("images/enemy.png").getImage(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        flash = !flash; 
        if (model.isGameOver()) {
            drawGameOver(g);
        } else {
            drawGame(g);
            
            // Vẽ màn hình PAUSE
            if (model.isPaused()) {
            	// Tham số thứ 4 (150) là kênh Alpha, dùng để tạo lớp màn đen mờ trong suốt
                g.setColor(new Color(0, 0, 0, 150)); 
                g.fillRect(0, 0, WIDTH, HEIGHT);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced", Font.BOLD, 40));
                g.drawString("PAUSED", WIDTH / 2 - 70, HEIGHT / 2);
                g.setFont(new Font("Monospaced", Font.PLAIN, 18));
                g.drawString("Nhấn SPACE để tiếp tục", WIDTH / 2 - 120, HEIGHT / 2 + 40);
            }
        }
    }

    private void drawGame(Graphics g) {
        g.setColor(NOKIA_BLACK);
        g.fillRect(0, 0, WIDTH, TILE_SIZE);
        g.fillRect(0, HEIGHT - TILE_SIZE, WIDTH, TILE_SIZE);
        g.fillRect(0, 0, TILE_SIZE, HEIGHT);
        g.fillRect(WIDTH - TILE_SIZE, 0, TILE_SIZE, HEIGHT);
        for (Point obs : model.getObstacles()) {
            g.fillRect(obs.x * TILE_SIZE, obs.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
        
        if (model.getFood() != null) g.drawImage(foodImg, model.getFood().x * TILE_SIZE, model.getFood().y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
        if (model.getSuperFood() != null && flash) g.drawImage(superFoodImg, model.getSuperFood().x * TILE_SIZE, model.getSuperFood().y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
        if (model.getPoisonFood() != null) g.drawImage(poisonImg, model.getPoisonFood().x * TILE_SIZE, model.getPoisonFood().y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);

        for (int i = 0; i < model.getSnakeBody().size(); i++) {
            Point p = model.getSnakeBody().get(i);
            Graphics2D g2d = (Graphics2D) g.create();
            
            g2d.translate(p.x * TILE_SIZE + TILE_SIZE / 2.0, p.y * TILE_SIZE + TILE_SIZE / 2.0);

            if (i == 0) {
                int extra = 0;
                if (model.getFood() != null && p.distance(model.getFood()) < 2) extra = 6; 
                char dir = model.getDirection();
                if (dir == 'D') g2d.rotate(Math.PI / 2);
                else if (dir == 'L') g2d.rotate(Math.PI);
                else if (dir == 'U') g2d.rotate(-Math.PI / 2);
                
                g2d.drawImage(headImg, -(TILE_SIZE + extra) / 2, -(TILE_SIZE + extra) / 2, TILE_SIZE + extra, TILE_SIZE + extra, this);
                
            } else {
                Point prev = model.getSnakeBody().get(i - 1);
                int dx = prev.x - p.x; int dy = prev.y - p.y;
                
                if (dx > 1) dx = -1; else if (dx < -1) dx = 1;
                if (dy > 1) dy = -1; else if (dy < -1) dy = 1;

                if (dx == 1) g2d.rotate(0);                 
                else if (dx == -1) g2d.rotate(Math.PI);     
                else if (dy == 1) g2d.rotate(Math.PI / 2);  
                else if (dy == -1) g2d.rotate(-Math.PI / 2);

                if (i == model.getSnakeBody().size() - 1) g2d.drawImage(tailImg, -TILE_SIZE / 2, -TILE_SIZE / 2, TILE_SIZE, TILE_SIZE, this);
                else g2d.drawImage(bodyImg, -(TILE_SIZE + 2) / 2, -TILE_SIZE / 2, TILE_SIZE + 2, TILE_SIZE, this);
            }
            g2d.dispose();
        }

        if (model.getMovingObstacle() != null && enemyImg != null) {
            g.drawImage(enemyImg, model.getMovingObstacle().x * TILE_SIZE, model.getMovingObstacle().y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
        }
        
        g.setColor(NOKIA_BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.drawString("Score: " + model.getScore(), 35, HEIGHT - 35);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(NOKIA_BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        g.drawString("GAME OVER", WIDTH / 2 - 110, HEIGHT / 2);
        
        g.setFont(new Font("Monospaced", Font.PLAIN, 18));
        g.drawString("Nhấn SPACE để chơi lại", WIDTH / 2 - 120, HEIGHT / 2 + 40);
        g.drawString("Tắt cửa sổ (X) để lưu điểm", WIDTH / 2 - 130, HEIGHT / 2 + 70);
    }
}