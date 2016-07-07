package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.ExpressionNode;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 5/20/2016.
 */
public class ExternalZone extends Container<Label>{
    private final ExpressionNode expression;
    private final Side side;
    private boolean dragging = false;
    private double dragTime;
    private BlockSource hoverSource;

    public ExternalZone(Side side, ExpressionNode expression){
        this.expression = expression;
        this.side = side;
        Label label = new Label(" ", Main.skin);
        label.setFontScale(BlockCreator.FONT_SCALE);
        setActor(label);
        Main.dragAndDrop.addTarget(new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if(!(source instanceof BlockSource)){
                    return false;
                }
                dragging = true;
                hoverSource = (BlockSource) source;
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                dragging = false;
                dragTime = 0;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                dragging = false;
                dragTime = 0;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(dragging){
            dragTime+=delta;
        }
        if(dragTime>= LabelBlock.HOVER_TIME){
            BaseNode relative = null;
            switch (side){
                case LEFT : relative = expression.getChildren().get(0);break;
                case RIGHT : relative = expression.getChildren().get(expression.getChildren().size-1);break;
            }
            hoverSource.move(relative, side);
            dragging = false;
            dragTime = 0;
        }
    }
}
