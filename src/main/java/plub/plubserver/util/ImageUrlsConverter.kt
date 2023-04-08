package plub.plubserver.util

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class ImageUrlsConverter : AttributeConverter<List<String>, String?> {
    private val splitChar = ","

    override fun convertToDatabaseColumn(attribute: List<String>): String? {
        return if (attribute.isNotEmpty()) {
            attribute.joinToString(splitChar)
        } else {
            null
        }
    }

    override fun convertToEntityAttribute(dbData: String?): List<String> {
        return dbData?.split(splitChar) ?: emptyList()
    }
}