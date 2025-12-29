//
//  CommonPageVC.swift
//
//

import UIKit



extension UIView {
    var parentViewController: UIViewController? {
        var parentResponder: UIResponder? = self
        while parentResponder != nil {
            // swiftlint:disable:next force_unwrapping
            parentResponder = parentResponder!.next
            if let viewController = parentResponder as? UIViewController {
                return viewController
            }
        }
        return nil
    }
}

extension Array {
    subscript (safe index: Index) -> Element? {
        0 <= index && index < count ? self[index] : nil
    }
}

class CommonPageView : UIView {

    var pageViewController : UIPageViewController = UIPageViewController()
    
    var arrViewController : [UIViewController] = [] {
        didSet{
            pageViewController.delegate = self
            pageViewController.dataSource = self
            
            if let firstVC = arrViewController[safe : selectedIndex] {
                 pageViewController.setViewControllers([firstVC], direction: .forward, animated: false, completion: nil)
            } else if let firstVC = arrViewController.first {
                pageViewController.setViewControllers([firstVC], direction: .forward, animated: false, completion: nil)
            }
        }
    }
    
    private(set) var selectedIndex = 0 {
        didSet {
            selectionChange?(selectedIndex)
        }
    }
    
    var selectionChange:  ((Int) -> Void)?
    //------------------------------------------------------
        
        //MARK:- Custom Method
    
    func setViewController(index: Int)  {
        if self.selectedIndex == index { return }
        if let firstVC = arrViewController[safe : index] {
             pageViewController.setViewControllers([firstVC], direction: index > selectedIndex ? .forward : .reverse, animated: true, completion: nil)
             self.selectedIndex = index
        }
    }
    
    func setSelectedIndex(index: Int) {
        self.selectedIndex = index
    }
    
    func setUpView() {
        
        // Add Page View With Constraint
        
        pageViewController = UIPageViewController(transitionStyle: .scroll, navigationOrientation: .horizontal, options: nil)
        pageViewController.view.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(pageViewController.view)
        
        NSLayoutConstraint.activate([
            pageViewController.view.topAnchor.constraint(equalTo: self.topAnchor),
            pageViewController.view.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            pageViewController.view.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            pageViewController.view.bottomAnchor.constraint(equalTo: self.bottomAnchor),
        ])
        
        pageViewController.view.backgroundColor = UIColor.clear
        
        self.parentViewController?.addChild(pageViewController)
        
        pageViewController.didMove(toParent: self.parentViewController)
        
    }
    
    
}


//--------------------------------------------------------------------------
//MARK:- Page View Methods

extension CommonPageView : UIPageViewControllerDelegate , UIPageViewControllerDataSource , UIGestureRecognizerDelegate {
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
        
        guard let viewControllerIndex = arrViewController.firstIndex(of: viewController) else {
            return nil
        }
        
        let previousIndex = viewControllerIndex - 1
        
        guard previousIndex >= 0 else {
            return nil
        }
        
        guard arrViewController.count > previousIndex else {
            return nil
        }
        
        return arrViewController[previousIndex]
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController? {
        
        guard let viewControllerIndex = arrViewController.firstIndex(of: viewController) else {
            return nil
        }
        
        let nextIndex = viewControllerIndex + 1
        let orderedViewControllersCount = arrViewController.count
        
        guard orderedViewControllersCount != nextIndex else {
            return nil
        }
        
        guard orderedViewControllersCount > nextIndex else {
            return nil
        }
        
        return arrViewController[nextIndex]
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
        if completed {
            if let vc = pageViewController.viewControllers?.first, let index = arrViewController.firstIndex(where: { $0 == vc }) {
                self.selectedIndex = index
            }
        }
    }
    
}


