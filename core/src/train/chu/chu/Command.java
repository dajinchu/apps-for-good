package train.chu.chu;

import com.badlogic.gdx.Gdx;

import java.util.Stack;

/**
 * Created by Arun on 3/28/2016.
 */
public abstract class Command {

    /**
     * Boolean that checks to see if the last action was redo or undo.
     */
    private boolean redoing=false;

    /**
     * Execute a given command.
     */
    public void execute(){

        //Clear Redo stack
        if(!redoing) {
            redoCommands.clear();
        }

        //Execute the postive action and adds command to undo stack
        positiveAction();
        undoCommands.push(this);
    }

    /**
     * Unexecute a given command (Undo)
     */
    public void unexecute(){
        Gdx.app.log("undo",this.toString());
        //Undoes command and pushes it to the redo stack
        negativeAction();
        redoCommands.push(this);
    }

    protected abstract void negativeAction();

    protected abstract void positiveAction();

    /**
     * Undoes last action, removes from undo stack.
     */
    public static void undo(){
        if(!undoCommands.empty()){
            undoCommands.pop().unexecute();
        }

    }

    /**
     * Redoes last undo, removes from redo stack.
     */
    public static void redo(){

        if(!redoCommands.empty()){
            Command command = redoCommands.pop();
            command.redoing = true;
            command.execute();
        }

    }

    protected static Stack<Command> undoCommands =new Stack<Command>();
    protected static Stack<Command> redoCommands =new Stack<Command>();

}
