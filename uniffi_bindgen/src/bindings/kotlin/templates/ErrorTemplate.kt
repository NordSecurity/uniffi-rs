{%- let type_name = type_|error_type_name %}
{%- let ffi_converter_name = type_|error_ffi_converter_name %}
{%- let canonical_type_name = type_|error_canonical_name %}

{% if e.is_flat() %}
{%- call kt::docstring(e, 0) %}
sealed class {{ type_name }}(message: String): Exception(message){% if contains_object_references %}, Disposable {% endif %} {
        {% for variant in e.variants() -%}
        {%- call kt::docstring(variant, 4) %}
        class {{ variant|error_variant|type_name }}(message: String) : {{ type_name }}(message)
        {% endfor %}

    companion object ErrorHandler : CallStatusErrorHandler<{{ type_name }}> {
        override fun lift(error_buf: RustBuffer.ByValue): {{ type_name }} = {{ ffi_converter_name }}.lift(error_buf)
    }
}
{%- else %}
{%- call kt::docstring(e, 0) %}
sealed class {{ type_name }}: Exception(){% if contains_object_references %}, Disposable {% endif %} {
    {% for variant in e.variants() -%}
    {%- call kt::docstring(variant, 4) %}
    {%- let variant_name = variant|error_variant|type_name %}
    class {{ variant_name }}(
        {% for field in variant.fields() -%}
        val {{ field.name()|var_name }}: {{ field|type_name}}{% if loop.last %}{% else %}, {% endif %}
        {% endfor -%}
    ) : {{ type_name }}() {
        override val message
            get() = "{%- for field in variant.fields() %}{{ field.name()|var_name|unquote }}=${ {{field.name()|var_name }} }{% if !loop.last %}, {% endif %}{% endfor %}"
    }
    {% endfor %}

    companion object ErrorHandler : CallStatusErrorHandler<{{ type_name }}> {
        override fun lift(error_buf: RustBuffer.ByValue): {{ type_name }} = {{ ffi_converter_name }}.lift(error_buf)
    }

    {% if contains_object_references %}
    @Suppress("UNNECESSARY_SAFE_CALL") // codegen is much simpler if we unconditionally emit safe calls here
    override fun destroy() {
        when(this) {
            {%- for variant in e.variants() %}
            is {{ type_name }}.{{ variant|error_variant|type_name }} -> {
                {%- if variant.has_fields() %}
                {% call kt::destroy_fields(variant) %}
                {% else -%}
                // Nothing to destroy
                {%- endif %}
            }
            {%- endfor %}
        }.let { /* this makes the `when` an expression, which ensures it is exhaustive */ }
    }
    {% endif %}
}
{%- endif %}

public object {{ e|ffi_converter_name }} : FfiConverterRustBuffer<{{ type_name }}> {
    override fun read(buf: ByteBuffer): {{ type_name }} {
        {% if e.is_flat() %}
            return when(buf.getInt()) {
            {%- for variant in e.variants() %}
            {{ loop.index }} -> {{ type_name }}.{{ variant|error_variant|type_name }}({{ Type::String.borrow()|read_fn }}(buf))
            {%- endfor %}
            else -> throw RuntimeException("invalid error enum value, something is very wrong!!")
        }
        {% else %}

        return when(buf.getInt()) {
            {%- for variant in e.variants() %}
            {{ loop.index }} -> {{ type_name }}.{{ variant|error_variant|type_name }}({% if variant.has_fields() %}
                {% for field in variant.fields() -%}
                {{ field|read_fn }}(buf),
                {% endfor -%}
            {%- endif -%})
            {%- endfor %}
            else -> throw RuntimeException("invalid error enum value, something is very wrong!!")
        }
        {%- endif %}
    }

    override fun allocationSize(value: {{ type_name }}): Int {
        {%- if e.is_flat() %}
        return 4
        {%- else %}
        return when(value) {
            {%- for variant in e.variants() %}
            is {{ type_name }}.{{ variant|error_variant|type_name }} -> (
                // Add the size for the Int that specifies the variant plus the size needed for all fields
                4
                {%- for field in variant.fields() %}
                + {{ field|allocation_size_fn }}(value.{{ field.name()|var_name }})
                {%- endfor %}
            )
            {%- endfor %}
        }
        {%- endif %}
    }

    override fun write(value: {{ type_name }}, buf: ByteBuffer) {
        when(value) {
            {%- for variant in e.variants() %}
            is {{ type_name }}.{{ variant|error_variant|type_name }} -> {
                buf.putInt({{ loop.index }})
                {%- for field in variant.fields() %}
                {{ field|write_fn }}(value.{{ field.name()|var_name }}, buf)
                {%- endfor %}
                Unit
            }
            {%- endfor %}
        }.let { /* this makes the `when` an expression, which ensures it is exhaustive */ }
    }

}
