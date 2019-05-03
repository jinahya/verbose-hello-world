package com.github.jinahya.hello;

import dagger.Component;
import dagger.MembersInjector;

@Component(modules = {HelloWorldImplInjectDagge2rModule.class})
interface HelloWorldImplInjectDagger2Component extends MembersInjector<HelloWorldImplInjectDagger2Test> {

}
