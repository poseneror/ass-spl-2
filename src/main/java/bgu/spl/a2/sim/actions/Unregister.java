package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class Unregister extends Action<String> {

    private String studentName;

    public Unregister(String studnetName){
        this.studentName = studnetName;
        setActionName("Unregister");
    }

    @Override
    protected void start() {
        CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(actorID);
        String courseName = actorID;
        StudentPrivateState studnet = (StudentPrivateState) pool.getPrivateState(studentName);
        //TODO: let student do this?
        Action<String> waitForStud = new Action<String>() {
            @Override
            protected void start() {
                complete("wait complete");
            }
        };
        sendMessage(waitForStud, studentName, studnet).subscribe(new callback() {
            @Override
            public void call() {
                Action<String> waitTillReg = new Action<String>() {
                    @Override
                    protected void start() {
                        complete("wait complete");
                    }
                };
                sendMessage(waitTillReg, courseName, course).subscribe(new callback() {
                    @Override
                    public void call() {
                        Action<String> removeGrade = new Action<String>() {
                            @Override
                            protected void start() {
                                studnet.getGrades().remove(courseName);
                                complete("Grade removed from student - " + studentName);
                            }
                        };
                        sendMessage(removeGrade, studentName, studnet).subscribe(new callback() {
                            @Override
                            public void call() {
                                Action<String> unreg = new Action<String>() {
                                    @Override
                                    protected void start() {
                                        course.removeStudent(studentName);
                                        complete(studentName + " removed from " + courseName);
                                    }
                                };
                                sendMessage(unreg, courseName, course).subscribe(new callback() {
                                    @Override
                                    public void call() {
                                        complete(studentName + " unregistered from " + courseName);
                                    }
                                });

                            }
                        });
                    }
                });
            }
        });

    }
}
