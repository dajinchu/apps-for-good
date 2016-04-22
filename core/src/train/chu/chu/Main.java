package train.chu.chu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.objecthunter.exp4j.ExpressionBuilder;

import javafx.scene.shape.MoveTo;

public class Main extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private Block row;
    private Label result;
    private Table rootTable;
    private ImageButton redo;
    private ImageButton undo;
    private Table keypad;
    private Table keyPadTabs;
    private int tabNum;
    private int prevtabNum;
    private int keyToggle;

    public static DragAndDrop dragAndDrop = new DragAndDrop();
    private Label debug;

    @Override
	public void create () {
        //Generate bitmap font from TrueType Font
        BitmapFont roboto=new BitmapFont(new FileHandle("roboto.fnt"),new FileHandle("roboto.png"),false);
        //Load skin with images and styles for use in scene2d ui elements
        Drawable green=new Image(new Texture("green.png")).getDrawable();
        Drawable undoImg=new Image(new Texture("undo.png")).getDrawable();
        Drawable redoImg=new Image(new Texture("redo.png")).getDrawable();
        skin = new Skin();
        skin.add("default", new Label.LabelStyle(roboto, Color.WHITE));
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
        row = new EvaluatorBlock();
        stage.addListener(new ActorGestureResizer(stage.getCamera(),row,new Vector2(1000,1000)));
        row.setPosition(100,100);
        stage.addActor(row);

        stage.addListener(new ClickListener(){
            public float x, y;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //track touch down location.. TODO maybe change this to also track time?
                this.x = x;
                this.y = y;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //Set selected on the Up event, but NOT if the click location has moved too much
                if(Math.abs(this.x-x)<10 && Math.abs(this.y-y)<10) {
                    row.setSelected();
                }
            }
        });

        //Instantiate labels and put them each in a block. Add each block to row
        final TrashCan trashCan = new TrashCan();
        trashCan.setDrawable(skin,"delete");
        trashCan.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {

                Command cmd=new ClearChildren(row);
                cmd.execute();
            }
        });


        redo = new ImageButton(redoImg);
        redo.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                Command.redo();
            }
        });


        undo = new ImageButton(undoImg);

        undo.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {
                Command.undo();
            }
        });

        //Key Pad toggle button
        final Drawable keytogsDown=new Image(new Texture("upArrow.png")).getDrawable();
        final Drawable keytogsUp=new Image(new Texture("downArrow.png")).getDrawable();
        final ImageButton keyPadToggle=new ImageButton(keytogsUp,keytogsDown,keytogsDown);


       keyPadToggle.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float z, float y) {

                if(keyToggle==0){
                    //Hide KeyPad, set to up arrow
                    keyToggle=1;
                    keyPadTabs.addAction(Actions.moveTo(keyPadTabs.getX(),-100, 0.5f, Interpolation.swingIn));
                    keypad.addAction(Actions.moveTo(keypad.getX(),-500, 0.5f,Interpolation.swingIn));

                    keyPadToggle.setChecked(true);


                }else{
                    //Bring up keypad, set to down arrow
                    keyToggle=0;
                    keyPadTabs.addAction(Actions.moveTo(keyPadTabs.getX(),400, 0.5f,Interpolation.swingOut));
                    keypad.addAction(Actions.moveTo(keypad.getX(),0, 0.5f,Interpolation.swingOut));

                    keyPadTabs.setVisible(true);
                    keypad.setVisible(true);
                    keyPadToggle.setChecked(false);
                }
            }
        });

        Group toolbar=new HorizontalGroup();
        //toolbar.addActor(trashCan);
        toolbar.addActor(keyPadToggle);
        toolbar.addActor(undo);
        toolbar.addActor(redo);




        result = new Label("finish",skin);
        result.setColor(Color.BLACK);
        result.setPosition(50,0);

        //KeyPad
        tabNum=1;
        //KeyPad tab generator
        keyPadTabs=new Table();
        for(int i=1; i<=10; i++){
            TextButton inputButton=new TextButton("T", skin);
            keyPadTabs.add(inputButton).width(50).height(50).colspan(i);
            final int valueof=i;
            inputButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float z, float y) {
                    tabNum=valueof;
                    tabChooser();
                }
            });
        }
        keyPadTabs.row();
        prevtabNum=tabNum;
        keypad=new Table();
        tabChooser();

        debug = new Label("",skin);
        debug.setPosition(20,40);
        debug.setFontScale(.6f);
        debug.setColor(Color.GRAY);

        stage.addActor(debug);

        //Populate rootTable

        rootTable.add(trashCan).expandX().left().top().expandY().top();
        rootTable.add(toolbar).expandX().right().top().expandY().top();
        rootTable.row();
        rootTable.add(result).expandX().right().colspan(2);
        rootTable.row();
        rootTable.add(keyPadTabs).expandX().right().colspan(2);
        rootTable.row();
        rootTable.add(keypad).expandX().right().colspan(2);


        stage.setViewport(new ScreenViewport());
	}

    @Override
    public void resize(int width, int height) {
        //Handle resize. This is still important on static windows (eg. Android) because it is
        // called once in the beginning of the app lifecycle, so instead of handling sizing in create,
        // it's clearer to do it here, and avoids doing it twice (create and resize are both called initially)
        //set stage viewport
        stage.getViewport().update(width,height,true);
        row.setPosition((width-row.getWidth())/2,(height-row.getHeight())/2);
    }

    @Override
	public void render () {
        //Wipe the screen clean with a white clear color
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        result.setColor(Color.BLACK);
        //Convert the blocks in HorizontalGroup to a string
        String s = row.getChildrenString();
        //Evaluate the expression
        //Use ExpressionBuilder from exp4j to perform the calculations and set the result text
        try{
            result.setText("="+new ExpressionBuilder(s).build().evaluate());
        }catch (IllegalArgumentException error){
            result.setColor(Color.RED);
            result.setText("Invalid");
        }

        debug.setText(s);

        if(Command.redoCommands.isEmpty()){
            redo.getImage().setColor(Color.GRAY);
        }else{
            redo.getImage().setColor(Color.BLACK);
        }

        if(Command.undoCommands.isEmpty()){
            undo.getImage().setColor(Color.GRAY);
        }else{
            undo.getImage().setColor(Color.BLACK);
        }
        //Scene2d. Step forward the world and draw the scene
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        //stage.setDebugAll(true);



	}

    public void dispose () {
        //When the app is destroyed, don't leave any memory leaks behind
        stage.dispose();
        skin.dispose();
    }

    public void tabChooser(){

        System.out.println(tabNum);
        String[][] keys;
        switch (tabNum){
            case 1:keys = new String[][]{
                    {"7","8","9","+", "sqrt"},
                    {"4","5","6","-", "square"},
                    {"1","2","3","*", ""},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 2:keys = new String[][]{
                    {"2","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 3:keys = new String[][]{
                    {"3","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 4:keys = new String[][]{
                    {"4","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 5:keys = new String[][]{
                    {"5","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 6:keys = new String[][]{
                    {"6","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 7:keys = new String[][]{
                    {"7","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 8:keys = new String[][]{
                    {"8","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 9:keys = new String[][]{
                    {"9","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            case 10:keys = new String[][]{
                    {"10","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;
            default: keys = new String[][]{
                    {"9","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
        }


        keyPadGenerator(keys);
    }


    public void keyPadGenerator(String[][] keys){

        //KeyPad
        keypad.clear();




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


                Actor inputButton=ButtonCreator.ButtonCreator(buttonTxt, skin);
                keypad.add(inputButton).width(i*100).height(100).colspan(i);
                inputButton.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float z, float y) {
                        Command cmd=new AddCommand(BlockCreator.BlockCreator(buttonTxt, skin), row);
                        //row.addActor(block);
                        cmd.execute();
                    }
                });


            }
            keypad.row();
        }

    }
}
