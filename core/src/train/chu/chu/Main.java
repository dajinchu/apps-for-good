package train.chu.chu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.objecthunter.exp4j.ExpressionBuilder;

public class Main extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private DragAndDrop dragAndDrop;
    private HorizontalGroup row;
    private Label result;
    private Table rootTable;

    @Override
	public void create () {
        //Generate bitmap font from TrueType Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        BitmapFont roboto = generator.generateFont(parameter);
        generator.dispose();

        //Load skin with images and styles for use in scene2d ui elements
        Drawable green=new Image(new Texture("green.png")).getDrawable();
        skin = new Skin();
        skin.add("default", new Label.LabelStyle(roboto, Color.BLACK));
        skin.add("delete", new Texture("delete.png"));
        skin.add("default", new TextButton.TextButtonStyle(green, green, green, roboto));

        //Instantiate Stage for scene2d management
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //Add a root table.
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        //row is the outermost ui element for the sandbox, it holds all the blocks
        //Really, it should be a block too, but all blocks are drag-and-drop-able, and can't be nested
        // so that wouldn't work.
        row = new HorizontalGroup();
        stage.addListener(new ActorGestureResizer(stage.getCamera(),row,new Vector2(1000,1000)));
        row.setPosition(100,100);
        stage.addActor(row);

        //Instantiate the DragAndDrop manager
        dragAndDrop = new DragAndDrop();

        //Instantiate labels and put them each in a block. Add each block to row
        TrashCan trashCan = new TrashCan(dragAndDrop);
        trashCan.setDrawable(skin,"delete");

        result = new Label("finish",skin);
        result.setColor(Color.BLACK);
        result.setPosition(50,0);

        //KeyPad
        Table keypad=new Table();
        String[][] keys = new String[][]{
                {"7","8","9","+"},
                {"4","5","6","-"},
                {"1","2","3","*"},
                {"0", "0", ".","/"}

        };

        //Keypad generator
        for(int x=0; x<keys.length; x++){
            for(int y=0; y<keys[0].length; y++){
                final String buttonTxt=keys[x][y];

                //Used to keep track of col-span.
                int i=1;
                //Look for repeated keys
                while(y<keys[0].length-1&&buttonTxt.equals(keys[x][y+i])){
                    i++;

                }

                //Skip forward to avoid repetition
                y+=i-1;
                //Make Button, create block at end of row if clicked.

                TextButton inputButton=new TextButton(buttonTxt, skin);
                keypad.add(inputButton).width(i*100).height(100).colspan(i);
                inputButton.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float z, float y) {
                        Block block;
                        block = new Block(dragAndDrop);
                        Label second = new Label(buttonTxt,skin);
                        block.addActor(second);
                        row.addActor(block);
                    }
                });
            }
            keypad.row();
        }

        //Populate rootTable
        rootTable.add(trashCan).expand().left().top();
        rootTable.row();
        rootTable.add(result).expandX().right();
        rootTable.row();
        rootTable.add(keypad).expandX().right();


        stage.setViewport(new ScreenViewport());
	}

    @Override
    public void resize(int width, int height) {
        //Handle resize. This is still important on static windows (eg. Android) because it is
        // called once in the beginning of the app lifecycle, so instead of handling sizing in create,
        // it's clearer to do it here, and avoids doing it twice (create and resize are both called initially)
        //set stage viewport
        stage.getViewport().update(width,height,true);
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
