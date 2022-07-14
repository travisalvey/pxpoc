package greenwood.pxpoc.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest


@EnableWebSecurity
class OAuth2LoginSecurityConfig(
    @Value("\${permit_all_matchers}")
    private val permitAllMatchers: Array<String>,
    //private val accessTokenResponseClient: OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers(*permitAllMatchers).permitAll()
            .anyRequest().authenticated()
        // .and()
        // .oauth2ResourceServer()
        //n cod .jwt()

        http.oauth2Login()
            .tokenEndpoint()
            .accessTokenResponseClient(accessTokenResponseClient())
            .and()
            .authorizationEndpoint().authorizationRequestRepository(authorizationRequestRepository())


    }

    @Bean
    fun accessTokenResponseClient(): OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
        return DefaultAuthorizationCodeTokenResponseClient()
    }

    @Bean
    fun authorizationRequestRepository(): AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
        return HttpSessionOAuth2AuthorizationRequestRepository()
    }

//    @Bean
//    fun oAuth2AuthorizationCodeAuthenticationProvider(): OAuth2AuthorizationCodeAuthenticationProvider {
//        return OAuth2AuthorizationCodeAuthenticationProvider(accessTokenResponseClient())
//    }

}