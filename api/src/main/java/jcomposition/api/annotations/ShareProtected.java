package jcomposition.api.annotations;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.SOURCE)
@Target({
        ElementType.TYPE,
        ElementType.METHOD
})
public @interface ShareProtected {
}
