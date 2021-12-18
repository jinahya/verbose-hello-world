package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-lib
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

import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.extension.ExtendWith;

@AddBeanClasses({HelloWorldCdiFactory.class})
@ExtendWith({WeldJunit5Extension.class})
@Slf4j
class HelloWorldWeldJunit5Test extends HelloWorldDiTest {

}
