package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

import train.chu.chu.model.ExpressionNode;
import train.chu.chu.model.Side;

/**
 * Created by Da-Jin on 3/25/2016.
 */
public class Expression extends VerticalGroup {

    HorizontalGroup row;
    private Label result;

    public Expression(ExpressionNode exp) {
        this.setPosition(exp.getX(),exp.getY());

        HorizontalGroup rowWithGhost = new HorizontalGroup();

        row = new HorizontalGroup();
        Actor leftghost = new GhostBlock(Side.LEFT, row);
        Actor rightghost = new GhostBlock(Side.RIGHT, row);

        rowWithGhost.addActor(leftghost);
        rowWithGhost.addActor(row);
        rowWithGhost.addActor(rightghost);

        result = new Label("", Main.skin);
        result.setColor(Color.BLACK);
        result.setFontScale(0.5f);
        this.addActor(row);
        this.addActor(result);

        DragAndDrop.Source source = new DragAndDrop.Source(result) {

            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                VerticalGroup dragActor = new VerticalGroup();
                dragActor.addActor(new PayloadBlock(row));
                Label l = new Label(result.getText().toString(), Main.skin);
                l.setColor(Color.BLACK);
                dragActor.addActor(l);
                dragActor.setScale(.25f);
                payload.setDragActor(dragActor);

                float scale = ScaleUtils.getTrueScale(Expression.this);
                Main.dragAndDrop.setDragActorPosition(-Expression.this.getWidth()*scale/2,
                        -Expression.this.getPrefHeight()*scale/2+Expression.this.getPrefHeight());
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                Gdx.app.log("Expression",event.getStageX()+" , "+event.getStageY());
                Expression.this.setPosition(event.getStageX()-getParent().getX(),event.getStageY()-getParent().getY());
            }
        };
        Main.dragAndDrop.addSource(source);
    }


    public Array<Block> getBlocks() {
        SnapshotArray<Actor> children = row.getChildren();
        Array<Block> ret = new Array<>();
        for (Actor a : children) {
            if (a instanceof Block) {
                ret.add((Block) a);
            }
        }
        return ret;
    }
}