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

| "foo" | Sequence | Action |
|:-----:|:--------:| ------ |
| 3     | 0        | Do     |
| 4     | 1        | Do     |
| 3     | 2        | Undo   |
| 4     | 3        | Redo   |
