package com.github.jinahya.hello.api;

import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Objects;

class DefaultHelloWorldGraphics implements HelloWorldGraphics {

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
