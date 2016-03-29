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
    protected void positiveAction() {
        name.addActor(newActor);
    }

    @Override
    protected void negativeAction() {
        int index=name.getChildren().indexOf(newActor, true);

        if(index>=0){
            name.removeActor(name.getChildren().get(index));
        }
    }
}
