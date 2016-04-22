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

    public enum Side {LEFT, RIGHT, IN}

    ;

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

        //Needed for move-in
        if (side == Side.IN) {
            targetParent.addActorAt(oldTargetIndex, targetActor);
            groupBlock.remove();
        }
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
            case IN:
                if (targetActor instanceof Block && ((Block) targetActor).getChildren().size > 1) {
                    groupBlock = (Block) targetActor;
                } else {
                    groupBlock = new Block();
                    groupBlock.addActor(targetActor);
                }
                targetParent.addActorAt(oldTargetIndex, groupBlock);
                groupBlock.setSelected();
                break;
        }

    }
}
