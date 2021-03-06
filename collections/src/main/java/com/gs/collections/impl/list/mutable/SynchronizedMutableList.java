/*
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.list.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.ByteFunction;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.ParallelListIterable;
import com.gs.collections.api.list.primitive.MutableBooleanList;
import com.gs.collections.api.list.primitive.MutableByteList;
import com.gs.collections.api.list.primitive.MutableCharList;
import com.gs.collections.api.list.primitive.MutableDoubleList;
import com.gs.collections.api.list.primitive.MutableFloatList;
import com.gs.collections.api.list.primitive.MutableIntList;
import com.gs.collections.api.list.primitive.MutableLongList;
import com.gs.collections.api.list.primitive.MutableShortList;
import com.gs.collections.api.multimap.list.MutableListMultimap;
import com.gs.collections.api.ordered.OrderedIterable;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.api.stack.MutableStack;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.collection.mutable.AbstractSynchronizedMutableCollection;
import com.gs.collections.impl.collection.mutable.SynchronizedCollectionSerializationProxy;
import com.gs.collections.impl.lazy.ReverseIterable;
import com.gs.collections.impl.lazy.parallel.list.SynchronizedParallelListIterable;
import net.jcip.annotations.GuardedBy;

/**
 * A synchronized view of a {@link MutableList}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableList#asSynchronized()
 */
public class SynchronizedMutableList<T>
        extends AbstractSynchronizedMutableCollection<T>
        implements MutableList<T>, Serializable
{
    private static final long serialVersionUID = 2L;

    SynchronizedMutableList(MutableList<T> newCollection)
    {
        super(newCollection);
    }

    SynchronizedMutableList(MutableList<T> newCollection, Object newLock)
    {
        super(newCollection, newLock);
    }

    /**
     * This method will take a MutableList and wrap it directly in a SynchronizedMutableList.  It will
     * take any other non-GS-collection and first adapt it will a ListAdapter, and then return a
     * SynchronizedMutableList that wraps the adapter.
     */
    public static <E, L extends List<E>> SynchronizedMutableList<E> of(L list)
    {
        MutableList<E> mutableList =
                list instanceof MutableList ? (MutableList<E>) list : ListAdapter.adapt(list);
        return new SynchronizedMutableList<E>(mutableList);
    }

    /**
     * This method will take a MutableList and wrap it directly in a SynchronizedMutableList.  It will
     * take any other non-GS-collection and first adapt it will a ListAdapter, and then return a
     * SynchronizedMutableList that wraps the adapter.  Additionally, a developer specifies which lock to use
     * with the collection.
     */
    public static <E, L extends List<E>> SynchronizedMutableList<E> of(L list, Object lock)
    {
        MutableList<E> mutableList =
                list instanceof MutableList ? (MutableList<E>) list : ListAdapter.adapt(list);
        return new SynchronizedMutableList<E>(mutableList, lock);
    }

    @Override
    @GuardedBy("getLock()")
    protected MutableList<T> getDelegate()
    {
        return (MutableList<T>) super.getDelegate();
    }

    public MutableList<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public MutableList<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    public MutableList<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    public MutableList<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    public MutableList<T> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
        }
    }

    @Override
    public MutableList<T> clone()
    {
        synchronized (this.getLock())
        {
            return SynchronizedMutableList.of(this.getDelegate().clone());
        }
    }

    protected Object writeReplace()
    {
        return new SynchronizedCollectionSerializationProxy<T>(this.getDelegate());
    }

    public boolean addAll(int index, Collection<? extends T> collection)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().addAll(index, collection);
        }
    }

    public T set(int index, T element)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().set(index, element);
        }
    }

    public void add(int index, T element)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().add(index, element);
        }
    }

    public T remove(int index)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().remove(index);
        }
    }

    public MutableList<T> subList(int fromIndex, int toIndex)
    {
        synchronized (this.getLock())
        {
            return SynchronizedMutableList.of(this.getDelegate().subList(fromIndex, toIndex), this.getLock());
        }
    }

    public T get(int index)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().get(index);
        }
    }

    public int lastIndexOf(Object o)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().lastIndexOf(o);
        }
    }

    public ListIterator<T> listIterator()
    {
        return this.getDelegate().listIterator();
    }

    public ListIterator<T> listIterator(int index)
    {
        return this.getDelegate().listIterator(index);
    }

    public ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new SynchronizedParallelListIterable<T>(this.getDelegate().asParallel(executorService, batchSize), this.getLock());
    }

    public int binarySearch(T key, Comparator<? super T> comparator)
    {
        synchronized (this.getLock())
        {
            return Collections.binarySearch(this, key, comparator);
        }
    }

    public int binarySearch(T key)
    {
        synchronized (this.getLock())
        {
            return Collections.binarySearch((List<? extends Comparable<? super T>>) this, key);
        }
    }

    public int indexOf(Object o)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().indexOf(o);
        }
    }

    public MutableList<T> distinct()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().distinct();
        }
    }

    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().corresponds(other, predicate);
        }
    }

    public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEach(fromIndex, toIndex, procedure);
        }
    }

    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().takeWhile(predicate);
        }
    }

    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
        }
    }

    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().dropWhile(predicate);
        }
    }

    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partitionWhile(predicate);
        }
    }

    public int detectIndex(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().detectIndex(predicate);
        }
    }

    public int detectLastIndex(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().detectLastIndex(predicate);
        }
    }

    public MutableList<T> take(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().take(count);
        }
    }

    public MutableList<T> drop(int count)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().drop(count);
        }
    }

    public void reverseForEach(Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().reverseForEach(procedure);
        }
    }

    public MutableList<T> sortThis(Comparator<? super T> comparator)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThis(comparator);
            return this;
        }
    }

    public MutableList<T> sortThis()
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThis();
            return this;
        }
    }

    public <V extends Comparable<? super V>> MutableList<T> sortThisBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisBy(function);
            return this;
        }
    }

    public MutableList<T> sortThisByInt(IntFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByInt(function);
            return this;
        }
    }

    public MutableList<T> sortThisByBoolean(BooleanFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByBoolean(function);
            return this;
        }
    }

    public MutableList<T> sortThisByChar(CharFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByChar(function);
            return this;
        }
    }

    public MutableList<T> sortThisByByte(ByteFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByByte(function);
            return this;
        }
    }

    public MutableList<T> sortThisByShort(ShortFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByShort(function);
            return this;
        }
    }

    public MutableList<T> sortThisByFloat(FloatFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByFloat(function);
            return this;
        }
    }

    public MutableList<T> sortThisByLong(LongFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByLong(function);
            return this;
        }
    }

    public MutableList<T> sortThisByDouble(DoubleFunction<? super T> function)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().sortThisByDouble(function);
            return this;
        }
    }

    public MutableList<T> reverseThis()
    {
        synchronized (this.getLock())
        {
            this.getDelegate().reverseThis();
            return this;
        }
    }

    public LazyIterable<T> asReversed()
    {
        synchronized (this.getLock())
        {
            return ReverseIterable.adapt(this);
        }
    }

    public MutableList<T> toReversed()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toReversed();
        }
    }

    public MutableStack<T> toStack()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toStack();
        }
    }

    public ImmutableList<T> toImmutable()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().toImmutable();
        }
    }

    public MutableList<T> tap(Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.forEach(procedure);
            return this;
        }
    }

    public MutableList<T> select(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().select(predicate);
        }
    }

    public <P> MutableList<T> selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectWith(predicate, parameter);
        }
    }

    public MutableList<T> reject(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().reject(predicate);
        }
    }

    public <P> MutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectWith(predicate, parameter);
        }
    }

    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partition(predicate);
        }
    }

    public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }
    }

    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectInstancesOf(clazz);
        }
    }

    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectBoolean(booleanFunction);
        }
    }

    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectByte(byteFunction);
        }
    }

    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectChar(charFunction);
        }
    }

    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectDouble(doubleFunction);
        }
    }

    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectFloat(floatFunction);
        }
    }

    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectInt(intFunction);
        }
    }

    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectLong(longFunction);
        }
    }

    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectShort(shortFunction);
        }
    }

    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collect(function);
        }
    }

    public <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWith(function, parameter);
        }
    }

    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectIf(predicate, function);
        }
    }

    public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().flatCollect(function);
        }
    }

    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().groupBy(function);
        }
    }

    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().groupByEach(function);
        }
    }

    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().zip(that);
        }
    }

    public MutableList<Pair<T, Integer>> zipWithIndex()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().zipWithIndex();
        }
    }

    public MutableList<T> asUnmodifiable()
    {
        synchronized (this.getLock())
        {
            return UnmodifiableMutableList.of(this);
        }
    }

    public MutableList<T> asSynchronized()
    {
        return this;
    }
}
