package Employee.Payroll.JDBC;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
		if(ioService.equals(IOService.DB_IO)) {
			return employeePayrollDB.getAverageSalaryByGender();
		}
		return null;
	}
	
	public Map<String, Double> readSumSalaryByGender(IOService ioService) {
		if(ioService.equals(IOService.DB_IO)) {
			return employeePayrollDB.getSumSalaryByGender();
		}
		return null;
	}
	
	public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
		employeePayrollList.add(employeePayrollDB.addEmployeeToPayroll(name, salary, startDate, gender));
	}
	
	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDB.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}
	
	public boolean checkEmployeePayrollAfterDeletion(String name) {
		for(EmployeePayrollData emp : employeePayrollList) {
			if(emp.name.equals(name)) {
				return true;
			}
		}
		return false;
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

	public void deleteEmployeeFromPayroll(String name) {
		employeePayrollDB.deleteEmployeeData(name);
	}

	public void addEmployeeToPayrollWithoutThreads(List<EmployeePayrollData> employeePayrollList) {
		employeePayrollList.forEach(employeePayrollData -> {
			//System.out.println("Employee being added : " + employeePayrollData.name);
			this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary, employeePayrollData.start, employeePayrollData.gender);
			//System.out.println("Employee added : " + employeePayrollData.name);
		});
	}
	
	public void addEmployeeToPayrollWithThreads(List<EmployeePayrollData> employeePayrollList) {
		Map<Integer, Boolean> empAdditionStatus = new HashMap<Integer, Boolean>();
		employeePayrollList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				empAdditionStatus.put(employeePayrollData.hashCode(), false);
				//System.out.println("Employee being added : " + Thread.currentThread().getName());
				this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary, employeePayrollData.start, employeePayrollData.gender);
				empAdditionStatus.put(employeePayrollData.hashCode(), true);
				//System.out.println("Employee added : " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employeePayrollData.name);
			thread.start();
		});
		while(empAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException e) {}
		}
	}

	public long countEntries(IOService ioService) {
		return employeePayrollList.size();
	}
}
