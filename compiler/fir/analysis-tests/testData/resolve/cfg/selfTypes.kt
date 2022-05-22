import kotlin.Self

@Self
class JustSelfAnnotation {
    fun anyFun(): String = "string"
}

@Self
class ReturnType {

    fun returnType(): Self {
        val res: Self  = this <!UNCHECKED_CAST!>as Self<!>
        return res
    }

}