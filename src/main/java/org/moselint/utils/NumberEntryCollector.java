package org.moselint.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class NumberEntryCollector<K extends Number, V extends Number, KR extends Number, VR extends Number> implements Collector<Map.Entry<K, V>, Map.Entry<Collection<Double>, Collection<Double>>, Map.Entry<KR, VR>> {

    private final Function<Double, KR> keyMap;
    private final Function<Double, VR> valueMap;

    public NumberEntryCollector(Function<Double, KR> key, Function<Double, VR> value) {
        this.keyMap = key;
        this.valueMap = value;
    }

    @Override
    public Supplier<Map.Entry<Collection<Double>, Collection<Double>>> supplier() {
        return () -> new AbstractMap.SimpleImmutableEntry<>(new HashSet<>(), new HashSet<>());
    }

    @Override
    public BiConsumer<Map.Entry<Collection<Double>, Collection<Double>>, Map.Entry<K, V>> accumulator() {
        return (entry1, entry2) -> {
            entry1.getKey().add(entry2.getKey().doubleValue());
            entry1.getValue().add(entry2.getValue().doubleValue());
        };
    }

    @Override
    public BinaryOperator<Map.Entry<Collection<Double>, Collection<Double>>> combiner() {
        return (entry1, entry2) -> {
            Set<Double> key = new HashSet<>();
            key.addAll(entry1.getKey());
            key.addAll(entry2.getKey());
            Set<Double> value = new HashSet<>();
            key.addAll(entry1.getValue());
            key.addAll(entry2.getValue());

            return new AbstractMap.SimpleImmutableEntry<>(key, value);
        };
    }

    @Override
    public Function<Map.Entry<Collection<Double>, Collection<Double>>, Map.Entry<KR, VR>> finisher() {
        return entry -> {
            double key = entry.getKey().parallelStream().mapToDouble(v -> v).sum();
            double value = entry.getValue().parallelStream().mapToDouble(v -> v).sum();
            return new AbstractMap.SimpleImmutableEntry<>(keyMap.apply(key), valueMap.apply(value));
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
