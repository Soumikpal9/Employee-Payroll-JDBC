/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Employee.Payroll.JDBC;

import org.junit.Before;
import org.junit.Test;

import Employee.Payroll.JDBC.EmployeePayrollService.IOService;
import junit.framework.Assert;

import static org.junit.Assert.*;

import io.restassured.RestAssured;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EmployeePayrollJDBCTest {
	
	
    @Test
    public void givenEmpPayrollDataInDB_ShouldMatchEmpCount() {
    	EmployeePayrollService empPayrollService = new EmployeePayrollService();
    	List<EmployeePayrollData> empPayrollData = empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	Assert.assertEquals(4, empPayrollData.size());
    }
    
    @Test 
    public void givenNewSalary_WhenUpdated_shouldMatchWithDB() {
    	EmployeePayrollService empPayrollService = new EmployeePayrollService();
    	empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	empPayrollService.updateEmployeeSalary("Soumik", 70000, IOService.DB_IO);
    	boolean result = empPayrollService.checkEmployeePayrollInSyncWithDB("Soumik");
    	Assert.assertTrue(result);
    }
    
    @Test 
    public void givenDateRange_WhenRetrieved_ShouldMatchEmpCount() {
    	EmployeePayrollService empPayrollService = new EmployeePayrollService();
    	empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	LocalDate startDate = LocalDate.of(2018, 01, 01);
    	LocalDate endDate = LocalDate.now();
    	List<EmployeePayrollData> employeePayrollData = empPayrollService.readEmployeePayrollForDateRange(IOService.DB_IO, startDate, endDate);
    	Assert.assertEquals(4, employeePayrollData.size());
    }
    
    @Test
    public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
    	EmployeePayrollService empPayrollService = new EmployeePayrollService();
    	empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	Map<String, Double> averageSalaryByGender = empPayrollService.readAverageSalaryByGender(IOService.DB_IO);
    	Assert.assertTrue(averageSalaryByGender.get("M").equals(65000.0) && averageSalaryByGender.get("F").equals(62500.0));
    }
    
    @Test
    public void givenPayrollData_WhenSumSalaryRetrievedByGender_ShouldReturnProperValue() {
    	EmployeePayrollService empPayrollService = new EmployeePayrollService();
    	empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	Map<String, Double> sumSalaryByGender = empPayrollService.readSumSalaryByGender(IOService.DB_IO);
    	Assert.assertTrue(sumSalaryByGender.get("M").equals(130000.0));
    }
    
    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
    	EmployeePayrollService empPayrollService = new EmployeePayrollService();
    	empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	empPayrollService.addEmployeeToPayroll("Soumik", 70000.0, LocalDate.now(), "M");
    	boolean result = empPayrollService.checkEmployeePayrollInSyncWithDB("Soumik");
    	Assert.assertTrue(result);
    }
    
    @Test
    public void givenEmployee_WhenDeleted_ShouldSyncWithDB() {
    	EmployeePayrollService empPayrollService = new EmployeePayrollService();
    	empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	empPayrollService.deleteEmployeeFromPayroll("Sreyansh", IOService.DB_IO);
    	boolean result = empPayrollService.checkEmployeePayrollAfterDeletion("Sreyansh");
    	Assert.assertTrue(result);
    }
    
    @Test 
    public void given3Employees_WhenAdded_ShouldMatchEmpCount() {
    	EmployeePayrollData[] empPayrollData = {
    			new EmployeePayrollData(0, "Aritra", 60000.0, "M", LocalDate.now()),
    			new EmployeePayrollData(0, "Sneha", 70000.0, "F", LocalDate.now()),
    			new EmployeePayrollData(0, "Anirban", 50000.0, "M", LocalDate.now())
    	};
    	EmployeePayrollService empPayrollService = new EmployeePayrollService();
    	empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	Instant start = Instant.now();
    	empPayrollService.addEmployeeToPayrollWithoutThreads(Arrays.asList(empPayrollData));
    	Instant end = Instant.now();
    	System.out.println("Duration without thread : " + Duration.between(start, end));
    	Instant threadStart = Instant.now();
    	empPayrollService.addEmployeeToPayrollWithThreads(Arrays.asList(empPayrollData));
    	Instant threadEnd = Instant.now();
    	System.out.println("Duration with thread : " + Duration.between(threadStart, threadEnd));
    	List<EmployeePayrollData> employeePayrollData = empPayrollService.readEmployeePayrollData(IOService.DB_IO);
    	Assert.assertEquals(7, employeePayrollData.size());
    }
}
