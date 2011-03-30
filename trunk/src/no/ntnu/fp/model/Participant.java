package no.ntnu.fp.model;

public class Participant {
	
	public enum Status {
		AWAITING_REPLY,ATTENDING,NOT_ATTENDING;
	}
	
	private Employee employee;
	private Status status;
	
	public Participant(Employee employee, Status status) {
		this.employee = employee;
		this.status = status;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public static Status intToEnum(int ordinal) {
		switch(ordinal) {
			case 1:
				return Status.ATTENDING;
			case 2:
				return Status.NOT_ATTENDING;
			default:
				return Status.AWAITING_REPLY;
		}
	}
}
