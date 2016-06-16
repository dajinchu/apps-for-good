package train.chu.chu;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.BlankNode;
import train.chu.chu.model.Model;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 6/15/2016.
 */
public abstract class BlockSource extends DragAndDrop.Source implements Trashable{
    private final Model model;
    private WidgetGroup dragActor;

    public BlockSource(WidgetGroup actor, Model model) {
        super(actor);
        this.model = model;
    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        getActor().setVisible(false);

        DragAndDrop.Payload payload = new DragAndDrop.Payload();

        dragActor = getDupe();
        payload.setDragActor(dragActor);
        float scale = ScaleUtils.getTrueScale(dragActor);
        Main.dragAndDrop.setDragActorPosition(-dragActor.getWidth()*scale/2,
                -dragActor.getHeight()*scale/2+dragActor.getHeight());
        return payload;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        if(getActor().getStage()==null)return;
        getActor().setVisible(true);
        if(getActor().getStage().hit(event.getStageX(),event.getStageY(),true)==null){
            Vector2 pos = ScaleUtils.positionWithin(Main.calcZone, event.getStageX(), event.getStageY());
            BlankNode blank = model.addExpression(pos.x, pos.y-dragActor.getPrefHeight()/4);
            moveInto(blank);
        }
    }

    protected abstract WidgetGroup getDupe();
    public abstract void move(BaseNode to, Side side);
    public abstract void moveInto(BlankNode into);
}
