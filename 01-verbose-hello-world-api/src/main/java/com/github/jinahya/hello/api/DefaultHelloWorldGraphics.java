package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class DefaultHelloWorldGraphics
        implements HelloWorldGraphics {

    private static final System.Logger logger =
            System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    DefaultHelloWorldGraphics(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Color[] getThreeColors() {
        if (threeColors == null) {
            threeColors = new Color[3];
            final var array = new byte[HelloWorld.BYTES];
            service.set(array);
            int index = 0;
            for (int i = 0; i < threeColors.length; i++) {
                int rgba = 0;
                for (int j = 0; j < 4; j++) {
                    rgba <<= Byte.SIZE;
                    rgba |= array[index++] & 0xFF;
                }
                final var argb = (rgba >>> Byte.SIZE) | (rgba << (Byte.SIZE * 3));
                threeColors[i] = new Color(argb, true);
            }
        }
        return Arrays.copyOf(threeColors, threeColors.length);
    }

    @Override
    public Color[] getFourColors() {
        if (fourColors == null) {
            fourColors = new Color[4];
            final var array = new byte[HelloWorld.BYTES];
            service.set(array);
            int index = 0;
            for (int i = 0; i < fourColors.length; i++) {
                int rgb = 0;
                for (int j = 0; j < 3; j++) {
                    rgb <<= Byte.SIZE;
                    rgb |= array[index++] & 0xFF;
                }
                fourColors[i] = new Color(rgb);
            }
        }
        return Arrays.copyOf(fourColors, fourColors.length);
    }

    @Override
    public <T extends Graphics> T fillWithThreeColors(final T graphics, final int x, final int y,
                                                      final int width, final int height) {
        final var colors = getThreeColors();
        final var w = width / colors.length;
        final var remainder = width % colors.length;
        var cx = x;
        for (int i = 0; i < colors.length; i++) {
            final var cw = w + (i < remainder ? 1 : 0);
            graphics.setColor(colors[i]);
            if (graphics instanceof Graphics2D g2d) {
                g2d.fill(new java.awt.geom.Rectangle2D.Double(cx, y, cw, height));
            } else {
                graphics.fillRect(cx, y, cw, height);
            }
            cx += cw;
        }
        return graphics;
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;

    private Color[] threeColors;

    private Color[] fourColors;
}
