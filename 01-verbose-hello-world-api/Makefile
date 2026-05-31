CC          = cc
CFLAGS      = -std=c99 -Wall -Wextra
LDFLAGS     = -lm

SRC_DIR     = src/main/c
TARGET_DIR  = target
SRCS        = $(wildcard $(SRC_DIR)/*.c)
BINS        = $(patsubst $(SRC_DIR)/%.c, $(TARGET_DIR)/%, $(SRCS))

TEST_SRC_DIR    = src/test/c
TEST_TARGET_DIR = $(TARGET_DIR)/test
TEST_SRCS       = $(wildcard $(TEST_SRC_DIR)/*.c)
TEST_BINS       = $(patsubst $(TEST_SRC_DIR)/%.c, $(TEST_TARGET_DIR)/%, $(TEST_SRCS))
TEST_CFLAGS     = $(CFLAGS) -pthread
TEST_LDFLAGS    = $(LDFLAGS) -pthread

ECHO_HDR        = $(TEST_SRC_DIR)/echo_.h
ECHO_S_BINS     = $(patsubst $(TEST_SRC_DIR)/%.c, $(TEST_TARGET_DIR)/%, $(wildcard $(TEST_SRC_DIR)/echo_s_*.c))
ECHO_A_BINS     = $(patsubst $(TEST_SRC_DIR)/%.c, $(TEST_TARGET_DIR)/%, $(wildcard $(TEST_SRC_DIR)/echo_a_*.c))

.PHONY: all run test echo-s echo-a echo-all clean

all: $(BINS) $(TEST_BINS)

$(TARGET_DIR):
	mkdir -p $(TARGET_DIR)

$(TEST_TARGET_DIR):
	mkdir -p $(TEST_TARGET_DIR)

$(BINS): $(TARGET_DIR)/%: $(SRC_DIR)/%.c | $(TARGET_DIR)
	$(CC) $(CFLAGS) -o $@ $< $(LDFLAGS)

$(TEST_BINS): $(TEST_TARGET_DIR)/%: $(TEST_SRC_DIR)/%.c | $(TEST_TARGET_DIR)
	$(CC) $(TEST_CFLAGS) -o $@ $< $(TEST_LDFLAGS)

$(ECHO_S_BINS) $(ECHO_A_BINS): $(ECHO_HDR)

run: $(BINS)
	@for bin in $(BINS); do \
		echo "==> Running: $$bin"; \
		./$$bin; \
		echo; \
	done

test: $(TEST_BINS)
	@for bin in $(TEST_BINS); do \
		echo "==> Running: $$bin"; \
		./$$bin; \
		echo; \
	done

echo-s: $(ECHO_S_BINS)
	@for bin in $(ECHO_S_BINS); do \
		echo "==> Running: $$bin"; \
		./$$bin; \
		echo; \
	done

echo-a: $(ECHO_A_BINS)
	@for bin in $(ECHO_A_BINS); do \
		echo "==> Running: $$bin"; \
		./$$bin; \
		echo; \
	done

echo-all: echo-s echo-a

clean:
	rm -f $(BINS) $(TEST_BINS)
