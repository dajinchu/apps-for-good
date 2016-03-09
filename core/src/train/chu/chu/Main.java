package train.chu.chu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    private OrthographicCamera camera;
    private ShapeRenderer shapes;

    public static final int GRID_ROWS = 20;
    public static final int GRID_COLS = 40;

    @Override
	public void create () {
		batch = new SpriteBatch();
        shapes = new ShapeRenderer();
		img = new Texture("badlogic.jpg");
        camera = new OrthographicCamera();
	}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width,height);
    }

    @Override
	public void render () {
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapes.setProjectionMatrix(camera.combined);


		batch.begin();
		if(Gdx.input.isTouched()){
            Vector3 touch = new Vector3();
            touch.set(Gdx.input.getX(), Gdx.input.getY(),0);
            camera.unproject(touch);
            batch.draw(img,touch.x,touch.y);
        }
        batch.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(.88f, .88f, .88f, 1);
        for(int i = 0; i < GRID_COLS; i++){
            shapes.line(i*50,0,i*50,GRID_ROWS*50);
        }
        for(int i = 0; i < GRID_ROWS; i++){
            shapes.line(0,i*50,GRID_COLS*50,i*50);
        }
        shapes.end();
	}
}
