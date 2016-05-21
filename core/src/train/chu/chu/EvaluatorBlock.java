package train.chu.chu;

import com.badlogic.gdx.Gdx;

import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Created by Da-Jin on 3/25/2016.
 */
public class EvaluatorBlock extends Block{

    private String result;

    public EvaluatorBlock() {
        super();
        setDraggable(false);
        setTargetable(false);
    }

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        Gdx.app.log("evaluator","building result");
        try {
            result = ""+new ExpressionBuilder(getChildrenString()).build().evaluate();
        }catch(Exception e){
            result = null;
        }
    }

    public String getResult() {
        return result;
    }
}
