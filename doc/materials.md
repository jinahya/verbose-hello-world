# Material



| Category               | Sign ($s$) | Biased Exponent ($e$) | Fraction ($f$) | Key Identifier / Requirement       |
|:-----------------------|:----------:|:---------------------:|:--------------:|:-----------------------------------|
| **Normal Numbers**     | 0 or 1     | $1 \le e \le 254$     | Any value      | $0 < e < \text{Max}$               |
| **Subnormal Numbers**  | 0 or 1     | **00000000**          | **Non-zero**   | $e = 0$, $f > 0$                   |
| **Zero (+/- 0)**       | 0 or 1     | **00000000**          | **000...000**  | $e = 0$, $f = 0$                   |
| **Infinity (+/- ∞)**   | 0 or 1     | **11111111**          | **000...000**  | $e = \text{Max}$, $f = 0$          |
| **NaN (General)**      | 0 or 1     | **11111111**          | **Non-zero**   | $e = \text{Max}$, $f > 0$          |
| ↳ **qNaN (Quiet)**     | 0 or 1     | **11111111**          | **1**xx...xxx  | **Fraction MSB = 1**               |
| ↳ **sNaN (Signaling)** | 0 or 1     | **11111111**          | **0**xx...xx**1**| **Fraction MSB = 0** (Rest $\neq$ 0)|


## Teaching order for IEEE 754 floating-point categories

When introducing IEEE 754 to others, present the categories in this order — from simplest bit pattern to most complex:

1. **Zeros** — all bits zero (sign bit distinguishes +0 and −0); the trivial anchor that introduces the sign bit
2. **Normal numbers** — the common case; teaches the biased exponent and the implicit leading 1 in the mantissa
3. **Subnormal numbers** — exponent all-zero, no implicit leading 1; the "graceful underflow" exception to the normal rule
4. **Infinities** — exponent all-one, mantissa all-zero; overflow result, introduces the special-exponent sentinel
5. **NaNs** — exponent all-one, mantissa non-zero; the most exotic, builds on infinity's special exponent

**Rationale**: teach the rule first (normal numbers), then the edge cases that modify or break it
(subnormal → infinity → NaN), bookended by zeros as the simplest anchor.

---

## IEEE 754 binary format

### Bit field terminology

| Raw Bit Field | Official IEEE 754 Name | Common "Math" Name |
|---|---|---|
| Sign | Sign | Sign |
| Exponent | Biased Exponent | Characteristic |
| Fraction | Trailing Significand | Mantissa |

### `float` layout (32 bits)

```
bit: 31 | 30-23   | 22-0
     sign  exponent  fraction
     1 bit  8 bits   23 bits
```

### `double` layout (64 bits)

```
bit: 63 | 62-52    | 51-0
     sign  exponent   fraction
     1 bit  11 bits   52 bits
```

---

## Category definitions and bit patterns

### 1. Zeros

Exponent = all zeros, fraction = all zeros. Sign bit distinguishes +0 and −0.

| value | sign | exponent | fraction |
|---|---|---|---|
| `+0.0f` / `+0.0d` | `0` | all zeros | all zeros |
| `-0.0f` / `-0.0d` | `1` | all zeros | all zeros |

### 2. Normal numbers

Exponent has at least one non-zero bit and is not all-ones. Implicit leading `1` in the significand.

| Java constant | sign | exponent | fraction | note |
|---|---|---|---|---|
| `+Float.MIN_NORMAL` | `0` | `00000001` | all zeros | MIN_EXPONENT = −126 |
| `+1.0f` | `0` | `01111111` | all zeros | biased exp = 127, value = 0 |
| `Math.ulp(1.0f)` | `0` | `01101000` | all zeros | FLT_EPSILON = 2^−23 |
| `+Float.MAX_VALUE` | `0` | `11111110` | all ones | MAX_EXPONENT = 127 |
| `+Double.MIN_NORMAL` | `0` | `00000000001` | all zeros | MIN_EXPONENT = −1022 |
| `+1.0d` | `0` | `01111111111` | all zeros | biased exp = 1023, value = 0 |
| `Math.ulp(1.0d)` | `0` | `01111001011` | all zeros | DBL_EPSILON = 2^−52 |
| `+Double.MAX_VALUE` | `0` | `11111111110` | all ones | MAX_EXPONENT = 1023 |

### 3. Subnormal (denormal) numbers

Exponent = all zeros, fraction ≠ zero. No implicit leading `1`. Lower boundary is zero (fraction also zero).

| Java expression | sign | exponent | fraction | note |
|---|---|---|---|---|
| `+Float.MIN_VALUE` | `0` | `00000000` | `000...001` | smallest float subnormal |
| `Float.intBitsToFloat(0x007FFFFF)` | `0` | `00000000` | `111...111` | largest float subnormal |
| `+Double.MIN_VALUE` | `0` | `00000000000` | `000...001` | smallest double subnormal |
| `Double.longBitsToDouble(0x000FFFFFFFFFFFFFL)` | `0` | `00000000000` | `111...111` | largest double subnormal |

### 4. Infinities

Exponent = all ones, fraction = all zeros.

| Java constant | sign | exponent | fraction |
|---|---|---|---|
| `Float.POSITIVE_INFINITY` | `0` | all ones | all zeros |
| `Float.NEGATIVE_INFINITY` | `1` | all ones | all zeros |
| `Double.POSITIVE_INFINITY` | `0` | all ones | all zeros |
| `Double.NEGATIVE_INFINITY` | `1` | all ones | all zeros |

### 5. NaNs

Exponent = all ones, fraction ≠ zero.

- **Quiet NaN (qNaN)**: fraction MSB = `1` — propagates silently
- **Signaling NaN (sNaN)**: fraction MSB = `0`, at least one other fraction bit non-zero — may trigger signal

#### Quiet NaNs (qNaN) — fraction MSB = `1`

| Java expression | sign | exponent | fraction MSB | remaining fraction |
|---|---|---|---|---|
| `Float.NaN` | `0` | all ones | `1` | `000...000` |
| `Float.intBitsToFloat(0x7FC00000)` | `0` | all ones | `1` | `000...000` |
| `Float.intBitsToFloat(0xFFC00000)` | `1` | all ones | `1` | `000...000` |
| `Double.NaN` | `0` | all ones | `1` | `000...000` |
| `Double.longBitsToDouble(0x7FF8000000000000L)` | `0` | all ones | `1` | `000...000` |
| `Double.longBitsToDouble(0xFFF8000000000000L)` | `1` | all ones | `1` | `000...000` |

#### Signaling NaNs (sNaN) — fraction MSB = `0`, rest non-zero

| Java expression | sign | exponent | fraction MSB | remaining fraction |
|---|---|---|---|---|
| `Float.intBitsToFloat(0x7F800001)` | `0` | all ones | `0` | `000...001` (bit 0) |
| `Float.intBitsToFloat(0x7F800002)` | `0` | all ones | `0` | `000...010` (bit 1) |
| `Float.intBitsToFloat(0x7FA00000)` | `0` | all ones | `0` | `010...000` (bit 21) |
| `Float.intBitsToFloat(0x7FBFFFFF)` | `0` | all ones | `0` | `011...111` (all but MSB) |
| `Double.longBitsToDouble(0x7FF0000000000001L)` | `0` | all ones | `0` | `000...001` (bit 0) |
| `Double.longBitsToDouble(0x7FF0000000000002L)` | `0` | all ones | `0` | `000...010` (bit 1) |
| `Double.longBitsToDouble(0x7FF4000000000000L)` | `0` | all ones | `0` | `010...000` (bit 50) |
| `Double.longBitsToDouble(0x7FF7FFFFFFFFFFFFL)` | `0` | all ones | `0` | `011...111` (all but MSB) |

---

## IEEE 754 float/double Java constants by category

### Normal number constants

Normal numbers have at least one non-zero bit in the exponent field (other than all ones).

| Constant | Description |
|---|---|
| `Float.MAX_VALUE` | Largest positive finite value |
| `Float.MIN_NORMAL` | Smallest positive normal value |
| `Float.MAX_EXPONENT` | Maximum exponent a finite float may have (127) |
| `Float.MIN_EXPONENT` | Minimum exponent a normalized float may have (-126) |

### Subnormal number constants

Subnormal numbers occur when the exponent bits are all zero, allowing the representation of
values closer to zero than `MIN_NORMAL` at the cost of precision.

| Constant | Description |
|---|---|
| `Float.MIN_VALUE` | Smallest positive nonzero value — this is a subnormal value |
| `0.0f` | Lower boundary of subnormals (exponent and fraction both zero) |
