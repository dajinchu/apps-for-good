package train.chu.chu.model;

/**
 * Created by Da-Jin on 6/7/2016.
 */
public enum Side {
    LEFT(0),
    RIGHT(1);

    private final int offset;
    Side(int offset){
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
