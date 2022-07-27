package greenwood.pxpoc.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaidToken(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("user_id")
    val userId: String
)
