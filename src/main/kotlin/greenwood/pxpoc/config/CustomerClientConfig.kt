package greenwood.pxpoc.config

import com.fasterxml.jackson.databind.ObjectMapper
import greenwood.pxpoc.client.CustomerServiceClient

import org.springframework.context.annotation.Configuration
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

//TODO: Basically a copy from other services
@Configuration
class CustomerClientConfig(
    @Value("\${customer.service.url}") val baseUrl: String
) {
    @Bean
    fun customerServiceClient(
        @Qualifier("internalOkHttpClient") okHttpClient: OkHttpClient,
        objectMapper: ObjectMapper
    ): CustomerServiceClient {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(okHttpClient)
            .build()

        return retrofit.create(CustomerServiceClient::class.java)
    }
}
