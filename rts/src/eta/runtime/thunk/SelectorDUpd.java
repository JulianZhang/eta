package eta.runtime.thunk;

import eta.runtime.stg.Closure;
import eta.runtime.stg.StgContext;
import eta.runtime.stg.DataCon;


public class SelectorDUpd extends SelectorUpd {

    public SelectorDUpd(int i, Closure p) {
        super(i, p);
    }

    @Override
    public Closure selectEnter(StgContext context, DataCon result) {
        context.D(1, result.getD(index));
        return null;
    }
}
