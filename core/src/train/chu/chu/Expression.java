package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Created by Da-Jin on 3/25/2016.
 */
public class Expression extends VerticalGroup{

    private Block row;
    private Label result;

    public Expression() {
        row = new Block(){
            @Override
            protected void childrenChanged() {
                super.childrenChanged();
                try {
                    result.setText(""+new ExpressionBuilder(row.getChildrenString()).build().evaluate());
                }catch(Exception e){
                    result.setText("false");
                }
            }
        };
        result = new Label("",Main.skin);
        result.setColor(Color.BLACK);
        result.setFontScale(0.25f);
        row.addActor(BlockCreator.BlockCreator("5"));
        this.addActor(row);
        this.addActor(result);
        row.setTargetable(false);
        row.setDraggable(false);
    }

    public void addBlock(Block b){
        new AddCommand(b, row).execute();
    }
    public Array<Block> getBlocks(){
        SnapshotArray<Actor> children = row.getChildren();
        Array<Block> ret = new Array<>();
        for(Actor a : children){
            if(a instanceof Block){
                ret.add((Block) a);
            }
        }
        return ret;
    }
}
