package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Visual representation of insertion point
 */
public class Cursor extends Actor {
    private static final Label LABEL = new Label(" ", Main.skin);
    private float visibleCount = 0;
    private static final float FLASH_SECONDS=.5f;

    public Cursor() {
        setColor(Color.BLACK);
        LABEL.setFontScale(BlockCreator.FONT_SCALE);
        this.setHeight(LABEL.getMinHeight()*.7f);
        setWidth(0);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        visibleCount+=delta;
        if(visibleCount>FLASH_SECONDS){
            setVisible(!isVisible());
            visibleCount = 0;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        BatchShapeUtils.drawLine(batch,getX()+1,getY(),getX()+1,getY()+getHeight(),2);
    }
}
