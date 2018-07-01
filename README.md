# immutable-collections
Persistent collections for the Java language.

## Why?

Immutable collections provide many desirable features. They can be
freely passed around without worrying about them being modified. Furthermore, they
are thread safe by definition. 

There are no immutable collections in the Java standard library. At least
not that are immutable by design. You may do:

```Java
List<Foo> immutable = Collections.unmodifiableList(list);
```

But for a user of that list there is no way to tell that calling, for instance, add
will yield a runtime error. Furthermore, to add an element to that list, it must be copied
which will be slow if the list is large.

## Examples

Lists
```Java
ImmutableList<Integer> list1 = ImmutableList.of(1, 2, 3);
ImmutableList<Integer> list2 = list1.add(4); // doesn't change list1
assert !list1.contains(4);
assert list2.contains(4);
```

Sets
```Java
ImmutableSet<Integer> set1 = ImmutableSet.of(1, 2, 3);
ImmutableSet<Integer> set2 = set1.add(4); // doesn't change set1
assert !set1.contains(4);
assert set2.contains(4);
```

Maps
```Java
ImmutableMap<String, String> map1 = ImmutableMap.singletonMap("foo", "bar");
ImmutableMap<String, String> map2 = map1.put("baz", "qux"); // doesn't change map1
assert !map1.contains("baz");
assert map2.contains("baz");
```
