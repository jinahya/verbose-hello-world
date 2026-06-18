CC      ?= cc
CFLAGS  ?= -Wall -Wextra -O2

API_DIR    := 01-verbose-hello-world-api
C_SRC_DIR  := $(API_DIR)/src/test/c
TARGET_DIR := $(API_DIR)/target/c

EPSILON_SRC := $(C_SRC_DIR)/epsilon.c
EPSILON_BIN := $(TARGET_DIR)/$(basename $(notdir $(EPSILON_SRC)))

.PHONY: all c clean-c

all: c

c: $(EPSILON_BIN)

$(EPSILON_BIN): $(EPSILON_SRC) | $(TARGET_DIR)
	$(CC) $(CFLAGS) $< -o $@

$(TARGET_DIR):
	mkdir -p $(TARGET_DIR)

clean-c:
	rm -rf $(TARGET_DIR)
