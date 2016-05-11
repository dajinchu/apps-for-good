package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Da-Jin on 4/19/2016.
 * Creates parantheses in appropriate locations
 */
public class ParenthesisCommand extends Command {

    private final Actor a1, a2;
    private Block p1, p2;

    /**
     * Creates parantheses
     * @param left the left actor
     * @param right the right actor
     */
    public ParenthesisCommand(Actor left, Actor right){
        this.a1 = left;
        this.a2 = right;
        p1 = BlockCreator.BlockCreator("(");
        p2 = BlockCreator.BlockCreator(")");
    }

    @Override
    /*
    Removes Actor
     */
    protected void negativeAction() {
        p1.remove();
        p2.remove();
    }

    @Override
    /*
    Creates Actor
     */
    protected void positiveAction() {
        a1.getParent().addActorBefore(a1,p1);
        a2.getParent().addActorAfter(a2,p2);
    }

    @Override
    public String toString() {
        return "Parenthesis";
    }
}
