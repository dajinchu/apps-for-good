package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * Created by Da-Jin on 5/20/2016.
 */
public class GhostBlock extends Container<Label>{
    private final HorizontalGroup expression;
    private final MoveCommand.Side side;
    private boolean dragging = false;
    private double dragTime;
    private Block hoverActor;

    public GhostBlock(MoveCommand.Side side, HorizontalGroup expression){
        this.expression = expression;
        this.side = side;
        Label label = new Label("g", Main.skin);
        label.setFontScale(BlockCreator.FONT_SCALE);
        setActor(label);
        Main.dragAndDrop.addTarget(new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if(!(source.getActor() instanceof Block)){
                    return false;
                }
                dragging = true;
                hoverActor = (Block)source.getActor();
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
        if(dragTime>=Block.HOVER_TIME){
            Block relative = null;
            switch (side){
                case LEFT : relative = (Block) expression.getChildren().get(0);break;
                case RIGHT : relative = (Block) expression.getChildren().get(expression.getChildren().size-1);break;
            }
            if(relative != hoverActor){
                hoverActor.moveRelative(relative, side);
            }
            dragging = false;
            dragTime = 0;
        }
    }
}
