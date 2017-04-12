# Okta Access Token Verifier

This example assumes you've setup an Authorization Server and an OIDC application

Look [here](http://developer.okta.com/docs/api/resources/oauth2.html#validating-access-tokens) for reference on
validating Okta access tokens

The examples below use [HTTPie](http://httpie.org)

## Build

```
mvn clean package
```

## Run

Follow these steps:

1. Obtain an Okta access token

    ```
    http --auth <client id>:<client secret> -f \
    https://<okta tenant url>/oauth2/<okta authorization server id>/v1/token \
    grant_type=password username=<email> password=<password> scope=offline_access
    ```
    
    You'll get back something like this:
    
    ```
    eyJhbGciOiJSUzI1NiIsImtpZCI6IkRwREtNUll6cFBNNjdRdjF2b21EbVJfOWZXc2dBak1IcUJMLVRiSnpDMDQifQ.eyJ2ZXIiOjEsImp0aSI6IkFULlpBMTRIbk1lNEtVeEdyQW9fMnFUWjhXRmNmRUt6TjNWLThKTnkxcmx5YjAiLCJpc3MiOiJodHRwczovL21pY2FoLm9rdGFwcmV2aWV3LmNvbS9vYXV0aDIvYXVzOXZtb3JrOHd3NXR3WmcwaDciLCJhdWQiOiJodHRwczovL2FmaXRuZXJkLmNvbS90ZXN0IiwiaWF0IjoxNDkxOTcwODMyLCJleHAiOjE0OTE5NzQ0MzIsImNpZCI6IlFTMlRUTGZVRTN6aXUzV0tlbDR0IiwidWlkIjoiMDB1OXZtZTk5bnh1ZHZ4WkEwaDciLCJzY3AiOlsib2ZmbGluZV9hY2Nlc3MiXSwic3ViIjoibWljYWgrb2t0YUBhZml0bmVyZC5jb20ifQ.RU4zIQIadATEcAt7Pd6SHqWsIprVrucmsqD7i3PKMskHsAcq8zYczFLyzprbSCAoTnM0ItR2VQ6gZuwt_u0S7wtWde_AJzeh-gWq5xRKpRub0MiUDGj-zT-Mhm4JED7I4NQMr1sZMTv0UhsFlc8YxF1o8SHehqDj63Ds1Cpu0IYIpB9orz0Wn-wTvX6z6KP_GeHWc_mhQXUjIKfisax0ZUk0LbH6CLU1ZEIIa-9G_PmpK6gwHASbbcfcCexXnDxUUgwi7e4tiBN7x4ocqzScQbY7ao3T4s6ybejd4oWO69sTGXF7m25-QrNAZJZMytdzqsI4x5EroNZI0GUtMTp0wQ
    ```
    
2. Obtain the [JWK]() with the public key to verify the signature

    ```
    http --auth <client id>:<client secret> -f \
    https://<okta tenant url>/oauth2/<okta authorization server id>/v1/keys
    ```
    
    You'll get back something like this:
    
    ```
    {
        "keys": [
            {
                "alg": "RS256",
                "e": "AQAB",
                "kid": "DpDKMRYzpPM67Qv1vomDmR_9fWsgAjMHqBL-TbJzC04",
                "kty": "RSA",
                "n": "mJL9cWnu9q5tosaYJoMX4IPM1F7dkGR5QNCbsBuZWz1MrY-H7XzfIVXoFx23_Rk_E4xsdlr-xQNVKy4LgfXG5YQNHBT591PpprHEf6myfirB8Ig08xtkSO9yQJWF_IoK_BkNnx3DFrIAeBs73cYztGIBQFCeL9OhgAZWif2ovMruEdQPlgoKuNzb39WnEPZX_hUINVPIc-4B6H3byzOLFUAAn8A1dxJHwQkqnp50bt6QyyvTPNyHla2qVeF1dBj4mJz_6CELpiHF42WgtWcrouekDgnRSa2ctRKbjt8-3OD8oXG3hRWPxrjhDhIj_otIV-MPP3VVEmIpVu2K9NKejw",
                "use": "sig"
            }
        ]
    }
    ```
    
3. Validate the access token
    
    ```
    java -jar target/access-token-verifier-0.0.1-SNAPSHOT-spring-boot.jar \
    <access token from step 1>
    <keys[0].n from step 2; ex: mJL9cWnu9q5tosaYJo...>
    <keys[0].e from step 2; ex: AQAB>
    ```
    
    You'll get back something like this:
    
    ```
    Verified Access Token
    {
      "header" : {
        "alg" : "RS256",
        "kid" : "<kid>"
      },
      "body" : {
        "ver" : <ver>,
        "jti" : "<jti>",
        "iss" : "https://<okta tenant url>/oauth2/<okta authorization server id>",
        "aud" : "<audience>",
        "iat" : <issued at>,
        "exp" : <expires>,
        "cid" : "<cid>",
        "uid" : "<uid>",
        "scp" : [ "offline_access" ],
        "sub" : "<subject>"
      },
      "signature" : "<signature>"
    }
    ```