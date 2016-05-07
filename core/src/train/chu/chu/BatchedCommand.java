package train.chu.chu;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by Da-Jin on 5/6/2016.
 */
public class BatchedCommand extends Command {
    private Array<Command> cmds = new Array<>();
    private boolean firstTime = true;

    public void add(Command c){
        cmds.add(c);
        c.positiveAction();
    }
    @Override
    protected void negativeAction() {
        Array<Command> reverse = new Array<>(cmds);
        reverse.reverse();
        for(Command c:reverse){
            c.negativeAction();
        }
    }

    @Override
    protected void positiveAction() {
        if(firstTime){
            firstTime = false;
            return;
        }
        for(Command c:cmds){
            c.positiveAction();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Batch:");
        for(Command c:cmds){
            sb.append(c);
        }
        return sb.toString();
    }
}
