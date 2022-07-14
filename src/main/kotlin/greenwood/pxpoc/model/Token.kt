package greenwood.pxpoc.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.Instant
import java.util.UUID

//TODO: This is basically a dupe of what exists to day in the Auth Service
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Token(
    val accessToken: String,

    val issuedAt: Instant?,

    val expiresAt: Instant?,

    val refreshExpiresAt: Instant?,

    val refreshIssuedAt: Instant?,

    val refreshToken: String?,

    val tokenType: String,

    val idToken: String,

    @JsonAlias("not-before-policy")
    val notBeforePolicy: Int,

    val sessionState: String,

    val scope: Set<String>
)