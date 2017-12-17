package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CheckObligations extends Action<String> {

    private List<String> students, conditions;
    private String cType;

    public CheckObligations(List<String> students, List<String> conditions, String cType){
        this.students = students;
        this.conditions = conditions;
        this.cType = cType;

        setActionName("Check Obligations");
    }


    @Override
    protected void start() {
        final Collection<Action<String>> actions = new ArrayList<>();
        for(String name : students){
            final String studentName = name;
            final Promise<Computer> promise = Warehouse.getInstance().getComputer(cType);
            final StudentPrivateState student = (StudentPrivateState) pool.getPrivateState(studentName);
            final SignStudent signStudent = new SignStudent();
            actions.add(signStudent);
            promise.subscribe(new callback() {
                @Override
                public void call() {
                    final long sign = promise.get().checkAndSign(conditions, student.getGrades());
                    signStudent.setSign(sign);
                    sendMessage(signStudent, studentName, student);
                    Warehouse.getInstance().releaseComputer(cType);
                }
            });
        }
        then(actions, new callback() {
            @Override
            public void call() {
                complete("Obligation check completed");
            }
        });
    }
}
