package se.plilja.imcollect.fingertrees;

class Split<SplitType, ValueType> {
    public final SplitType left;
    public final ValueType value;
    public final SplitType right;

    Split(SplitType left, ValueType value, SplitType right) {
        this.left = left;
        this.value = value;
        this.right = right;
    }
}
