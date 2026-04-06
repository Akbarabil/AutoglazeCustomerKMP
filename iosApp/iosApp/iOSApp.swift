import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

init() {
        KoinInitKt.initKoin()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}