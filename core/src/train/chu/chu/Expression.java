package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Created by Da-Jin on 3/25/2016.
 */
public class Expression extends VerticalGroup {

    private Block row;
    private Label result;

    public Expression() {
        row = new Block() {
            @Override
            protected void childrenChanged() {
                super.childrenChanged();
                try {
                    result.setText("" + new ExpressionBuilder(row.getChildrenString()).build().evaluate());
                } catch (Exception e) {
                    result.setText("false");
                }
            }
        };
        result = new Label("", Main.skin);
        result.setColor(Color.BLACK);
        result.setFontScale(0.25f);
        row.addActor(BlockCreator.BlockCreator("5"));
        this.addActor(row);
        this.addActor(result);
        row.setTargetable(false);
        row.setDraggable(false);

        DragAndDrop.Source source = new DragAndDrop.Source(result) {

            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                VerticalGroup dragActor = new VerticalGroup();
                dragActor.addActor(row.getDragActor());
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
                Expression.this.setPosition(event.getStageX(),event.getStageY() );
            }
        };
        Main.dragAndDrop.addSource(source);
    }


    public void addBlock(Block b) {
        new AddCommand(b, row).execute();
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
