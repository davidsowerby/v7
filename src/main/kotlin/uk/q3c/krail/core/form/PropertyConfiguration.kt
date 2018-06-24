package uk.q3c.krail.core.form

import com.vaadin.data.Converter
import com.vaadin.ui.AbstractField
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.I18NKey
import java.io.Serializable
import kotlin.reflect.KClass


enum class StyleSize {
    tiny, small, normal, large, huge, no_change
}

enum class StyleAlignment(val value: String) {
    align_left(""), align_center("align-center"), align_right("align-right"), no_change("no-change")
}

enum class StyleBorderless {
    yes, no, no_change
}

@FormDsl
class StyleAttributes {
    var size = StyleSize.no_change
    var borderless = StyleBorderless.no_change
    var alignment = StyleAlignment.no_change

    fun merge(fromParent: StyleAttributes) {
        if (alignment == StyleAlignment.no_change) {
            alignment = fromParent.alignment
        }
        if (borderless == StyleBorderless.no_change) {
            borderless = fromParent.borderless
        }
        if (size == StyleSize.no_change) {
            size = fromParent.size
        }
    }
}

/**
 * If [SectionConfiguration] is not being scanned automatically, [PropertyConfiguration] must be fully populated manually.
 *
 * When [SectionConfiguration] is being scanned automatically, any manually specified values take precedence, (thus overriding the defaults) but otherwise:
 *
 * - [propertyType] is taken from the property declaration in the entity class
 * - [componentClass] is selected using [FormSupport.componentFor]
 * - [converterClass] is selected using [FormSupport.converterFor]
 * - [validations] are additive - that is, any manually defined [KrailValidator]s are combined with those read from JSR 303 annotations from the entity class.
 *
 * When setting validation, http://piotrnowicki.com/2011/02/float-and-double-in-java-inaccurate-result/
 *
 * [caption] and [description] must be set manually
 */
@FormDsl
class PropertyConfiguration(val name: String, override val parentConfiguration: ParentConfiguration) : ChildConfiguration, FormConfigurationCommon, Serializable {
    var propertyType: KClass<out Any> = Any::class
    var componentClass: Class<out AbstractField<*>> = AbstractField::class.java
    var converterClass: Class<out Converter<*, *>> = Converter::class.java
    var caption: I18NKey = LabelKey.Unnamed
    var description: I18NKey = DescriptionKey.No_description_provided
    var validations: MutableList<KrailValidator<*>> = mutableListOf()
    override var styleAttributes = StyleAttributes()


    fun merge() {
        var formCompleted = false
        var inheritedConfiguration = parentConfiguration
        while (!formCompleted) {
            styleAttributes.merge(inheritedConfiguration.styleAttributes)
            if (inheritedConfiguration is FormConfiguration) {
                formCompleted = true
            } else {
                inheritedConfiguration = (inheritedConfiguration as ChildConfiguration).parentConfiguration
            }

        }
    }
}

class InvalidTypeForValidator(targetClass: KClass<*>, validatorType: String) : RuntimeException("$targetClass is an invalid type for a $validatorType")
class InvalidValueForValidator(msg: String) : RuntimeException(msg)

