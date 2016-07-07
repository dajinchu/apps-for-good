package train.chu.chu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * Created by Da-Jin on 4/9/2016.
 */
public class PayloadBlock extends HorizontalGroup {

    public PayloadBlock(WidgetGroup b){
        float firstParentScale = ScaleUtils.getTrueScale(b);
        this.setScale(firstParentScale);
        //Iterate through children and add them to the clone group
        for (Actor a : b.getChildren()) {
            if (a instanceof SelectedBlock) {
                //If the child is a block class, ask that nested block to duplicate too
                PayloadBlock childDupe = new PayloadBlock((WidgetGroup) a);
                childDupe.setScale(1f);
                this.addActor(childDupe);
            }
            if(a instanceof LabelBlock){
                this.addActor(cloneLabel(((LabelBlock) a).getActor()));
            }
            if(a instanceof Label){
                this.addActor(cloneLabel((Label) a));
            }
        }
        pack();
    }

    private Label cloneLabel(Label original) {
        //Create a new Label instance, and give it the same properties as the old one
        Label dupe = new Label(original.getText(), original.getStyle());
        dupe.setFontScale(original.getFontScaleX());
        dupe.setColor(original.getColor());
        //And add it to the clone group, of course
        return dupe;
    }
}
