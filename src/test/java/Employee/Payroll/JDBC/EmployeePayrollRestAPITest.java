package Employee.Payroll.JDBC;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayrollRestAPITest {
	@Before
    public void setup() {
    	RestAssured.baseURI = "http://localhost";
    	RestAssured.port = 3000;
    }
	
	private EmployeePayrollData[] getEmpList() {
		Response response = RestAssured.get("/employees");
		EmployeePayrollData[] arrOfEmp = new Gson().fromJson(response.asString(),  EmployeePayrollData[].class);
		return arrOfEmp;
	}
	
	private Response addEmpToJSONServer(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employees");
	}
	
	private Response updateEmpToJSONServer(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.put("/employees/" + employeePayrollData.id);
	}
	
	@Test
	public void givenEmployeeDataInJsonServer_WhenRetrived_ShouldMatchCount() {
		EmployeePayrollData[] arrOfEmp = getEmpList();
		EmployeePayrollService empPayrollService;
		empPayrollService = new EmployeePayrollService(Arrays.asList(arrOfEmp));
		long entries = empPayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
		assertEquals(3, entries);
	}
	
	@Test
	public void givenNewEmp_WhenAdded_ShouldReturn201ResponseAndCount() {
		EmployeePayrollData[] arrOfEmp = getEmpList();
		EmployeePayrollService empPayrollService;
		empPayrollService = new EmployeePayrollService(Arrays.asList(arrOfEmp));
		
		EmployeePayrollData empPayrollData = null;
		empPayrollData = new EmployeePayrollData(0, "Sneha", 65000.0, "F", LocalDate.now());
		Response response = addEmpToJSONServer(empPayrollData);
		int statusCode = response.getStatusCode();
		assertEquals(201, statusCode);
		
		EmployeePayrollData[] arrOfEmployee = getEmpList();
		empPayrollService = new EmployeePayrollService(Arrays.asList(arrOfEmployee));
		long entries = empPayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
		assertEquals(4, entries);
	}
	
	@Test
	public void givenListOfNewEmp_WhenAdded_ShouldReturn201ResponseAndCount() {
		EmployeePayrollData[] arrOfEmp = getEmpList();
		EmployeePayrollService empPayrollService;
		empPayrollService = new EmployeePayrollService(Arrays.asList(arrOfEmp));
		
		EmployeePayrollData[] arrOfEmpPayroll = {
				new EmployeePayrollData(0, "Sudhanshu", 58000.0, "M", LocalDate.now()),
				new EmployeePayrollData(0, "Manav", 55000.0, "M", LocalDate.now()),
				new EmployeePayrollData(0, "Shivangi", 75000.0, "F", LocalDate.now())
		};
		for(EmployeePayrollData empPayrollData : arrOfEmpPayroll) {
			Response response = addEmpToJSONServer(empPayrollData);
			int statusCode = response.getStatusCode();
			assertEquals(201, statusCode);
		}
		
		EmployeePayrollData[] arrOfEmployee = getEmpList();
		empPayrollService = new EmployeePayrollService(Arrays.asList(arrOfEmployee));
		long entries = empPayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
		assertEquals(7, entries);
	}
	
	@Test 
	public void givenNewSalaryForEmp_WhenAdded_ShouldReturn200Response() {
		EmployeePayrollData[] arrOfEmp = getEmpList();
		EmployeePayrollService empPayrollService;
		empPayrollService = new EmployeePayrollService(Arrays.asList(arrOfEmp));
		
		empPayrollService.updateEmployeeSalary("Soumik", 75000.0, EmployeePayrollService.IOService.REST_IO);
		EmployeePayrollData empPayrollData = empPayrollService.getEmployeePayrollData("Soumik");
		
		Response response = updateEmpToJSONServer(empPayrollData);
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
	}
}
