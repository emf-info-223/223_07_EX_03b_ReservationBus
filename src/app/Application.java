package app;

import ctrl.Controller;
import views.View;

/**
 * Module 223 - BusCompany.
 *
 * @author <a href="mailto:paul.friedli@edufr.ch">Paul Friedli</a>
 * @version 1.0.0
 */
public class Application {

    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller();
        controller.setRefView(view);
        view.setRefController(controller);
        controller.start();
    }

}
