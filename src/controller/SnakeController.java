package controller;

import model.SnakeModel;
import view.SnakeView;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeController implements ActionListener, KeyListener {
    private SnakeModel model;
    private SnakeView view;
    private Timer timer;
    private Random random;
    private int initialDelay;
    private int currentLevel;
    private int foodEatenCount = 0;

    private final int MAX_X = SnakeView.WIDTH / SnakeView.TILE_SIZE;
    private final int MAX_Y = SnakeView.HEIGHT / SnakeView.TILE_SIZE;

    public SnakeController(SnakeModel model, SnakeView view, int initialDelay, int level) {
        this.model = model;
        this.view = view;
        this.initialDelay = initialDelay;
        this.currentLevel = level;
        this.random = new Random();
        this.view.addKeyListener(this);
        
        // Đổi màu nền theo cấp độ
        if (level == 1) this.view.setBackground(new Color(139, 147, 105));
        else if (level == 2) this.view.setBackground(new Color(238, 232, 170));
        else if (level == 3) this.view.setBackground(new Color(240, 128, 128));

        initGame();
    }

    private void initGame() {
        model.getSnakeBody().clear();
        model.getSnakeBody().add(new Point(5, 5));
        model.getSnakeBody().add(new Point(4, 5));
        model.setDirection('R');
        model.setScore(0);
        model.setGameOver(false);
        foodEatenCount = 0;
        model.setSuperFood(null);
        model.setPoisonFood(null);

        model.getObstacles().clear();
        setupObstacles(currentLevel);
        spawnFood();
        
        if (timer != null) timer.stop();
        timer = new Timer(initialDelay, this);
        timer.start();
    }

    // --- HÀM KIỂM TRA VÙNG AN TOÀN CỦA CHỮ SCORE ---
    private boolean isScoreArea(int x, int y) {
        // Chữ Score nằm ở góc dưới bên trái (x khoảng 0-7, y khoảng MAX_Y-3 tới MAX_Y)
        return (x < 8 && y > MAX_Y - 4);
    }

    private void setupObstacles(int level) {
        int midX = MAX_X / 2;
        int midY = MAX_Y / 2;
        
        if (level == 1) {
            for (int i = 0; i < 8; i++) {
                int ox, oy;
                do {
                    ox = random.nextInt(MAX_X - 2) + 1;
                    oy = random.nextInt(MAX_Y - 2) + 1;
                } while (isScoreArea(ox, oy) || (ox < 8 && oy < 8)); // Tránh vùng Score và vùng Snake mới sinh
                model.getObstacles().add(new Point(ox, oy));
            }
        } else if (level == 2) {
            for (int i = 5; i <= MAX_X - 5; i++) {
                if (i < midX - 3 || i > midX + 3) {
                    // Chỉ vẽ tường nếu nó không đè lên vùng Score
                    if (!isScoreArea(i, midY - 5)) model.getObstacles().add(new Point(i, midY - 5));
                    if (!isScoreArea(i, midY + 5)) model.getObstacles().add(new Point(i, midY + 5));
                }
            }
        } else if (level == 3) {
            for (int i = 3; i < 8; i++) {
                // Tường góc chữ L, có kiểm tra tránh vùng Score
                addObstacleSafe(i, 3); addObstacleSafe(3, i);
                addObstacleSafe(MAX_X - i, 3); addObstacleSafe(MAX_X - 3, i);
                addObstacleSafe(i, MAX_Y - 3); addObstacleSafe(3, MAX_Y - i);
                addObstacleSafe(MAX_X - i, MAX_Y - 3); addObstacleSafe(MAX_X - 3, MAX_Y - i);
            }
            addObstacleSafe(midX, midY);
        }
    }

    private void addObstacleSafe(int x, int y) {
        if (!isScoreArea(x, y)) {
            model.getObstacles().add(new Point(x, y));
        }
    }

    private void spawnFood() {
        model.setFood(generateSafePoint());
        if (foodEatenCount > 0 && foodEatenCount % 5 == 0) {
            model.setSuperFood(generateSafePoint());
        } else {
            model.setSuperFood(null);
        }
        if (currentLevel > 1 && random.nextInt(10) > 6) {
            model.setPoisonFood(generateSafePoint());
        } else {
            model.setPoisonFood(null);
        }
    }

    private Point generateSafePoint() {
        Point p;
        do {
            p = new Point(random.nextInt(MAX_X - 2) + 1, random.nextInt(MAX_Y - 2) + 1);
        } while (model.getSnakeBody().contains(p) || model.getObstacles().contains(p) || isScoreArea(p.x, p.y));
        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!model.isGameOver()) {
            move();
            checkCollision();
            view.repaint();
        }
    }

    private void move() {
        Point head = model.getSnakeBody().get(0);
        Point newHead = new Point(head.x, head.y);
        switch (model.getDirection()) {
            case 'U': newHead.y--; break;
            case 'D': newHead.y++; break;
            case 'L': newHead.x--; break;
            case 'R': newHead.x++; break;
        }

        if (newHead.x < 0) newHead.x = MAX_X - 1;
        else if (newHead.x >= MAX_X) newHead.x = 0;
        if (newHead.y < 0) newHead.y = MAX_Y - 1;
        else if (newHead.y >= MAX_Y) newHead.y = 0;

        model.getSnakeBody().add(0, newHead);

        if (newHead.equals(model.getFood())) {
            model.setScore(model.getScore() + 10);
            foodEatenCount++;
            Toolkit.getDefaultToolkit().beep();
            spawnFood();
            if (timer.getDelay() > 30) timer.setDelay(timer.getDelay() - 3);
        } else if (model.getSuperFood() != null && newHead.equals(model.getSuperFood())) {
            model.setScore(model.getScore() + 50);
            model.setSuperFood(null);
            Toolkit.getDefaultToolkit().beep();
        } else if (model.getPoisonFood() != null && newHead.equals(model.getPoisonFood())) {
            model.setScore(Math.max(0, model.getScore() - 20));
            model.setPoisonFood(null);
            Toolkit.getDefaultToolkit().beep();
        } else {
            model.getSnakeBody().remove(model.getSnakeBody().size() - 1);
        }
    }

    private void checkCollision() {
        Point head = model.getSnakeBody().get(0);
        for (int i = 1; i < model.getSnakeBody().size(); i++) {
            if (head.equals(model.getSnakeBody().get(i))) endGame();
        }
        if (model.getObstacles().contains(head)) endGame();
    }

    private void endGame() {
        model.setGameOver(true);
        timer.stop();
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && model.getDirection() != 'R') model.setDirection('L');
        else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && model.getDirection() != 'L') model.setDirection('R');
        else if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && model.getDirection() != 'D') model.setDirection('U');
        else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && model.getDirection() != 'U') model.setDirection('D');
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}