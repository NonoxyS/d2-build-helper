//
//  iosApp.swift
//
//  D2BuildHelper-iOS
//

import ComposeApp
import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(
        _: UIApplication,
        didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        if let window = window {
            window.rootViewController = MainViewControllerKt.MainViewController(environment: .prod)
            window.makeKeyAndVisible()
        }
        return true
    }
}
