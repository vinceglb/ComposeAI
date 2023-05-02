import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @Environment(\.colorScheme) var colorScheme: ColorScheme
    
    var body: some View {
        let bg = ColorKt.getBackgroundColor(isDarkTheme: colorScheme == .dark)
        let color = Color(
            red: bg.first?.doubleValue ?? 0,
            green: bg.second?.doubleValue ?? 0,
            blue: bg.third?.doubleValue ?? 0
        )
        
        ZStack {
            color.ignoresSafeArea()
            
            ComposeView()
                .ignoresSafeArea(.keyboard)  // Compose has own keyboard handler
        }
    }
}



