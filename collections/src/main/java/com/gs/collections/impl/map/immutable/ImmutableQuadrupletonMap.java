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

package com.gs.collections.impl.map.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.tuple.Tuples;

final class ImmutableQuadrupletonMap<K, V>
        extends AbstractImmutableMap<K, V>
        implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final K key1;
    private final V value1;
    private final K key2;
    private final V value2;
    private final K key3;
    private final V value3;
    private final K key4;
    private final V value4;

    ImmutableQuadrupletonMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        this.key1 = key1;
        this.value1 = value1;
        this.key2 = key2;
        this.value2 = value2;
        this.key3 = key3;
        this.value3 = value3;
        this.key4 = key4;
        this.value4 = value4;
    }

    public RichIterable<K> keysView()
    {
        return Lists.immutable.with(this.key1, this.key2, this.key3, this.key4).asLazy();
    }

    public RichIterable<V> valuesView()
    {
        return Lists.immutable.with(this.value1, this.value2, this.value3, this.value4).asLazy();
    }

    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return Lists.immutable.with(
                Tuples.pair(this.key1, this.value1),
                Tuples.pair(this.key2, this.value2),
                Tuples.pair(this.key3, this.value3),
                Tuples.pair(this.key4, this.value4)).asLazy();
    }

    public int size()
    {
        return 4;
    }

    public boolean containsKey(Object key)
    {
        return Comparators.nullSafeEquals(this.key4, key)
                || Comparators.nullSafeEquals(this.key3, key)
                || Comparators.nullSafeEquals(this.key2, key)
                || Comparators.nullSafeEquals(this.key1, key);
    }

    public boolean containsValue(Object value)
    {
        return Comparators.nullSafeEquals(this.value4, value)
                || Comparators.nullSafeEquals(this.value3, value)
                || Comparators.nullSafeEquals(this.value2, value)
                || Comparators.nullSafeEquals(this.value1, value);
    }

    public V get(Object key)
    {
        if (Comparators.nullSafeEquals(this.key4, key))
        {
            return this.value4;
        }
        if (Comparators.nullSafeEquals(this.key3, key))
        {
            return this.value3;
        }
        if (Comparators.nullSafeEquals(this.key2, key))
        {
            return this.value2;
        }
        if (Comparators.nullSafeEquals(this.key1, key))
        {
            return this.value1;
        }
        return null;
    }

    public Set<K> keySet()
    {
        return Sets.immutable.with(this.key1, this.key2, this.key3, this.key4).castToSet();
    }

    public Collection<V> values()
    {
        return Lists.immutable.with(this.value1, this.value2, this.value3, this.value4).castToList();
    }

    @Override
    public int hashCode()
    {
        return this.keyAndValueHashCode(this.key1, this.value1)
                + this.keyAndValueHashCode(this.key2, this.value2)
                + this.keyAndValueHashCode(this.key3, this.value3)
                + this.keyAndValueHashCode(this.key4, this.value4);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Map))
        {
            return false;
        }
        Map<K, V> that = (Map<K, V>) other;
        return that.size() == this.size()
                && this.keyAndValueEquals(this.key1, this.value1, that)
                && this.keyAndValueEquals(this.key2, this.value2, that)
                && this.keyAndValueEquals(this.key3, this.value3, that)
                && this.keyAndValueEquals(this.key4, this.value4, that);
    }

    @Override
    public String toString()
    {
        return "{"
                + this.key1 + '=' + this.value1 + ", "
                + this.key2 + '=' + this.value2 + ", "
                + this.key3 + '=' + this.value3 + ", "
                + this.key4 + '=' + this.value4 + '}';
    }

    @Override
    public ImmutableMap<V, K> flipUniqueValues()
    {
        return Maps.immutable.with(this.value1, this.key1, this.value2, this.key2, this.value3, this.key3, this.value4, this.key4);
    }

    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        procedure.value(this.key1, this.value1);
        procedure.value(this.key2, this.value2);
        procedure.value(this.key3, this.value3);
        procedure.value(this.key4, this.value4);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        procedure.value(this.key1);
        procedure.value(this.key2);
        procedure.value(this.key3);
        procedure.value(this.key4);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        procedure.value(this.value1);
        procedure.value(this.value2);
        procedure.value(this.value3);
        procedure.value(this.value4);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        objectIntProcedure.value(this.value1, 0);
        objectIntProcedure.value(this.value2, 1);
        objectIntProcedure.value(this.value3, 2);
        objectIntProcedure.value(this.value4, 3);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        procedure.value(this.value1, parameter);
        procedure.value(this.value2, parameter);
        procedure.value(this.value3, parameter);
        procedure.value(this.value4, parameter);
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        Pair<K2, V2> pair1 = function.value(this.key1, this.value1);
        Pair<K2, V2> pair2 = function.value(this.key2, this.value2);
        Pair<K2, V2> pair3 = function.value(this.key3, this.value3);
        Pair<K2, V2> pair4 = function.value(this.key4, this.value4);

        return Maps.immutable.with(pair1.getOne(), pair1.getTwo(), pair2.getOne(), pair2.getTwo(), pair3.getOne(), pair3.getTwo(), pair4.getOne(), pair4.getTwo());
    }

    @Override
    public <R> ImmutableMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return Maps.immutable.with(
                this.key1, function.value(this.key1, this.value1),
                this.key2, function.value(this.key2, this.value2),
                this.key3, function.value(this.key3, this.value3),
                this.key4, function.value(this.key4, this.value4));
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        if (predicate.accept(this.key1, this.value1))
        {
            return Tuples.pair(this.key1, this.value1);
        }
        if (predicate.accept(this.key2, this.value2))
        {
            return Tuples.pair(this.key2, this.value2);
        }
        if (predicate.accept(this.key3, this.value3))
        {
            return Tuples.pair(this.key3, this.value3);
        }
        if (predicate.accept(this.key4, this.value4))
        {
            return Tuples.pair(this.key4, this.value4);
        }
        return null;
    }

    @Override
    public ImmutableMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return this.filter(predicate);
    }

    @Override
    public ImmutableMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return this.filter(Predicates2.not(predicate));
    }

    private ImmutableMap<K, V> filter(Predicate2<? super K, ? super V> predicate)
    {
        int result = 0;

        if (predicate.accept(this.key1, this.value1))
        {
            result |= 1;
        }
        if (predicate.accept(this.key2, this.value2))
        {
            result |= 2;
        }
        if (predicate.accept(this.key3, this.value3))
        {
            result |= 4;
        }
        if (predicate.accept(this.key4, this.value4))
        {
            result |= 8;
        }

        switch (result)
        {
            case 1:
                return Maps.immutable.with(this.key1, this.value1);
            case 2:
                return Maps.immutable.with(this.key2, this.value2);
            case 3:
                return Maps.immutable.with(this.key1, this.value1, this.key2, this.value2);
            case 4:
                return Maps.immutable.with(this.key3, this.value3);
            case 5:
                return Maps.immutable.with(this.key1, this.value1, this.key3, this.value3);
            case 6:
                return Maps.immutable.with(this.key2, this.value2, this.key3, this.value3);
            case 7:
                return Maps.immutable.with(this.key1, this.value1, this.key2, this.value2, this.key3, this.value3);
            case 8:
                return Maps.immutable.with(this.key4, this.value4);
            case 9:
                return Maps.immutable.with(this.key1, this.value1, this.key4, this.value4);
            case 10:
                return Maps.immutable.with(this.key2, this.value2, this.key4, this.value4);
            case 11:
                return Maps.immutable.with(this.key1, this.value1, this.key2, this.value2, this.key4, this.value4);
            case 12:
                return Maps.immutable.with(this.key3, this.value3, this.key4, this.value4);
            case 13:
                return Maps.immutable.with(this.key1, this.value1, this.key3, this.value3, this.key4, this.value4);
            case 14:
                return Maps.immutable.with(this.key2, this.value2, this.key3, this.value3, this.key4, this.value4);
            case 15:
                return Maps.immutable.with(this.key1, this.value1, this.key2, this.value2, this.key3, this.value3, this.key4, this.value4);
            default:
                return Maps.immutable.empty();
        }
    }

    private Object writeReplace()
    {
        return new ImmutableMapSerializationProxy<K, V>(this);
    }
}
