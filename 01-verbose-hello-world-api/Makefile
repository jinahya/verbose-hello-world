.POSIX:

# Builds every C program under src/test/c into target/c/<name>.
#
# Portable to any POSIX make (GNU make, BSD make, ...); hence no wildcard, no pattern rule, and one
# explicit rule per program. Add a line to BINS, and a rule below, for each new program.
#
# Override on the command line; e.g., `make CC=gcc c`.
#
# See src/test/c/CMakeLists.txt for a cross-platform (including Windows) alternative.

CC     = cc
CFLAGS = -std=c23 -Wall -Wextra -O2
LDLIBS = -lm

SRC = src/test/c
OUT = target/c

BINS = $(OUT)/epsilon $(OUT)/fixed_integral $(OUT)/floating $(OUT)/ieee754 $(OUT)/integral

.PHONY: all c clean-c

all: c

c: $(BINS)

$(OUT)/epsilon: $(SRC)/epsilon.c
	mkdir -p $(OUT)
	$(CC) $(CFLAGS) -o $@ $(SRC)/epsilon.c $(LDLIBS)

$(OUT)/fixed_integral: $(SRC)/fixed_integral.c
	mkdir -p $(OUT)
	$(CC) $(CFLAGS) -o $@ $(SRC)/fixed_integral.c $(LDLIBS)

$(OUT)/floating: $(SRC)/floating.c
	mkdir -p $(OUT)
	$(CC) $(CFLAGS) -o $@ $(SRC)/floating.c $(LDLIBS)

$(OUT)/ieee754: $(SRC)/ieee754.c
	mkdir -p $(OUT)
	$(CC) $(CFLAGS) -o $@ $(SRC)/ieee754.c $(LDLIBS)

$(OUT)/integral: $(SRC)/integral.c
	mkdir -p $(OUT)
	$(CC) $(CFLAGS) -o $@ $(SRC)/integral.c $(LDLIBS)

clean-c:
	rm -rf $(OUT)
