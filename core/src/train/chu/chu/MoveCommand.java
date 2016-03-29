package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * Created by Arun on 3/29/2016.
 */
public class MoveCommand extends Command {

    private Actor act1;
    private Actor act2;

    public MoveCommand(Actor act1, Actor act2) {
        this.act1=act1;
        this.act2=act2;

    }

    @Override
    protected void negativeAction() {
        positiveAction();
    }

    @Override
    protected void positiveAction() {
        act1.getParent().swapActor(act1, act2);
        ((WidgetGroup) act1.getParent()).invalidate();
    }
}
