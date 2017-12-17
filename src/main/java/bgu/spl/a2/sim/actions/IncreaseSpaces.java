package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class IncreaseSpaces extends Action<String> {
    private int amount;

    public IncreaseSpaces(int amount){
        this.amount = amount;

        setActionName("Increase Spaces");
    }

    @Override
    protected void start() {
        CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(actorID);
        course.setAvailableSpots(course.getAvailableSpots() + amount);
        complete("Course spaces increased by " + amount);
    }
}
