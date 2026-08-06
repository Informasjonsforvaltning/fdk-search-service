package no.digdir.fdk.searchservice.elastic

import no.digdir.fdk.searchservice.model.RelationType
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class RelationTypeToStringConverter : Converter<RelationType, String> {
    override fun convert(source: RelationType): String = source.value
}

@ReadingConverter
class StringToRelationTypeConverter : Converter<String, RelationType> {
    override fun convert(source: String): RelationType = RelationType.fromValue(source)
}
