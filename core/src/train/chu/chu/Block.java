package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * Created by Da-Jin on 3/9/2016.
 */
public class Block extends HorizontalGroup {
    private final DragAndDrop.Target target;

    //Center rect is the detection area for getting out of the way, or merging blocks
    private Rectangle centerRect = new Rectangle();


    public Block(final DragAndDrop dad, final Label child){
        dad.addSource(new DragAndDrop.Source(this) {
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject("Some payload!");

                Block.this.setVisible(false);

                Label draglabel = new Label(child.getText(),child.getStyle());
                draglabel.setColor(0,0,0,1);
                payload.setDragActor(draglabel);
                dad.setDragActorPosition(-(draglabel.getWidth()/2), draglabel.getHeight()/2);

                /*Label validLabel = new Label("valid!", skin);
                    validLabel.setColor(0, 1, 0, 1);
                    payload.setValidDragActor(validLabel);

                    Label invalidLabel = new Label("invalid!", skin);
                    invalidLabel.setColor(1, 0, 0, 1);
                    payload.setInvalidDragActor(invalidLabel);
*/
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                Block.this.setVisible(true);
            }
        });
        target = new DragAndDrop.Target(this) {
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                //Something is being dragged over this target
                if (source.getActor() == Block.this) {
                    System.out.println("Same actor");
                    return false;
                }

                if (centerRect.contains(x, y)) {
                    System.out.println("contained");
                    System.out.println(getParent().swapActor(getActor(), source.getActor()));
                    ((WidgetGroup) getParent()).invalidate();
                }
                System.out.println("drag");
                getActor().setColor(Color.GREEN);
                return true;
            }

            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(Color.BLACK);
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
            }
        };
        dad.addTarget(target);
    }

    @Override
    public void layout() {
        super.layout();
        centerRect.set(
                getWidth() * .3f, getHeight() * .3f,
                getWidth() * .4f, getHeight() * .4f);//TODO getActor unneeded
    }
}
