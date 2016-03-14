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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.SnapshotArray;

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
		batch = new SpriteBatch();
        shapes = new ShapeRenderer();
		img = new Texture("badlogic.jpg");
        camera = new OrthographicCamera();

        //Generate font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        BitmapFont roboto = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        skin.add("default", new Label.LabelStyle(roboto, Color.WHITE));
        skin.add("badlogic", new Texture("badlogic.jpg"));
        skin.add("tmp", new Texture("tmp.png"));

        row = new HorizontalGroup();
        row.setBounds(0,0,300,300);
        stage.addActor(row);

        dragAndDrop = new DragAndDrop();

        Label first = new Label("*",skin);
        first.setColor(Color.BLACK);
        Block block = new Block(dragAndDrop, first);
        block.addActor(first);
        row.addActor(block);

        Label second = new Label("8",skin);
        second.setColor(Color.BLACK);
        block = new Block(dragAndDrop, second);
        block.addActor(second);
        row.addActor(block);

        Label num = new Label("132",skin);
        num.setColor(Color.BLACK);
        block = new Block(dragAndDrop, num);
        block.addActor(num);
        row.addActor(block);

        SnapshotArray<Actor> children = row.getChildren();
        for(final Actor actor : children) {

        }
	}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width,height);
        stage.getViewport().update(width,height);
        OrthographicCamera stageCamera = (OrthographicCamera) stage.getCamera();
        stageCamera.zoom=.7f;
        stageCamera.setToOrtho(false,width,height);
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
        skin.dispose();
    }
}
