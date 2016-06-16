package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.BlankNode;
import train.chu.chu.model.Model;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 6/8/2016.
 */
public class SelectedBlock extends HorizontalGroup {

    Model model;

    public SelectedBlock(final Model model){
        this.model = model;
        Main.dragAndDrop.addSource(new BlockSource(this, model) {
            @Override
            protected WidgetGroup getDupe() {
                return new PayloadBlock(SelectedBlock.this);
            }

            @Override
            public void move(BaseNode to, Side side) {
                model.moveSelected(to,side);
            }

            @Override
            public void moveInto(BlankNode into) {
                model.moveSelectedInto(into);
            }

            @Override
            public void trash() {
                model.removeSelected();
            }
        });
    }
}
