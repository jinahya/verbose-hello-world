package com.github.jinahya.hello;

import dagger.Component;
import dagger.MembersInjector;

@Component(modules = {HelloWorldImplInjectDaggerModule.class})
interface HelloWorldImplInjectDaggerComponent extends MembersInjector<HelloWorldImplInjectDaggerTest> {

}
