import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.Random;


/*TODO:
 *  + remove RunningKeyListener when PAUSE is pressed.*
 * */

public class GamePanel extends JPanel implements ActionListener {
    Configs configs = new Configs();
    TimerRunning timerRunning;
    MyKeyAdapter myKeyAdapter = new MyKeyAdapter();
    boolean myKeyAdapter_isAlive = false;

    GamePanel() {
        configs.random = new Random();
        this.setPreferredSize(new Dimension(Configs.SCREEN_WIDTH, Configs.SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(configs.myKeyAdapterMENU);
        configs.myKeyAdapterMENU_isAlive = true;
        newApple();
        configs.timerProcess = new Timer(Configs.PROCESSING_DELAY, this);
        configs.timerProcess.start();
        timerRunning = new TimerRunning();
    }

    public void initGame() {
        configs.bodyParts = 6;
        configs.direction = 'R';
        configs.applesEaten = 0;
        configs.gameStarted = false;
        configs.running = false;
        configs.pause = false;
        /* configs.gameOver = false; *//*!must not initialize!*/
        configs.enterWasPressed = false;
        configs.pressEnterToRestart_Flippable = true;
        Arrays.fill(configs.x, 0);//reset array
        Arrays.fill(configs.y, 0);//reset array
        Configs.speed = Configs.Normal;
        Configs.DELAY = Configs.Normal_DELAY;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        processDisplay(g);
    }

    public void processDisplay(Graphics g) {
        if (!configs.gameStarted) {//display MENU
            displayMenu(g);
        }
        if (configs.running && configs.gameStarted) {
            displayRunning(g);
        }
        if (configs.pause && configs.gameStarted) {//display "PAUSE" message
            pause(g);
        }
        if (configs.gameOver && !configs.running && configs.gameStarted) {//display "Game Over" message
            gameOver(g);
        }
    }

    public void newApple() {
        boolean checkAppleX = true; //initialize with 'true'
        boolean checkAppleY = true; //initialize with 'true'

        while (checkAppleX || checkAppleY) {
            /*generate appleX:*/
            if (checkAppleX) {
                configs.appleX = configs.random.nextInt(Configs.SCREEN_WIDTH / Configs.UNIT_SIZE) * Configs.UNIT_SIZE;//generate appleX
                checkAppleX = false; //loop was ended successfully. means no collision.
            }

            /*generate appleY:*/
            if (checkAppleY) {
                configs.appleY = configs.random.nextInt(Configs.SCREEN_HEIGHT / Configs.UNIT_SIZE) * Configs.UNIT_SIZE;
                checkAppleY = false; //loop was ended successfully. means no collision.
            }

            /*check for collision with snake's body:*/
            for (int i = configs.bodyParts - 1; i >= 0; i--) {
                if ((configs.x[i] == configs.appleX) && (configs.y[i] == configs.appleY)) {
                    //collision with snake's body was found! regenerate appleX and appleY, and restart checking.
                    checkAppleX = true; // indicate that we need to randomise again
                    checkAppleY = true; // indicate that we need to randomise again
                    break;
                }
            }
        }
    }

    public void move() {
        for (int i = configs.bodyParts; i > 0; i--) {
            configs.x[i] = configs.x[i - 1];
            configs.y[i] = configs.y[i - 1];
        }

        switch (configs.direction) {
            case 'U':
                configs.y[0] = configs.y[0] - Configs.UNIT_SIZE;
                break;
            case 'D':
                configs.y[0] = configs.y[0] + Configs.UNIT_SIZE;
                break;
            case 'L':
                configs.x[0] = configs.x[0] - Configs.UNIT_SIZE;
                break;
            case 'R':
                configs.x[0] = configs.x[0] + Configs.UNIT_SIZE;
                break;
        }

    }

    public void checkApple() {
        if ((configs.x[0] == configs.appleX) && (configs.y[0] == configs.appleY)) {
            configs.bodyParts++;
            configs.applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for (int i = configs.bodyParts - 1; i > 1; i--) {
            if ((configs.x[0] == configs.x[i]) && (configs.y[0] == configs.y[i])) {
                configs.running = false;
                configs.gameOver = true;
                break;
            }
        }
        //check if head touches left border
        if (configs.x[0] < 0) {
            configs.running = false;
            configs.gameOver = true;
        }
        //check if head touches right border
        if (configs.x[0] >= Configs.SCREEN_WIDTH) { //note: needs to be >= here
            configs.running = false;
            configs.gameOver = true;
        }
        //check if head touches top border
        if (configs.y[0] < 0) {
            configs.running = false;
            configs.gameOver = true;
        }
        //check if head touches bottom border
        if (configs.y[0] >= Configs.SCREEN_HEIGHT) {//note: needs to be >= here
            configs.running = false;
            configs.gameOver = true;
        }
    }

    public void displayRunning(Graphics g) {
        /*
			for(int i=0;i<Configs.SCREEN_HEIGHT/UNIT_SIZE;i++) {//paint grid
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, Configs.SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, Configs.SCREEN_WIDTH, i*UNIT_SIZE);
			}
			*/
        g.setColor(Color.red);
        g.fillOval(configs.appleX, configs.appleY, Configs.UNIT_SIZE, Configs.UNIT_SIZE);

        for (int i = 0; i < configs.bodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.green);
            } else {

                g.setColor(configs.snakeBodyColor);
                //g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
            }
            g.fillRect(configs.x[i], configs.y[i], Configs.UNIT_SIZE, Configs.UNIT_SIZE);
        }
        g.setColor(Color.red);
        g.setFont(configs.font_Score);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + configs.applesEaten, (Configs.SCREEN_WIDTH - metrics.stringWidth("Score: " + configs.applesEaten)) / 2, g.getFont().getSize());
    }

    public void displayMenu(Graphics g) {
        g.setFont(configs.font_MenuAndPAUSE);
        g.setColor(Color.red);
        FontMetrics chooseSpeedMetrics = getFontMetrics(g.getFont());
        g.drawString("Choose Speed:", (Configs.SCREEN_WIDTH - chooseSpeedMetrics.stringWidth("Choose Speed:")) / 2, 200);
        //display TEXT:
        g.setColor(Color.white);
        g.drawString("Slow", (Configs.SCREEN_WIDTH - chooseSpeedMetrics.stringWidth("Slow")) / 2, 300);
        g.drawString("Normal", (Configs.SCREEN_WIDTH - chooseSpeedMetrics.stringWidth("Normal")) / 2, 400);
        g.drawString("Fast", (Configs.SCREEN_WIDTH - chooseSpeedMetrics.stringWidth("Fast")) / 2, 500);

        g.setFont(configs.font_PressESC);
        FontMetrics pressESCToPauseMetrics = getFontMetrics(g.getFont());
        g.drawString("Press ESC to pause", (Configs.SCREEN_WIDTH - pressESCToPauseMetrics.stringWidth("Press ESC to pause")) / 2, 700);

        //display EMPTY RECTANGLE:
        if (Configs.speed == Configs.Slow) {
            Configs.DELAY = Configs.Slow_DELAY; //set delay
            g.drawRect((Configs.SCREEN_WIDTH - chooseSpeedMetrics.stringWidth("Choose Speed:")) / 2, 250, chooseSpeedMetrics.stringWidth("Choose Speed:"), 70);
        } else if (Configs.speed == Configs.Normal) {
            Configs.DELAY = Configs.Normal_DELAY; //set delay
            g.drawRect((Configs.SCREEN_WIDTH - chooseSpeedMetrics.stringWidth("Choose Speed:")) / 2, 350, chooseSpeedMetrics.stringWidth("Choose Speed:"), 70);
        } else if (Configs.speed == Configs.Fast) {
            Configs.DELAY = Configs.Fast_DELAY; //set delay
            g.drawRect((Configs.SCREEN_WIDTH - chooseSpeedMetrics.stringWidth("Choose Speed:")) / 2, 450, chooseSpeedMetrics.stringWidth("Choose Speed:"), 70);
        }
    }

    public void pause(Graphics g) {
        g.setColor(Color.white);
        g.setFont(configs.font_MenuAndPAUSE);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("PAUSE", (Configs.SCREEN_WIDTH - metrics.stringWidth("PAUSE")) / 2, Configs.SCREEN_HEIGHT / 2);
    }

    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.red);
        g.setFont(configs.font_Score);
        FontMetrics scoreMetrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + configs.applesEaten, (Configs.SCREEN_WIDTH - scoreMetrics.stringWidth("Score: " + configs.applesEaten)) / 2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.red);
        g.setFont(configs.font_gameOver);
        FontMetrics gameOverMetrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (Configs.SCREEN_WIDTH - gameOverMetrics.stringWidth("Game Over")) / 2, Configs.SCREEN_HEIGHT / 2);

        //display blinking "Press ENTER to restart" message
        g.setColor(Color.white);
        g.setFont(configs.font_pressEnterToRestart);
        FontMetrics pressEnterToRestartMetrics = getFontMetrics(g.getFont());
        if (configs.pressEnterToRestart_Flippable)//pressEnterToRestartBOOL is flipped on each PRESS_ENTER_TO_RESTART_DELAY timing
            g.drawString("Press ENTER to restart", (Configs.SCREEN_WIDTH - pressEnterToRestartMetrics.stringWidth("Press ENTER to restart")) / 2, (Configs.SCREEN_HEIGHT / 2) + 200);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* once the game has started, remove the KeyListener for the menu.
        then, create the myKeyAdapter, instead of this adapter. */
        if (configs.gameStarted && configs.myKeyAdapterMENU_isAlive) {
            removeKeyListener(configs.myKeyAdapterMENU);
            configs.myKeyAdapterMENU_isAlive = false;
            timerRunning.setDelay(Configs.DELAY); //timerRunning - set Configs.DELAY of calculations
            timerRunning.start();
            addKeyListener(myKeyAdapter);
            myKeyAdapter_isAlive = true;
        }

        if (configs.gameOver && !configs.enterWasPressed && configs.gameOver_once) {//only when game ends - this iteration happens once
            addKeyListener(configs.myKeyAdapterPressEnterToRestart);//listen to the ENTER key
            configs.myKeyAdapterPressEnterToRestart_isAlive = true;
            configs.pressEnterToRestartTimer.start();//blinking
            timerRunning.stop();
            removeKeyListener(myKeyAdapter);
            myKeyAdapter_isAlive = false;
            configs.enterWasPressed = false;//always reset to false
            configs.gameOver_once = false;
        }

        if (configs.gameOver && configs.enterWasPressed) {//restart game:
            removeKeyListener(configs.myKeyAdapterPressEnterToRestart);//remove the listen to the ENTER key
            configs.myKeyAdapterPressEnterToRestart_isAlive = false;
            configs.pressEnterToRestartTimer.stop();
            initGame(); //reset everything, including: configs.enterWasPressed = false;
            addKeyListener(configs.myKeyAdapterMENU);
            configs.myKeyAdapterMENU_isAlive = true;
            configs.gameOver = false;
            configs.gameOver_once = true;
        }

        repaint();//always repaint
    }

    public class TimerRunning implements ActionListener {
        final private Timer timer = new Timer(Configs.DELAY, this);//calculations Configs.DELAY as user requested

        public void start() {
            timer.start();
        }

        public void stop() {
            timer.stop();
        }

        public void setDelay(int delay) {
            timer.setDelay(delay);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!configs.pause && configs.gameStarted) {//calculate positions
                move();
                checkApple();
                checkCollisions();
            }
        }
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (configs.direction != 'R') {
                        configs.direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (configs.direction != 'L') {
                        configs.direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (configs.direction != 'D') {
                        configs.direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (configs.direction != 'U') {
                        configs.direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    configs.pause = !configs.pause; //flip pause state
                    if (configs.pause && configs.running)
                        timerRunning.stop();
                    if (!configs.pause && configs.running)
                        timerRunning.start();
                    break;
            }
        }
    }
}
