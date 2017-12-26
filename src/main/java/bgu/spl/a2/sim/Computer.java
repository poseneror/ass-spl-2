package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {

	String computerType;
	long failSig;
	long successSig;
	
	public Computer(String computerType, long successSig, long failSig)
	{
		this.computerType = computerType;
		this.successSig = successSig;
		this.failSig = failSig;
	}
	
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		//TODO:check councurency problems
		boolean pass = true;
		for(String courseName : courses){
			if(coursesGrades.get(courseName) < 56){
				pass = false;
			}
		}
		if(pass){
			return successSig;
		} else {
			return failSig;
		}
	}
}
