package train.chu.chu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Da-Jin on 4/10/2015.
 */
public class Bench {

    private static HashMap<String, Long> starts = new HashMap<String, Long>();
    private static long tmp;
    private static FileHandle fpsFile;

    public static void start(String tag){
        starts.put(tag, TimeUtils.millis());
    }
    public static long end(String tag){
        tmp = TimeUtils.timeSinceMillis(starts.get(tag));
        starts.remove(tag);
        if(fpsFile==null){
            fpsFile = Gdx.files.external(new SimpleDateFormat("'Logfrog/'MM-dd-yyyy'/fps 'hh-mm a'.txt'").format(new Date()));
        }
        fpsFile.writeString(tag+": "+tmp+"\n", true);
        return tmp;
    }
}