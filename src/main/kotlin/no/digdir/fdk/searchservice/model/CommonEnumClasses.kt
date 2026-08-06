package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class SearchType {
    CONCEPT,
    DATASET,
    DATA_SERVICE,
    INFORMATION_MODEL,
    SERVICE,
    EVENT,
}

enum class MediaTypeOrExtentType {
    UNKNOWN,
    MEDIA_TYPE,
    FILE_TYPE,
}

enum class SpecializedType {
    DATASET_SERIES,
    LIFE_EVENT,
    BUSINESS_EVENT,

    @JsonProperty("publicService")
    PUBLIC_SERVICE,

    @JsonProperty("service")
    SERVICE,
}
