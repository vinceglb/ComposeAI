//
//  ComposeAIApp.swift
//  ComposeAI
//
//  Created by Vincent Guillebaud on 27/03/2024.
//

import SwiftUI
import ComposeApp

@main
struct ComposeAIApp: App {
    init() {
        NapierProxyKt.debugBuild()
        AppModuleKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
