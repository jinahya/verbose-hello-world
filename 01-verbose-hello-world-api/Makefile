CC         = cc
CFLAGS     = -std=c99 -Wall -Wextra
LDFLAGS    = -lm

SRC_DIR    = src/main/c
TARGET_DIR = target
SRCS       = $(wildcard $(SRC_DIR)/*.c)
BINS       = $(patsubst $(SRC_DIR)/%.c, $(TARGET_DIR)/%, $(SRCS))

.PHONY: all run clean

all: $(BINS)

$(TARGET_DIR):
	mkdir -p $(TARGET_DIR)

$(BINS): $(TARGET_DIR)/%: $(SRC_DIR)/%.c | $(TARGET_DIR)
	$(CC) $(CFLAGS) -o $@ $< $(LDFLAGS)

run: all
	@for bin in $(BINS); do \
		echo "==> Running: $$bin"; \
		./$$bin; \
		echo; \
	done

clean:
	rm -f $(BINS)
