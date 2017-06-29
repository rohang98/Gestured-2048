package lab4_204_44.uwaterloo.ca.lab4_204_44;

import android.content.Context;
import android.widget.RelativeLayout;

// All this is needed for code expansion

public abstract class GameBlockTemplate extends android.support.v7.widget.AppCompatImageView {

    public RelativeLayout relativeLayout;

    public GameBlockTemplate(Context context, RelativeLayout rl){
        super(context);
        this.relativeLayout = rl;
    }

    public abstract void setDestination();

    public abstract void move();

}