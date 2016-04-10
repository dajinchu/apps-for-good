package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
    private Rectangle leftRect = new Rectangle();
    private final double timeInBlock=0.085;


    private Source source;
    private Target target;

    //There can only be one block selected at a time
    private static Block selectedBlock;

    public Block() {
        this.dad = Main.dragAndDrop;

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
                if(dad.isDragging())return; //Doesn't count as a click if it's the start of drag
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
                PayloadBlock dragActor = new PayloadBlock(Block.this);
                payload.setDragActor(dragActor);
                //setDragActorPosition is to offset the dragActor from the pointer location
                // without this part, the pointer is always dragging the actor by its left edge
                // instead, this offsets to be held by where it was picked up
                float scale = ScaleUtils.getTrueScale(Block.this);
                dad.setDragActorPosition(-x * scale, (dragActor.getPrefHeight() / 2 - y) * scale);

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

            float timeCenter;
            double timeLeft=0;
            double timeRight=0;

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
                        timeLeft+= Gdx.graphics.getDeltaTime();
                        //System.out.println(Gdx.graphics.getDeltaTime());

                    } else if (x > getWidth() * .7f) {
                        timeRight+=Gdx.graphics.getDeltaTime();
                        //System.out.println(Gdx.graphics.getDeltaTime());
                    }else{
                        timeLeft=0;
                        timeRight=0;

                        timeCenter+=Gdx.graphics.getDeltaTime();
                    }

                if(timeLeft>=timeInBlock) {
                    Command cmd = new MoveCommand(getActor(), source.getActor(), MoveCommand.Side.LEFT);
                    cmd.execute();
                    timeLeft=0;
                    timeRight=0;
                }else if(timeRight>=timeInBlock){
                    Command cmd = new MoveCommand(getActor(), source.getActor(), MoveCommand.Side.RIGHT);
                    cmd.execute();
                    timeLeft=0;
                    timeRight=0;
                }else if(timeCenter>=timeInBlock*4){
                    timeCenter=0;
                    new MoveCommand(getActor(),source.getActor(), MoveCommand.Side.IN).execute();
                }

                getActor().setColor(Color.GREEN);
                return true;
            }

            public void reset(Source source, Payload payload) {
                getActor().setColor(Color.BLACK);
                timeLeft=0;
                timeRight=0;
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                timeLeft=0;
                timeRight=0;
            }
        };
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
        Gdx.app.log(getChildrenString(),"Layout");
        pack();
    }

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        setAllChildrenSelect(isSelected());
        if(getChildren().size>1){
            this.pad(5);
            //TODO Display the group connection graphically
        }
    }

    public void setDraggable(boolean draggable) {
        dad.removeTarget(target);
        dad.removeSource(source);
        if (draggable) {
            dad.addTarget(target);
            dad.addSource(source);
        }
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
        Block oldSelected = selectedBlock;
        selectedBlock = this;
        if(oldSelected != null) {
            oldSelected.setAllChildrenSelect(false);
        }
        setAllChildrenSelect(true);
    }

    private void setAllChildrenSelect(boolean select){
        for(Actor child: getChildren()){
            if(child instanceof Block) {
                ((Block) child).setAsChildOfSelected(select);
            }
        }
    }

    private void setAsChildOfSelected(boolean childOf){
        //Private convenience method to switch a block on/off as the child of the selected block
        // Needed because this needs to happen in setSelected, but also in addActor to apply the
        // childOfSelected behavior to newly added blocks too
        setDraggable(childOf);
        if(childOf) {
            setNestedColors(Color.valueOf("3F51B5"));
        } else {
            setNestedColors(Color.BLACK);
        }
    }

    public boolean isSelected(){
        //It is possible to instead use a selected flag in each Block, but it would be prone to failure
        return this == Block.selectedBlock;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(getChildren().size==0 || (getChildren().size==1 && getChildren().get(0) instanceof Label)) return;

        if (isTransform()) applyTransform(batch, computeTransform());
        batch.setColor(Color.BLACK);
        BatchShapeUtils.drawDashedRectangle(batch, 0, 0, getWidth(), getHeight(), 2);
        if (isTransform()) resetTransform(batch);

    }
}
