package com.hrtx.config.annotation;


import com.hrtx.global.PowerConsts;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NoRepeat {
}

