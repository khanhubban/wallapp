//
//  ScrollOffsetController.swift
//  WallApp
//

import WallApp
import Foundation

@MainActor
final class ScrollOffsetController: ObservableObject {
    
    @Published var offset: CGFloat = 0
    
    private var maxOffset: CGFloat = 0
    let scrollScaler: CGFloat
    let scrollTarget: ScrollTarget
    
    //to animate view at the end of scroll end
    var scrollEndCompletion: ((CGFloat) -> Void)?
    
    private var scrollEndTask: Task<Void, Never>? = nil
    
    private var previousOffset: CGFloat = 0
    private var startOffset: CGFloat = 0
    
    init(startOffset: CGFloat = 0, scrollScaler: CGFloat = 1.82, scrollTarget: ScrollTarget = OtherScrollTarget()) {
//        Log.d("[ScrollOffsetController] init with scrollScaler: \(scrollScaler), tag: \(tag)")
        self.scrollScaler = scrollScaler
        self.scrollTarget = scrollTarget
        self.offset = startOffset
        self.startOffset = startOffset
    }
    
    func onScroll(offset: CGFloat, upperBound: CGFloat, autoScrollEnd: Bool = false) {
        if maxOffset == 0 { return }
        if let scrollTarget = scrollTarget as? SearchBarExploreScrollTarget {
            handleScrollForExploreSearchBar(offset: offset, upperBound: upperBound, topStickyOffset: scrollTarget.topStickyOffset)
        } else {
            let offset = max(0, (min(upperBound, offset))) // if screen is at top position and we still do the pull then offset must not be in negative.
            let delta = offset - previousOffset
            DispatchQueue.main.async { [weak self] in
                guard let self else { return }
                self.offset = (self.offset + -delta * 1/self.scrollScaler).clamped(to: -self.maxOffset...0)
                self.previousOffset = offset
                if autoScrollEnd {
                    self.scrollEndTask?.cancel()
                    self.scrollEndTask = Task {
                        do {
                            try await Task.sleep(for: .milliseconds(250))
                            self.onScrollEnd()
                        } catch {
                            //                    Log.d("[ScrollOffsetController] onScrollEnd() task cancelled")
                        }
                    }
                }
            }
//        Log.d("[ScrollOffsetController] previousOffset: \(previousOffset), offset: \(offset), delta: \(delta), resultOffset: \(self.offset)")
        }
    }
    
    func onScrollEnd() {
        if scrollTarget is SearchBarExploreScrollTarget {
            return
        }
        let fraction = 1 - (-offset / maxOffset)
//        Log.d("[ScrollOffsetController] fraction: \(fraction)")
        if fraction > 0.3 {
            setToShow()
        } else {
            setToHide()
        }
    }
    
    func setToShow() {
        self.scrollEndCompletion?(0.0)
        DispatchQueue.main.async {
            self.offset = 0
        }
    }
    
    func setToHide() {
        self.scrollEndCompletion?(-maxOffset)
        self.offset = -maxOffset
    }
    
    func update(maxOffset: CGFloat) {
        self.maxOffset = maxOffset
    }
    
    private func handleScrollForExploreSearchBar(offset: CGFloat, upperBound: CGFloat, topStickyOffset: CGFloat) {
        let barHeight = maxOffset
        let delta = offset - previousOffset
        
//        Log.d("[SearchBarAnim] offset: \(offset), delta: \(delta), barHeight: \(barHeight), maxOffset: \(maxOffset), startOffset: \(startOffset)")
        
        if delta > 0 { // swiping up
            self.offset = max(-barHeight, self.offset - delta)
        } else if delta < 0 { // swiping down
            if offset <= (startOffset - topStickyOffset) { // startOffset is the headerHeight in Explore, we want the search bar to offset down with the feed
                self.offset = startOffset - offset
            } else {
                self.offset = min(topStickyOffset, self.offset - delta) // sticks to the top of the screen while scrolling via finger gesture downwards and not have the highlights in view
            }
        }
        
//        Log.d("[SearchBarAnim] new offset: \(self.offset)")
        
        previousOffset = offset
    }
}


// MARK: - ScrollTarget
protocol ScrollTarget {}

struct SearchBarExploreScrollTarget: ScrollTarget {
    let topStickyOffset: CGFloat
}

struct OtherScrollTarget: ScrollTarget {}
