import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Game extends JFrame {

    private static final int TILE_SIZE = 40;
    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;
    private static final int ALL_TILES = WIDTH * HEIGHT;
    private static final int DELAY = 150;

    private final int[] x = new int[ALL_TILES];
    private final int[] y = new int[ALL_TILES];

    private int bodyParts = 3;
    private int applesEaten = 0;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;

    public Game() {
        random = new Random();
        setTitle("Змейка");

        // window size
        int windowWidth = WIDTH * TILE_SIZE;
        int windowHeight = HEIGHT * TILE_SIZE + 40;

        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // init snake
        for (int i = 0; i < bodyParts; i++) {
            x[i] = (WIDTH/2 - i) * TILE_SIZE;
            y[i] = (HEIGHT/2) * TILE_SIZE;
        }

        newApple();
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        startGame();
    }

    public void startGame() {
        running = true;
        timer = new Timer(DELAY, e -> {
            if (running) {
                move();
                checkApple();
                checkCollisions();
                repaint();
            }
        });
        timer.start();
    }

    public void newApple() {
        appleX = random.nextInt(WIDTH) * TILE_SIZE;
        appleY = random.nextInt(HEIGHT) * TILE_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= TILE_SIZE;
            case 'D' -> y[0] += TILE_SIZE;
            case 'L' -> x[0] -= TILE_SIZE;
            case 'R' -> x[0] += TILE_SIZE;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // body in body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // grani
        if (x[0] < 0 || x[0] >= WIDTH * TILE_SIZE || y[0] < 0 || y[0] >= HEIGHT * TILE_SIZE) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }
    //pokaz game and enter
    private class GamePanel extends JPanel implements KeyListener {

        public GamePanel() {
            setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
            setBackground(Color.BLACK);
            setFocusable(true);//фокусируемая панель, для обработки клавиш
            addKeyListener(this);//отмечает текущий объектa gamePanel
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);//вызов реализации JPanel

            if (running) {
                // apple
                g.setColor(Color.RED);
                g.fillOval(appleX, appleY, TILE_SIZE, TILE_SIZE);

                // Змейка
                for (int i = 0; i < bodyParts; i++) {
                    if (i == 0) {
                        g.setColor(Color.PINK); // head
                    } else {
                        g.setColor(new Color(132, 0, 190)); // body
                    }
                    g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
                }

                // count
                g.setColor(Color.WHITE);
                g.setFont(new Font("Times New Roman", Font.BOLD, 20));
                g.drawString("Счет: " + applesEaten, 10, 20);
            } else {
                // the end
                g.setColor(new Color(170,0,150));
                g.setFont(new Font("Times New Roman", Font.BOLD, 40));
                g.drawString("Игра окончена!",
                        getWidth()/2 - 150, getHeight()/2 - 50);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
                g.drawString("Счет: " + applesEaten,
                        getWidth()/2 - 40, getHeight()/2);

                g.drawString("Нажмите пробел для рестарта",
                        getWidth()/2 - 140, getHeight()/2 + 50);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        bodyParts = 3;
                        applesEaten = 0;
                        direction = 'R';
                        for (int i = 0; i < bodyParts; i++) {
                            x[i] = (WIDTH/2 - i) * TILE_SIZE;
                            y[i] = HEIGHT/2 * TILE_SIZE;
                        }
                        startGame();
                    }
                    break;
            }
        }

        @Override public void keyTyped(KeyEvent e) {}//для клавиш с символами
        @Override public void keyReleased(KeyEvent e) {}//нужно только нажатие клавиш а не опускание
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Game();
        });
    }
}