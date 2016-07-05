package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
public class LabelBlock extends Container<Label> {

    private final BaseNode node;
    private final Model model;
    protected DragAndDrop dad;
    //Center rect is the detection area for getting out of the way, or merging blocks
    public static final double HOVER_TIME =.08;

    private BlockSource hoverActor;

    private enum TargetState{LEFT,RIGHT,NOT};
    private TargetState targetState = TargetState.NOT;
    private double targetHoverCount = 0;

    protected Source source;

    protected Target target = new DragAndDrop.Target(this) {

        public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
            //Something is being dragged over this target
            if (source.getActor() == LabelBlock.this || !(source instanceof BlockSource)) {
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
            hoverActor = (BlockSource) source;
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


    public LabelBlock(final BaseNode node, final Model model) {
        this.dad = Main.dragAndDrop;
        this.model = model;
        this.node = node;
        this.source = new BlockSource(this, model) {
            @Override
            protected WidgetGroup getDupe() {
                return new PayloadBlock(LabelBlock.this);
            }

            @Override
            public void move(BaseNode to, Side side) {
                node.move(to,side);
            }

            @Override
            public void moveInto(BlankNode into) {
                node.moveInto(into);
            }

            @Override
            public void trash() {
                node.remove();
            }
        };

        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(x<getWidth()/2){
                    model.getInsertionPoint().move(node,Side.LEFT);
                }else{
                    model.getInsertionPoint().move(node,Side.RIGHT);
                }
            }
        });
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
