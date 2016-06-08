package train.chu.chu.model;

        import com.badlogic.gdx.utils.Array;

/**
 * Created by Da-Jin on 6/5/2016.
 * One level below root in expression tree
 * Calculates final result
 * Stores its position in sandbox world
 */
        public class ExpressionNode implements Positioned{

            private int x,y;
            private Array<Node> children;

            protected ExpressionNode(int x, int y) {
                this.x = x;
                this.y = y;
                this.children = new Array<>();;
            }

            @Override
            public int getX() {
                return x;
            }
            @Override
            public void setX(int x) {
                this.x = x;
            }
            @Override
            public int getY() {
                return y;
    }
    @Override
    public void setY(int y) {
        this.y = y;
    }

    public Array<Node> getChildren() {
        return children;
    }
}
