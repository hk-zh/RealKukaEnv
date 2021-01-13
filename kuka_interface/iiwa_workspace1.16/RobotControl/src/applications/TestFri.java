package applications;

import com.kuka.roboticsAPI.RoboticsAPIContext;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;

import robot.SunriseConnector;
import utility.SingleInstanceChecker;

public class TestFri extends RoboticsAPIApplication {

    public TestFri(RoboticsAPIContext ctx) {
        super(ctx);
    }


    public static void main(String[] args) {
        RoboticsAPIContext.useGracefulInitialization(true);
        new SingleInstanceChecker().start();

        TestFri app = new TestFri(RoboticsAPIContext.createFromResource(TestFri.class, "RoboticsAPI.config.xml"));

        SunriseConnector.initialize(app);

        app.run();
    }

    @Override
    public void run() {
//        SunriseConnector.getInstance().advancedGravcompLoop();
        SunriseConnector.getInstance().advancedGravcomp();
    }

}
