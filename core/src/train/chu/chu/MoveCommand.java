package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * Created by Arun on 3/29/2016.
 */
public class MoveCommand extends Command {

    private Actor act1;
    private Actor act2;
    private boolean left;
    private int index=0;

    public MoveCommand(Actor act1, Actor act2, boolean left) {
        this.act1=act1;
        index=act2.getParent().getChildren().indexOf(act2,true);
        this.act2=act2;
        this.left=left;

    }

    @Override
    /**
     * Undoes last  move action
     */
    protected void negativeAction() {
       act1.getParent().addActorAt(index, act2);

    }

    @Override
    /**
     * Moves actor to new location. Moves to the left or right.
     */
    protected void positiveAction() {

        if(left){
            act1.getParent().addActorBefore(act1, act2);

        }else{
            act1.getParent().addActorAfter(act1, act2);
        }

    }
}
