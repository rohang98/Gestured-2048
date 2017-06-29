package lab4_204_44.uwaterloo.ca.lab4_204_44;

// All libraries need to be imported in order for the functions from these libraries to work
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class accSensorHandler implements SensorEventListener {

    // This is the filter constant set-up
    private final float FILTER_CONSTANT = 12f;

    // Set up of FSM states and signatures
    enum myState{WAIT, RISE_A, FALL_A, FALL_B, RISE_B, DETERMINED};
    enum myState2{WAIT2, RISE_A2, FALL_A2, FALL_B2, RISE_B2, DETERMINED2};

    // Signatures needed so that the movement of block can be recorded
    private enum TYPE{LEFT, RIGHT, UP, DOWN, X};
    private static TYPE type = TYPE.X;

    // Creates an instance of the GameLoopTask so that the movement of the block can be made
    private static GameLoopTask GLTask;

    // The state the FSM will start in
    myState state = myState.WAIT;
    myState2 state2 = myState2.WAIT2;

    // The signatures that will be used in the FSM
    enum mySig{SIG_A, SIG_B, SIG_X};
    enum mySig2{SIG_A2, SIG_B2, SIG_X2};

    // The initial signature of the FSM
    mySig signature = mySig.SIG_X;
    mySig2 signature2 = mySig2.SIG_X2;

    // Set up of the threshold values, found after lots of trial and error
    final float[] THRES_A = {0.5f, 2.0f, -0.4f};
    final float[] THRES_B = {-0.5f, -2.0f, 0.4f};

    // These second set of threshold values are for up/down as a separate FSM was made to implement those directions
    final float[] THRES_A2 = {0.1f, 3.0f, -2.0f};
    final float[] THRES_B2 = {-0.5f, -3.0f, 2.0f};


    // Counters that will be used in the FSM
    final int SAMPLEDEFAULT = 30;
    int sampleCounter = SAMPLEDEFAULT;
    int sampleCounter2 = SAMPLEDEFAULT;

    // Needed to communicate with the main function
    public accSensorHandler(GameLoopTask gameLoopTask){
        this.GLTask = gameLoopTask;
    }

    // The entire FSM; created through UML diagram
    public void callFSM1(){

        // Calculates the change in x-value to determine if the direction is left or right
        float deltaH = historyReading[99][0] - historyReading[98][0];

        // Switch statements were used to implement the FSM
        switch(state){

            // For each case, it checks if it values are enough to move to the next stage, if not, then undetermined is printed meaning nothing is certain
            // In addition now, once a direction is certain, there is another state that has the direction that will be used to send the information back to the GameLoopTask, so the actual block movees

            case WAIT:
                sampleCounter = SAMPLEDEFAULT;
                signature = mySig.SIG_X;
                type = TYPE.X;

                if(deltaH > THRES_A[0]){
                    state = myState.RISE_A;
                }
                else if(deltaH < THRES_B[0]){
                    state = myState.FALL_B;
                }

                break;

            case RISE_A:
                if(deltaH <= 0){
                    if(historyReading[99][0] >= THRES_A[1]){
                        state = myState.FALL_A;
                    }
                    else{
                        state = myState.DETERMINED;
                    }
                }
                break;

            case FALL_A:
                if(deltaH >= 0){
                    if (historyReading[99][0] <= THRES_A[2]) {
                        signature = mySig.SIG_A;
                    }
                    state = myState.DETERMINED;
                    type = TYPE.RIGHT;

                }
                break;

            case FALL_B:

                if(deltaH >= 0){
                    if(historyReading[99][0] <= THRES_B[1]){
                        state = myState.RISE_B;
                    }
                    else{
                        state = myState.DETERMINED;
                    }
                }
                break;

            case RISE_B:

                if(deltaH <= 0){
                    if (historyReading[99][0] >= THRES_B[2]) {
                        signature = mySig.SIG_B;
                    }
                    state = myState.DETERMINED;
                    type = TYPE.LEFT;
                }
                break;

            case DETERMINED:
                break;

            default:
                state = myState.WAIT;
                break;

        }

        // Does this 30 times so that a definite decision is reached which is accurate
        sampleCounter--;

        // Sends the information back to GameLoopTask to actually move the block
        if(type == TYPE.LEFT) {
            GLTask.setMovement(GameLoopTask.Movement.LEFT);
        }

        else if(type == TYPE.RIGHT) {
            GLTask.setMovement(GameLoopTask.Movement.RIGHT);
        }

        else if(type == TYPE.UP) {
            GLTask.setMovement(GameLoopTask.Movement.UP);
        }

        else if(type == TYPE.DOWN) {
            GLTask.setMovement(GameLoopTask.Movement.DOWN);
        }

        else{
            GLTask.setMovement(GameLoopTask.Movement.NO_MOVEMENT);
        }

    }


    // A second FSM was implemented in order to keep track of up/down
    public void callFSM2(){

        // Calculates the change in y-values to check if a certain state is reached
        float deltaH2 = historyReading[99][1] - historyReading[98][1];

        // For each case, it checks if it values are enough to move to the next stage, if not, then undetermined is printed meaning nothing is certain
        // Same cases for the direction again
        switch(state2){

            case WAIT2:
                sampleCounter2 = SAMPLEDEFAULT;
                signature2 = mySig2.SIG_X2;
                type = TYPE.X;

                if(deltaH2 > THRES_A2[0]){
                    state2 = myState2.RISE_A2;
                }
                else if(deltaH2 < THRES_B2[0]){
                    state2 = myState2.FALL_B2;
                }
                break;

            case RISE_A2:
                if(deltaH2 <= 0){
                    if(historyReading[99][1] >= THRES_A2[1]){
                        state2 = myState2.FALL_A2;
                    }
                    else{
                        state2 = myState2.DETERMINED2;
                    }
                }
                break;

            case FALL_A2:
                if(deltaH2 >= 0){
                    if (historyReading[99][1] <= THRES_A2[1]) {
                        signature2 = mySig2.SIG_A2;
                    }
                    state2 = myState2.DETERMINED2;
                    type = TYPE.UP;
                }
                break;

            case FALL_B2:

                if(deltaH2 >= 0){
                    if(historyReading[99][1] <= THRES_B2[1]){
                        state2 = myState2.RISE_B2;
                    }
                    else{
                        state2 = myState2.DETERMINED2;
                    }
                }
                break;

            case RISE_B2:

                if(deltaH2 <= 0){
                    if (historyReading[99][1] >= THRES_B2[1]) {
                        signature2 = mySig2.SIG_B2;
                    }
                    state2 = myState2.DETERMINED2;
                    type = TYPE.DOWN;
                }
                break;

            case DETERMINED2:
                break;

            default:
                state2 = myState2.WAIT2;
                break;

        }
        sampleCounter2--;

        // Sends the information back to the GameLoopTask to move the block
        if(type == TYPE.LEFT) {
            GLTask.setMovement(GameLoopTask.Movement.LEFT);
        }

        else if(type == TYPE.RIGHT) {
            GLTask.setMovement(GameLoopTask.Movement.RIGHT);
        }

        else if(type == TYPE.UP) {
            GLTask.setMovement(GameLoopTask.Movement.UP);
        }

        else if(type == TYPE.DOWN) {
            GLTask.setMovement(GameLoopTask.Movement.DOWN);
        }

        else{
            GLTask.setMovement(GameLoopTask.Movement.NO_MOVEMENT);
        }

    }

    // Needed function, serves no purpose
    public void onAccuracyChanged(Sensor s, int i) {}

    // Creates the array of data
    private float[][] historyReading = new float[100][3];

    // Stores the data from the accelerometer into the array
    private void insertHistoryReading(float[] values){
        for(int i = 1; i < 100; i++){
            historyReading[i - 1][0] = historyReading[i][0];
            historyReading[i - 1][1] = historyReading[i][1];
            historyReading[i - 1][2] = historyReading[i][2];
        }

        // Filter constant implementation, which is found by storing the same value divided by a constant determined to eliminate other factors
        historyReading[99][0] += (values[0] - historyReading[99][0]) / FILTER_CONSTANT;
        historyReading[99][1] += (values[1] - historyReading[99][1]) / FILTER_CONSTANT;
        historyReading[99][2] += (values[2] - historyReading[99][2]) / FILTER_CONSTANT;

        // After filtering the data, call FSM for signature analysis
        callFSM1();
        callFSM2();

        // Make sure that by the 30th sample, the FSM result is generated.  If not, it is a bad signature
        // Outputs the value if a certain signature is obtained, otherwise undetermined
        if(sampleCounter <= 0){

            if(state == myState.DETERMINED){
                if(signature == mySig.SIG_B)
                    Lab4_204_44.textOutput.setText("LEFT");

                else if(signature == mySig.SIG_A)
                    Lab4_204_44.textOutput.setText("RIGHT");

                else
                    Lab4_204_44.textOutput.setText("Undetermined");
            }

            else{
                state = myState.WAIT;
                Lab4_204_44.textOutput.setText("Undetermined");
            }

            sampleCounter = SAMPLEDEFAULT;
            state = myState.WAIT;

        }


        // Repeats the same thing as above, just for the second FSM implementing up/down
        else if(sampleCounter2 <= 0){

            if(state2 == myState2.DETERMINED2){
                if(signature2 == mySig2.SIG_B2)
                    Lab4_204_44.textOutput.setText("DOWN");

                else if(signature2 == mySig2.SIG_A2)
                    Lab4_204_44.textOutput.setText("UP");

                else
                    Lab4_204_44.textOutput.setText("Undetermined");
            }

            else{
                state2 = myState2.WAIT2;
                Lab4_204_44.textOutput.setText("Undetermined");
            }

            sampleCounter2 = SAMPLEDEFAULT;
            state2 = myState2.WAIT2;

        }
    }

    // Grabs the values from the linear acceleration sensor from the phone
    @Override
    public void onSensorChanged(SensorEvent se) {

        if(se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            insertHistoryReading(se.values);
        }
    }

}