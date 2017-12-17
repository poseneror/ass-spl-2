package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;

public class ParticipateInCourse extends Action<Boolean> {

    private Integer grade;
    private String studentName;
    public ParticipateInCourse(Integer grade, String studentName){
        super();
        this.grade = grade;
        this.studentName = studentName;

        setActionName("Participate In Course");
    }

    @Override
    protected void start() {
        CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(actorID);
        List<String> prequisites = course.getPrequisites();
        StudentPrivateState student = (StudentPrivateState) pool.getPrivateState(studentName);

        //TODO: check if can implement atomic integers - what if 2 students register at the same time when there is one spot left
        if(course.getAvailableSpots() > 0){
            boolean pass = true;
            for(String pre : prequisites){
                if(!student.getGrades().containsKey(pre)){
                    pass = false;
                }
            }
            if(pass){
                course.addStudent(studentName);
                //TODO: let student do this?
                student.getGrades().put(actorID, grade);
                complete(true);
            } else {
                complete(false);
            }
        } else {
            complete(false);
        }
    }
}
