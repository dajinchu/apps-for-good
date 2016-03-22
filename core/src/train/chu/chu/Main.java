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
        skin.add("badlogic", new Texture("badlogic.jpg"));
        skin.add("default", new TextButton.TextButtonStyle(green, green, green, roboto));

        //Instantiate Stage for scene2d management
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //Add a root table.
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom();
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



        result = new Label("finish",skin);
        result.setColor(Color.BLACK);
        result.setPosition(50,0);
        rootTable.add(result);


        //Table Test Stuff
        Table keypad=new Table();

        Table operations=new Table();
        TextButton plus = new TextButton("+", skin);
        operations.add(plus).width(100).height(100);
        plus.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                Block block;
                block = new Block(dragAndDrop);
                Label second = new Label("+",skin);
                block.addActor(second);
                row.addActor(block);
            }
        });
        operations.row();
        TextButton minus = new TextButton("-", skin);
        minus.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                Block block;
                block = new Block(dragAndDrop);
                Label second = new Label("-",skin);
                block.addActor(second);
                row.addActor(block);
            }
        });
        operations.add(minus).width(100).height(100);
        operations.row();
        TextButton times = new TextButton("*", skin);
        operations.add(times).width(100).height(100);
        times.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                Block block;
                block = new Block(dragAndDrop);
                Label second = new Label("*",skin);
                block.addActor(second);
                row.addActor(block);
            }
        });
        operations.row();

        Table bottomRow=new Table();

        TextButton zero = new TextButton("0", skin);
        bottomRow.add(zero).width(100).height(100);
        zero.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                Block block;
                block = new Block(dragAndDrop);
                Label second = new Label(""+0,skin);
                block.addActor(second);
                row.addActor(block);
            }
        });
        TextButton dec = new TextButton(".", skin);
        bottomRow.add(dec).width(100).height(100);
        dec.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                Block block;
                block = new Block(dragAndDrop);
                Label second = new Label(".",skin);
                block.addActor(second);
                row.addActor(block);
            }
        });
        TextButton back = new TextButton("<-", skin);
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                int index=row.getChildren().size-1;
                if(index>=0) {
                    row.removeActor(row.getChildren().get(index));
                }
            }
        });

        bottomRow.add(back).width(100).height(100);
        TextButton div = new TextButton("/", skin);
        bottomRow.add(div).width(100).height(100);
        div.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                Block block;
                block = new Block(dragAndDrop);
                Label second = new Label("/",skin);
                block.addActor(second);
                row.addActor(block);
            }
        });





        Table numpad=new Table();
        for(int i=7; i>=1; i-=3){
            for(int x=i; x<=i+2; x++){
                TextButton inputButton=new TextButton(""+x, skin);
                numpad.add(inputButton).width(100).height(100);
                final int finalX = x;
                inputButton.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float z, float y) {
                        Block block;
                        block = new Block(dragAndDrop);
                        Label second = new Label(""+ finalX,skin);
                        block.addActor(second);
                        row.addActor(block);
                    }
                });
            }
            numpad.row();
            Table innerKeypad=new Table();
            innerKeypad.add(numpad);
            innerKeypad.add(operations);
            innerKeypad.row();
            keypad.add(innerKeypad);
            keypad.row();
            keypad.add(bottomRow);
            keypad.row();
        }






        rootTable.row();
        rootTable.add(keypad);


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
