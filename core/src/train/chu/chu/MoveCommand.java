package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Arun on 3/29/2016.
 */
public class MoveCommand extends Command {

    private Actor targetActor;
    private Actor sourceActor;
    private Group targetParent, oldSourceParent;
    private int oldSourceIndex = 0, oldTargetIndex = 0;
    private Side side;

    //only for move-in
    private Block groupBlock;

    public enum Side {LEFT, RIGHT}

    ;

    /**
     * Moves actor to new location
     * @param targetActor the Actor the current actor will be placed next to
     * @param sourceActor the Actor that is being moved
     * @param side left or right of the targetActor
     */
    public MoveCommand(Actor targetActor, Actor sourceActor, Side side) {
        this.targetActor = targetActor;
        this.sourceActor = sourceActor;
        this.targetParent = targetActor.getParent();
        this.oldSourceParent = sourceActor.getParent();
        this.oldTargetIndex = targetParent.getChildren().indexOf(targetActor, true);
        this.oldSourceIndex = oldSourceParent.getChildren().indexOf(sourceActor, true);
        this.side = side;
    }

    @Override
    /**
     * Undoes last  move action
     */
    protected void negativeAction() {
        oldSourceParent.addActorAt(oldSourceIndex, sourceActor);
    }

    @Override
    /**
     * Moves actor to new location. Moves according to side enum.
     */
    protected void positiveAction() {
        switch (side) {
            case LEFT:
                targetParent.addActorBefore(targetActor, sourceActor);
                break;
            case RIGHT:
                targetParent.addActorAfter(targetActor, sourceActor);
                break;
        }

    }
}
