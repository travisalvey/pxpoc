package greenwood.pxpoc.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class WebConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        //I like the buffer here so we can read the response more than once (i.e. read it to log it, read it for normal processing)
        val restTemplate = RestTemplate(
            BufferingClientHttpRequestFactory(
                SimpleClientHttpRequestFactory()
            )
        )


        val messageConverter = createMappingJacksonHttpMessageConverter()
        messageConverter.supportedMediaTypes = listOf(MediaType.ALL)
        restTemplate.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter())
        return restTemplate
    }


    @Bean
    fun objectMapper() : ObjectMapper {
        return ObjectMapper()
            .registerModules(KotlinModule.Builder().build(), JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    private fun createMappingJacksonHttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val converter = MappingJackson2HttpMessageConverter()
        converter.objectMapper=objectMapper()
        return converter
    }
}