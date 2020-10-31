package Employee.Payroll.JDBC;

import java.time.LocalDate;
import java.util.List;

public class EmployeePayrollData {
	public int id;
	public String name;
	public double salary;
	public LocalDate start;
	public String gender;
	public List<String> deptList;
	
	public EmployeePayrollData(Integer id, String name, Double salary, String gender) {
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.gender = gender;
	}
	
	public EmployeePayrollData(Integer id, String name, Double salary, String gender, LocalDate start) {
		this(id,name,salary, gender);
		this.start = start;
	}
	
	public EmployeePayrollData(Integer id, String name, Double salary, String gender, LocalDate start, List<String> deptList) {
		this(id,name,salary, gender, start);
		this.deptList = deptList;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		EmployeePayrollData that = (EmployeePayrollData)o;
		return id == that.id && 
				Double.compare(that.salary, salary) == 0 &&
				name.contentEquals(that.name) && this.gender.contentEquals(that.gender) && this.deptList.equals(that.deptList);
	}
}
