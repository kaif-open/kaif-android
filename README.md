### io.kaif

# Development

## Build Variant
 * Debug & Production
 will use real kaif.io,

## Prepare secret file

To access resource from kaif.io(please check https://kaif.io/developer/doc),you need to create app to get client secret and client id,fill it in secret.xml for api accessing.

1. secret.xml
```
<resources>
    <string name="client_secret">[your client secret]</string>
    <string name="client_id">[your client id]</string>
</resources>
```

2. put secret.xml under

```
src/debug/res/values
src/release/res/values
```

3. modify settings.xml

```
src/debug/res/values/settings.xml
src/relase/res/values/settings.xml
```

change content as you set at https://kaif.io/developer/client-app
ex:
 
```
<string name="redirect_uri">oauthkf://kf-sample/callback</string>
<string name="redirect_uri_host">kf-sample</string>
<string name="redirect_uri_path_prefix">/callback</string>
<string name="redirect_uri_scheme">oauthkf</string>
```
 
# Release
```
kaif-android/secret
```

 * please copy corresponding secret files(key and passwords) to correct locations.

 * NEVER commit secret files to git !!! See kaif-android/.gitignore

 * after secret files ready, you can execute apk.sh to generate release apk  
 `./apk.sh ~/Desktop/`

