package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.BlankNode;
import train.chu.chu.model.Model;
import train.chu.chu.model.Node;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 6/9/2016.
 */
public class KeypadButton extends Container<TextButton> {
    private final Model model;
    private final String text;
    private Node insert;

    public KeypadButton(final String text, final Model model){
        this.model = model;
        this.text = text;
        TextButton tmp = new TextButton(text, Main.skin);
        tmp.getLabel().setFontScale(.36f);
        setTouchable(Touchable.enabled);
        tmp.setTouchable(Touchable.disabled);
        setActor(tmp);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float z, float y) {
                if(model.getExpressions().size==0){
                    model.addExpression(50,50);

                }
                model.addBlock(text,model.getExpressions().first());
            }
        });
        Main.dragAndDrop.addSource(new BlockSource(this,model){
            @Override
            protected WidgetGroup getDupe() {
                KeypadButton.this.setVisible(true);

                Label clone = new Label(text, Main.skin);
                clone.setFontScale(BlockCreator.FONT_SCALE);
                clone.setColor(Color.BLACK);
                return new Container<>(clone);
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
            public void moveInto(BlankNode into) {

                model.addBlock(text,into.getExpression()).moveInto(into);
            }

            @Override
            public void trash() {
                //Do nothing
            }
        });
    }
}
