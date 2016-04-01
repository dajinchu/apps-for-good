package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Arun on 3/30/2016.
 */
public class RemoveCommand extends Command {

    private Actor act;
    private int index=0;
    private Group parent;
    public RemoveCommand(Actor act){
        this.act=act;
        index=act.getParent().getChildren().indexOf(act,true);
        parent=act.getParent();
    }


    @Override
    /**
     * Adds actor at previous location
     */
    protected void negativeAction() {
        parent.addActorAt(index, act);
    }

    @Override
    /**
     * Removes action
     */
    protected void positiveAction() {

        act.remove();
    }
}
