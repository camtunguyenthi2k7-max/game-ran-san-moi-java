package controller;

import model.SnakeModel;
import view.SnakeView;
import utils.SoundManager;

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
        model.setPaused(false); // Reset trạng thái Pause
        foodEatenCount = 0;
        model.setSuperFood(null);
        model.setPoisonFood(null);

        model.getObstacles().clear();
        setupObstacles(currentLevel);
        spawnFood();
        
        // Khởi tạo chướng ngại vật tuần tra
        model.setMovingObstacle(new Point(MAX_X / 2, MAX_Y / 2)); 
        model.setMovingDir('D');

        if (timer != null) timer.stop();
     // Dùng Timer của Swing làm vòng lặp game để không bị đơ giao diện (Freeze GUI)
        timer = new Timer(initialDelay, this);
        timer.start();

        SoundManager.playBackgroundMusic("sounds/bgm.wav");
    }

    private boolean isScoreArea(int x, int y) {
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
                } while (isScoreArea(ox, oy) || (ox < 8 && oy < 8)); 
                model.getObstacles().add(new Point(ox, oy));
            }
        } else if (level == 2) {
            for (int i = 5; i <= MAX_X - 5; i++) {
                if (i < midX - 3 || i > midX + 3) {
                    if (!isScoreArea(i, midY - 5)) model.getObstacles().add(new Point(i, midY - 5));
                    if (!isScoreArea(i, midY + 5)) model.getObstacles().add(new Point(i, midY + 5));
                }
            }
        } else if (level == 3) {
            for (int i = 3; i < 8; i++) {
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
        if (!model.isGameOver() && !model.isPaused()) {
            move();
            moveMovingObstacle(); 
            checkCollision();
            view.repaint();
        }
    }

    private void moveMovingObstacle() {
        Point mo = model.getMovingObstacle();
        if (mo == null) return;
     // Tỷ lệ 15% bẻ lái ngẫu nhiên 1 trong 4 hướng (Thuật toán AI tuần tra)
        if (random.nextInt(100) < 15) {
            char[] dirs = {'U', 'D', 'L', 'R'};
            model.setMovingDir(dirs[random.nextInt(4)]);
        }

        int nextX = mo.x;
        int nextY = mo.y;

        if (model.getMovingDir() == 'U') nextY--;
        else if (model.getMovingDir() == 'D') nextY++;
        else if (model.getMovingDir() == 'L') nextX--;
        else if (model.getMovingDir() == 'R') nextX++;

        if (nextX <= 1) { 
            nextX = 2; model.setMovingDir('R'); 
        } else if (nextX >= MAX_X - 2) { 
            nextX = MAX_X - 3; model.setMovingDir('L'); 
        }
        
        if (nextY <= 1) { 
            nextY = 2; model.setMovingDir('D'); 
        } else if (nextY >= MAX_Y - 5) { 
            nextY = MAX_Y - 6; model.setMovingDir('U'); 
        }

        mo.x = nextX;
        mo.y = nextY;
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
            
            SoundManager.playSound("sounds/eat.wav"); 
            spawnFood();
            if (timer.getDelay() > 30) timer.setDelay(timer.getDelay() - 3);
            
        } else if (model.getSuperFood() != null && newHead.equals(model.getSuperFood())) {
            model.setScore(model.getScore() + 50);
            model.setSuperFood(null);
            SoundManager.playSound("sounds/eat.wav");
            
        } else if (model.getPoisonFood() != null && newHead.equals(model.getPoisonFood())) {
            model.setScore(Math.max(0, model.getScore() - 20));
            model.setPoisonFood(null);
            SoundManager.playSound("sounds/die.wav"); 
            
        } else {
            model.getSnakeBody().remove(model.getSnakeBody().size() - 1);
        }
    }

    private void checkCollision() {
        Point head = model.getSnakeBody().get(0);
        
        // 1. Kiểm tra rắn tự cắn đuôi
        for (int i = 1; i < model.getSnakeBody().size(); i++) {
            if (head.equals(model.getSnakeBody().get(i))) endGame();
        }
        
        // 2. Kiểm tra đụng chướng ngại vật tĩnh
        if (model.getObstacles().contains(head)) endGame();

        // 3. KIỂM TRA ĐỤNG QUÁI VẬT (BẢN CẬP NHẬT VÁ LỖI)
        Point enemy = model.getMovingObstacle();
        if (enemy != null) {
            // Tông thẳng đầu vào quái -> Chết
            if (head.equals(enemy)) {
                endGame();
            } 
            // Quái đi xuyên qua đầu, đụng vào thân, hoặc dẫm lên đuôi -> Cũng chết
            else if (model.getSnakeBody().contains(enemy)) {
                endGame();
            }
        }
    }

    private void endGame() {
        model.setGameOver(true);
        timer.stop();
        SoundManager.stopBackgroundMusic(); 
        SoundManager.playSound("sounds/die.wav"); 
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
 
        if (key == KeyEvent.VK_SPACE) {
            if (model.isGameOver()) {
                initGame();
                view.repaint();
            } else {
                model.setPaused(!model.isPaused()); 
                if (model.isPaused()) {
                    timer.stop();
                    SoundManager.stopBackgroundMusic();
                } else {
                    timer.start();
                    SoundManager.playBackgroundMusic("sounds/bgm.wav");
                }
                view.repaint(); // Cập nhật màn hình mờ
            }
            return; 
        }

        // ĐIỀU CHỈNH HƯỚNG ĐI
        if (!model.isGameOver() && !model.isPaused()) {	
        	// Chặn lỗi đi ngược (Reverse Movement) để rắn không tự cắn cổ
            if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && model.getDirection() != 'R') model.setDirection('L');
            else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && model.getDirection() != 'L') model.setDirection('R');
            else if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && model.getDirection() != 'D') model.setDirection('U');
            else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && model.getDirection() != 'U') model.setDirection('D');
        }
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}