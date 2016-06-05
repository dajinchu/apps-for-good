package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.StringBuilder;

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
    private ParenthesisBlock open, close;
    private boolean trashing = false;
    private StringBuilder sb = new StringBuilder();

    protected class ParenthesisContainerSource extends BlockSource {

        public ParenthesisContainerSource(Actor actor) {
            super(actor);
        }

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
            for (Actor a : contents) {
                a.setVisible(true);
            }
            ParenthesisContainer.this.setVisible(true);

            if(trashing)return;
            parenthesis.getParent().addActorAfter(parenthesis, ParenthesisContainer.this);
            putContentsIn();
        }
    }

    public ParenthesisContainer(ParenthesisBlock block) {
        setDraggable(false);
        setTargetable(false);
        this.side = block.side;
        this.parenthesis = block;
        this.source = new ParenthesisContainerSource(this);
        setDraggable(true);
        block.getParent().addActorAfter(block, this);

        Group parent = getParent();
        SnapshotArray<Actor> siblings = parent.getChildren();

        //Parenthesis might be open or close, so it might need to go at the beginning or end.
        // Make it all much simpler by just removing it and adding everything from open->close
        if (side == Side.OPENING) {
            parent.addActorAfter(this, parenthesis);
        } else {
            parent.addActorBefore(this, parenthesis);
        }

        //Find the open and close parenthesis.
        if (this.side == Side.OPENING) {
            open = parenthesis;
            int counter = 0;
            for (int i = siblings.indexOf(open, true) + 1; i < siblings.size; i++) {
                if (siblings.get(i) instanceof ParenthesisBlock) {
                    if (((ParenthesisBlock) siblings.get(i)).side == Side.CLOSING) {
                        counter--;
                    } else if (((ParenthesisBlock) siblings.get(i)).side == Side.OPENING) {
                        counter++;
                    }
                }
                if (counter == -1) {
                    close = (ParenthesisBlock) siblings.get(i);
                    break;
                }
            }
        } else {
            close = parenthesis;
            int counter = 0;
            for (int i = siblings.indexOf(close, true) - 1; i >= 0; i--) {
                if (siblings.get(i) instanceof ParenthesisBlock) {
                    if (((ParenthesisBlock) siblings.get(i)).side == Side.CLOSING) {
                        counter++;
                    } else if (((ParenthesisBlock) siblings.get(i)).side == Side.OPENING) {
                        counter--;
                    }
                }
                if (counter == -1) {
                    open = (ParenthesisBlock) siblings.get(i);
                    break;
                }
            }
        }

        //Copy all children into contents so we can keep track of them
        Gdx.app.log("ParenContain",siblings.indexOf(open,true)+" close: "+siblings.indexOf(close, true));
        for(int i = siblings.indexOf(open, true); i <= siblings.indexOf(close, true); i++){
            contents.add(siblings.get(i));
        }

        sb.setLength(0);
        for (Actor a : contents) {
            if (a instanceof Block) {
                sb.append(((Block) a).getChildrenString());
            }
            if (a instanceof Label) {
                sb.append(((Label) a).getText());
            }
        }
        childrenString = sb.toString();

        putContentsIn();
    }

    @Override
    protected void updateChildrenString() {

    }

    private void putContentsIn(){
        //Put all things in contents in as children, create payload block, and take everything back out.
        // They need to be in so payload creates everything correctly, but need to be removed so that they
        // interact with the row and the undo system properly.
        for(Actor a: contents) {
            this.addActor(a);
            a.setTouchable(Touchable.disabled);
        }
        this.setTouchable(Touchable.enabled);
    }
    private void takeContentsOut(){
        //Take contents out of this container.
        // Allows commands to be performed on the contents without messing up undo
        getParent().addActorAfter(this,contents.get(0));
        for(int i = 1; i < contents.size; i ++){
            getParent().addActorAfter(contents.get(i-1), contents.get(i));
        }
        for(Actor a:contents){
            a.setTouchable(Touchable.enabled);
        }
        this.setTouchable(Touchable.disabled);
    }

    protected PayloadBlock getDragActor() {
        //Create payload block that has everything in it, and then empty self so that contents can be
        // moved individually, making them work with the undo system
        PayloadBlock payloadBlock = new PayloadBlock(this);
        takeContentsOut();
        for(Actor a:contents){
            a.setVisible(false);
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

        SnapshotArray<Actor> atSiblings = at.getParent().getChildren();
        try {
            if ((side == MoveCommand.Side.RIGHT && atSiblings.get(atSiblings.indexOf(at, true) + 1) == open) ||
                    (side == MoveCommand.Side.LEFT && atSiblings.get(atSiblings.indexOf(at, true) - 1) == close)) {
                return;
            }
        }catch(IndexOutOfBoundsException e){

        }

        //Blocks need to be dragged individually to make sure undo/redo works, as the container probably
        // won't exist during undo operations
        BatchedCommand batch = new BatchedCommand();
        batch.add(new MoveCommand(at,contents.get(0),side));
        for(int i = 1; i < contents.size; i ++){
            batch.add(new MoveCommand(contents.get(i-1),contents.get(i), MoveCommand.Side.RIGHT));
        }
        Gdx.app.log("Parenthesis",batch.toString());
        batch.execute();
    }
    public void trash(){
        //Trash the container and everything inside in an undo-friendly manner
        trashing = true;
        BatchedCommand batched = new BatchedCommand();
        for(Actor a:contents) {
            batched.add(new RemoveCommand(a));
        }//TODO move the contents out too. Probably shouldn't overid remove atually
        batched.execute();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (isTransform()) applyTransform(batch, computeTransform());
        batch.setColor(Color.BLACK);
        BatchShapeUtils.drawDashedRectangle(batch, 0, 0, getWidth(), getHeight(), 2);
        if (isTransform()) resetTransform(batch);
    }

    public void unselect(){
        //Basically get rid of the container.
        takeContentsOut();
        remove();
    }
}
