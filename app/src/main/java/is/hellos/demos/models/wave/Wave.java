package is.hellos.demos.models.wave;


import android.support.annotation.Nullable;

public class Wave {

    private final float value;

    @Nullable
    private Wave next = null;

    public Wave(final float value) {
        this.value = value;
    }

    public void setNext(@Nullable final Wave next) {
        this.next = next;
    }

    public float getValue() {
        return value;
    }

    @Nullable
    public Wave getNext() {
        return next;
    }

    public Wave addNext(final float value) {
        next = new Wave(value);
        return next;
    }
}
