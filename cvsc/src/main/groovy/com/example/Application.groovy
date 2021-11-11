package com.example

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic

@CompileStatic
class Application {
    static ApplicationContext applicationContext
    static void main(String[] args) {
        applicationContext = Micronaut.run(Application, args)
    }
}
