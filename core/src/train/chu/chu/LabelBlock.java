package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.BlankNode;
import train.chu.chu.model.Model;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 3/9/2016.
 */
public class LabelBlock extends Container<Label> implements Block {//TODO clean this class the fuck up (restart?)

    private final BaseNode node;
    private final Model model;
    protected DragAndDrop dad;
    //Center rect is the detection area for getting out of the way, or merging blocks
    public static final double HOVER_TIME =.08;

    private Block hoverActor;

    @Override
    public void move(BaseNode to, Side side) {
        node.move(to,side);
    }

    @Override
    public void trash() {
        node.remove();
    }

    private enum TargetState{LEFT,RIGHT,NOT};
    private TargetState targetState = TargetState.NOT;
    private double targetHoverCount = 0;

    //DragAndDrop has Source, Payload, and Target
    //Source is what we drag from.
    //Payload is the thing getting dragged.
    //Targets are things that payloads can be dropped onto. They listen for payloads interacting with them.
    //When blocks are dragged, they create a lookalike for the Payload
    protected class BlockSource extends Source{
        public BlockSource(Actor actor) {
            super(actor);
        } //Give source this Block in constructor as the SourceActor
        public Payload dragStart(InputEvent event, float x, float y, int pointer) {
            //This block is being dragged. We have to supply a Payload to get dragged with the mouse.
            Payload payload = new Payload();

            //Set this block invisible. It should appear to the user that they are dragging the
            // block itself, not a look-alike. This block should disappear, and leave only the payload visible
            LabelBlock.this.setVisible(false);

            //Duplicate this block, and set that Actor as the Payload dragActor.
            // Duplication does NOT create a Block, merely a WidgetGroup lookalike. Making it a block
            // might be problematic, as then the payload Block would have all the functions of a placed Block.
            // It just needs to look the same.
            PayloadBlock dragActor = getDragActor();
            payload.setDragActor(dragActor);
            //setDragActorPosition is to offset the dragActor from the pointer location
            // without this part, the pointer is always dragging the actor by its left edge
            // instead, this offsets to be held by where it was picked up
            float scale = ScaleUtils.getTrueScale(LabelBlock.this);
            dad.setDragActorPosition(-dragActor.getWidth()*scale/2,
                    -dragActor.getHeight()*scale/2+dragActor.getHeight());//Add getHeight to y to offset some stupid stuff done internally in DragAndDrop source code

            return payload;
        }

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
            //Drag stopped. Wherever block ended up, make it visible again. Payload is automatically
            // destroyed by DragAndDrop
            if(getStage()==null)return;
            LabelBlock.this.setVisible(true);
            if(getStage().hit(event.getStageX(),event.getStageY(),true)==null){
                Vector2 pos = ScaleUtils.positionWithin(Main.calcZone, event.getStageX(), event.getStageY());
                BlankNode blank = model.addExpression(pos.x, pos.y-getMinHeight()/4);
                node.moveInto(blank);
            }
        }
    }
    protected Source source = new BlockSource(this);

    protected PayloadBlock getDragActor() {
        return new PayloadBlock(LabelBlock.this);
    }

    protected Target target = new DragAndDrop.Target(this) {

        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
            //Something is being dragged over this target
            if (source.getActor() == LabelBlock.this || !(source.getActor() instanceof Block)) {
                //This probably won't happen. But to be sure, this prevents dragging over itself
                return false;
            }

            if (x < getWidth() * .4f) {
                //The centerRect has been entered, go swap with payload's source.
                // To the user this looks this block is getting out of the way of what's being dragged
                // It's a little different in code because the payload is just sort of 'representing'
                // the source Block.
                targetState = TargetState.LEFT;
                //System.out.println(Gdx.graphics.getDeltaTime());

            } else if (x > getWidth() * .6f) {
                targetState = TargetState.RIGHT;
                //System.out.println(Gdx.graphics.getDeltaTime());
            } else {
                targetState = TargetState.NOT;
                targetHoverCount = 0;
            }
            hoverActor = (Block) source.getActor();
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


    public LabelBlock(BaseNode node, Model model) {
        this.dad = Main.dragAndDrop;
        this.model = model;
        this.node = node;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(targetState != TargetState.NOT){
            targetHoverCount += delta;
        }
        if(targetHoverCount> HOVER_TIME){
            switch (targetState){
                case LEFT: hoverActor.move(this.getNode(), Side.LEFT); break;
                case RIGHT:hoverActor.move(this.getNode(), Side.RIGHT);break;
            }
            targetState = TargetState.NOT;
            targetHoverCount = 0;
        }
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
        //Gdx.app.log(getChildrenString(),"targetable to " + targetable);
    }

    public BaseNode getNode(){
        return node;
    }
}
