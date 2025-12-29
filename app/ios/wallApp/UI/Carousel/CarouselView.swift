//
//  CarouselView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct CarouselView: View {
    
    @GestureState private var dragState = DragState.inactive
    @State var carouselLocation = 0
    @State var relativeLoc = 0
    
    let render: RenderIos
    let theme: Theme
    var viewState: CarouselViewState
    var viewSpec: CarouselViewSpec
    
    private var pageCount: Int {
        viewState.pages.count * 3
    }
    
    private var pageHeight: CGFloat {
        let topPadding = viewSpec.padding?.top ?? 0
        let bottomPadding = viewSpec.padding?.bottom ?? 0
        return (viewSpec.height - topPadding - bottomPadding).toCGFloat()
    }
    
    private var pageWidth: CGFloat {
        viewSpec.pageWidth.toCGFloat()
    }
    
    var body: some View {
        ZStack {
            // copy pages three times into a new pages array
            let pages = viewState.pages + viewState.pages + viewState.pages
            ForEach(pages.indices, id: \.self) { i in
                let page = pages[i]
                let view = CommonView(
                    viewState: page.view.viewState,
                    viewSpec: ExhibitViewSpec(width: pageWidth, height: pageHeight),
                    applyScrollParallax: page.view.applyScrollParallax
                )
                RenderedView(render: render, theme: theme, view: view)
                    .frame(width: pageWidth, height: pageHeight)
                    .scaleEffect(i == relativeLoc ? 1 : 0.82)
                    .opacity(self.getOpacity(i))
                    .offset(x: self.getOffset(i))
                    .animation(.interpolatingSpring(stiffness: 300.0, damping: 30.0, initialVelocity: 10.0), value: UUID())
                    .onTapGesture {
                        page.onClick?.invoke()
                    }
            }
            if viewSpec.edgeButtonWidth > 0 {
                HStack {
                    EdgeButton(onTap: prevPage)
                    Spacer()
                    EdgeButton(onTap: nextPage)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .gesture(
            DragGesture()
                .updating($dragState) { drag, state, transaction in
                    state = .dragging(translation: drag.translation)
                }
                .onEnded(onDragEnded)
        )
    }
    
    private func onDragEnded(drag: DragGesture.Value) {
        let dragThreshold: CGFloat = 200
        if drag.predictedEndTranslation.width > dragThreshold || drag.translation.width > dragThreshold {
            carouselLocation = carouselLocation - 1
        } else if drag.predictedEndTranslation.width < -dragThreshold || drag.translation.width < -dragThreshold {
            carouselLocation = carouselLocation + 1
        }
        updateRelativeLoc()
    }
    
    private func nextPage() {
        carouselLocation = carouselLocation + 1
        updateRelativeLoc()
    }
    
    private func prevPage() {
        carouselLocation = carouselLocation - 1
        updateRelativeLoc()
    }
    
    private func updateRelativeLoc() {
        relativeLoc = (carouselLocation % pageCount + pageCount) % pageCount
    }

    func getOpacity(_ i: Int) -> Double{
        let positions = [
            i,
            i + 1, i - 1,
            i + 2, i - 2,
            (i + 1) - pageCount, (i - 1) + pageCount,
            (i + 2) - pageCount, (i - 2) + pageCount
        ]

        if positions.contains(relativeLoc) {
            return 1
        } else {
            return 0
        }

    }
    
    func getOffset(_ i: Int) -> CGFloat {
        // Calculate differences considering wrap-around
        let forwardDiff = (i - relativeLoc + pageCount) % pageCount
        let backwardDiff = (relativeLoc - i + pageCount) % pageCount

        // Determine the minimal circular difference and its direction (+/-)
        let diff = forwardDiff <= backwardDiff ? forwardDiff : -backwardDiff

        // Calculate offset based on the difference
        switch diff {
        case 0:
            return self.dragState.translation.width
        case -3...3:
            return self.dragState.translation.width + CGFloat(diff) * (pageWidth)
        default:
            return 10000 // Arbitrary large number for 'else' case
        }
    }

    @ViewBuilder
    func EdgeButton(onTap: @escaping () -> Void) -> some View {
        Rectangle()
            .fill(.clear)
            .contentShape(Rectangle())
            .frame(width: viewSpec.edgeButtonWidth.toCGFloat())
            .frame(maxHeight: .infinity)
            .onTapGesture(perform: onTap)
    }
}


enum DragState {
    case inactive
    case dragging(translation: CGSize)
    
    var translation: CGSize {
        switch self {
        case.inactive:
            return .zero
        case.dragging(let translation):
            return translation
        }
    }
    
    var isDragging: Bool {
        switch self {
        case .inactive:
            return false
        case.dragging:
            return true
        }
    }
}

final class ExhibitViewSpec: ViewSpec {
    let width: CGFloat
    let height: CGFloat
    
    init(width: CGFloat, height: CGFloat) {
        self.width = width
        self.height = height
    }
}
