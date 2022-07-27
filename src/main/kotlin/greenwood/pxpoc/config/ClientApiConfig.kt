package greenwood.pxpoc.config

import greenwood.common.client.RequestIdInterceptor
import mu.KLogging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

//TODO: Just a copy from other projects
@Configuration
class ClientApiConfig {
    companion object : KLogging()

    @Bean("externalOkHttpClient")
    fun externalOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()

        //issue with cert matching name
        val hostnameVerifier = HostnameVerifier { _, _ -> true }

        if (logger.isTraceEnabled) {
            builder.addInterceptor(HttpLoggingInterceptor()
                .apply { this.level = HttpLoggingInterceptor.Level.BODY })
        } else {
            builder.addInterceptor(HttpLoggingInterceptor()
                .apply { this.level = HttpLoggingInterceptor.Level.BASIC })
        }
        builder.hostnameVerifier(hostnameVerifier)
        return builder.build()
    }

    @Bean("internalOkHttpClient")
    //fun internalOkHttpClient(requestIdInterceptor: RequestIdInterceptor): OkHttpClient {
    fun internalOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            //.addInterceptor(requestIdInterceptor)

        if (logger.isTraceEnabled) {
            builder.addInterceptor(HttpLoggingInterceptor()
                .apply { this.level = HttpLoggingInterceptor.Level.BODY })
        } else {
            builder.addInterceptor(HttpLoggingInterceptor()
                .apply { this.level = HttpLoggingInterceptor.Level.BASIC })
        }

        return builder.build()
    }

}
