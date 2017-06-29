package lab4_204_44.uwaterloo.ca.lab4_204_44;

// All libraries need to be imported in order for the functions from these libraries to work
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

// This class extends TimerTask because this process occurs periodically
public class GameLoopTask extends TimerTask {

    // Variables needed for storing everything
    private Activity myActivity;
    private Context myContext;
    private RelativeLayout rl;

    // Linked list needed to keep the blocks
    public static LinkedList<GameBlock> blocks = new LinkedList<>();

    // Mimics the directions so they can be used in the GameBlock.java file
    public enum Movement {UP, DOWN, LEFT, RIGHT, NO_MOVEMENT}

    // Tracks the movement of the block
    private Movement movement = Movement.NO_MOVEMENT;

    // 2-D array to store the boolean grid which keeps track of spawn points for the blocks
    public static boolean[][] gameBoardGrid = new boolean[4][4];

    // Variables needed to spawn the blocks in locations on the grid
    private int x = 0;
    private int y = 0;

    // Randomly creates a block
    Random randomBlock = new Random();

    // States if a block is able to be created
    private boolean createCheck = false;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

    // Method needed for this function that stores all the variables and sets the initial position of the block
    public GameLoopTask(Lab4_204_44 activity, Context applicationContext, RelativeLayout MyRL) {

        this.myActivity = activity;
        this.myContext = applicationContext;
        this.rl = MyRL;

        createBlock();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

    // Function needed to create the GameBlock and display it on the screen
    public void createBlock() {

        // Variables created to keep track of all the possible places the block could generate in
        int[][] points = new int[16][2];
        int count = 0;
        int index;

        // Goes through the boolean grid to check which spots are open
        for(int counter = 0; counter < 4 ; counter++) {

            for (int counter2 = 0; counter2 < 4; counter2++) {

                if (!gameBoardGrid[counter][counter2] && counter < 4 && counter2 < 4 ) {

                    // Stores the free points in an array
                    points[count][0] = counter;
                    points[count][1] = counter2;

                    // Increases the size of the array
                    count++;

                    // Block can be created since a spot is free
                    createCheck = true;

                    break;
                }
            }
        }

        // If it isn't possible to create a block, end the game and display a message
        if (!createCheck) {

            TextView endGame = new TextView(myContext);
            endGame.setText("GAME OVER");
            endGame.setTextSize(50f);
            endGame.setX(180f);
            endGame.setTextColor(Color.BLACK);
            endGame.setBackgroundColor(Color.WHITE);
            endGame.setPadding(10, 10, 10, 10);
            rl.addView(endGame);
        }

        // Randomly choose an available point to add the block
        else {

            index = randomBlock.nextInt(count);

            // Sets up the variables, updates the grid, and then adds the block
            this.x = points[index][0];
            this.y = points[index][1];
            this.gridUpdate(x, y);

            // Calls the creator function from the GameBlock method to create a new block in the available position
            blocks.add(new GameBlock(this.myContext, rl, x, y));
        }

        // For next movement
        createCheck = false;
    }

    // Updates the grid with the next point and sets the boolean grid coordinates
    private void gridUpdate(int x, int y){
        this.gameBoardGrid[x][y] = true;
        this.x = this.pixelToGrid(x);
        this.y = this.pixelToGrid(y);
    }

    // Uses the coordinates to decide a slot on the game board
    private int pixelToGrid(int i){
        switch(i){

            case 0:
                i = -54;
                break;

            case 1:
                i = 214;
                break;

            case 2:
                i = 482;
                break;

            case 3:
                i = 750;
                break;
        }
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

    // This is the method that will run periodically
    public void run() {

        // Allows everything to be run in the main activity
        myActivity.runOnUiThread(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

                    @Override
                    public void run() {

                        // Text for winning the game
                        if(GameBlock.gameWin){

                            TextView winGameText = new TextView(myContext);
                            winGameText.setText("YOU WIN!!");
                            winGameText.setTextSize(50f);
                            winGameText.setX(0f);
                            winGameText.setY(0f);
                            winGameText.setTextColor(Color.BLACK);
                            winGameText.setBackgroundColor(Color.WHITE);
                            winGameText.setPadding(5,5,5,5);
                            rl.addView(winGameText);

                        }

                        // If any movement occurs, then the block will be told to move in that direction
                        if(movement != Movement.NO_MOVEMENT) {

                            if (blocks.get(blocks.size()-1).movementCompleted || !blocks.get(blocks.size()-1).entrance) {

                                for( int i = 0 ; i < blocks.size() ; i++) {

                                    // Sets the movement of each block
                                    blocks.get(i).setBlockDirection(movement);

                                    // Boolean variable to ensure GameBlock is only being called once
                                    blocks.get(i).once = true;
                                }


                            }
                        }

                        // Goes through each block's move function
                        else{

                            for (int i = 0; i < blocks.size() ; i++) {
                                blocks.get(i).move();
                            }

                            // If the last block is moved, then a new block will spawn
                            if(GameBlock.makeBlock){
                                createBlock();
                            }

                        }
                    }
                }
        );

    }


    // Updates the variable that tracks the direction of the block movement
    public void setMovement(Movement mov) {
        movement = mov;
    }

}