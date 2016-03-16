package train.chu.chu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    private OrthographicCamera camera;
    private ShapeRenderer shapes;

    public static final int GRID_ROWS = 20;
    public static final int GRID_COLS = 40;
    public static final int GRID_SIZE = 50;
    private Stage stage;
    private Skin skin;
    private DragAndDrop dragAndDrop;
    private HorizontalGroup row;

    @Override
	public void create () {
        //Instantiate batch+shapes for drawing, camera for transforming, and img for temporary use
        //note that Stage has its own camera. this one is different
		batch = new SpriteBatch();
        shapes = new ShapeRenderer();
		img = new Texture("badlogic.jpg");
        camera = new OrthographicCamera();

        //Generate bitmap font from TrueType Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        BitmapFont roboto = generator.generateFont(parameter);
        generator.dispose();

        //Instantiate Stage for scene2d management
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //Load skin with images and styles for use in scene2d ui elements
        skin = new Skin();
        skin.add("default", new Label.LabelStyle(roboto, Color.WHITE));
        skin.add("badlogic", new Texture("badlogic.jpg"));
        skin.add("tmp", new Texture("tmp.png"));

        //row is the outermost ui element for the sandbox, it holds all the blocks
        //Really, it should be a block too, but all blocks are drag-and-drop-able, and can't be nested
        // so that wouldn't work.
        row = new HorizontalGroup();
        row.setBounds(0,0,300,300);
        stage.addActor(row);

        //Instantiate the DragAndDrop manager
        dragAndDrop = new DragAndDrop();

        //Instantiate labels and put them each in a block. Add each block to row
        Label first = new Label("*",skin);
        first.setColor(Color.BLACK);
        Block block = new Block(dragAndDrop);
        block.addActor(first);
        row.addActor(block);

        block = new Block(dragAndDrop);
        Label second = new Label("8",skin);
        second.setColor(Color.BLACK);
        block.addActor(second);
        row.addActor(block);

        Label num = new Label("132",skin);
        num.setColor(Color.BLACK);
        block = new Block(dragAndDrop);
        block.addActor(num);

        row.addActor(block);
	}

    @Override
    public void resize(int width, int height) {
        //Handle resize. This is still important on static windows (eg. Android) because it is
        // called once in the beginning of the app lifecycle, so instead of handling sizing in create,
        // it's clearer to do it here, and avoids doing it twice (create and resize are both called initially)
        camera.setToOrtho(false,width,height);
        //set stage viewport
        stage.getViewport().update(width,height);
        //get stage camera, so we can mess with it a bit
        OrthographicCamera stageCamera = (OrthographicCamera) stage.getCamera();
        //zoom in the camera, and put it where it can see the main row
        stageCamera.zoom=.7f;
        stageCamera.setToOrtho(false,width,height);
    }

    @Override
	public void render () {
        //Wipe the screen clean with a white clear color
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update the camera. just needs to happen each frame in case camera got moved
        camera.update();

        //Batch and Shapes need the camera to transform their coordinates accordingly.
        // ie. drawing at (x,y) can't just happen directly at those pixels, batch/shapes need to
        // transform (x,y) based on the camera, so that they draw things to appear as if from the perspective of camera
        batch.setProjectionMatrix(camera.combined);
        shapes.setProjectionMatrix(camera.combined);

        //Scene2d. Step forward the world and draw the scene
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        stage.setDebugAll(true);


        //Draw the grid lines.
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
        //When the app is destroyed, don't leave any memory leaks behind
        batch.dispose();
        shapes.dispose();
        stage.dispose();
        skin.dispose();
    }
}
