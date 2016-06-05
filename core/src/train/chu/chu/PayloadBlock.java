package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by Da-Jin on 4/9/2016.
 */
public class PayloadBlock extends HorizontalGroup {

    public PayloadBlock(Block b){
        float firstParentScale = ScaleUtils.getTrueScale(b);
        this.setScale(firstParentScale);
        //Iterate through children and add them to the clone group
        for (Actor a : b.getChildren()) {
            if (a instanceof Block) {
                //If the child is a block class, ask that nested block to duplicate too
                PayloadBlock childDupe = new PayloadBlock((Block) a);
                childDupe.setScale(1f);
                this.addActor(childDupe);
            } else {
                //Otherwise, it's a label, so cast it and get access to text and style
                Label aLabel = (Label) a;
                //Create a new Label instance, and give it the same properties as the old one
                Label dupe = new Label(aLabel.getText(), aLabel.getStyle());
                dupe.setFontScale(aLabel.getFontScaleX());
                dupe.setColor(aLabel.getColor());
                //And add it to the clone group, of course
                this.addActor(dupe);
            }
        }
        pack();
    }
}
