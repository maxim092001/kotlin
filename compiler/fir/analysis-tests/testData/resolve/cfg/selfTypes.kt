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
class ReturnTypeWithTypeParameters<T, A, F> {
    fun returnType(): Self {
        return this <!UNCHECKED_CAST!>as Self<!>
    }
}

class InnerClass {
    @Self
    inner class Inner {
        fun returnType(): Self {
            return this <!UNCHECKED_CAST!>as Self<!>
        }
    }
}

class StaticClass {
    @Self
    class Static {
        fun returnType(): Self {
            return this <!UNCHECKED_CAST!>as Self<!>
        }
    }
}

@Self
class InnerSelfClass {
    inner class Self {
        fun returnSelf(): InnerSelfClass.Self {
            return this
        }
    }

    fun returnType(): Self {
        return this <!UNCHECKED_CAST!>as Self<!>
    }

    fun returnSelfClassType(): InnerSelfClass.Self {
        return InnerSelfClass().Self()
    }
}

@Self
class TypeAliasSelf {
    @Suppress("TOPLEVEL_TYPEALIASES_ONLY")
    typealias Self = String

    fun returnType(): Self {
        return this <!UNCHECKED_CAST!>as Self<!>
    }

    fun returnTypealias(): TypeAliasSelf.Self {
        return "typealias"
    }
}
