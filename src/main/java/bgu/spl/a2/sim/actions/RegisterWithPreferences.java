package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RegisterWithPreferences extends Action<String> {

    private List<String> preferences;
    private List<Integer> grades;

    private Iterator<String> prefIter;
    private Iterator<Integer> gradeIter;

    public RegisterWithPreferences(List<String> preferences, List<Integer> grades){
        this.preferences = preferences;
        this.grades = grades;
        this.prefIter = preferences.iterator();
        this.gradeIter = grades.iterator();
    }

    @Override
    protected void start() {
        if(prefIter.hasNext() && gradeIter.hasNext()) {
            String preference = prefIter.next();
            Integer grade = gradeIter.next();
            final ParticipateInCourse participate = new ParticipateInCourse(grade, actorID);
            CoursePrivateState course = (CoursePrivateState) pool.getPrivateState(preference);
            Collection<Action<Boolean>> actions = new ArrayList<>();
            actions.add(participate);
            sendMessage(participate, preference, course);
            then(actions, new callback() {
                @Override
                public void call() {
                    if (participate.getResult().get()) {
                        complete("Student registered by preferences to " + preferences.get(0));
                    } else {
                        start();
                    }
                }
            });
        } else {
            complete("Student registration was unsuccessful");
        }
    }
}
