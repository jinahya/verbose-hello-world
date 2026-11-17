# companion-chapters

The companion chapters of *Verbose Hello World: Java I/O with 'hello, world'*: the chapters the book leaves out, each
rendered alone as a PDF and an HTML page.

| File | Chapter |
|---|---|
| `java-sql.pdf`, `java-sql.html` | `java.sql` (`JdbcHelloWorld`) |
| `java-util-bitset.pdf`, `java-util-bitset.html` | `java.util.BitSet` use cases (Jaccard, Hamming, sieve, Bloom filter, Bitap) |

They are generated from the book's manuscript, in the book's repository, by every build of the book, and are not
edited here:

```shell
$ ./mvnw generate-resources                # the book, then these
$ ./mvnw -Pcompanion generate-resources    # these only
```
