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
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;

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
    private boolean landscape;
    private int size;
    private TrashCan trashCan = null;
    private Group toolbar;

    public static DragAndDrop dragAndDrop = new DragAndDrop();
    private Label debug;
    private VerticalGroup calcZone;

    private ArrayList<Float[]> circle = new ArrayList<>();
    private Float[] p1, p2;

    @Override
	public void create () {

            //Generate bitmap font from TrueType Font
            SmartFontGenerator fontGen = new SmartFontGenerator();
            FileHandle exoFile = Gdx.files.internal("Roboto-Light.ttf");
            BitmapFont robotoKeypadTabs = fontGen.createFont(exoFile, "exo-small", (int)Math.min(480,Math.min(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())*.1));
            BitmapFont robotoKeypad = fontGen.createFont(exoFile, "exo-medium", (int)Math.min(480,Math.min(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())*.09));
            BitmapFont roboto = fontGen.createFont(exoFile, "exo-large", (int)Math.min(480,(Math.min(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())*.5)));
            //BitmapFont roboto = fontGen.createFont(exoFile, "exo-large", 480);



            /*
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Light.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 72
            BitmapFont roboto = generator.generateFont(parameter);
            generator.dispose();
            */
            //Load skin with images and styles for use in scene2d ui elements
            Drawable green=new Image(new Texture("green.png")).getDrawable();
                skin = new Skin();
                Drawable undoImg;
                Drawable redoImg;
                final Drawable keytogsUp;
                final Drawable keytogsDown;
            if(Math.min(Gdx.graphics.getWidth(),Gdx.graphics.getHeight())<1000){
                undoImg=new Image(new Texture("undo.png")).getDrawable();
                redoImg=new Image(new Texture("redo.png")).getDrawable();
                keytogsDown=new Image(new Texture("upArrow.png")).getDrawable();
                keytogsUp=new Image(new Texture("downArrow.png")).getDrawable();
                skin.add("delete", new Texture("delete.png"));
            }else{
                undoImg=new Image(new Texture("undoLarge.png")).getDrawable();
                redoImg=new Image(new Texture("redoLarge.png")).getDrawable();
                keytogsDown=new Image(new Texture("upArrowLarge.png")).getDrawable();
                keytogsUp=new Image(new Texture("downArrowLarge.png")).getDrawable();
                skin.add("delete", new Texture("deleteLarge.png"));
            }




            skin.add("default", new Label.LabelStyle(roboto, Color.WHITE));

            skin.add("default", new TextButton.TextButtonStyle(green, green, green, robotoKeypad));

            //Instantiate Stage for scene2d management
            stage = new Stage();
            Gdx.input.setInputProcessor(stage);



            //Add a root table.
            rootTable = new Table();
            rootTable.setFillParent(true);
            stage.addActor(rootTable);

            //row is the outermost ui element for the sandbox, it holds all the blocks
            row = new EvaluatorBlock();

            result = new Label("",skin);
            result.setFontScale(0.25f);

            calcZone = new VerticalGroup();
            calcZone.addActor(result);
            calcZone.addActor(row);
            stage.addListener(new ActorGestureResizer(stage.getCamera(),calcZone,new Vector2(1000,1000)));
            stage.addActor(calcZone);

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
            stage.addListener(new ActorGestureListener(){
                float minx, maxx, miny, maxy;
                boolean drawing = false;
                @Override
                public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(stage.hit(x,y,true)!=null){
                        return;
                    }
                    drawing = true;
                    minx = x;
                    maxx = x;
                    miny = y;
                    maxy = y;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if(!drawing)return;
                    float avgy = (miny+maxy)/2;
                    Actor left = stage.hit(minx,avgy,true);
                    Actor right = stage.hit(maxx,avgy,true);
                    if(left!=null && right!= null) {
                        new ParenthesisCommand(left, right, skin).execute();
                    }
                    drawing = false;
                    circle.clear();
                }

                @Override
                public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                    if(!drawing)return;
                    if(x<minx){
                        minx = x;
                    }
                    if(x>maxx){
                        maxx = x;
                    }
                    if(y<miny){
                        miny = y;
                    }
                    if(y>maxy){
                        maxy = y;
                    }
                    circle.add(new Float[]{x,y});
                }
            });

            //Creates the trash can
            trashCan = new TrashCan();
            trashCan.setDrawable(skin, "delete");

            trashCan.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float z, float y) {

                    Command cmd=new ClearChildren(row);
                    cmd.execute();
                }
            });


            //Creates the redo button
            redo = new ImageButton(redoImg);

            redo.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float z, float y) {
                    Command.redo();
                }
            });

            //creates the undo button
            undo = new ImageButton(undoImg);
            undo.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float z, float y) {
                    Command.undo();
                }
            });

            //Key Pad toggle button

            final ImageButton keyPadToggle=new ImageButton(keytogsUp,keytogsDown,keytogsDown);

            //Toggle the keypad on and off
           keyPadToggle.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float z, float y) {

                    if(keyToggle==0){
                        //Hide KeyPad, set to up arrow
                        keyToggle=1;
                        keyPadTabs.addAction(Actions.moveTo(keyPadTabs.getX(),-100, 0.5f, Interpolation.swingIn));
                        keypad.addAction(Actions.moveTo(keypad.getX(),-(size*4+100), 0.5f,Interpolation.swingIn));
                        keyPadToggle.setChecked(true);


                    }else{
                        //Bring up keypad, set to down arrow
                        keyToggle=0;
                        keyPadTabs.addAction(Actions.moveTo(keyPadTabs.getX(),size*4, 0.5f,Interpolation.swingOut));
                        keypad.addAction(Actions.moveTo(keypad.getX(),0, 0.5f,Interpolation.swingOut));
                        keyPadToggle.setChecked(false);
                    }
                }
            });

            //Create the toolbar, keypad toggle, undo/redo buttons
            toolbar=new HorizontalGroup();
            toolbar.addActor(keyPadToggle);
            toolbar.addActor(undo);
            toolbar.addActor(redo);




            //Debugger
            debug = new Label("",skin);
            debug.setPosition(20,40);
            debug.setFontScale(.15f);
            debug.setColor(Color.GRAY);

            stage.addActor(debug);



            stage.setViewport(new ScreenViewport());
	}

    @Override
    public void resize(int width, int height) {
        //Handle resize. This is still important on static windows (eg. Android) because it is
        // called once in the beginning of the app lifecycle, so instead of handling sizing in create,
        // it's clearer to do it here, and avoids doing it twice (create and resize are both called initially)
        //set stage viewport
        stage.getViewport().update(width,height,true);
        //calcZone.setPosition((width-calcZone.getWidth())/2,(height-calcZone.getHeight())/2);
        if(rootTable.getChildren().contains(keyPadTabs,true)){
            rootTable.clearChildren();
        }

        if(Gdx.graphics.getWidth()>Gdx.graphics.getHeight()){
            landscape=true;
            size=Gdx.graphics.getWidth()/10;
            System.out.println("landscape:"+size);
            calcZone.setPosition((float)((Gdx.graphics.getWidth()-((size*5)))/2),(height-calcZone.getHeight())/2+50);

        }else{
            landscape=false;
            size=Gdx.graphics.getWidth()/5;
            System.out.println("portrait:"+size);
            calcZone.setPosition((width-calcZone.getWidth())/2,(float)(Gdx.graphics.getHeight()-((size*4.5)/4)));


        }
        System.out.println("Run");
        //KeyPad
        tabNum=1;

        //KeyPad tab generator, generates 10 different tabs
        keyPadTabs=new Table();
        for(int i=1; i<=10; i++){

            TextButton inputButton=new TextButton(""+i, skin);
            inputButton.getLabel().setFontScale(0.5f);
            keyPadTabs.add(inputButton).width(size/2).height(size/2).colspan(i);
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

        rootTable.setZIndex(998);
        row.setZIndex(1);
        //Populate rootTable
        rootTable.add(trashCan).expandX().left().top().expandY().top();
        rootTable.add(toolbar).expandX().right().top().expandY().top();
        rootTable.row();
        rootTable.add(keyPadTabs).expandX().right().colspan(2);
        rootTable.row();
        rootTable.add(keypad).expandX().right().colspan(2);
        keyPadTabs.setVisible(false);
    }

    @Override
	public void render () {
        //Wipe the screen clean with a white clear color
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        result.setColor(Color.DARK_GRAY);
        //Convert the blocks in HorizontalGroup to a string
        String s = row.getChildrenString();
        //Evaluate the expression
        //Use ExpressionBuilder from exp4j to perform the calculations and set the result text
        if(s.isEmpty()){
            result.setText("");
        }else {
            try {
                result.setText("=" + new ExpressionBuilder(s).build().evaluate());
            } catch (IllegalArgumentException error) {
                result.setColor(Color.RED);
                result.setText("false");
            }
        }

        debug.setText(s);


        //Change the color of the redo/undo button to gray if stack is empty.
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

        stage.getBatch().begin();
        stage.getBatch().setColor(Color.BLACK);
        for(int i = 0; i < circle.size()-1; i++){
            p1 = circle.get(i);
            p2 = circle.get(i+1);
            BatchShapeUtils.drawLine(stage.getBatch(), p1[0],p1[1],p2[0],p2[1],2);
        }
        stage.getBatch().end();

	}

    public void dispose () {
        //When the app is destroyed, don't leave any memory leaks behind
        stage.dispose();
        skin.dispose();
    }

    public void tabChooser(){

        //Choose between the 10 different tabs
        System.out.println(tabNum);
        String[][] keys;

        //The arrays for the 10 different tabs (Only the first tab is real right now, N is a placeholder).
        switch (tabNum){
            case 1:keys = new String[][]{
                    {"7","8","9","+", "^"},
                    {"4","5","6","-", "^-2"},
                    {"1","2","3","*", "("},
                    {"0", "0", ".","/", ")"}

            };
                break;
            case 2:keys = new String[][]{
                    {"2","2","2","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
                break;

            default: keys = new String[][]{
                    {"7","8","9","+", "N"},
                    {"4","5","6","-", "N"},
                    {"1","2","3","*", "N"},
                    {"0", "0", ".","/", "N"}

            };
        }

        //Generate the keypad
        keyPadGenerator(keys);
    }


    public void keyPadGenerator(String[][] keys){

        //Clear the existing keypad
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
                keypad.add(inputButton).width(i*size).height(size).colspan(i);
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
