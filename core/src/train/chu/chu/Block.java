package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by Da-Jin on 3/9/2016.
 */
public class Block extends HorizontalGroup {

    private DragAndDrop dad;
    //Center rect is the detection area for getting out of the way, or merging blocks
    private Rectangle centerRect = new Rectangle();

    private Source source;
    private Target target;

    //There can only be one block selected at a time
    private static Block selectedBlock;

    public Block(final DragAndDrop dad) {
        this.dad = dad;

        //Block becomes 'selected' when clicked, allowing its children to be dragged or clicked as well
        ClickListener selectionListener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //This block can only become selected if its parent was already selected
                if (getParent() instanceof Block && ((Block)getParent()).isSelected() && getChildren().size>1) {
                    //Stop the event so that the listener on stage doesn't get triggered and reset the selection to the outer levels
                    event.stop();
                    //Return TRUE so that the successive touchUp event will be received
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //IMPORTANT: This code will only be reached if touchDown return true, ie. this block is "selectable"
                setSelected();
            }
        };
        addListener(selectionListener);

        //DragAndDrop has Source, Payload, and Target
        //Source is what we drag from.
        //Payload is the thing getting dragged.
        //Targets are things that payloads can be dropped onto. They listen for payloads interacting with them.
        //When blocks are dragged, they create a lookalike for the Payload
        this.source = new Source(this) { //Give source this Block in constructor as the SourceActor
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                //This block is being dragged. We have to supply a Payload to get dragged with the mouse.
                Payload payload = new Payload();
                payload.setObject("Some payload!");

                //Set this block invisible. It should appear to the user that they are dragging the
                // block itself, not a look-alike. This block should disappear, and leave only the payload visible
                Block.this.setVisible(false);

                //Duplicate this block, and set that Actor as the Payload dragActor.
                // Duplication does NOT create a Block, merely a WidgetGroup lookalike. Making it a block
                // might be problematic, as then the payload Block would have all the functions of a placed Block.
                // It just needs to look the same.
                WidgetGroup dragActor = getDuplicateForDragging();
                payload.setDragActor(dragActor);
                //setDragActorPosition is to offset the dragActor from the pointer location
                // without this part, the pointer is always dragging the actor by its left edge
                // instead, this offsets to be held by where it was picked up
                dad.setDragActorPosition(-x * getFirstParentScale(), (dragActor.getPrefHeight() / 2 - y) * getFirstParentScale());

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                //Drag stopped. Wherever block ended up, make it visible again. Payload is automatically
                // destroyed by DragAndDrop
                Block.this.setVisible(true);
            }
        };
        this.target = new Target(this) {
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                //Something is being dragged over this target
                if (source.getActor() == Block.this) {
                    //This probably won't happen. But to be sure, this prevents dragging over itself
                    return false;
                }

                if (centerRect.contains(x, y)) {
                    //The centerRect has been entered, go swap with payload's source.
                    // To the user this looks this block is getting out of the way of what's being dragged
                    // It's a little different in code because the payload is just sort of 'representing'
                    // the source Block.
                    getParent().swapActor(getActor(), source.getActor());//TODO this probably really shouldn't be a swap: it looks weird when you go around other blocks to place one far away. shouldn't be swap, it should remove source, and insert it here, but it'll have to decide which side of this block to put it on
                    ((WidgetGroup) getParent()).invalidate();
                }
                getActor().setColor(Color.GREEN);
                return true;
            }

            public void reset(Source source, Payload payload) {
                getActor().setColor(Color.BLACK);
            }

            public void drop(Source source, Payload payload, float x, float y, int pointer) {
            }
        };
    }

    private float getFirstParentScale() {
        //Iterate through parents and find first parent that has been scale.
        // It should find sandbox main container and return its scale
        // Can't just use this.scale because parents can affect Block's scale but block does not inherit the scale value
        Actor scale = this;
        while (scale.getScaleX() == 1 && scale.hasParent()) {
            scale = scale.getParent();
        }
        return scale.getScaleX();
    }

    private WidgetGroup getDuplicateForDragging() {
        //Create a lookalike of this block, for Payloads
        WidgetGroup g = new HorizontalGroup();
        g.setScale(getFirstParentScale());
        //Iterate through children and add them to the clone group
        for (Actor a : getChildren()) {
            //TODO extract and improve the duping process into some kind of CloneUtils that would clone Actors
            if (a.getClass() == Block.class) {
                //If the child is a block class, ask that nested block to duplicate too
                g.addActor(((Block) a).getDuplicateForDragging());
            } else {
                //Otherwise, it's a label, so cast it and get access to text and style
                Label aLabel = (Label) a;
                //Create a new Label instance, and give it the same properties as the old one
                Label dupe = new Label(aLabel.getText(), aLabel.getStyle());
                dupe.setColor(aLabel.getColor());
                //And add it to the clone group, of course
                g.addActor(dupe);
            }
        }
        return g;
    }

    @Override
    public void layout() {
        super.layout();
        //When this block's layout has to be recalculated (see super.layout()), we can also recalculate the centerRect.
        //This can't be done just once on creation, because the children might not have been added,
        // and the block's children could change. This is the best place to calculate centerRect.
        centerRect.set(
                getWidth() * .3f, 0,
                getWidth() * .4f, getHeight());
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        if(actor instanceof Block) {
            ((Block) actor).setDraggable(isSelected());
        }
    }

    public void setDraggable(boolean draggable) {
        if (draggable) {
            dad.addTarget(target);
            dad.addSource(source);
        } else {
            dad.removeTarget(target);
            dad.removeSource(source);
        }
    }

    public String getChildrenString(){
        //Recursive function goes through Block children and asks for their strings too
        // getText for Label children
        StringBuilder sb = new StringBuilder();
        for(Actor a : getChildren()){
            if(a.getClass() == Block.class){
                sb.append(((Block)a).getChildrenString());
            }
            if(a.getClass() == Label.class){
                sb.append(((Label)a).getText());
            }
        }
        return sb.toString();
    }

    public void setSelected(){
        //Set this block as the selected one
        if(selectedBlock != null) {
            for (Actor child : selectedBlock.getChildren()) {
                ((Block) child).setDraggable(false);
            }
        }
        selectedBlock = this;
        for(Actor child: selectedBlock.getChildren()){
            ((Block) child).setDraggable(true);
        }
    }

    public boolean isSelected(){
        //It is possible to instead use a selected flag in each Block, but it would be prone to failure
        return this.equals(Block.selectedBlock);
    }
}
