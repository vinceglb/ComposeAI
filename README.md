<p align="center">
  <img src="https://github.com/EBfVince/ComposeAI/assets/24540801/02e51744-c826-4d7b-af2e-b509fb8af3f5" alt="Compose AI" />
</p>

<p align="center">An Android & iOS application ChatGPT like made with Compose Multiplatform</p>
  
<p align="center">
  
  <a href="https://www.jetbrains.com/fr-fr/lp/compose-multiplatform/">
    <img src="https://img.shields.io/badge/-android-brightgreen" alt="Android">
  </a>
  
  <a href="https://www.jetbrains.com/fr-fr/lp/compose-multiplatform/">
    <img src="https://img.shields.io/badge/-iOS-lightgrey" alt="iOS">
  </a>
  
  <a href="https://openai.com/">
    <img src="https://img.shields.io/badge/AI-OpenAI-blueviolet" alt="OpenAI">
  </a>
  
  <a href="https://gitmoji.dev">
    <img src="https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg" alt="Gitmoji">
  </a>
    
  <a href="https://github.com/EBfVince/ComposeAI/stargazers">
    <img alt="GitHub stars" src="https://img.shields.io/github/stars/EBfVince/ComposeAI">
  </a>
  
</p>

## Stack

- 💄 [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) for the UI on Android & iOS
- 🎨 [Material 3](https://material.io/) for the Design System
- 🧠 [OpenAI Kotlin](https://github.com/aallam/openai-kotlin) for OpenAI API client
- 🧭 [Voyager](https://github.com/adrielcafe/voyager) for the navigation library
- 💡 [Koin](https://insert-koin.io/) for the dependency injection framework
- 🗃️ [SQLDelight](https://github.com/cashapp/sqldelight) for native SQLite database
- ⚙️ [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) for saving simple key-value data
- 🧑‍🎨 [ImageLoader](https://github.com/qdsfdhvh/compose-imageloader/tree/master) for displaying images from URL
- 🌐 [Libres](https://github.com/Skeptick/libres) for resources generation
- 🔊 [Napier](https://github.com/AAkira/Napier) for easy logging
- 🔨 [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) for BuildConfig for Kotlin Multiplatform Project
- 🔥 Firebase [Analytics](https://firebase.google.com/docs/analytics) & [Crashlytics](https://firebase.google.com/docs/crashlytics) for usage and crash reporting
- 💸 [AdMob](http://admob.google.com/) for ads revenue

## Architecture

The app architecture is based on the latest guidelines from Google. You can take a look [here](https://developer.android.com/topic/architecture?hl=fr) for more information and [here](https://github.com/android/nowinandroid) for an official example.

## Gettings started

Compose AI use the OpenAI API with GPT3.5-turbo the get the responses. We need to setup your own OpenAI API key before launching the app.

- Create your OpenAI developper account [here](https://platform.openai.com/)
- Create a new API key [here](https://platform.openai.com/account/api-keys) or use an existing one
- Open the local.properties in the root of the project
- Add your key like this `openai_api_key=[YOUR OPENAI API KEY HERE]`

## Sources

Here are some of the websites or repositories that helped me to create this project:

- [Compose Multiplatform iOS / Android template](https://github.com/JetBrains/compose-multiplatform-ios-android-template)
- [The #compose-ios channel on the official Kotlin Slack](https://kotlinlang.slack.com/archives/C0346LWVBJ4/p1678888063176359)
- [KMPStarterOS](https://github.com/AppKickstarter/KMPStarterOS) : Open source template for Kotlin multiplatform and Compose by [@LouisDuboscq](https://github.com/LouisDuboscq)

I want to thank all the whole Jetbrains team for making Compose Multiplatform possible, and all the amazing community that surrounds it ❤️
