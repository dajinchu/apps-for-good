package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Da-Jin on 4/10/2016.
 */
public class ScaleUtils {

    public static float getTrueScale(Actor scale) {
        //Iterate through parents and find first parent that has been scale.
        // It should find sandbox main container and return its scale
        // Can't just use this.scale because parents can affect an actor's scale but actor does not inherit the scale value
        while (scale.getScaleX() == 1 && scale.hasParent()) {
            scale = scale.getParent();
        }
        return scale.getScaleX();
    }
}
