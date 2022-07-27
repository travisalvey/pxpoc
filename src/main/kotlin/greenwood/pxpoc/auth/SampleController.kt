package greenwood.pxpoc.auth

import com.nimbusds.jwt.SignedJWT
import greenwood.pxpoc.model.PlaidToken
import greenwood.pxpoc.model.Token
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.Principal
import java.util.Arrays
import java.util.Base64
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator
import org.springframework.security.crypto.keygen.StringKeyGenerator
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.util.CollectionUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

private val DEFAULT_SECURE_KEY_GENERATOR: StringKeyGenerator = Base64StringKeyGenerator(
    Base64.getUrlEncoder().withoutPadding(), 96
)

private val DEFAULT_STATE_GENERATOR: StringKeyGenerator = Base64StringKeyGenerator(
    Base64.getUrlEncoder()
)

private fun createHash(value: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(value.toByteArray(StandardCharsets.US_ASCII))
    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
}

@Suppress("ForbiddenComment")
@RestController
class SampleController(
    private val oAuth2AccessTokenResponseClient: OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>,
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val authorizationRequestRepository: AuthorizationRequestRepository<OAuth2AuthorizationRequest>,
    private val restTemplate: RestTemplate,
    @Value("\${spring.security.oauth2.client.registration.poc_client.client-id}")
    private val registrationId: String
) {

    companion object : KLogging()

    @GetMapping("/sample")
    fun getSample(principal: Principal, request: HttpServletRequest, response: HttpServletResponse) {
        logger.debug("REMOVE ME. Principal : $principal")
    }

    @GetMapping("/mock/plaid/callback")
    fun plaidCallback(request: HttpServletRequest): Token? {
        logger.debug("called /mock/plaid/callback")
        logger.debug("Plaid callback executed")
        logger.debug("REMOVE ME.  Principal: ${request.userPrincipal}")
        for ((key, value) in request.parameterMap) {
            logger.debug("$key = ${Arrays.toString(value)}")
        }

        //Since this is just our plaid mock callback endpoint, we need to turn around and call the /users/auth_code
        // endpoint.  We should pass whatever we expect from plaid

        //TODO: should be passed as a registrationId I think?
        val clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId)
        val params = toMultiMap(request.parameterMap)
        val oAuth2AuthorizationResponse = convert(params, clientRegistration.redirectUri)

        //TODO: Note, this is unusual.  For this POC, we're passing the session id along with the callback parameters.
        // Since this method is just mimicking the Plaid callback handler, this will allow our code/token exchange endpoint
        // to load the OAuth2AuthorizationRequest from the session (if it exists)
        val session: HttpSession? = request.getSession(false)
        val headers = HttpHeaders()
        session?.let { headers.add("COOKIE", "JSESSIONID=${session.id}") }
        val entity = HttpEntity<String>(headers)

        //TODO: What is plaid going to send.  Some of their docs show a json payload
        val response = restTemplate.exchange(
            "http://localhost:8080/users/auth_code?code={code}&state={state}&session_state={sessionState}",
            GET,
            entity,
            Token::class.java, mapOf(
                Pair("code", oAuth2AuthorizationResponse.code),
                Pair("state", oAuth2AuthorizationResponse.state),
                Pair("sessionState", params.get("session_state"))
            )
        )

        logger.debug("Response: $response")
        return response.body

    }

    @GetMapping("/users/auth_code", produces = ["application/json"])
    fun exchangeForAccessToken(request: HttpServletRequest): Token {
        logger.debug("called /users/auth_code")

        /* What other params do we expect plaid to send? -- check their api specs
            - X-PLAID-CLIENT-ID and X-PLAID-SECRET  -- how do we validate/secure these??
            - do they include their redirect URI? -- Sounds like we have to verify it here?
        */

        /* Query params passed by Plaid
            state=<PLAID_GENERATED>
            response_type=code
            redirect_uri=<PLAID_GENERATED>
            client_id=PLAID
            institution_id=ins_<YOUR_INSTITUTION_ID>
            application_id=<PLAID_APPLICATION_ID>
         */

        //TODO: Should we just use a simple rest client and hit keycloak for the token instead of trying to reuse
        // all the Spring infrastructure??

        val clientRegistration: ClientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId)

        //go ahead and use the OAuth2AuthorizationRequest if it is saved on the session, no need to generate it.  Otherwise,
        // create our own
        val authorizationRequest =
            authorizationRequestRepository.loadAuthorizationRequest(request) ?: generateAuthorizationRequest(
                clientRegistration
            )

        val oAuth2AuthorizationResponse = convert(request.parameterMap, clientRegistration.redirectUri)
        val oAuth2AuthorizationCodeGrantRequest = OAuth2AuthorizationCodeGrantRequest(
            clientRegistration, OAuth2AuthorizationExchange(
                authorizationRequest, oAuth2AuthorizationResponse
            )
        )
        val oAuth2AccessTokenResponse =
            oAuth2AccessTokenResponseClient.getTokenResponse(oAuth2AuthorizationCodeGrantRequest)
        logger.debug("REMOVE ME. AccessToken: ${oAuth2AccessTokenResponse.accessToken.tokenValue}")

        //TODO: What response does Plaid actually want?
        val responseToken =  createResponseToken(oAuth2AccessTokenResponse)
        logger.debug { "REMOVE ME. Full Response Token: $responseToken" }

        val plaidToken = createPlaidToken(oAuth2AccessTokenResponse)
        logger.debug {"REMOVE ME. Plaid Token: $plaidToken"}
        return responseToken
    }

    private fun createResponseToken(oAuth2AccessTokenResponse: OAuth2AccessTokenResponse): Token {
        val accessToken = oAuth2AccessTokenResponse.accessToken
        val refreshToken = oAuth2AccessTokenResponse.refreshToken
        val additionalParams = oAuth2AccessTokenResponse.additionalParameters
        val responseToken = Token(
            accessToken = accessToken.tokenValue,
            issuedAt = accessToken.issuedAt,
            expiresAt = accessToken.expiresAt,
            refreshToken = refreshToken?.tokenValue,
            refreshIssuedAt = refreshToken?.issuedAt,
            refreshExpiresAt = refreshToken?.expiresAt,
            tokenType = accessToken.tokenType.value,
            idToken = additionalParams.get("id_token").toString(),
            notBeforePolicy = additionalParams.get("not-before-policy") as Int,
            sessionState = additionalParams.get("session_state").toString(),
            scope = accessToken.scopes
        )
        return responseToken
    }

    private fun createPlaidToken(oAuth2AccessTokenResponse: OAuth2AccessTokenResponse) : PlaidToken {
        val accessToken = oAuth2AccessTokenResponse.accessToken.tokenValue
        val additionalParams = oAuth2AccessTokenResponse.additionalParameters
        val idToken = additionalParams.get("id_token").toString()

        //claims.get("sub")
        return PlaidToken(accessToken = accessToken, getClaim(idToken, "sub"))
    }

    private fun getClaim(token : String, claimKey : String) : String {
        val parsedJWT = SignedJWT.parse(token)
        val claims = parsedJWT.payload.toJSONObject()
        return claims.get(claimKey).toString()
    }


    private fun generateAuthorizationRequest(clientRegistration: ClientRegistration): OAuth2AuthorizationRequest {
        val builder = OAuth2AuthorizationRequest.authorizationCode().attributes { attrs: MutableMap<String, Any> ->
            attrs[OAuth2ParameterNames.REGISTRATION_ID] = clientRegistration.registrationId
        }

        if (!CollectionUtils.isEmpty(clientRegistration.scopes) && clientRegistration.scopes.contains(OidcScopes.OPENID)) {
            val nonce = DEFAULT_SECURE_KEY_GENERATOR.generateKey()
            val nonceHash = createHash(nonce)
            builder.attributes { attrs: MutableMap<String, Any> ->
                attrs[OidcParameterNames.NONCE] = nonce
            }
            builder.additionalParameters { params: MutableMap<String, Any> ->
                params[OidcParameterNames.NONCE] = nonceHash
            }
        }

        builder.clientId(clientRegistration.clientId)
            .authorizationUri(clientRegistration.providerDetails.authorizationUri)
            .redirectUri(clientRegistration.redirectUri)
            .scopes(clientRegistration.scopes)
            .state(DEFAULT_STATE_GENERATOR.generateKey())

        logger.info("Generated the OAuth2AuthorizationRequest detailsfrom scratch for ${clientRegistration.registrationId}")
        return builder.build()
    }

    private fun convert(map: Map<String, Array<String>>, redirectUri: String): OAuth2AuthorizationResponse {
        return convert(toMultiMap(map), redirectUri)
    }

    private fun convert(request: MultiValueMap<String, String>, redirectUri: String): OAuth2AuthorizationResponse {
        val code = request.getFirst(OAuth2ParameterNames.CODE)
        val errorCode = request.getFirst(OAuth2ParameterNames.ERROR)
        val state = request.getFirst(OAuth2ParameterNames.STATE)
        if (StringUtils.hasText(code)) {
            return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build()
        }
        val errorDescription = request.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION)
        val errorUri = request.getFirst(OAuth2ParameterNames.ERROR_URI)

        return OAuth2AuthorizationResponse.error(errorCode)
            .redirectUri(redirectUri)
            .errorDescription(errorDescription)
            .errorUri(errorUri)
            .state(state)
            .build()

    }

    fun toMultiMap(map: Map<String, Array<String>>): MultiValueMap<String, String> {
        val params: MultiValueMap<String, String> = LinkedMultiValueMap(map.size)
        map.forEach { (key: String, values: Array<String>) ->
            if (values.isNotEmpty()) {
                for (value in values) {
                    params.add(key, value)
                }
            }
        }
        return params
    }

}