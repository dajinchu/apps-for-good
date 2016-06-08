package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.StringBuilder;

import train.chu.chu.model.Node;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 3/9/2016.
 */
public class Block extends HorizontalGroup {//TODO clean this class the fuck up (restart?)

    private final Node node;
    protected DragAndDrop dad;
    //Center rect is the detection area for getting out of the way, or merging blocks
    public static final double HOVER_TIME =.08;



    private Block hoverActor;

    private enum TargetState{LEFT,RIGHT,NOT};
    private TargetState targetState = TargetState.NOT;
    private double targetHoverCount = 0;

    protected String childrenString = "";
    private StringBuilder sb = new StringBuilder();

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
            Block.this.setVisible(false);

            //Duplicate this block, and set that Actor as the Payload dragActor.
            // Duplication does NOT create a Block, merely a WidgetGroup lookalike. Making it a block
            // might be problematic, as then the payload Block would have all the functions of a placed Block.
            // It just needs to look the same.
            PayloadBlock dragActor = getDragActor();
            payload.setDragActor(dragActor);
            //setDragActorPosition is to offset the dragActor from the pointer location
            // without this part, the pointer is always dragging the actor by its left edge
            // instead, this offsets to be held by where it was picked up
            float scale = ScaleUtils.getTrueScale(Block.this);
            dad.setDragActorPosition(-dragActor.getWidth()*scale/2,
                    -dragActor.getHeight()*scale/2+dragActor.getHeight());//Add getHeight to y to offset some stupid stuff done internally in DragAndDrop source code

            return payload;
        }

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
            //Drag stopped. Wherever block ended up, make it visible again. Payload is automatically
            // destroyed by DragAndDrop
            Block.this.setVisible(true);
        }
    }
    protected Source source = new BlockSource(this);

    protected PayloadBlock getDragActor() {
        return new PayloadBlock(Block.this);
    }

    protected Target target = new DragAndDrop.Target(this) {

        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
            //Something is being dragged over this target
            if (source.getActor() == Block.this || !(source.getActor() instanceof Block)) {
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


    public Block(Node node) {
        this.dad = Main.dragAndDrop;
        this.node = node;
        setDraggable(true);
        setTargetable(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(targetState != TargetState.NOT){
            targetHoverCount += delta;
        }
        if(targetHoverCount> HOVER_TIME){
            switch (targetState){
                case LEFT:hoverActor.moveRelative(this, Side.LEFT);break;
                case RIGHT:hoverActor.moveRelative(this, Side.RIGHT);break;
            }
            targetState = TargetState.NOT;
            targetHoverCount = 0;
        }
    }

    public void moveRelative(Block at, Side side){
        int offset;
        if(side==Side.LEFT){
            offset=-1;
        }else{
            offset= 1;
        }
        try {
            int atIndex = at.getParent().getChildren().indexOf(at, true);
            if (at.getParent().getChildren().get(atIndex + offset) == this) return;
        } catch (IndexOutOfBoundsException e){

        }
        node.move(at.getNode(), side);
    }

    @Override
    public void layout() {
        super.layout();
        //When this block's layout has to be recalculated (see super.layout()), we can also recalculate the centerRect.
        //This can't be done just once on creation, because the children might not have been added,
        // and the block's children could change. This is the best place to calculate centerRect.
       // Gdx.app.log(getChildrenString(),"Layout");
        pack();
    }

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        updateChildrenString();
    }

    protected void updateChildrenString() {
        //Recursive function goes through Block children and asks for their strings too
        // getText for Label children
        sb.setLength(0);
        for (Actor a : getChildren()) {
            if (a instanceof Block) {
                sb.append(((Block) a).getChildrenString());
            }
            if (a instanceof Label) {
                sb.append(((Label) a).getText());
            }
        }
        childrenString = sb.toString();
    }

    public String getChildrenString() {
        return childrenString;
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

    public Node getNode(){
        return node;
    }
}
