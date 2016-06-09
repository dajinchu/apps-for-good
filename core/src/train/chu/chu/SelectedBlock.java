package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.Model;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 6/8/2016.
 */
public class SelectedBlock extends HorizontalGroup implements Block{

    Model model;

    public SelectedBlock(Model model){
        this.model = model;
        Main.dragAndDrop.addSource(new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                Payload payload = new Payload();

                PayloadBlock dragActor = new PayloadBlock(SelectedBlock.this);
                payload.setDragActor(dragActor);
                float scale = ScaleUtils.getTrueScale(SelectedBlock.this);
                Main.dragAndDrop.setDragActorPosition(-dragActor.getWidth()*scale/2,
                        -dragActor.getHeight()*scale/2+dragActor.getHeight());
                return payload;
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
