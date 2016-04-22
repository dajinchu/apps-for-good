package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
    private static final double HOVER_TIME =.5;


    private Source source;
    private Target target;
    private Actor hoverActor;

    private enum TargetState{LEFT,RIGHT,NOT};
    private TargetState targetState = TargetState.NOT;
    private double targetHoverCount = 0;

    //There can only be one block selected at a time
    private static Block selectedBlock;

    public Block() {
        this.dad = Main.dragAndDrop;

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
                PayloadBlock dragActor = new PayloadBlock(Block.this);
                payload.setDragActor(dragActor);
                //setDragActorPosition is to offset the dragActor from the pointer location
                // without this part, the pointer is always dragging the actor by its left edge
                // instead, this offsets to be held by where it was picked up
                float scale = ScaleUtils.getTrueScale(Block.this);
                dad.setDragActorPosition(-x * scale, (dragActor.getPrefHeight() - y) * scale);

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                //Drag stopped. Wherever block ended up, make it visible again. Payload is automatically
                // destroyed by DragAndDrop
                Block.this.setVisible(true);
            }
        };

        target = new DragAndDrop.Target(this) {

            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                //Something is being dragged over this target
                if (source.getActor() == Block.this) {
                    //This probably won't happen. But to be sure, this prevents dragging over itself
                    return false;
                }

                if (x < getWidth() * .3f) {
                    //The centerRect has been entered, go swap with payload's source.
                    // To the user this looks this block is getting out of the way of what's being dragged
                    // It's a little different in code because the payload is just sort of 'representing'
                    // the source Block.
                    targetState = TargetState.LEFT;
                    //System.out.println(Gdx.graphics.getDeltaTime());

                } else if (x > getWidth() * .7f) {
                    targetState = TargetState.RIGHT;
                    //System.out.println(Gdx.graphics.getDeltaTime());
                } else {
                    targetState = TargetState.NOT;
                    targetHoverCount = 0;
                }
                hoverActor = source.getActor();
                return true;
            }

            public void reset(Source source, Payload payload) {
                getActor().setColor(Color.BLACK);
                targetState = TargetState.NOT;
                targetHoverCount = 0;
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                targetState = TargetState.NOT;
                targetHoverCount = 0;
            }
        };
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(targetState != TargetState.NOT){
            targetHoverCount += delta;
        }
        if(targetHoverCount> HOVER_TIME){
            switch (targetState){
                case LEFT:new MoveCommand(this, hoverActor, MoveCommand.Side.LEFT).execute();break;
                case RIGHT:new MoveCommand(this, hoverActor, MoveCommand.Side.RIGHT).execute();break;
            }
            targetState = TargetState.NOT;
            targetHoverCount = 0;
        }
    }

    @Override
    public void layout() {
        super.layout();
        //When this block's layout has to be recalculated (see super.layout()), we can also recalculate the centerRect.
        //This can't be done just once on creation, because the children might not have been added,
        // and the block's children could change. This is the best place to calculate centerRect.
        Gdx.app.log(getChildrenString(),"Layout");
        pack();
    }

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        setAllChildrenSelect(isSelected());
        if(getChildren().size==0 || (getChildren().size==1 && getChildren().get(0) instanceof Label)) return;
        this.pad(5);

    }

    public void setDraggable(boolean draggable) {
        dad.removeSource(source);
        if (draggable) {
            dad.addSource(source);
        }
    }

    public void setTargetable(boolean targetable){
        dad.removeTarget(target);
        if(targetable){
            dad.addTarget(target);
        }
        Gdx.app.log(getChildrenString(),"targetable to " + targetable);
    }

    public void setNestedColors(Color color){
        //Recursive function that works the same way as getChildrenString, but with color setting
        for(Actor a : getChildren()) {
            if (a instanceof Block) {
                ((Block) a).setNestedColors(color);
            } else {
                a.setColor(color);
            }
        }
    }

    public String getChildrenString(){
        //Recursive function goes through Block children and asks for their strings too
        // getText for Label children
        StringBuilder sb = new StringBuilder();
        if(getChildren().size>1)sb.append("(");
        for(Actor a : getChildren()){
            if(a.getClass() == Block.class){
                sb.append(((Block)a).getChildrenString());
            }
            if(a.getClass() == Label.class){
                sb.append(((Label)a).getText());
            }
        }
        if(getChildren().size>1) sb.append(")");
        return sb.toString();
    }

    public void setSelected(){
        //Set this block as the selected one
        Gdx.app.log(getChildrenString(),"setselected");
        Block oldSelected = selectedBlock;
        selectedBlock = this;
        if(oldSelected != null) {
            oldSelected.setColor(Color.BLACK);
            oldSelected.setAllChildrenSelect(false);
        }
        setAllChildrenSelect(true);

    }

    private void setAllChildrenSelect(boolean select){
        //setTargetable(!select);
        for(Actor child: getChildren()){
            if(child instanceof Block) {
                ((Block) child).setAsChildOfSelected(select);
            }
        }
        if(select){
            Group parent = getParent();
            Block childContainingSelected = this;
            while(parent instanceof Block){
                for(Actor child : parent.getChildren()){
                    if(child instanceof Block && child!=childContainingSelected){
                        ((Block) child).setTargetable(true);
                    }//TODO Else set untargetable?
                }
                if(parent.hasParent()){
                    childContainingSelected = (Block) parent;
                    parent = parent.getParent();
                } else {
                    break;
                }
            }
        }
    }

    private void setAsChildOfSelected(boolean childOf){
        //Private convenience method to switch a block on/off as the child of the selected block
        // Needed because this needs to happen in setSelected, but also in addActor to apply the
        // childOfSelected behavior to newly added blocks too
        setDraggable(childOf);
        setTargetable(childOf);

        Color set = Color.BLACK;
        if(childOf) {
            set = Color.valueOf("3F51B5");
        }
        if((getChildren().size==1 && getChildren().get(0) instanceof Label)){
            //This is a block with just a label inside, it has no outline, so color the label
            getChildren().get(0).setColor(set);
        }else{
            //This is a group-block, color the outline, NOT the label children
            this.setColor(set);
        }
    }

    public boolean isSelected(){
        //It is possible to instead use a selected flag in each Block, but it would be prone to failure
        return this == Block.selectedBlock;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        //Dashed-line border
        if(getChildren().size==0 || (getChildren().size==1 && getChildren().get(0) instanceof Label)) return;//TODO This set of ifs is repeatedly needed
        if (isTransform()) applyTransform(batch, computeTransform());
        batch.setColor(getColor());
        BatchShapeUtils.drawDashedRectangle(batch, 0, 0, getWidth(), getHeight(), 2);
        if (isTransform()) resetTransform(batch);

    }
}
