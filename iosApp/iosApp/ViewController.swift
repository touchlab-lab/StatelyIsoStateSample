import UIKit
import app

class ViewController: UIViewController {
    @IBAction func addMapItemsClicked(_ sender: Any) {
        StateSampleKt.testData()
    }
    
    @IBAction func addMapItemsDetachedClicked(_ sender: Any) {
        DetachedStateSampleKt.testDetachedData()
    }
    
    @IBAction func perfCheckClicked(_ sender: Any) {
        StateSampleKt.perfCheck1()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        label.text = Proxy().proxyHello()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @IBOutlet weak var label: UILabel!
}
