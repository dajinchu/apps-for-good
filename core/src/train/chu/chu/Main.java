package train.chu.chu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    private OrthographicCamera camera;
    private ShapeRenderer shapes;

    public static final int GRID_ROWS = 20;
    public static final int GRID_COLS = 40;
    public static final int GRID_SIZE = 50;
    private Stage stage;
    private DragAndDrop dragAndDrop;

    @Override
	public void create () {
		batch = new SpriteBatch();
        shapes = new ShapeRenderer();
		img = new Texture("badlogic.jpg");
        camera = new OrthographicCamera();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        final Skin skin = new Skin();
        skin.add("default", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        skin.add("badlogic", new Texture("badlogic.jpg"));

        final Image sourceImage = new Image(skin, "badlogic");
        sourceImage.setBounds(50, 125, 100, 100);
        stage.addActor(sourceImage);

        Image validTargetImage = new Image(skin, "badlogic");
        validTargetImage.setBounds(200, 50, 100, 100);
        stage.addActor(validTargetImage);


        dragAndDrop = new DragAndDrop();
        dragAndDrop.addSource(new Source(sourceImage) {
            public Payload dragStart (InputEvent event, float x, float y, int pointer) {
                this.getActor().setVisible(false);
                Payload payload = new Payload();
                payload.setObject("Some payload!");

                payload.setDragActor(new Image(skin,"badlogic"));

                Label validLabel = new Label("Some payload!", skin);
                validLabel.setColor(0, 1, 0, 1);
                payload.setValidDragActor(validLabel);

                Label invalidLabel = new Label("Some payload!", skin);
                invalidLabel.setColor(1, 0, 0, 1);
                payload.setInvalidDragActor(invalidLabel);

                return payload;
            }
        });
        dragAndDrop.addTarget(new Target(validTargetImage) {
            private boolean beganDrag = false;//Has the dragging process begun?
            private float initX, initY;

            //Center rect is the detection area for getting out of the way, or merging blocks
            private Rectangle centerRect = new Rectangle(
                    getActor().getWidth()*.3f,getActor().getHeight()*.3f,
                    getActor().getWidth()*.4f,getActor().getHeight()*.4f);

            public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
                //Something is being dragged over this target
                if(!beganDrag){
                    //Haven't begun drag before this, so this is the first received coords in this drag
                    beganDrag = true;
                    //Record initx and y so if centerRect is entered, we know which way to move.
                    initX=x;
                    initY=y;
                }
                System.out.println(Gdx.input.getX()-payload.getValidDragActor().getX());
                if(centerRect.contains(x,y)){
                    int absX = (int)Math.abs(initX-x);
                    int absY = (int)Math.abs(initY-y);
                    int moveX=0, moveY=0;
                    if(absX>=absY){
                        moveX = (int)Math.signum(initX-x);
                    }else{
                        moveY = (int)Math.signum(initY-y);
                    }
                    move(payload.getDragActor(),moveX,moveY);
                }
                getActor().setColor(Color.GREEN);
                return true;
            }
            private void move(Actor mover, int xdir, int ydir){
                System.out.println(xdir+","+ydir);
                //Mover just entered our centerRect, and making room for it.
                getActor().moveBy(mover.getWidth()*xdir,mover.getHeight()*ydir);
            }

            public void reset (DragAndDrop.Source source, Payload payload) {
                getActor().setColor(Color.WHITE);
                beganDrag = false;
            }

            public void drop (Source source, Payload payload, float x, float y, int pointer) {
                System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
            }
        });
	}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width,height);
        stage.getViewport().update(width,height,true);
    }

    @Override
	public void render () {
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapes.setProjectionMatrix(camera.combined);

        //Scene2d
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        stage.setDebugAll(true);

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(.88f, .88f, .88f, 1);
        //Vertical grid lines
        for(int i = 0; i < GRID_COLS; i++){
            shapes.line(i*GRID_SIZE,0,i*GRID_SIZE,GRID_ROWS*GRID_SIZE);
        }
        //Horizontal grid lines
        for(int i = 0; i < GRID_ROWS; i++){
            shapes.line(0,i*GRID_SIZE,GRID_COLS*GRID_SIZE,i*GRID_SIZE);
        }
        shapes.end();
	}

    public void dispose () {
        stage.dispose();
    }
}
