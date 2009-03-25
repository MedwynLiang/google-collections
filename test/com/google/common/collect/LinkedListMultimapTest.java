/*
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.collect.testing.IteratorFeature.MODIFIABLE;
import com.google.common.collect.testing.IteratorTester;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Tests for {@code LinkedListMultimap}.
 *
 * @author Mike Bostock
 */
public class LinkedListMultimapTest extends AbstractListMultimapTest {

  @Override protected LinkedListMultimap<String, Integer> create() {
    return LinkedListMultimap.create();
  }

  public void testCreateFromMultimap() {
    Multimap<String, Integer> multimap = createSample();
    LinkedListMultimap<String, Integer> copy =
        LinkedListMultimap.create(multimap);
    assertEquals(multimap, copy);
  }

  public void testCreateFromSize() {
    LinkedListMultimap<String, Integer> multimap
        = LinkedListMultimap.create(20);
    multimap.put("foo", 1);
    multimap.put("bar", 2);
    multimap.put("foo", 3);
    assertEquals(ImmutableList.of(1, 3), multimap.get("foo"));
  }

  public void testCreateFromIllegalSize() {
    try {
      LinkedListMultimap.create(-20);
      fail();
    } catch (IllegalArgumentException expected) {}
  }

  /* "Linked" prefix avoids collision with AbstractMultimapTest. */

  public void testLinkedToString() {
    assertEquals("{foo=[3, -1, 2, 4, 1], bar=[1, 2, 3, 1]}",
        createSample().toString());
  }

  public void testLinkedGetAdd() {
    LinkedListMultimap<String, Integer> map = create();
    map.put("bar", 1);
    Collection<Integer> foos = map.get("foo");
    foos.add(2);
    foos.add(3);
    map.put("bar", 4);
    map.put("foo", 5);
    assertEquals("{bar=[1, 4], foo=[2, 3, 5]}", map.toString());
    assertEquals("[bar=1, foo=2, foo=3, bar=4, foo=5]",
        map.entries().toString());
  }

  public void testLinkedGetInsert() {
    ListMultimap<String, Integer> map = create();
    map.put("bar", 1);
    List<Integer> foos = map.get("foo");
    foos.add(2);
    foos.add(0, 3);
    map.put("bar", 4);
    map.put("foo", 5);
    assertEquals("{bar=[1, 4], foo=[3, 2, 5]}", map.toString());
    assertEquals("[bar=1, foo=3, foo=2, bar=4, foo=5]",
        map.entries().toString());
  }

  public void testLinkedPutInOrder() {
    Multimap<String, Integer> map = create();
    map.put("foo", 1);
    map.put("bar", 2);
    map.put("bar", 3);
    assertEquals("{foo=[1], bar=[2, 3]}", map.toString());
    assertEquals("[foo=1, bar=2, bar=3]", map.entries().toString());
  }

  public void testLinkedPutOutOfOrder() {
    Multimap<String, Integer> map = create();
    map.put("bar", 1);
    map.put("foo", 2);
    map.put("bar", 3);
    assertEquals("{bar=[1, 3], foo=[2]}", map.toString());
    assertEquals("[bar=1, foo=2, bar=3]", map.entries().toString());
  }

  public void testLinkedPutAllMultimap() {
    Multimap<String, Integer> src = create();
    src.put("bar", 1);
    src.put("foo", 2);
    src.put("bar", 3);
    Multimap<String, Integer> dst = create();
    dst.putAll(src);
    assertEquals("{bar=[1, 3], foo=[2]}", dst.toString());
    assertEquals("[bar=1, foo=2, bar=3]", src.entries().toString());
  }

  public void testLinkedReplaceValues() {
    Multimap<String, Integer> map = create();
    map.put("bar", 1);
    map.put("foo", 2);
    map.put("bar", 3);
    map.put("bar", 4);
    assertEquals("{bar=[1, 3, 4], foo=[2]}", map.toString());
    map.replaceValues("bar", asList(1, 2));
    assertEquals("[bar=1, foo=2, bar=2]", map.entries().toString());
    assertEquals("{bar=[1, 2], foo=[2]}", map.toString());
  }

  public void testLinkedClear() {
    ListMultimap<String, Integer> map = create();
    map.put("foo", 1);
    map.put("foo", 2);
    map.put("bar", 3);
    List<Integer> foos = map.get("foo");
    Collection<Integer> values = map.values();
    assertEquals(asList(1, 2), foos);
    assertContentsInOrder(values, 1, 2, 3);
    map.clear();
    assertEquals(Collections.emptyList(), foos);
    assertContentsInOrder(values);
    assertEquals("[]", map.entries().toString());
    assertEquals("{}", map.toString());
  }

  public void testLinkedKeySet() {
    Multimap<String, Integer> map = create();
    map.put("bar", 1);
    map.put("foo", 2);
    map.put("bar", 3);
    map.put("bar", 4);
    assertEquals("[bar, foo]", map.keySet().toString());
    map.keySet().remove("bar");
    assertEquals("{foo=[2]}", map.toString());
  }

  public void testLinkedKeys() {
    Multimap<String, Integer> map = create();
    map.put("bar", 1);
    map.put("foo", 2);
    map.put("bar", 3);
    map.put("bar", 4);
    assertEquals("[bar=1, foo=2, bar=3, bar=4]",
        map.entries().toString());
    assertContentsInOrder(map.keys(), "bar", "foo", "bar", "bar");
    map.keys().remove("bar"); // bar is no longer the first key!
    assertEquals("{foo=[2], bar=[3, 4]}", map.toString());
  }

  public void testLinkedValues() {
    Multimap<String, Integer> map = create();
    map.put("bar", 1);
    map.put("foo", 2);
    map.put("bar", 3);
    map.put("bar", 4);
    assertEquals("[1, 2, 3, 4]", map.values().toString());
    map.values().remove(2);
    assertEquals("{bar=[1, 3, 4]}", map.toString());
  }

  public void testLinkedEntries() {
    Multimap<String, Integer> map = create();
    map.put("bar", 1);
    map.put("foo", 2);
    map.put("bar", 3);
    Iterator<Map.Entry<String, Integer>> entries = map.entries().iterator();
    Map.Entry<String, Integer> entry = entries.next();
    assertEquals("bar", entry.getKey());
    assertEquals(1, (int) entry.getValue());
    entry = entries.next();
    assertEquals("foo", entry.getKey());
    assertEquals(2, (int) entry.getValue());
    entry.setValue(4);
    entry = entries.next();
    assertEquals("bar", entry.getKey());
    assertEquals(3, (int) entry.getValue());
    assertFalse(entries.hasNext());
    entries.remove();
    assertEquals("{bar=[1], foo=[4]}", map.toString());
  }

  public void testLinkedAsMapEntries() {
    Multimap<String, Integer> map = create();
    map.put("bar", 1);
    map.put("foo", 2);
    map.put("bar", 3);
    Iterator<Map.Entry<String, Collection<Integer>>> entries
        = map.asMap().entrySet().iterator();
    Map.Entry<String, Collection<Integer>> entry = entries.next();
    assertEquals("bar", entry.getKey());
    assertContentsInOrder(entry.getValue(), 1, 3);
    try {
      entry.setValue(Arrays.<Integer>asList());
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    entries.remove(); // clear
    entry = entries.next();
    assertEquals("foo", entry.getKey());
    assertContentsInOrder(entry.getValue(), 2);
    assertFalse(entries.hasNext());
    assertEquals("{foo=[2]}", map.toString());
  }

  /**
   * Test calling setValue() on an entry returned by multimap.entries().
   */
  @Override public void testEntrySetValue() {
    ListMultimap<String, Integer> multimap = create();
    multimap.put("foo", 1);
    multimap.put("bar", 3);
    Collection<Map.Entry<String, Integer>> entries = multimap.entries();
    Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
    Map.Entry<String, Integer> entrya = iterator.next();
    Map.Entry<String, Integer> entryb = iterator.next();

    int oldValue = entrya.setValue(2);
    assertEquals(1, oldValue);
    assertFalse(multimap.containsEntry("foo", 1));
    assertTrue(multimap.containsEntry("foo", 2));
    assertTrue(multimap.containsEntry("bar", 3));
    assertEquals(2, (int) entrya.getValue());
    assertEquals(3, (int) entryb.getValue());
  }

  public void testEntriesAfterMultimapUpdate() {
    ListMultimap<String, Integer> multimap = create();
    multimap.put("foo", 2);
    multimap.put("bar", 3);
    Collection<Map.Entry<String, Integer>> entries = multimap.entries();
    Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
    Map.Entry<String, Integer> entrya = iterator.next();
    Map.Entry<String, Integer> entryb = iterator.next();

    assertEquals(2, (int) multimap.get("foo").set(0, 4));
    assertFalse(multimap.containsEntry("foo", 2));
    assertTrue(multimap.containsEntry("foo", 4));
    assertTrue(multimap.containsEntry("bar", 3));
    assertEquals(4, (int) entrya.getValue());
    assertEquals(3, (int) entryb.getValue());

    assertTrue(multimap.put("foo", 5));
    assertTrue(multimap.containsEntry("foo", 5));
    assertTrue(multimap.containsEntry("foo", 4));
    assertTrue(multimap.containsEntry("bar", 3));
    assertEquals(4, (int) entrya.getValue());
    assertEquals(3, (int) entryb.getValue());
  }

  @SuppressWarnings("unchecked")
  public void testEntriesIteration() throws Exception {
    List<Entry<String, Integer>> list = Lists.newArrayList(
        Maps.immutableEntry("foo", 2),
        Maps.immutableEntry("foo", 3),
        Maps.immutableEntry("bar", 4),
        Maps.immutableEntry("bar", 5),
        Maps.immutableEntry("foo", 6));

    new IteratorTester<Entry<String, Integer>>(6, MODIFIABLE, list,
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<Entry<String, Integer>> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        return multimap.entries().iterator();
      }

      @Override protected void verify(List<Entry<String, Integer>> elements) {
        assertEquals(elements, Lists.newArrayList(multimap.entries()));
      }
    }.test();
  }

  public void testKeysIteration() throws Exception {
    new IteratorTester<String>(6, MODIFIABLE, newArrayList("foo", "foo", "bar",
        "bar", "foo"), IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<String> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        return multimap.keys().iterator();
      }

      @Override protected void verify(List<String> elements) {
        assertEquals(elements, Lists.newArrayList(multimap.keys()));
      }
    }.test();
  }

  public void testValuesIteration() throws Exception {
    new IteratorTester<Integer>(6, MODIFIABLE, newArrayList(2, 3, 4, 5, 6),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<Integer> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        return multimap.values().iterator();
      }

      @Override protected void verify(List<Integer> elements) {
        assertEquals(elements, Lists.newArrayList(multimap.values()));
      }
    }.test();
  }

  public void testKeySetIteration() throws Exception {
    new IteratorTester<String>(6, MODIFIABLE, newLinkedHashSet(asList(
        "foo", "bar", "baz", "dog", "cat")),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<String> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        multimap.putAll("baz", asList(7, 8));
        multimap.putAll("dog", asList(9));
        multimap.putAll("bar", asList(10, 11));
        multimap.putAll("cat", asList(12, 13, 14));
        return multimap.keySet().iterator();
      }

      @Override protected void verify(List<String> elements) {
        assertEquals(newHashSet(elements), multimap.keySet());
      }
    }.test();
  }

  @SuppressWarnings("unchecked")
  public void testAsSetIteration() throws Exception {
    Set<Entry<String, Collection<Integer>>> set = Sets.newLinkedHashSet(asList(
        Maps.immutableEntry("foo",
            (Collection<Integer>) asList(2, 3, 6)),
        Maps.immutableEntry("bar",
            (Collection<Integer>) asList(4, 5, 10, 11)),
        Maps.immutableEntry("baz",
            (Collection<Integer>) asList(7, 8)),
        Maps.immutableEntry("dog",
            (Collection<Integer>) asList(9)),
        Maps.immutableEntry("cat",
            (Collection<Integer>) asList(12, 13, 14))
    ));

    new IteratorTester<Entry<String, Collection<Integer>>>(6, MODIFIABLE, set,
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<Entry<String, Collection<Integer>>>
          newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        multimap.putAll("baz", asList(7, 8));
        multimap.putAll("dog", asList(9));
        multimap.putAll("bar", asList(10, 11));
        multimap.putAll("cat", asList(12, 13, 14));
        return multimap.asMap().entrySet().iterator();
      }

      @Override protected void verify(
          List<Entry<String, Collection<Integer>>> elements) {
        assertEquals(newHashSet(elements), multimap.asMap().entrySet());
      }
    }.test();
  }
}