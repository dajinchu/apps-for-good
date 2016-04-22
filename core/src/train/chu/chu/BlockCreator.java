package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Arun on 4/6/2016.
 */
public class BlockCreator {

    /**
     * Takes a string and a skin and returns a block with a label attached
     * @param str the str for the block
     * @param skin the skin
     * @return Block, the block
     */
    public static Block BlockCreator(String str, Skin skin){

        String name=ButtonCreator.generateString(str);

        //Creates Block
        Block block;
        block = new Block();
        block.setTouchable(Touchable.enabled);
        //Creates Label
        Label second = new Label(name,skin);
        second.setTouchable(Touchable.disabled);
        second.setColor(Color.BLACK);
        block.addActor(second);

        //Returns Block
        return block;
    }

}
