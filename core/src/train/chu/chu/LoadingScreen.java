package train.chu.chu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by Da-Jin on 9/10/2016.
 */
public class LoadingScreen implements Screen {
    private final ScreenManager screenManager;
    private AssetManager manager;
    private Texture logo;
    private SpriteBatch batch;

    private int logoX, logoY, logoSize, progressY, progressMargin;

    //These are the file names of all textures we need with .png redacted
    //They will also be the names of the styles stored in skin
    private String[] textureNames = {"redo","undo","upArrow","downArrow","parenthesis","newExp","backspace","delete","green"};

    public LoadingScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        manager = new AssetManager();
        manager.load("finalLogo.png", Texture.class);
        manager.finishLoading();
        logo = manager.get("finalLogo.png");

        for(String name:textureNames) {
            manager.load(name+".png", Texture.class);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(230 / 255f, 74 / 255f, 25 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(manager.update()){
            //done loading textures, put them into skin
            Skin skin = new Skin();
            for(String name:textureNames) {
                skin.add(name, new Image((Texture) manager.get(name+".png")).getDrawable(), Drawable.class);
            }

            //Generate bitmap font from TrueType Font
            SmartFontGenerator fontGen = new SmartFontGenerator();
            FileHandle exoFile = Gdx.files.internal("Roboto-Light.ttf");
            BitmapFont roboto = fontGen.createFont(exoFile, "exo-large", (int) Math.min(480, (Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * .25)));

            skin.add("default", new Label.LabelStyle(roboto, Color.WHITE));
            Drawable color = skin.getDrawable("green");
            skin.add("default", new TextButton.TextButtonStyle(color,color,color, roboto));
            screenManager.startMain(skin);
        }
        batch.begin();
        batch.draw(logo, logoX, logoY, logoSize, logoSize);
        batch.setColor(1,1,1,.5f);
        BatchShapeUtils.drawLine(batch, progressMargin, progressY, Gdx.graphics.getWidth() - progressMargin, progressY, 5);
        batch.setColor(Color.WHITE);
        BatchShapeUtils.drawLine(batch, progressMargin, progressY, progressMargin+manager.getProgress()*(Gdx.graphics.getWidth()-progressMargin*2), progressY, 5);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        //Logo is half the length of the shorter screen side
        logoSize = Math.min(width, height)/2;
        logoX = (width - logoSize) / 2;
        logoY = (height - logoSize) / 2;
        //3/4 of the way between the bottom of screen to bottom of logo
        progressY = logoY*3/4;
        progressMargin = (int) (width*.1);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
