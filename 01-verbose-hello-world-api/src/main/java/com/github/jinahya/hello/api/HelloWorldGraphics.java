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
import java.util.Objects;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public interface HelloWorldGraphics {

    static HelloWorldGraphics newInstance(final HelloWorld service) {
        return new DefaultHelloWorldGraphics(
                Objects.requireNonNull(service, "service is null")
        );
    }

    // ------------------------------------------------------------------------------------ java.awt

    /**
     * Returns three colors derived from the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * <table>
     * <caption>hello-world-bytes as RGBA colors</caption>
     * <tr><th>RGBA</th><th>R</th><th>G</th><th>B</th><th>A</th></tr>
     * <tr><td><pre style="color: white; background-color: #68656C6C;">#68656C6C</pre></td><td><pre style="color: white; background-color: #680000;">h (0x68)</pre></td><td><pre style="color: white; background-color: #006500;">e (0x65)</pre></td><td><pre style="color: white; background-color: #00006C;">l (0x6C)</pre></td><td><pre style="color: white; background-color: gray;">l (0x6C)</pre></td></tr>
     * <tr><td><pre style="color: white; background-color: #6F2C2077;">#6F2C2077</pre></td><td><pre style="color: white; background-color: #6F0000;">o (0x6F)</pre></td><td><pre style="color: white; background-color: #002C00;">, (0x2C)</pre></td><td><pre style="color: white; background-color: #000020;">  (0x20)</pre></td><td><pre style="color: white; background-color: gray;">w (0x77)</pre></td></tr>
     * <tr><td><pre style="color: white; background-color: #6F726C64;">#6F726C64</pre></td><td><pre style="color: white; background-color: #6F0000;">o (0x6F)</pre></td><td><pre style="color: white; background-color: #007200;">r (0x72)</pre></td><td><pre style="color: white; background-color: #00006C;">l (0x6C)</pre></td><td><pre style="color: white; background-color: gray;">d (0x64)</pre></td></tr>
     * </table>
     *
     * @return an array of three {@link Color} objects.
     */
    Color[] getThreeColors();

    /**
     * Returns four colors derived from the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * <table>
     * <caption>hello-world-bytes as colors</caption>
     * <tr><th>RGB</th><th>R</th><th>G</th><th>B</th></tr>
     * <tr><td><pre style="color: white; background-color: #68656C;">#68656C</pre></td><td><pre style="color: white; background-color: #680000;">h (0x68)</pre></td><td><pre style="color: white; background-color: #006500;">e (0x65)</pre></td><td><pre style="color: white; background-color: #00006C;">l (0x6C)</pre></td></tr>
     * <tr><td><pre style="color: white; background-color: #6C6F2C;">#6C6F2C</pre></td><td><pre style="color: white; background-color: #6C0000;">l (0x6C)</pre></td><td><pre style="color: white; background-color: #006F00;">o (0x6F)</pre></td><td><pre style="color: white; background-color: #00002C;">, (0x2C)</pre></td></tr>
     * <tr><td><pre style="color: white; background-color: #20776F;">#20776F</pre></td><td><pre style="color: white; background-color: #200000;">  (0x20)</pre></td><td><pre style="color: white; background-color: #007700;">w (0x77)</pre></td><td><pre style="color: white; background-color: #00006F;">o (0x6F)</pre></td></tr>
     * <tr><td><pre style="color: white; background-color: #726C64;">#726C64</pre></td><td><pre style="color: white; background-color: #720000;">r (0x72)</pre></td><td><pre style="color: white; background-color: #006C00;">l (0x6C)</pre></td><td><pre style="color: white; background-color: #000064;">d (0x64)</pre></td></tr>
     * </table>
     *
     * @return an array of four {@link Color} objects.
     */
    Color[] getFourColors();

    // fill three, horizontally divided, rectangles with getThreeColors
    // use Rectangle2D.Double if graphics is Graphics2D
    <T extends Graphics> T fillWithThreeColors(T graphics, int x, int y, int width, int height);
}
