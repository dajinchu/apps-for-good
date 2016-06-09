package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.*;

import train.chu.chu.model.SelectionContainerNode;

/**
 * Created by Da-Jin on 6/8/2016.
 */
public class SelectedBlock extends HorizontalGroup implements Block{

    private final SelectionContainerNode node;

    public SelectedBlock(SelectionContainerNode node){
        this.node = node;
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
    public SelectionContainerNode getNode() {
        return node;
    }
}
