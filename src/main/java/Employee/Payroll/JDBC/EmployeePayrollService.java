package Employee.Payroll.JDBC;

import java.time.LocalDate;
import java.util.List;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollJDBC employeePayrollDB;

	public EmployeePayrollService() {
		employeePayrollDB = EmployeePayrollJDBC.getInstance();
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}
	
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = employeePayrollDB.readData();
		}
		return this.employeePayrollList;
	}
	
	public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
		if(ioService.equals(IOService.DB_IO)) {
			return employeePayrollDB.getEmployeePayrollForDateRange(startDate, endDate);
		}
		return null;
	}
	
	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDB.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}
	
	public void updateEmployeeSalary(String name, double salary) {
		int result = employeePayrollDB.updateData(name, salary);
		if(result == 0)	return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if(employeePayrollData != null)	employeePayrollData.salary = salary;
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		EmployeePayrollData employeePayrollData;
		employeePayrollData = this.employeePayrollList.stream()
													  .filter(emp -> emp.name.equals(name))
													  .findFirst()
													  .orElse(null);
		return employeePayrollData;
	}
}
