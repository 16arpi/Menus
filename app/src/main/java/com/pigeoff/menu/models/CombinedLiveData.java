package com.pigeoff.menu.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class CombinedLiveData<T, K, S> extends MediatorLiveData<S> {

    private T data1;
    private K data2;

    public CombinedLiveData(LiveData<T> source1, LiveData<K> source2, Combine<T, K, S> combine) {
        super.addSource(source1, item -> {
            data1 = item;
            setValue(combine.combine(data1, data2));
        });
        super.addSource(source2, item -> {
            data2 = item;
            setValue(combine.combine(data1, data2));
        });
    }

    @Override
    public <S1> void addSource(@NonNull LiveData<S1> source, @NonNull Observer<? super S1> onChanged) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S1> void removeSource(@NonNull LiveData<S1> toRemote) {
        throw new UnsupportedOperationException();
    }

    public interface Combine<T, K, S> {
        S combine(T data1, K data2);
    }

    public static class CombinedObject<T, K> {
        public T a;
        public K b;

        public CombinedObject(T a, K b) {
            this.a = a;
            this.b = b;
        }
    }
}
