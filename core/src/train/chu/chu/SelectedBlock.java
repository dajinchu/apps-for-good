package train.chu.chu;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.BlankNode;
import train.chu.chu.model.Model;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 6/8/2016.
 */
public class SelectedBlock extends HorizontalGroup implements Block{

    Model model;

    public SelectedBlock(final Model model){
        this.model = model;
        Main.dragAndDrop.addSource(new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                setVisible(false);

                Payload payload = new Payload();

                PayloadBlock dragActor = new PayloadBlock(SelectedBlock.this);
                payload.setDragActor(dragActor);
                float scale = ScaleUtils.getTrueScale(SelectedBlock.this);
                Main.dragAndDrop.setDragActorPosition(-dragActor.getWidth()*scale/2,
                        -dragActor.getHeight()*scale/2+dragActor.getHeight());
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, DragAndDrop.Target target) {
                if(getStage()==null)return;
                setVisible(true);
                if(getStage().hit(event.getStageX(),event.getStageY(),true)==null){
                    Vector2 pos = ScaleUtils.positionWithin(Main.calcZone, event.getStageX(), event.getStageY());
                    BlankNode blank = model.addExpression(pos.x, pos.y-getMinHeight()/4);
                    model.moveSelectedInto(blank);
                }
            }
        });
    }

    @Override
    public void move(BaseNode to, Side side) {
        model.moveSelected(to,side);
    }

    @Override
    public void trash() {
        model.removeSelected();
    }
}
