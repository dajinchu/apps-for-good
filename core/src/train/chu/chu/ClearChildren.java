package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arun on 4/1/2016.
 */
public class ClearChildren extends Command {

    Group row;
    List<Actor> children=new ArrayList<Actor>();
    public ClearChildren(Group row){
        this.row=row;
    }
    @Override
    protected void negativeAction() {
        for(int i=0; i<children.size();i++){
            row.addActorAt(i, children.get(i));
        }
    }

    @Override
    protected void positiveAction() {
        for(int i=0; i<row.getChildren().size;i++){
            children.add(i, row.getChildren().get(i));
        }
        row.clearChildren();

    }
}
