# qBittorrent Kotlin

![Maven Central](https://img.shields.io/maven-central/v/org.drewcarlson/qbittorrent-client?label=maven&color=blue)
![](https://github.com/DrewCarlson/qBittorrent-Kotlin/workflows/Tests/badge.svg)

Multiplatform Kotlin wrapper for the [qBittorrent](https://github.com/qbittorrent/qBittorrent/) Web API using [Ktor](https://ktor.io).

**Features**

- Targets qBittorrent WebUI API [v2.8.3](https://github.com/qbittorrent/qBittorrent/wiki/WebUI-API-(qBittorrent-4.1))
- Two modules: `client` contains all the HTTP code, `models` contains only the serializable data models
- Automatic authentication handling when interacting with the API
- [Coroutine Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/index.html) APIs wrapping the syncing endpoints
- Add torrents with HTTP/Magnet URLs and file paths

## Usage

For a comprehensive list of available endpoints and to understand the returned data, see the [qBittorrent API Docs](https://github.com/qbittorrent/qBittorrent/wiki/WebUI-API-(qBittorrent-4.1)).

QBittorrentClient only requires `baseUrl` assuming default credentials are used.
```kotlin
val client = QBittorrentClient(
    baseUrl = "http://localhost:8080",
    username = "admin",
    password = "adminadmin",
    // When subscribed to a syncing Flow, the API is polled at this rate
    syncInterval = 5.seconds,
    httpClient = HttpClient(),
    dispatcher = Dispatchers.Default,
)
```

Add a new torrent:
```kotlin
client.addTorrent {
    // Add HTTP/Magnet URLs:
    urls.add("magnet:?xt=urn:btih:c12fe1c06bba254a9dc9f519b335aa7c1367a88a")
    // Or torrent file paths:
    torrents.add("~/Downloads/my.torrent")
    // Or torrent file names and ByteArrays:
    rawTorrents["my.torrent"] = getFileBytes()
    // ... configure other optional parameters
    savepath = "/downloads"
}
```

Subscribe to MainData updates:
```kotlin
client.observeMainData()
    .collect { mainData ->
        println("Server Status: ${mainData.serverState.connectionStatus}")
        println("Tracking ${mainData.torrents.size} torrents.")
    }
```

Subscribe to Torrent updates:

```kotlin
client.observeTorrent("c12fe1c06bba254a9dc9f519b335aa7c1367a88a")
    .collect { torrent ->
        println("${torrent.name} : ${torrent.state}")
    }
```

Note that the `maindata` endpoint is only polled at the provided `syncInterval` while collecting either the
`observeMainData()` or `observeTorrent(...)` `Flow`s.
When all instances of these `Flow`s are completed/cancelled no API endpoints will be called until explicitly requested.

## Download

![Maven Central](https://img.shields.io/maven-central/v/org.drewcarlson/qbittorrent-client?label=maven&color=blue)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/org.drewcarlson/qbittorrent-client?server=https%3A%2F%2Fs01.oss.sonatype.org)

![](https://img.shields.io/static/v1?label=&message=Platforms&color=grey)
![](https://img.shields.io/static/v1?label=&message=Js&color=blue)
![](https://img.shields.io/static/v1?label=&message=Jvm&color=blue)
![](https://img.shields.io/static/v1?label=&message=Linux&color=blue)
![](https://img.shields.io/static/v1?label=&message=macOS&color=blue)
![](https://img.shields.io/static/v1?label=&message=Windows&color=blue)
![](https://img.shields.io/static/v1?label=&message=iOS&color=blue)
![](https://img.shields.io/static/v1?label=&message=tvOS&color=blue)
![](https://img.shields.io/static/v1?label=&message=watchOS&color=blue)

```kotlin
repositories {
  mavenCentral()
  // (Optional) For Snapshots:
  maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
  implementation("org.drewcarlson:qbittorrent-client:$qbittorrent_version")
  
  // Optional: Just the data models without the client/http lib.
  implementation("org.drewcarlson:qbittorrent-models:$qbittorrent_version")
}
```


Note: it is required to specify a Ktor client engine implementation.
([Documentation](https://ktor.io/clients/http-client/multiplatform.html))

```kotlin
dependencies {
  // Jvm/Android
  implementation("io.ktor:ktor-client-okhttp:$ktor_version")
  implementation("io.ktor:ktor-client-android:$ktor_version")
  // iOS
  implementation("io.ktor:ktor-client-darwin:$ktor_version")
  // macOS/Windows/Linux
  implementation("io.ktor:ktor-client-curl:$ktor_version")
  // Javascript/NodeJS
  implementation("io.ktor:ktor-client-js:$ktor_version")
}
``` 

## License
```
Copyright (c) 2020 Andrew Carlson

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
