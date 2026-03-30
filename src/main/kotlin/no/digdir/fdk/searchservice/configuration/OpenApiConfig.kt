package no.digdir.fdk.searchservice.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("FDK Search Service API")
                    .description(
                        """
                        A REST API providing powerful search capabilities for Norway's national data catalog, enabling discovery and search of public sector data resources.
                        
                        ## About Data.norge.no
                        This API provides search functionality for the data catalog published on [data.norge.no](https://data.norge.no), operated by the Norwegian Digitalisation Agency (Digitaliseringsdirektoratet). The catalog serves as Norway's central registry for public sector data resources, promoting fair competition and enabling data reuse for both commercial and non-commercial purposes.
                        
                        ## What You Can Search
                        The search API provides access to an extensive range of resources from the Norwegian public sector, including:
                        - **Datasets**: Structured data collections with detailed metadata descriptions
                        - **Concepts**: Controlled vocabularies, taxonomies, and standardized terms used across government
                        - **Data Services**: APIs and data services with technical specifications
                        - **Information Models**: Data schemas, specifications, and interoperability standards
                        - **Services & Events**: Service descriptions and event information from public sector activities
                        
                        ## API Capabilities
                        - **Full-Text Search**: Powerful search across titles, descriptions, keywords, and additional metadata
                        - **Advanced Filtering**: Filter by access rights, data themes, spatial coverage, organization, formats, and more
                        - **Resource-Specific Search**: Search within specific resource types or across all resources
                        - **Autocomplete Suggestions**: Get search suggestions for improved user experience
                        - **Pagination**: Navigate through large result sets with configurable page size
                        - **Sorting**: Sort results by various criteria (e.g., first harvested date)
                        - **Aggregations**: Get facet counts for filtering options
                        - **Search Profiles**: Use specialized search profiles for domain-specific use cases (e.g., transport)
                        
                        ## Use Cases
                        - **Data Discovery**: Programmatically search and discover available public sector data
                        - **Application Integration**: Integrate search functionality into applications and systems
                        - **Autocomplete**: Provide search suggestions in user interfaces
                        - **Metadata Analysis**: Search and analyze structured metadata for data governance
                        - **Data Reuse**: Enable fair competition by providing equal access to search capabilities
                        - **Interoperability**: Find resources using standardized information models and concepts
                        
                        ## Data Governance
                        Content is provided by the organizations themselves, with each organization responsible for managing their content in the catalogs. The Norwegian Digitalisation Agency is responsible for the operation and development of the platform.
                        
                        For more information about finding and using data, visit [data.norge.no](https://data.norge.no/nb/docs/finding-data) or learn more [about the platform](https://data.norge.no/nb/about).
                        """.trimIndent(),
                    ).version("1.0.0"),
            )
}

