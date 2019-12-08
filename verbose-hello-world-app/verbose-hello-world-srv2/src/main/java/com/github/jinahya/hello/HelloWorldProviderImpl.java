package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv2
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

public class HelloWorldProviderImpl implements HelloWorldProvider {

    @Override
    public HelloWorld getInstance() {
        try {
            return Class.forName("com.github.jinahya.hello.HelloWorldImpl").asSubclass(HelloWorld.class)
                    .getConstructor().newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate HelloWorldImpl", roe);
        }
    }
}
