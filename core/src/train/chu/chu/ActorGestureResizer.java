package train.chu.chu;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

/**
 * Created by Da-Jin on 12/20/2014.
 */
public class ActorGestureResizer extends ActorGestureListener {

    private final Vector2 worldSize;
    private final Actor actor;
    private final Camera cam;
    private final Stage stage;
    Vector3 delta = new Vector3();
    float previousDistance, previousInitial;
    private boolean validPinch = false;

    //Save RAM by instantiating all Vectors as fields and setting them, rather than instantiating new ones
    private Vector3 previousPointer1= new Vector3(), previousInitial2 = new Vector3(), previousInitial1 = new Vector3(), previousPointer2 = new Vector3();
    private Vector3 initialPointer1=new Vector3(), initialPointer2=new Vector3(),pointer1=new Vector3(),pointer2=new Vector3();
    private Vector3 tmp1 =  new Vector3(), tmp2 = new Vector3();

    public ActorGestureResizer(Stage stage, Actor actor, Vector2 worldSize){
        //Camera is just used for unprojecting touch events
        this.cam = stage.getCamera();
        this.actor = actor;
        this.stage = stage;
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
        initialPointer1.set(i1,0);
        initialPointer2.set(i2,0);
        pointer1.set(f1,0);
        pointer2.set(f2,0);
        //unproject using cam to get the screen coordinates.
        cam.unproject(initialPointer1);
        cam.unproject(initialPointer2);
        cam.unproject(pointer1);
        cam.unproject(pointer2);
        if(!previousInitial1.equals(initialPointer1)&&!previousInitial2.equals(initialPointer2)){
            //Starting a new gesture
            //Just make previous initial to avoid jumpiness from the previous gesture's values carrying over
            previousPointer1 = initialPointer1;
            previousPointer2 = initialPointer2;
            //Get actors, using i1 & i2 to access stage coords
            Actor a1 = stage.hit(i1.x,i1.y,true);
            Actor a2 = stage.hit(i2.x,i2.y,true);
            validPinch = isValidActor(a1) && isValidActor(a2);
        }

        if(!validPinch)return;

        zoomCam(initialPointer1.dst(initialPointer2), pointer1.dst(pointer2));
        tmp1.set(previousPointer1);
        tmp2.set(pointer1);
        delta = tmp1.add(previousPointer2).scl(.5f).sub(tmp2.add(pointer2).scl(.5f));
        panCam(delta.x, delta.y);

        previousInitial1.set(initialPointer1);
        previousInitial2.set(initialPointer2);
        previousPointer1=pointer1.cpy();//TODO figure out why it doesn't work when these are .set()
        previousPointer2=pointer2.cpy();
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

    private boolean isValidActor(Actor a){
        return a==null||a instanceof LabelBlock;
    }
}
