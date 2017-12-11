package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;

public class ParticipateInCourse extends Action {

    private Integer grade;
    private String name;
    public ParticipateInCourse(Integer grade, String name){
        super();
        this.grade = grade;
        this.name = name;
    }

    @Override
    protected void start() {
        CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(actorID);
        List<String> prequisites = course.getPrequisites();
        StudentPrivateState student = (StudentPrivateState) pool.getPrivateState(name);

        //TODO: check if can implement atomic integers - what if 2 students register at the same time when there is one spot left
        if(course.getAvailableSpots() > 0){
            boolean pass = true;
            for(String pre : prequisites){
                if(!student.getGrades().containsKey(pre)){
                    pass = false;
                }
            }
            if(pass){
                course.addStudent(name);
                student.getGrades().put(actorID, grade);
            }
        }
    }
}
