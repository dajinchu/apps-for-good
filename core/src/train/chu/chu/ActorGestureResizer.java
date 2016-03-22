package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

/**
 * Created by Da-Jin on 12/20/2014.
 */
public class ActorGestureResizer extends ActorGestureListener {

    private final Vector2 worldSize;
    private final Actor actor;
    private final Camera cam;
    Vector3 delta = new Vector3();
    float previousDistance, previousInitial;

    private Vector3 previousPointer1= new Vector3(), previousInitial2 = new Vector3(), previousInitial1 = new Vector3(), previousPointer2 = new Vector3();

    public ActorGestureResizer(Camera stageCamera, Actor actor, Vector2 worldSize){
        //Camera is just used for unprojecting touch events
        this.cam = stageCamera;
        this.actor = actor;
        this.worldSize = worldSize;
        clamp();
    }

    //Moving camera around, zoomCam and panCam are calcs used by pinch gesture
    public void zoomCam(float initialDistance, float distance) {
        if(previousInitial!=initialDistance){
            //Starting a new gesture
            //Just make previousDistance initial to avoid jumpiness from the previous gesture's values carrying over
            previousDistance=initialDistance;
        }
        actor.setScale(actor.getScaleX()*distance/previousDistance);
        clamp();
        Gdx.app.log("Zoom", initialDistance+" "+distance);
        //Set previous, as this frame has ended
        previousDistance = distance;
        previousInitial = initialDistance;
    }
    public void panCam(float deltaX, float deltaY) {
        actor.moveBy(-deltaX, deltaY);
        clamp();
    }
    //Pinch is for panning/zooming camera around
    @Override
    public void pinch(InputEvent event, Vector2 i1, Vector2 i2, Vector2 f1, Vector2 f2) {
        //Vectors are using world coordinates from stage, they need to be in screen coordinates.
        // Cam transformations should act the same no matter how zoomed in or transformed the cam is already,
        // so we use absolute screen coords to keep it independent of transforms.
        Vector3 initialPointer1 = new Vector3(i1,0);
        Vector3 initialPointer2 = new Vector3(i2,0);
        Vector3 pointer1 = new Vector3(f1,0);
        Vector3 pointer2 = new Vector3(f2,0);
        //unproject using cam to get the screen coordinates.
        cam.unproject(initialPointer1);
        cam.unproject(initialPointer2);
        cam.unproject(pointer1);
        cam.unproject(pointer2);
        try{
            Gdx.app.log("Pinch before",initialPointer1+" "+initialPointer2+" "+ previousPointer1+" "+previousPointer2+" "+pointer1+" "+pointer2+" "+delta.x+" "+delta.y);
        }catch (NullPointerException e){}
        if(!previousInitial1.equals(initialPointer1)&&!previousInitial2.equals(initialPointer2)){
            //Starting a new gesture
            //Just make previous initial to avoid jumpiness from the previous gesture's values carrying over
            previousPointer1 = initialPointer1;
            previousPointer2 = initialPointer2;
        }

        zoomCam(initialPointer1.cpy().dst(initialPointer2), pointer1.cpy().dst(pointer2));
        delta = previousPointer1.cpy().add(previousPointer2).scl(.5f).sub(pointer1.cpy().add(pointer2).scl(.5f));
        panCam(delta.x, delta.y);
        Gdx.app.log("Pinch after", initialPointer1+" "+initialPointer2+" "+ previousPointer1+" "+previousPointer2+" "+pointer1+" "+pointer2+" "+delta.x+" "+delta.y);

        previousInitial1 = initialPointer1.cpy();
        previousInitial2 = initialPointer2.cpy();
        previousPointer1 = pointer1.cpy();
        previousPointer2 = pointer2.cpy();
        return;
    }

    //Should clamp after moving camera, it keeps cam within bounds
    public void clamp(){//TODO clamp
        /*cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, worldSize.x*2 / cam.viewportWidth);
        cam.position.x = MathUtils.clamp(cam.position.x, 0, worldSize.x);
        cam.position.y = MathUtils.clamp(cam.position.y, 0, worldSize.y);
        cam.update();*/
        //Gdx.app.log("GESTURES",cam.viewportWidth+" "+cam.zoom);
    }
}
