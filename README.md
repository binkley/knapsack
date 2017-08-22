# Knapsack

A Kotlin library for do/undo/redo maps with SQLite and JGit

## Design

`Knapsack` is a map view of a history of maps relying on CSV text files
stored in Git to provide history, and SQLite over the files providing search
and query features.

For example, if the "foo" key has a value of `3`, and later has a value of
`4`, you can undo the change to "foo" to restore the value to `3`, and then
redo the change to return the value to `4`, all without loss of the history
of the value of "foo".  In this example the history would be:

| Sequence | Action | "foo" |
|:--------:|:------:|:-----:|
| 0        | Do     | 3     |
| 1        | Do     | 4     |
| 2        | Undo   | 3     |
| 3        | Redo   | 4     |

## Technology

* [JGit](https://github.com/eclipse/jgit) - Git for Java
* [Kotlin Exposed](https://github.com/JetBrains/Exposed) - JDBC for Kotlin
* [HyperSQL](http://hsqldb.org/doc/guide/texttables-chapt.html) - Local,
  text-based database
