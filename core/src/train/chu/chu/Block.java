package train.chu.chu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * Created by Da-Jin on 3/9/2016.
 */
public class Block extends HorizontalGroup {

    //Center rect is the detection area for getting out of the way, or merging blocks
    private Rectangle centerRect = new Rectangle();

    public Block(final DragAndDrop dad){
        dad.addSource(new DragAndDrop.Source(this) {
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject("Some payload!");

                Block.this.setVisible(false);

                WidgetGroup dragActor = getDuplicateForDragging();
                dragActor.invalidate();
                dragActor.layout();
                payload.setDragActor(dragActor);
                dad.setDragActorPosition(-x,(dragActor.getPrefHeight()/2-y));


                System.out.println(getChildren().get(0).getName()+": "+-(dragActor.getPrefWidth()/2)+", "+dragActor.getPrefHeight()/2+"  click "+x+", "+y);
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
        dad.addTarget(new DragAndDrop.Target(this) {
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
        });
    }

    private WidgetGroup getDuplicateForDragging(){
        WidgetGroup g = new HorizontalGroup();
        for(Actor a : getChildren()) {
            if (a.getClass() == Block.class) {
                g.addActor(((Block) a).getDuplicateForDragging());
            } else {
                Label aLabel = (Label) a;
                Label dupe = new Label(aLabel.getText(), aLabel.getStyle());
                dupe.setColor(0, 0, 0, 1);
                g.addActor(dupe);
            }
        }
        return g;
    }

    @Override
    public void layout() {
        super.layout();
        centerRect.set(
                getWidth() * .3f, getHeight() * .3f,
                getWidth() * .4f, getHeight() * .4f);
    }
}
