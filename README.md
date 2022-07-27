# Plaid Exchange (PX) POC scratch area

Simple notes:
1. http://localhost:8080/sample -- redirects to local Keycloak login, passing a redirect/callback url
2. Login with credentials.
    * Previously configured in keycloak (stored in postgres)
      * talvey
      * 1234
      * realm: plaid_realm

   **Note, if you've logged in previously, you may need to clear existing sessions in Keycloak to execute the full OAuth2 process

3. Successful login calls the provided redirect/callback url http://127.0.0.1:8080/mock/plaid/callback
4. /mock/plaid/callback turns around and calls our locally hosted /users/auth_code (as plaid would)
5. /users/auth_code uses a oAuth2AccessTokenResponseClient to exchange the auth code for an access token and returns it (to the /mock/plaid/callback)
6. (not implemented) /mock/plaid/callback should call the original /sample with the newly minted token

   1. right now, we just return the Token to the browser

