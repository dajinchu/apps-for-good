package train.chu.chu;

/**
 * Created by Da-Jin on 4/27/2016.
 */
public class ParenthesisBlock extends Block {
    private static ParenthesisBlock moveBlock;
    private ParenthesisContainer container;

    public enum Side {OPENING, CLOSING}
    Side side;

    public static void clearSelection(){
        if(moveBlock!=null){
            moveBlock.setMoving(false);
        }
    }

    public ParenthesisBlock(final Side side){
        this.side = side;
    }
    public boolean isMoving() {
        return this == moveBlock;
    }

    public void toggleMoving(){
        setMoving(!isMoving());
    }

    public void setMoving(boolean moving) {
        if (moving) {
            if(moveBlock!=null){
                moveBlock.setMoving(false);
            }
            moveBlock=this;
            container = new ParenthesisContainer(this);
        } else {
            ParenthesisBlock oldMove = moveBlock;
            moveBlock=null;
            if(oldMove!=null && oldMove!=this){
                oldMove.setMoving(false);
            }
            if(this.getParent() == container) {
                container.getParent().addActorBefore(container, this);
                container.remove();
            }
        }
    }
}