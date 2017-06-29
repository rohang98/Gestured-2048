package lab4_204_44.uwaterloo.ca.lab4_204_44;

// All libraries need to be imported in order for the functions from these libraries to work
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;

// One class was used for all the functions in this lab
public class Lab4_204_44 extends AppCompatActivity {

    // Initialization of all the variables that are used in the entire lab

    // To display the direction of the gesture
    static TextView textOutput;

    // Something that was needed in order for the coordinate system of the screen to be obtained
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

    // All the data and text that is displayed was created in the OnCreate, so it displays the information onto the screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_204_44);

        // There is one label in the XML that will be used for all the text that will be displayed on the screen
        // Relative layout was now used because things are going to be moved around the screen using coordinates
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.label1);

        // Set the gameboard to this size, so the entire gameboard shows
        relativeLayout.getLayoutParams().width = 1080;
        relativeLayout.getLayoutParams().height = 1080;

        // Sets the gameboard as the background in the size stated above
        relativeLayout.setBackgroundResource(R.drawable.gameboard);

        // Creates an instance of GameLoopTask and also of accelerometer handler
        GameLoopTask gameLoopTask = new GameLoopTask(this, getApplicationContext(), relativeLayout);
        final accSensorHandler accSensor = new accSensorHandler(gameLoopTask);

        // Creates a new timer, and basically calls the gameLoopTask every 10ms, with a 10ms delay, performs the movement
        Timer gameLoopTimer = new Timer();
        gameLoopTimer.schedule(gameLoopTask, 10, 10);

        // Must first create the sensor event manager (SEM) for each sensor type and then request the SEM to register each sensor
        // Linear acceleration and delay game were used in order to minimize the effects of other factors on the values obtained
        SensorManager accSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = accSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accSensorManager.registerListener(accSensor, accelerometer, accSensorManager.SENSOR_DELAY_GAME);

        // Outputs the direction and displays the resulting direction onto the screen
        textOutput = new TextView(getApplicationContext());
        textOutput.setTextColor(Color.BLACK);
        textOutput.setTextSize(30);
        relativeLayout.addView(textOutput);
    }

}