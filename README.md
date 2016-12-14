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
