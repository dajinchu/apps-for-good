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

    public Expression(final ExpressionNode exp) {
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
                PayloadBlock actor = new PayloadBlock(row);
                actor.setScale(1);
                dragActor.addActor(actor);
                Label l = new Label(result.getText().toString(), Main.skin);
                l.setColor(Color.BLACK);
                l.setFontScale(.5f);
                dragActor.addActor(l);
                payload.setDragActor(dragActor);

                float scale = ScaleUtils.getTrueScale(Expression.this);
                Main.dragAndDrop.setDragActorPosition(-dragActor.getWidth()*scale/2,
                        -dragActor.getHeight()*scale/2+dragActor.getHeight()+result.getHeight()*scale);

                dragActor.setScale(scale);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                Gdx.app.log("Expression",event.getStageX()+" , "+event.getStageY());
                exp.move((event.getStageX()-getParent().getX())/getParent().getScaleX(),
                        (event.getStageY()-getParent().getY())/getParent().getScaleX()+result.getHeight());
            }
        };
        Main.dragAndDrop.addSource(source);
    }

    public void setResult(String result) {
        this.result.setText(result);
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