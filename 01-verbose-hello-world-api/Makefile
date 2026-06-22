CC      ?= cc
CFLAGS  ?= -Wall -Wextra -O2
LDLIBS  ?= -lm

C_SRC_DIR  := src/test/c
TARGET_DIR := target/c

# every *.c under the C source dir becomes a binary of the same base name
C_SRCS := $(wildcard $(C_SRC_DIR)/*.c)
C_BINS := $(patsubst $(C_SRC_DIR)/%.c,$(TARGET_DIR)/%,$(C_SRCS))

.PHONY: all c clean-c

all: c

c: $(C_BINS)

$(TARGET_DIR)/%: $(C_SRC_DIR)/%.c | $(TARGET_DIR)
	$(CC) $(CFLAGS) $< -o $@ $(LDLIBS)

$(TARGET_DIR):
	mkdir -p $(TARGET_DIR)

clean-c:
	rm -rf $(TARGET_DIR)
