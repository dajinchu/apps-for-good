package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import train.chu.chu.model.ExpressionNode;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 3/25/2016.
 */
public class Expression extends Stack {

    private final Container<Button> back;
    private final VerticalGroup content;
    private final ExpressionNode expressionNode;
    private final HorizontalGroup resultGroup;
    HorizontalGroup row;
    private Label result;
    NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(new Texture("card.png"),15,15,15,15));

    private class ExpressionSource extends DragAndDrop.Source implements Trashable {
        public ExpressionSource(Actor actor) {
            super(actor);
        }

        @Override
        public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
            setVisible(false);
            DragAndDrop.Payload payload = new DragAndDrop.Payload();
            WidgetGroup dragActor = new WidgetGroup();
            Stack dragStack = new Stack();
            PayloadBlock actor = new PayloadBlock(row);
            actor.setScale(1);
            HorizontalGroup g = new HorizontalGroup();
            g.addActor(new ExternalZone(Side.LEFT,expressionNode));
            g.addActor(actor);
            g.addActor(new ExternalZone(Side.LEFT,expressionNode));
            PayloadBlock resultClone = new PayloadBlock(resultGroup);
            resultClone.setScale(1);
            VerticalGroup content = new VerticalGroup();
            content.addActor(g);
            content.addActor(resultClone);
            content.padBottom(10);
            Container<Button> back = new Container<>(new Button(patch));
            dragStack.add(back);
            dragStack.add(content);
            dragActor.addActor(dragStack);
            payload.setDragActor(dragActor);

            float scale = ScaleUtils.getTrueScale(Main.calcZone);

            back.size(content.getPrefWidth(),content.getPrefHeight());
            dragActor.setScale(scale);

            Main.dragAndDrop.setDragActorPosition(-content.getPrefWidth()*scale/2,
                    -content.getPrefHeight()*scale/2);
            dragStack.pack();

            return payload;
        }

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
            setVisible(true);
            Gdx.app.log("Expression",event.getStageX()+" , "+event.getStageY());
            Vector2 pos = ScaleUtils.positionWithin(Main.calcZone, event.getStageX(), event.getStageY());
            expressionNode.move(pos.x,pos.y);
        }
        @Override
        public void trash(){
            expressionNode.remove();
        }
    };

    public Expression(final ExpressionNode exp) {
        this.expressionNode = exp;
        content = new VerticalGroup();

        final HorizontalGroup rowWithGhost = new HorizontalGroup();

        row = new HorizontalGroup(){
            @Override
            protected void childrenChanged() {
                super.childrenChanged();
                Expression.this.validate();
            }
        };
        Actor leftghost = new ExternalZone(Side.LEFT, exp);
        Actor rightghost = new ExternalZone(Side.RIGHT, exp);

        rowWithGhost.addActor(leftghost);
        rowWithGhost.addActor(row);
        rowWithGhost.addActor(rightghost);

        resultGroup = new HorizontalGroup();
        result = new Label("", Main.skin);
        result.setColor(Color.GRAY);
        result.setFontScale(0.25f);
        Label equalSign = new Label("=", Main.skin);
        equalSign.setColor(Color.GRAY);
        equalSign.setFontScale(0.25f);

        resultGroup.addActor(equalSign);
        resultGroup.addActor(result);
        resultGroup.setTouchable(Touchable.disabled);

        content.addActor(rowWithGhost);
        content.addActor(resultGroup);
        content.padBottom(10);

        back = new Container<>(new Button(patch));
        this.add(back);

        this.add(content);

        DragAndDrop.Source source = new ExpressionSource(back);
        Main.dragAndDrop.addSource(source);
    }

    @Override
    public void layout() {
        pack();
        back.size(content.getPrefWidth(),content.getPrefHeight());
        super.layout();
        Gdx.app.log("expression","layout"+ content.getPrefWidth()+","+content.getPrefHeight()+"  "+getWidth()+","+getHeight());
    }


    public void setResult(String result) {
        this.result.setText(result);
    }

}