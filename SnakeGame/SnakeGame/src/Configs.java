import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Configs {
    Configs() {
    }

    static final int SCREEN_WIDTH = 1300;
    static final int SCREEN_HEIGHT = 750;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final byte PROCESSING_DELAY = 0;
    static final int Slow_DELAY = 175;
    static final int Normal_DELAY = 120;
    static final int Fast_DELAY = 75;
    static final byte Slow = -1;
    static final byte Normal = 0;
    static final byte Fast = 1;
    static final int PRESS_ENTER_TO_RESTART_DELAY = 500;
    final int[] x = new int[GAME_UNITS]; //physical size of the snake
    final int[] y = new int[GAME_UNITS]; //physical size of the snake
    static byte speed = Normal; //initialize with 0 (0 = Normal) //user sets the game speed (DELAY). default is Normal.
    static int DELAY = Normal_DELAY; //user sets the game speed (DELAY). default is Normal.
    int bodyParts = 6; //logical size of the snake
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R'; //initial direction is RIGHT
    boolean gameStarted = false;
    boolean running = false;
    boolean pause = false;
    boolean gameOver = false;
    boolean gameOver_once = true;
    boolean enterWasPressed = false;
    boolean pressEnterToRestart_Flippable = true;
    MyKeyAdapterMENU myKeyAdapterMENU = new MyKeyAdapterMENU();
    boolean myKeyAdapterMENU_isAlive = false;
    Timer timerProcess;
    MyKeyAdapterPressEnterToRestart myKeyAdapterPressEnterToRestart = new MyKeyAdapterPressEnterToRestart();//for blinking message
    boolean myKeyAdapterPressEnterToRestart_isAlive = false;
    PressEnterToRestartTimer pressEnterToRestartTimer = new PressEnterToRestartTimer();//for blinking message
    Random random;

    //Fonts:
    Font font_Score = new Font("Ink Free", Font.BOLD, 40);
    Font font_MenuAndPAUSE = new Font("Ink Free", Font.BOLD, 50);
    Font font_PressESC = new Font("Ink Free", Font.BOLD, 30);
    Font font_gameOver = new Font("Ink Free", Font.BOLD, 75);
    Font font_pressEnterToRestart = new Font("Ink Free", Font.BOLD, 35);
    //Colors:
    Color snakeBodyColor = new Color(8, 169, 64);


    public class MyKeyAdapterMENU extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (speed > -1)
                        speed -= 1;
                    break;
                case KeyEvent.VK_DOWN:
                    if (speed < 1)
                        speed += 1;
                    break;
                case KeyEvent.VK_ENTER:
                    running = true; //start game
                    gameStarted = true; //start game
                    break;
            }
        }
    }

    public class MyKeyAdapterPressEnterToRestart extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                running = true; //start game
//                gameStarted = true; //start game
                enterWasPressed = true; //signal that the ENTER key was pressed
            }
        }
    }

    public class PressEnterToRestartTimer implements ActionListener {
        final private Timer timer = new Timer(PRESS_ENTER_TO_RESTART_DELAY, this);//calculations DELAY as user requested

        public void start() {
            timer.start();
        }

        public void stop() {
            timer.stop();
        }

        /*this class is provided for blinking message "Press ENTER to restart"*/
        @Override
        public void actionPerformed(ActionEvent e) {
            pressEnterToRestart_Flippable = !pressEnterToRestart_Flippable; //flip boolean
        }
    }
}
