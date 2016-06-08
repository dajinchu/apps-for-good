package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import train.chu.chu.model.Node;

/**
 * Created by Arun on 4/6/2016.
 */
public class BlockCreator {
    public static final float FONT_SCALE = 0.5f;
    /**
     * Takes a string and a skin and returns a block with a label attached
     * @param node the node for the block
     * @param skin the skin
     * @return Block, the block
     */
    public static Block BlockCreator(Node node, Skin skin){

        String name=node.getData();

        //Creates Block
        Block block;
        switch(name){
            default:
                block = new Block(node);
        }
        block.setTouchable(Touchable.enabled);
        //Creates Label
        Label second = new Label(name,skin);
        second.setFontScale(FONT_SCALE);
        second.setTouchable(Touchable.disabled);
        second.setColor(Color.BLACK);
        block.addActor(second);
        if(node.isSelected())second.setColor(Color.BLUE);
        //Returns Block
        return block;
    }

}
