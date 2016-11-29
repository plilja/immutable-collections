package se.plilja.imcollect.fingertrees;

/**
 * @param <M> The measure
 * @param <T> The type being measured
 */
interface Measure<M, T> {
    M identity();

    M combine(M a, M b);

    M measure(T value);
}
