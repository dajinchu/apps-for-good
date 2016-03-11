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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
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
        parameter.size = 48;
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


        Label first = new Label("X",skin);
        first.setColor(Color.BLACK);
        row.addActor(first);

        Label second = new Label("8",skin);
        second.setColor(Color.BLACK);
        row.addActor(second);

        Label num = new Label("132",skin);
        num.setColor(Color.BLACK);
        row.addActor(num);

        dragAndDrop = new DragAndDrop();
        SnapshotArray<Actor> children = row.getChildren();
        for(final Actor actor : children) {
            dragAndDrop.addSource(new Source(actor) {
                public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    Payload payload = new Payload();
                    payload.setObject("Some payload!");

                    Label draglabel = new Label(((Label)actor).getText(),skin);
                    draglabel.setColor(0,0,0,1);
                    payload.setDragActor(draglabel);
                    dragAndDrop.setDragActorPosition(-(actor.getWidth()/2), actor.getHeight()/2);

                    /*Label validLabel = new Label("valid!", skin);
                    validLabel.setColor(0, 1, 0, 1);
                    payload.setValidDragActor(validLabel);

                    Label invalidLabel = new Label("invalid!", skin);
                    invalidLabel.setColor(1, 0, 0, 1);
                    payload.setInvalidDragActor(invalidLabel);
*/
                    return payload;
                }
            });
            dragAndDrop.addTarget(new Target(actor) {
                //Center rect is the detection area for getting out of the way, or merging blocks
                private Rectangle centerRect = new Rectangle(
                        getActor().getWidth() * .3f, getActor().getHeight() * .3f,
                        getActor().getWidth() * .4f, getActor().getHeight() * .4f);

                public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                    //Something is being dragged over this target
                    if(source.getActor()==actor){
                        System.out.println("Same actor");
                        return false;
                    }
                    if (centerRect.contains(x, y)) {
                        System.out.println("contained");
                        System.out.println(row.swapActor(getActor(), source.getActor()));
                        row.invalidate();
                    }
                    getActor().setColor(Color.GREEN);
                    return true;
                }

                public void reset(DragAndDrop.Source source, Payload payload) {
                    getActor().setColor(Color.BLACK);
                }

                public void drop(Source source, Payload payload, float x, float y, int pointer) {
                    System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
                }
            });
        }
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
        skin.dispose();
    }
}
