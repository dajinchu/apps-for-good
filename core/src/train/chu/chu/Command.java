package train.chu.chu;

import java.util.Stack;

/**
 * Created by Arun on 3/28/2016.
 */
public abstract class Command {

    private boolean redoing=false;

    public void execute(){
        if(!redoing) {
            redoCommands.clear();
        }
        positiveAction();
        undoCommands.push(this);
    }

    public void unexecute(){
        negativeAction();
        redoCommands.push(this);
    }

    protected abstract void negativeAction();

    protected abstract void positiveAction();

    public static void undo(){
        if(!undoCommands.empty()){
            undoCommands.pop().unexecute();
        }

    }

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
