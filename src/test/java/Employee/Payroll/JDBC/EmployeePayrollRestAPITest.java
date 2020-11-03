package Employee.Payroll.JDBC;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import junit.framework.Assert;

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
	
	/*private Response addEmpToJsonServer(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employees");
	}*/
	
	@Test
	public void givenEmployeeDataInJsonServer_WhenRetrived_ShouldMatchCount() {
		EmployeePayrollData[] arrOfEmp = getEmpList();
		EmployeePayrollService empPayrollService;
		empPayrollService = new EmployeePayrollService(Arrays.asList(arrOfEmp));
		long entries = empPayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
		System.out.println(entries);
		Assert.assertEquals(3, entries);
	}
}
