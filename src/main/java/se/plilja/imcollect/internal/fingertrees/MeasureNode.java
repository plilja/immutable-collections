package se.plilja.imcollect.internal.fingertrees;

class MeasureNode<M, T> implements Measure<M, Node<T, M>> {
    private final Measure<M, T> wrapped;

    MeasureNode(Measure<M, T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public M identity() {
        return wrapped.identity();
    }

    @Override
    public M combine(M a, M b) {
        return wrapped.combine(a, b);
    }

    @Override
    public M measure(Node<T, M> value) {
        return value.measured();
    }
}
