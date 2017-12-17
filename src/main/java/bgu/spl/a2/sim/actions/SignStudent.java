package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class SignStudent extends Action<String> {

    private long sign;

    public SignStudent(){
        setActionName("Sign Student");
    }

    public void setSign(long sign) {
        this.sign = sign;
    }

    @Override
    protected void start() {
        StudentPrivateState student = (StudentPrivateState) pool.getPrivateState(actorID);
        student.setSignature(sign);
        complete("Student signature updated");
    }
}
