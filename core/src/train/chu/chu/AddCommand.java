package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;

/**
 * Created by Arun on 3/28/2016.
 */
public class AddCommand extends Command {

    Actor newActor;
    HorizontalGroup name;

    public AddCommand(Actor newActor, HorizontalGroup name){
        this.newActor=newActor;
        this.name=name;
    }

    @Override
    /**
     * Adds an actor to the end of the group
     */
    protected void positiveAction() {
        name.addActor(newActor);
    }

    @Override
    /**
     * Removes actor
     */
    protected void negativeAction() {
        newActor.remove();
    }

    @Override
    public String toString() {
        return "add "+newActor.toString();
    }
}
