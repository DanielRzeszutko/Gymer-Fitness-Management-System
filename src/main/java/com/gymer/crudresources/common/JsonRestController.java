package com.gymer.crudresources.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public @interface JsonRestController {

    @AliasFor(annotation = RestController.class)
    String value() default "";

}
