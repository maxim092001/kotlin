import kotlin.Self

@Self
class JustSelfAnnotation {
    fun anyFun(): String = "string"
}

@Self
class ReturnType {
    fun returnTypeWithVal(): Self {
        val res: Self  = this <!UNCHECKED_CAST!>as Self<!>
        return res
    }
}

@Self
class ReturnTypeWithTypeParameter<T> {
    fun returnType(): Self {
        return this <!UNCHECKED_CAST!>as Self<!>
    }
}

@Self
class ReturnTypeWithTypeParameterы<T, A, F> {
    fun returnType(): Self {
        return this <!UNCHECKED_CAST!>as Self<!>
    }
}

class InnerClass {
    @Self
    class Inner {
        fun returnType(): Self {
            return this <!UNCHECKED_CAST!>as Self<!>
        }
    }
}