package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class SnakeModel {
    private List<Point> snakeBody;
    private Point food;
    private Point superFood;   // Mới thêm
    private Point poisonFood;  // Mới thêm
    private List<Point> obstacles;
    private char direction;
    private int score;
    private boolean gameOver;

    public SnakeModel() {
        snakeBody = new ArrayList<>();
        obstacles = new ArrayList<>();
        direction = 'R';
        score = 0;
        gameOver = false;
    }

    // --- GETTERS & SETTERS ---
    public List<Point> getSnakeBody() { return snakeBody; }
    public Point getFood() { return food; }
    public void setFood(Point food) { this.food = food; }
    
    public Point getSuperFood() { return superFood; }
    public void setSuperFood(Point superFood) { this.superFood = superFood; }
    
    public Point getPoisonFood() { return poisonFood; }
    public void setPoisonFood(Point poisonFood) { this.poisonFood = poisonFood; }
    
    public List<Point> getObstacles() { return obstacles; }
    public char getDirection() { return direction; }
    public void setDirection(char direction) { this.direction = direction; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
}