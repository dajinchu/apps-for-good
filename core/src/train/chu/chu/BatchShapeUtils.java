package train.chu.chu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Da-Jin on 4/9/2016.
 * For Drawing shapes more efficiently.
 */
public class BatchShapeUtils {

    private static TextureRegion dot = new TextureRegion(new Texture("dot.png"));

    private static float len, dx, dy, dist, rad;
    private static Vector2 vector2 = new Vector2();

    public static void drawOutline(Batch batch, Actor actor, int thickness){
        drawDashedRectangle(batch, actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight(), thickness);
    }

    public static void drawDashedRectangle(Batch batch, float x, float y, float w, float h, int thickness){
        drawDashedLine(batch, x,   y,   x+w, y,   thickness);
        drawDashedLine(batch, x+w, y,   x+w, y+h, thickness);
        drawDashedLine(batch, x+w, y+h, x,   y+h, thickness);
        drawDashedLine(batch, x,   y+h, x,   y,   thickness);
    }

    public static void drawDashedLine(Batch batch, float x1, float y1, float x2, float y2, int thickness){
        vector2.set(x2-x1,y2-y1);
        len = vector2.len();
        for(float i = 0; i < len; i+=20){
            vector2.clamp(len - i - 10, len - i - 10);
            batch.draw(dot, x1+vector2.x, y1+vector2.y, 0, 0, 10, thickness, 1, 1, vector2.angle());
        }
    }

    public static void drawLine(Batch batch, float x1, float y1, float x2, float y2, int thickness){
        dx = x2-x1;
        dy = y2-y1;
        dist = (float)Math.sqrt(dx*dx + dy*dy);
        rad = (float)Math.atan2(dy, dx);
        batch.draw(dot, x1, y1, 0,0, dist, thickness, 1, 1, (float) Math.toDegrees(rad));
    }

}
