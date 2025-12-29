//
//  ColorExtensions.swift
//  WallApp
//

import WallApp
import SwiftUI

extension ColorToken {
    func toColor(themeColors: ThemeColors) -> Color? {
        switch onEnum(of: self) {
        case .custom(let custom):
            return custom.color.toColor()
        case .localContent(_):
            return themeColors.onSurfaceVariant.toColor()
        case .themeBackground(_):
            return themeColors.background.toColor()
        case .themeOnBackground(_):
            return themeColors.onBackground?.color.toColor()
        case .themeOnPrimary(_):
            return themeColors.onPrimary?.color.toColor()
        case .themeOnSecondary(_):
            return themeColors.onSecondary?.color.toColor()
        case .themeOnSurface(_):
            return themeColors.onSurface?.color.toColor()
        case .themeOnSurfaceVariant(_):
            return themeColors.onSurfaceVariant.toColor()
        case .themeOnTertiary(_):
            return themeColors.onTertiary?.color.toColor()
        case .themePrimary(_):
            return themeColors.primary.toColor()
        case .themeSecondary(_):
            return themeColors.secondary.toColor()
        case .themeSurface(_):
            return themeColors.surface.toColor()
        case .themeSurfaceVariant(_):
            return themeColors.surfaceVariant.toColor()
        case .themeTertiary(_):
            return themeColors.tertiary?.color.toColor()
        case .themeScrim(_):
            return themeColors.scrim?.color.toColor()
        case .transparent(_):
            return .clear
        }
    }
}
