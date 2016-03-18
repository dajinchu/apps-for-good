package train.chu.chu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.StringBuilder;

import net.objecthunter.exp4j.ExpressionBuilder;

public class Main extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private DragAndDrop dragAndDrop;
    private HorizontalGroup row;
    private Label result;

    @Override
	public void create () {
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

        //row is the outermost ui element for the sandbox, it holds all the blocks
        //Really, it should be a block too, but all blocks are drag-and-drop-able, and can't be nested
        // so that wouldn't work.
        row = new HorizontalGroup();
        row.setBounds(0,0,300,300);
        stage.addActor(row);

        //Instantiate the DragAndDrop manager
        dragAndDrop = new DragAndDrop();

        //Instantiate labels and put them each in a block. Add each block to row
        Block block;

        block = new Block(dragAndDrop);
        Label second = new Label("8",skin);
        second.setColor(Color.BLACK);
        block.addActor(second);
        row.addActor(block);

        Label first = new Label("*",skin);
        first.setColor(Color.BLACK);
        block = new Block(dragAndDrop);
        block.addActor(first);
        row.addActor(block);

        Label num = new Label("5",skin);
        num.setColor(Color.BLACK);
        block = new Block(dragAndDrop);
        block.addActor(num);
        row.addActor(block);

        first = new Label("+",skin);
        first.setColor(Color.BLACK);
        block = new Block(dragAndDrop);
        block.addActor(first);
        row.addActor(block);

        num = new Label("3",skin);
        num.setColor(Color.BLACK);
        block = new Block(dragAndDrop);
        block.addActor(num);

        row.addActor(block);

        result = new Label("finish",skin);
        result.setColor(Color.BLACK);
        result.setPosition(50,0);
        stage.addActor(result);
	}

    @Override
    public void resize(int width, int height) {
        //Handle resize. This is still important on static windows (eg. Android) because it is
        // called once in the beginning of the app lifecycle, so instead of handling sizing in create,
        // it's clearer to do it here, and avoids doing it twice (create and resize are both called initially)
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

        //Evaluate the expression
        //Convert the blocks in HorizontalGroup to a string
        StringBuilder sb = new StringBuilder();
        for(Actor a : row.getChildren()){
            sb.append(((Label)((Block)a).getChildren().get(0)).getText());
        }
        //Use ExpressionBuilder from exp4j to perform the calculations and set the result text
        try{
            result.setColor(Color.BLACK);
            result.setText("="+new ExpressionBuilder(sb.toString()).build().evaluate());
        }catch (IllegalArgumentException error){
            result.setColor(Color.RED);
            result.setText("Invalid");
        }

        //Scene2d. Step forward the world and draw the scene
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        stage.setDebugAll(true);
	}

    public void dispose () {
        //When the app is destroyed, don't leave any memory leaks behind
        stage.dispose();
        skin.dispose();
    }
}
