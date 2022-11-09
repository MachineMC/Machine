package me.pesekjak.machine.world;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used by BlockData classes to show which data values
 * are available for each special BlockData.
 */
// This class is used by the code generators, edit with caution.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyRange {

    String[] available() default {};

}
