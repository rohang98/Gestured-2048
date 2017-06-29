package lab4_204_44.uwaterloo.ca.lab4_204_44;

// All libraries need to be imported in order for the functions from these libraries to work
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Random;

// This class extends ImageView so that the movement appears on the screen
public class GameBlock extends GameBlockTemplate {

    // Values of the maximum and minimum coordinates of where the block can move to (basically where the block stops after going in a certain direction)
    public final float slotIsolation = 268;
    private final float xmin = -55;
    private final float ymin = -55;
    private final float xmax = 750;
    private final float ymax = 750;

    // Boolean variable created to control the number of times setDirection is called
    public boolean once = true;

    // Keeps track of the movement
    private GameLoopTask.Movement movement = GameLoopTask.Movement.NO_MOVEMENT;

    // Sets the size of the block
    private final float IMAGE_SCALE = 0.6f;

    // Used for tracking current x coordinate
    private float myCoordX;
    private float myCoordY;

    // Used for tracking desired x coordinate
    private float targetCoordX;
    private float targetCoordY;

    // Keeps track of the velocity and acceleration
    private float velocity = 0;
    private final float acc = 4.0f;

    // Boolean variable to track if the movement of the block is completed
    public boolean movementDone = false;

    // To check if it entered the movement correctly
    // Boolean variable created to check if it entered the movement correctly
    public boolean entrance = false;

    // Used to randomly generate a number for the new block that is created
    private Random randomNum = new Random();

    // Will display the number on the block that is created
    public TextView numberText;

    // Used to keep track of the numbers on the block
    public int blockNumber;

    // Boolean variable created to determine how many blocks need to move in setDirection based on the blocks that are currently merging
    public boolean blockMerging = false;

    // Boolean variable needed to keep track if the movement in motion is completed
    public  boolean movementCompleted = false;

    // Boolean variable needed to signal that the block is to be destroyed after the next movement has occurred
    private boolean destroyBlock = false;

    // Boolean variable that is used in the GameLoopTask that dictates when the next game block should be created
    public static boolean makeBlock = false;

   // Boolean variable that is used to signal when the highest block is 256, meaning the game is won and the victory message should be displayed
    public static boolean gameWin = false;

    // Boolean variables needed to keep track of the location (coordinates) of the blocks on the grid
    public int boolX;
    public int boolY;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

    // Method needed for the function
    public GameBlock(Context myContext, RelativeLayout relativeLayout, float coordX, float coordY) {

        // Receives the states from other classes
        super(myContext, relativeLayout);

        // Draws the game block, sets the size and starting coordinates of the block and updates the x and y coordinates
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.myCoordX = coordX;
        this.myCoordY = coordY;
        this.setX(myCoordX);
        this.setY(myCoordY);
        this.setImageResource(R.drawable.gameblock);
        this.setXY((int) coordX,(int) coordY);

        // Generates the block numbers
        blockNumber = (randomNum.nextInt(2)) + 2 ;

        if(blockNumber == 3)
            blockNumber = 4;

        // Creates the block number text inside the block
        numberText = new TextView(myContext);
        numberText.setText(String.format("    " + blockNumber));
        numberText.setTextSize(18f);
        numberText.setTextColor(Color.BLACK);
        numberText.setX(coordX + 70);
        numberText.setY(coordY + 90);

        // Displays the block along with the number to the screen
        relativeLayout.addView(this);
        relativeLayout.addView(numberText);
        numberText.bringToFront();

    }

    // Creates the grid by using the coordinates, splits the screen up into actual blocks instead of using coordinates
    private void setXY(int X, int Y){

        // One needed for x
        if(X == -54)
            this.boolX = 0;

        else if( X == 214 )
            this.boolX = 1;

        else if( X == 482 )

            this.boolX = 2;

        else
            this.boolX = 3;

        // Other needed for y
        if(Y == -54)
            this.boolY =0;

        else if ( Y == 214 )
            this.boolY = 1;

        else if ( Y == 482 )
            this.boolY = 2;

        else
            this.boolY = 3;

    }

    // Function needed to reset the boolean variables for merging
    private void clearAll(){
        for(int i = 0; i < GameLoopTask.blocks.size() ; i++) {

            GameLoopTask.blocks.get(i).blockMerging = false;
        }
    }

    // Sets the direction of the block to a private function
    public void setBlockDirection(GameLoopTask.Movement mov) {
        this.movement = mov;
    }

    // Function created to remove blocks and their numbers from the screen
    public void destroy(){

        relativeLayout.removeView(this);                        // Deletes the block from the screen
        relativeLayout.removeView(this.numberText);             // Deletes the number on the block
        GameLoopTask.blocks.remove(this);                       // Removes the block from the linked list
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

    @Override
    // Function needed that actually moves the blocks around on the screen (along with their numbers)
    public void move() {

        // Sets the target coordinates (only occurs once)
        if(once) {

            setDestination();
            once = false;
        }

        // Will only be set to true once the last block to move is finished its movement
        makeBlock = false;

        // Switch cases that all move the block and their number, but either x or y is changed depending on the direction of the hand gesture
        switch (movement){
            case UP:

                movementDone = false;

                if (!entrance) {

                    setX(myCoordX += 2);
                    numberText.setX(myCoordX + 70);
                    entrance = true;
                }

                entrance = true;
                velocity += acc;

                if ((myCoordY - velocity) > targetCoordY) {
                    setY(myCoordY -= velocity);
                    numberText.setY(myCoordY + 90);
                }

                else {

                    velocity = 0;
                    setY(targetCoordY);
                    myCoordY = targetCoordY;
                    this.movementDone = true;
                    numberText.setY(myCoordY + 90);

                    // If the block is the last game block to move, then set the boolean variable to true so that the next block can be created
                    if(GameLoopTask.blocks.get(GameLoopTask.blocks.size()-1).boolX == this.boolX && GameLoopTask.blocks.get(GameLoopTask.blocks.size() - 1 ).boolY == this.boolY)
                        makeBlock = true;

                    // If the block has to be destroyed, destroy the block after the movement is completed
                    if(destroyBlock)
                        destroy();
                }

                break;

            // Same thing as above for the other cases, however different coordinates may change depending on the direction of the hand gesture
            case DOWN:

                movementDone = false;

                if (!entrance) {

                    setX(myCoordX += 2);
                    numberText.setX(myCoordX + 70);
                    entrance = true;
                }

                entrance = true;
                velocity += acc;

                if ((myCoordY + velocity) < targetCoordY) {

                    setY(myCoordY += velocity);
                    numberText.setY(myCoordY + 90);
                }

                else {

                    setY(targetCoordY);
                    myCoordY = targetCoordY;
                    this.movementDone = true;
                    numberText.setY(myCoordY + 90);

                    if(GameLoopTask.blocks.get(GameLoopTask.blocks.size()-1).boolX == this.boolX && GameLoopTask.blocks.get(GameLoopTask.blocks.size() - 1 ).boolY == this.boolY)
                        makeBlock = true;

                    if(destroyBlock)
                        destroy();
                }

                break;

            case LEFT:

                movementDone = false;

                if (!entrance) {

                    setX(myCoordX += 2);
                    numberText.setY(myCoordY + 90);
                    entrance = true;
                }

                entrance = true;
                velocity += acc;

                if ((myCoordX - velocity) > targetCoordX) {
                    setX(myCoordX -= velocity);
                    numberText.setX(myCoordX + 70);
                }

                else {

                    velocity = 0;
                    setX(targetCoordX);
                    myCoordX = targetCoordX;
                    numberText.setX(myCoordX + 70);
                    this.movementDone = true;

                    if(GameLoopTask.blocks.get(GameLoopTask.blocks.size()-1).boolX == this.boolX && GameLoopTask.blocks.get(GameLoopTask.blocks.size() - 1 ).boolY == this.boolY)
                        makeBlock = true;

                    if(destroyBlock)
                        destroy();
                }

                break;

            case RIGHT:

                movementDone = false;

                if (!entrance) {
                    setY(myCoordY += 2);
                    numberText.setY(myCoordY + 90);
                    entrance = true;
                }

                entrance = true;
                velocity += acc;

                if ((myCoordX + velocity) < targetCoordX) {
                    setX(myCoordX += velocity);
                    numberText.setX(myCoordX + 70);
                }

                else {
                    velocity = 0;
                    setX(targetCoordX);
                    myCoordX = targetCoordX;
                    numberText.setX(myCoordX + 70);
                    this.movementDone = true;

                    if(GameLoopTask.blocks.get(GameLoopTask.blocks.size()-1).boolX == this.boolX && GameLoopTask.blocks.get(GameLoopTask.blocks.size() - 1 ).boolY == this.boolY)
                        makeBlock = true;

                    if(destroyBlock)
                        destroy();
                }

                break;
        }
    }

    // Goes through all of the game blocks in the linked list and assigns the block a boolean coordinate and returns the block number
    public int getBlockNumberFromGrid(float[] check){

        for (int a = 0 ; a < GameLoopTask.blocks.size() ; ++a){

            if(GameLoopTask.blocks.get(a).boolX == check[0] && GameLoopTask.blocks.get(a).boolY == check[1]){

                return GameLoopTask.blocks.get(a).blockNumber;
            }
        }
        return 0;
    }

    // Updates the block numbers (TextViews) and if the block number reaches 256, the boolean variable for the game win is true
    public void setNewText(){

        blockNumber += blockNumber;
        numberText.setText(String.format("    " + blockNumber));

        if(blockNumber == 64)
            gameWin = true;
    }

    // Same thing as what is done above but defined differently
    public GameBlock blockOnGrid (float[] check){

        for (int a = 0 ; a < GameLoopTask.blocks.size() ; ++a){

            if(GameLoopTask.blocks.get(a).boolX == check[0] && GameLoopTask.blocks.get(a).boolY == check[1]){

                return GameLoopTask.blocks.get(a);
            }
        }
        return null;
    }

    // Sets all the boolean variables back to false so that they can all be used again
    public  void resetGrid(){

        for(int count1 = 0; count1 < 4 ; ++count1){

            for(int count2 = 0; count2 < 4 ; ++count2){

                GameLoopTask.gameBoardGrid[count1][count2] = false;
            }
        }

        // Goes through the game blocks in the linked list and sets the coordinates with blocks on them to true
        for(int a = 0; a < GameLoopTask.blocks.size() ; ++a) {

            GameLoopTask.gameBoardGrid[GameLoopTask.blocks.get(a).boolX][GameLoopTask.blocks.get(a).boolY] = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

    @Override
    // Function needed to set the destination of the block, taking into account if other blocks are in the way
    public void setDestination() {

        // Variables needed to determine how many blocks need to move
        int blockCount = 0;
        int slotSpace;

        // Array needed to store boolean coordinates of the grid
        float[] checkPoint = new float[2];

        // Boolean variable needed as a flag to indicate whether the first gameblock in the desired direction has been found
        boolean foundBlock = false;

        // Used to track how many game blocks are merging
        int gbMergers = 0;

        // If its the first game block in setDirection, clear all merging booleans
        if(GameLoopTask.blocks.getFirst() == this) {
            clearAll();
        }

        // If its the last game block, then reset the grid
        if(GameLoopTask.blocks.getLast() == this)
            resetGrid();

        // Goes through each gesture to merge any blocks in the same direction
        switch (this.movement) {

            case UP:

                // X-coordinate will not change
                this.targetCoordX = this.myCoordX;

                // Update the array to store the x-coordinate as the current boolean x-coordinate
                checkPoint[0] = this.boolX;

                // Updates how many slots are between the block and where it is intending to go
                slotSpace = this.boolY;

                // If there are no slots, then the end goal is reached
                if(slotSpace == 0) {

                    // Update the y-coordinate
                    this.targetCoordY = ymin;

                    // Update the grid
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;

                    // Update the boolean coordinates
                    this.boolY = 0;

                    // Update the grid
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                    break;
                }

                // Check if there are more game blocks in the line
                for(int i = this.boolY - 1; i >= 0; --i){

                    checkPoint[1] = i;

                    if(blockOnGrid(checkPoint) != null){
                        blockCount++;
                    }
                }

                // If there are not any game blocks in the same line, then the movement should go to the end and update the boolean coordinates along with the grid
                if(blockCount == 0){

                    this.targetCoordY = ymin;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;
                    this.boolY = 0;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                    break;
                }

                //Check what blocks are already merging, and subtract them from the block count (will become one block at the end)
                for(int i = this.boolY - 1; i >= 0; --i){

                    checkPoint[1] = i;

                    if(blockOnGrid(checkPoint) != null) {

                        if (blockOnGrid(checkPoint).blockMerging ) {
                            gbMergers++;

                            if(gbMergers % 2 == 0)
                                blockCount--;
                        }
                    }
                }

                // If there is a block in the same line, then goes into this if statement
                if(blockCount != 0){

                    // Finds the first block and checks its block number to check whether blocks will merge
                    for(int i = slotSpace - 1; i >=0; --i){

                        checkPoint[1] = i ;

                        // Found the block, so its done
                        if(foundBlock)
                            break;

                        // Must be first block if not null
                        if(blockOnGrid(checkPoint) != null) {
                            foundBlock = true;

                            // Checks if the block numbers are the same
                            if (getBlockNumberFromGrid(checkPoint) == this.blockNumber) {

                                //Checks if the block isn't already merging
                                if (!blockOnGrid(checkPoint).blockMerging) {

                                    // Merged block so decrease the block count
                                    blockCount--;

                                    // Destroy the block and make the merge boolean true
                                    this.destroyBlock = true;
                                    this.blockMerging = true;
                                    blockOnGrid(checkPoint).blockMerging = true;

                                    // Make the block merge and update the block number
                                    blockOnGrid(checkPoint).setNewText();
                                }
                            }
                        }
                    }
                }

                // Now the actual movement for the animation is created

                // Block is in motion
                GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;

                // Moves the block the number of slots and updates the corresponding boolean coordinate
                if(blockCount != 0) {

                    this.targetCoordY = ymin+(blockCount*slotIsolation);
                    this.boolY = blockCount;
                }

                // Update the grid to the minimum values
                else {

                    this.targetCoordY = ymin;
                    this.boolY = 0;
                }

                // Update the grid with new coordinates
                GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                break;

            // Repeats the same process for all directions, but uses the corresponding coordinate depending on the direction of the hand gesture
            case DOWN:

                this.targetCoordX = this.myCoordX;

                checkPoint[0] = this.boolX;

                slotSpace = 3 - this.boolY;

                if(slotSpace == 0) {

                    this.targetCoordY = ymax;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;
                    this.boolY = 3;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                    break;
                }

                for(int i = this.boolY + 1; i < 4 ; i++){

                    checkPoint[1] = i;

                    if(blockOnGrid(checkPoint) != null){
                        blockCount++;
                    }
                }

                if(blockCount == 0){

                    this.targetCoordY = ymax;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;
                    this.boolY = 3;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                    break;
                }

                for(int i = this.boolY + 1; i < 4; i++){

                    checkPoint[1] = i;
                    if(blockOnGrid(checkPoint) != null) {

                        if (blockOnGrid(checkPoint).blockMerging) {
                            gbMergers++;

                            if(gbMergers%2 == 0)
                                blockCount--;
                        }
                    }
                }

                if(blockCount != 0){

                    for ( int i = this.boolY + 1 ; i < 4 ; i++) {
                        checkPoint[1] = i;

                        if (foundBlock)
                            break;

                        if (blockOnGrid(checkPoint) != null) {
                            foundBlock = true;

                            if (getBlockNumberFromGrid(checkPoint) == this.blockNumber) {

                                if (!blockOnGrid(checkPoint).blockMerging) {

                                    blockCount--;
                                    this.destroyBlock = true;
                                    this.blockMerging = true;
                                    blockOnGrid(checkPoint).blockMerging = true;
                                    blockOnGrid(checkPoint).setNewText();

                                }

                            }
                        }
                    }
                }

                GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;

                if(blockCount != 0) {

                    this.targetCoordY = ymax - (blockCount * slotIsolation);
                    this.boolY = 3 - blockCount;
                }

                else {

                    this.targetCoordY = ymax;
                    this.boolY = 3;
                }
                GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                break;

            case LEFT:

                this.targetCoordY = this.myCoordY;
                checkPoint[1] = this.boolY;

                slotSpace = this.boolX;
                if(slotSpace == 0 ) {

                    this.targetCoordX = xmin;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;
                    this.boolX = 0;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                    break;
                }

                for(int i = this.boolX - 1; i>=0; --i){

                    checkPoint[0] = i;

                    if(blockOnGrid(checkPoint) != null){
                        blockCount++;
                    }

                }

                if(blockCount == 0){

                    this.targetCoordX = xmin;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;

                    this.boolX = 0;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                    break;
                }


                for(int i = this.boolX - 1; i>=0; --i){

                    checkPoint[0] = i;

                    if(blockOnGrid(checkPoint) != null) {

                        if (blockOnGrid(checkPoint).blockMerging ) {
                            gbMergers++;

                            if(gbMergers % 2 == 0)
                                blockCount--;
                        }
                    }
                }

                if(blockCount != 0){

                    for(int i = slotSpace - 1; i>=0 ; --i){
                        checkPoint[0] = i ;

                        if(foundBlock)
                            break;

                        if(blockOnGrid(checkPoint) != null) {
                            foundBlock = true;

                            if (getBlockNumberFromGrid(checkPoint) == this.blockNumber) {

                                if (!blockOnGrid(checkPoint).blockMerging) {
                                    blockCount--;
                                    this.destroyBlock = true;
                                    this.blockMerging = true;
                                    blockOnGrid(checkPoint).blockMerging = true;
                                    blockOnGrid(checkPoint).setNewText();
                                }
                            }
                        }
                    }
                }

                //Now set up the displacement stuff

                GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;

                if(blockCount != 0) {

                    this.targetCoordX = xmin+(blockCount*slotIsolation);
                    this.boolX = blockCount;
                }

                else {

                    this.targetCoordX = xmin;
                    this.boolX = 0;
                }

                GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                break;

            case RIGHT:

                this.targetCoordY = this.myCoordY;
                checkPoint[1] = this.boolY;

                slotSpace = 3 - this.boolX;

                if(slotSpace == 0) {

                    this.targetCoordX = xmax;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;

                    this.boolX = 3;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                    break;
                }



                for(int i = this.boolX+1; i<4 ; ++i){

                    checkPoint[0] = i;
                    if(blockOnGrid(checkPoint) != null){
                        blockCount++;
                    }
                }

                if(blockCount == 0){

                    this.targetCoordX = xmax;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;

                    this.boolX = 3;
                    GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                    break;
                }

                for(int i = this.boolX+1; i<4; ++i){

                    checkPoint[0] = i;

                    if(blockOnGrid(checkPoint) != null) {

                        if (blockOnGrid(checkPoint).blockMerging) {
                            gbMergers++;

                            if(gbMergers % 2 == 0)
                                blockCount--;
                        }
                    }
                }

                if(blockCount != 0){

                    for (int i = this.boolX+1 ; i<4 ; ++i) {
                        checkPoint[0] = i;

                        if (foundBlock)
                            break;

                        if (blockOnGrid(checkPoint) != null) {
                            foundBlock = true;

                            if (getBlockNumberFromGrid(checkPoint) == this.blockNumber) {

                                if (!blockOnGrid(checkPoint).blockMerging) {

                                    blockCount--;
                                    this.destroyBlock = true;
                                    this.blockMerging = true;
                                    blockOnGrid(checkPoint).blockMerging = true;
                                    blockOnGrid(checkPoint).setNewText();

                                }

                            }
                        }
                    }
                }

                GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = false;

                if(blockCount != 0) {

                    this.targetCoordX = xmax-(blockCount*slotIsolation);
                    this.boolX = 3 - blockCount;
                }

                else {

                    this.targetCoordX = xmax;
                    this.boolX = 3;
                }

                GameLoopTask.gameBoardGrid[this.boolX][this.boolY] = true;

                break;

            case NO_MOVEMENT:
                break;
        }

    }
}