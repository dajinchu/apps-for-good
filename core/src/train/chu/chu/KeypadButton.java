package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.Model;
import train.chu.chu.model.Node;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 6/9/2016.
 */
public class KeypadButton extends Container<TextButton> implements Block{
    private final Model model;
    private final String text;
    private Node insert;

    public KeypadButton(final String text, final Model model){
        this.model = model;
        this.text = text;
        TextButton tmp = new TextButton(text, Main.skin);
        tmp.getLabel().setFontScale(.36f);
        setActor(tmp);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float z, float y) {
                model.addBlock(text,model.getExpressions().first());
            }
        });
        Main.dragAndDrop.addSource(new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Label clone = new Label(text, Main.skin);
                clone.setFontScale(BlockCreator.FONT_SCALE);
                clone.setColor(Color.BLACK);
                Container<Label> dragActor = new Container<>(clone);
                payload.setDragActor(dragActor);
                float scale = ScaleUtils.getTrueScale(KeypadButton.this);
                Main.dragAndDrop.setDragActorPosition(-dragActor.getWidth()*scale/2,
                        -dragActor.getHeight()*scale/2+dragActor.getHeight());
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                insert = null;
            }
        });
    }
    @Override
    public void move(BaseNode to, Side side) {
        //This means the payload has been dragged somewhere
        if(insert ==null){
            insert = model.insertBlock(text, to.getExpression(),to,side);
        } else {
            insert.move(to,side);
        }
    }

    @Override
    public void trash() {
        //Do nothing
    }
}
