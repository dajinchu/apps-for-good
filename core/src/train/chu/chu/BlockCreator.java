package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import train.chu.chu.model.BaseNode;
import train.chu.chu.model.Model;

/**
 * Created by Arun on 4/6/2016.
 */
public class BlockCreator {
    public static final float FONT_SCALE = 0.5f;
    /**
     * Takes a string and a skin and returns a block with a label attached
     *
     **/
    public static LabelBlock BlockCreator(BaseNode node, Model model){

        String name=node.getData();

        //Creates Block
        LabelBlock block;
        switch(name){
            default:
                block = new LabelBlock(node,model);
        }
        block.setTouchable(Touchable.enabled);
        //Creates Label
        Label second = new Label(name,Main.skin);
        second.setFontScale(FONT_SCALE);
        second.setTouchable(Touchable.disabled);
        second.setColor(Color.BLACK);
        block.setActor(second);
        //Returns Block
        return block;
    }

}
