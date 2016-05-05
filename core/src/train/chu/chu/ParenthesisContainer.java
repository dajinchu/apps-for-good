package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

import train.chu.chu.ParenthesisBlock.Side;

/**
 * Created by Da-Jin on 5/2/2016.
 * Parenthesis container is a group that should appear when parenthesisBlock is clicked
 * When dragged, it finds the other parenthesis and moves everything in between.
 */
public class ParenthesisContainer extends Block {
    private final ParenthesisBlock parenthesis;
    //contents is just like getChildren(), but we need it to keep track of what this container is
    // "in charge" of. The children may be actually inside or outside this, but this is still responsible for them.
    private Array<Actor> contents = new Array<>();

    Side side;

    protected class ParenthesisContainerSource extends BlockSource {

        public ParenthesisContainerSource(Actor actor) {
            super(actor);
        }
        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
            for(Actor a:contents){
                a.setVisible(true);
            }
            ParenthesisContainer.this.setVisible(true);
            parenthesis.getParent().addActorAfter(parenthesis, ParenthesisContainer.this);
            addActor(parenthesis);
        }
    }

    public ParenthesisContainer(ParenthesisBlock block) {
        this.side = block.side;
        this.parenthesis = block;
        this.source = new ParenthesisContainerSource(this);
        block.getParent().addActorAfter(block,this);
        addActor(block);
    }

    @Override
    public String getChildrenString() {
        return parenthesis.getChildrenString();
    }


    protected PayloadBlock getDragActor() {
        Group parent = getParent();
        SnapshotArray<Actor> siblings = parent.getChildren();
        setTouchable(Touchable.enabled);

        //Parenthesis might be open or close, so it might need to go at the beginning or end.
        // Make it all much simpler by just removing it and adding everything from open->close
        if(side == Side.OPENING) {
            parent.addActorAfter(this,parenthesis);
        }else{
            parent.addActorBefore(this,parenthesis);
        }

        ParenthesisBlock open = null, close = null;

        //Find the open and close parenthesis.
        if (this.side == Side.OPENING) {
            open = parenthesis;
            for (int i = siblings.indexOf(open, true) + 1; i < siblings.size; i++) {
                if (siblings.get(i) instanceof ParenthesisBlock &&
                        ((ParenthesisBlock) siblings.get(i)).side == Side.CLOSING) {
                    close = (ParenthesisBlock) siblings.get(i);
                    break;
                }
            }
        } else {
            close = parenthesis;
            for (int i = siblings.indexOf(close, true) - 1; i >= 0; i--) {
                if (siblings.get(i) instanceof ParenthesisBlock &&
                        ((ParenthesisBlock) siblings.get(i)).side == Side.OPENING) {
                    open = (ParenthesisBlock) siblings.get(i);
                    break;
                }
            }
        }

        //Copy all children into contents so we can keep track of them
        // Also make them invisible
        contents.clear();
        Gdx.app.log("ParenContain",siblings.indexOf(open,true)+" close: "+siblings.indexOf(close, true));
        for(int i = siblings.indexOf(open, true); i <= siblings.indexOf(close, true); i++){
            contents.add(siblings.get(i));
            siblings.get(i).setVisible(false);
        }

        //Put all things in contents in as children, create payload block, and take everything back out.
        // They need to be in so payload creates everything correctly, but need to be removed so that they
        // interact with the row and the undo system properly.
        for(Actor a: contents) {
            this.addActor(a);
        }
        PayloadBlock payloadBlock = new PayloadBlock(this);
        parent.addActorAfter(this,contents.get(0));
        for(int i = 1; i < contents.size; i ++){
            parent.addActorAfter(contents.get(i-1), contents.get(i));
        }

        //Throw paren container out of the row, it has nothing in it anyway.
        // It still needs to be in stage though, so just add it in nowhere land
        getStage().addActor(this);
        return payloadBlock;
    }

    @Override
    public void moveRelative(Block at, MoveCommand.Side side) {
        //Make sure we aren't dragging on ourself
        if(contents.contains(at,true))return;

        //Blocks need to be dragged individually to make sure undo/redo works, as the container probably
        // won't exist during undo operations
        new MoveCommand(at,contents.get(0),side).execute();
        for(int i = 1; i < contents.size; i ++){
            new MoveCommand(contents.get(i-1),contents.get(i), MoveCommand.Side.RIGHT).execute();
        }
    }
}
