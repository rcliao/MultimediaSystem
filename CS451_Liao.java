/*******************************************************
 CS451 Multimedia Software Systems
 *******************************************************/

// import all homework assignments
import ctrl.*;
import views.*;
import models.*;

public class CS451_Liao {
	public static void main(String[] args) {
		// the program has been designed in MVC pattern, please look to the class for the detail
		/***************************************
			Model is doing the image conversion
			Controller is going to set up the event responding to the view
			View is going to set up the GUI
		 **************************************/

		ImageModel model = new ImageModel();
		Views view = new Views(model);
		Controllers controller = new Controllers(model, view);
	}
}