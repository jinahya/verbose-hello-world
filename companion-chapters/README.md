# companion-chapters

The companions of *Verbose Hello World: Java I/O with 'hello, world'*: for every package the book covers, what the book
leaves out of it, rendered alone as a PDF and an HTML page named after the package, dots to hyphens, with `-companion`:
`java-io-companion.pdf` / `java-io-companion.html`, …, `org-reactivestreams-companion.pdf` / `.html`. A package with
nothing left out yet still has its (empty) pair.

| Package | Contents |
|---|---|
| `java.sql` | the `java.sql` chapter (`JdbcHelloWorld`) |
| `java.util` | `BitSet` use cases (Jaccard, Hamming, sieve, Bloom filter, Bitap) |

They are generated from the book's manuscript, in the book's repository, by every build of the book, and are not
edited here:

```shell
$ ./mvnw generate-resources                # the book, then these
$ ./mvnw -Pcompanion generate-resources    # these only
```
