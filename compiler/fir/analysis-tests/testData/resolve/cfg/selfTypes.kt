import kotlin.Self

@Self
class JustSelfAnnotation {
    fun anyFun(): String = "string"
}

@Self
class ReturnType {

    fun returnType(): Self = this as Self

}