### io.kaif

# Development

## Build Variant

* Debug & Production will use real kaif.io,

## Prepare secret file

To access resource from kaif.io(please check https://kaif.io/developer/doc ), you need to create app to get client secret and client id, fill it in api.properties for api accessing.

1. Create api.properties in `kaif-android/secret/api.properties`

    ```
    CLIENT_ID=[your client id]
    CLIENT_SECRET=[your client secret]
    ```

2. Edit `app/src/main/res/values/settings.xml`, change content of redirect_uri to your app's setting

    ```
    <string name="redirect_uri">[full redirect uri]</string>
    <string name="redirect_uri_host">[host of redirect uri]</string>
    <string name="redirect_uri_path_prefix">[path of redirect uri]</string>
    <string name="redirect_uri_scheme">[scheme of redirect uri]</string>
    ```
 
# Packaging
```
kaif-android/secret
```

1. Generate keystore file and move to `kaif-android/secret/kaif-keystore.jks`
2. Create `kaif-android/secret/password.properties`

    ```
    KEY_STORE_PASSWORD=[your key store password]
    RELEASE_KEY_PASSWORD=[your release key password]
    ```
    
    * NEVER commit `kaif-keystore.jks` and `password.properties` to git !!! See kaif-android/.gitignore

3. You can execute apk.sh to generate release apk
    `./apk.sh ~/Desktop/`

