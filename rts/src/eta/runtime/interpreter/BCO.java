package eta.runtime.interpreter;

import eta.runtime.stg.Value;
import eta.runtime.stg.StgContext;
import eta.runtime.stg.Closure;
import eta.runtime.io.Array;
import eta.runtime.io.ByteArray;

import static eta.runtime.RuntimeLogging.barf;

public class BCO extends Value {
    public final ByteArray instrs;
    public final ByteArray literals;
    public final Array ptrs;
    public final int arity;
    public final int[] bitmap;

    public BCO(ByteArray instrs, ByteArray literals, Array ptrs,
                  int arity, int[] bitmap) {
        this.instrs = instrs;
        this.literals = literals;
        this.ptrs = ptrs;
        this.arity = arity;
        this.bitmap = bitmap;
    }
}
