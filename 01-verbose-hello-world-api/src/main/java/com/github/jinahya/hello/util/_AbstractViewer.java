package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2022 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * A class renders socket options of all kinds of sockets.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldNetUtils#printSocketOptions(Class, Object)
 */
@Slf4j
abstract class _AbstractViewer {

    static {
        LoggingUtils.setLevel("java.awt", "INFO");
        LoggingUtils.setLevel("javax.swing", "INFO");
        LoggingUtils.setLevel("sun", "INFO");
    }

    static {
        // https://www.formdev.com/flatlaf/macos/
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    static boolean init(final String[] args, final Class<?> alternative) throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            if (alternative != null) {
                alternative.getMethod("main", String[].class).invoke(null, (Object) args);
            }
            return false;
        }
        {
            FlatLightLaf.setup();
        }
        return true;
    }

    static void show(final JFrame frame) {
        Objects.requireNonNull(frame, "frame is null");
        if (SystemInfo.isMacFullWindowContentSupported) {
            frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        }
        frame.pack();
        if (false) {
            // set width with device's max width
            final var environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final var device = environment.getDefaultScreenDevice();
            final var mode = device.getDisplayMode();
            frame.setSize(new Dimension(mode.getWidth(), frame.getHeight()));
        }
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
