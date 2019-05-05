package com.github.jinahya.hello;

import dagger.Component;
import dagger.MembersInjector;

@Component(modules = {HelloWorldDiDaggerModule.class})
interface HelloWorldDiDaggerComponent extends MembersInjector<HelloWorldDiDaggerTest> {

}
