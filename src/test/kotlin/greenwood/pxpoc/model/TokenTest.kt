package greenwood.pxpoc.model

import com.nimbusds.jwt.EncryptedJWT
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.PlainJWT
import com.nimbusds.jwt.SignedJWT
import io.kotlintest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt
import io.kotlintest.shouldBe
import java.util.Base64
import org.json.JSONObject

class TokenTest {

private val idToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4bXJudExrQm1OVElMRzhqTGswcndxd0VfcVVZOHhRdGczbFJkRno2ZTFrIn0.eyJleHAiOjE2NTc3NTIwNTUsImlhdCI6MTY1Nzc1MTc1NSwiYXV0aF90aW1lIjoxNjU3NzUxNzUwLCJqdGkiOiI1Yjg5ZDM0ZS1jZjMzLTQ3YTYtYjU5OC1jOTY1N2I3MDZhOTIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjkwODAvcmVhbG1zL3BsYWlkX3JlYWxtIiwiYXVkIjoicG9jX2NsaWVudCIsInN1YiI6ImViNTk0MDVmLWU2ZDEtNGUyMC05NzM5LTkwOWI4M2RiN2NkYSIsInR5cCI6IklEIiwiYXpwIjoicG9jX2NsaWVudCIsIm5vbmNlIjoidWl1a0lIZzE0RkdYN3lTSW5uZVNSaERBdV9HZ0Vmc1pseXR5YllEbVBqZyIsInNlc3Npb25fc3RhdGUiOiIwNDdlYWQ4ZS0yZmE3LTRmMTItYTMzOS0xN2M3OGMyMWJkNjIiLCJhdF9oYXNoIjoiLXBsNjlNMEI2RG9nMzdCOVFGMFhBZyIsImFjciI6IjEiLCJzaWQiOiIwNDdlYWQ4ZS0yZmE3LTRmMTItYTMzOS0xN2M3OGMyMWJkNjIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJUcmF2aXMgQWx2ZXkiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0YWx2ZXkiLCJnaXZlbl9uYW1lIjoiVHJhdmlzIiwiZmFtaWx5X25hbWUiOiJBbHZleSJ9.6jePG-eo84RteF4y_BkXkepc8neiviRGdAQSTFBw-f-iJ-0bHJUUiWslxm-vSaB5zgXIjvQYhcpvXDyCF7ALuZ239gsXkWftJ78CvFerptTK0U2Ug_nrgYSaMoOkMWHortWPUFyfvnGb"

    @Test
    fun `should parse token`() {
        val parts = idToken.split("\\.".toRegex())
        parts.size shouldBe 3
        val decodedHeaders = String(Base64.getUrlDecoder().decode(parts[0]))
        decodedHeaders shouldContain "RS256"
        decodedHeaders shouldContain "JWT"
        val decodedPayload = String(Base64.getUrlDecoder().decode(parts[1]))
        val decodedSignature= String(Base64.getUrlDecoder().decode(parts[2]))

        val payload = JSONObject(decodedPayload)
        val sub = payload.get("sub")
        val name = payload.get("name")
        println("From payload.  sub: ${sub}, name: ${name}")
        sub shouldBe "eb59405f-e6d1-4e20-9739-909b83db7cda"
        name shouldBe "Travis Alvey"


    }

    @Test
    fun `should parse token also`() {
        val parsedJWT = SignedJWT.parse(idToken)
        val claims = parsedJWT.payload.toJSONObject()
        claims.get("sub") shouldBe "eb59405f-e6d1-4e20-9739-909b83db7cda"

    }
}