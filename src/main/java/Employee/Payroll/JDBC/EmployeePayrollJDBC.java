/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Employee.Payroll.JDBC;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollJDBC {
	private static EmployeePayrollJDBC employeePayrollDB;
	private PreparedStatement employeePayrollDataStatement;
	
	private EmployeePayrollJDBC() {}
	
	public static EmployeePayrollJDBC getInstance() {
		if(employeePayrollDB == null) {
			employeePayrollDB = new EmployeePayrollJDBC();
		}
		return employeePayrollDB;
	}
	
	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee?useSSL=false";
		String userName = "root";
		String password = "Resurrection@5";
		Connection connection;
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		return connection;
	}

	public List<EmployeePayrollData> readData() {
		String sql = "SELECT * FROM emp_payroll ; ";
		return this.getEmployeePayrollDataUSingDB(sql);
	}
	
	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List<EmployeePayrollData> employeePayrollList = null;
		if(this.employeePayrollDataStatement == null) {
			this.prepareStatementForEmployeeData();
		}
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet result = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(result);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	
	public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("SELECT * FROM emp_payroll WHERE start BETWEEN '%s' AND '%s'", Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getEmployeePayrollDataUSingDB(sql);
	}

	private List<EmployeePayrollData> getEmployeePayrollDataUSingDB(String sql) {
		List<EmployeePayrollData> empPayrollList = new ArrayList<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			empPayrollList = this.getEmployeePayrollData(result);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return empPayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while(result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				String gender = result.getString("gender");
				LocalDate start = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id,name,salary,gender,start));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public int updateData(String name, double salary) {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}
	
	public Map<String, Double> getAverageSalaryByGender() {
		String sql = "SELECT gender, AVG(salary) AS avg_salary FROM emp_payroll GROUP BY gender";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("avg_salary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return genderToAverageSalaryMap;
	}
	
	public Map<String, Double> getSumSalaryByGender() {
		String sql = "SELECT gender, SUM(salary) AS sum_salary FROM emp_payroll GROUP BY gender";
		Map<String, Double> genderToSumSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("sum_salary");
				genderToSumSalaryMap.put(gender, salary);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return genderToSumSalaryMap;
	}
	
	public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate startDate, String gender) {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format("INSERT INTO emp_payroll (name, salary, start, gender) VALUES ('%s', %s, '%s', '%s')", name, salary, Date.valueOf(startDate), gender);
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if(result.next())	employeeId = result.getInt("id");
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, salary, gender, startDate);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}
	
	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		try(Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO emp_payroll (name, salary, start, gender) VALUES ('%s', '%s', '%s', '%s')", name, salary, Date.valueOf(startDate), gender);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if(result.next())	employeeId = result.getInt(1);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			}
			catch(SQLException e1) {
				e1.printStackTrace();
			}
		}
		try(Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			System.out.println(deductions);
			double taxable_pay = salary - deductions;
			double tax = taxable_pay * 0.1;
			double net_pay = salary - tax;
			String sql = String.format("INSERT INTO payroll_details (employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", employeeId, salary, deductions, taxable_pay, tax, net_pay);
			int rowAffected = statement.executeUpdate(sql);
			if(rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary, gender, startDate);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			}
			catch(SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(connection != null) {
				try {
					connection.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return employeePayrollData;
	}
	
	private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update emp_payroll set salary = %.2f where name = %s", salary, name);
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
		String sql = "update emp_payroll set salary = ? where name = ?";
		try(Connection connection = this.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			return preparedStatement.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM emp_payroll WHERE name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteEmployeeData(String name) {
		String sql = String.format("DELETE FROM emp_payroll WHERE name = '%s'", name);
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}	
}
