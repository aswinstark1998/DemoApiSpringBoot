package com.wellsfargo.aswin.controller;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wellsfargo.aswin.entity.Employee;
import com.wellsfargo.aswin.helper.ExcelHelper;

/*
 * @Author - Aswin. A
 * @Email - asweinnovate@gmail.com
 */

@RestController
public class ApiController {

	// Just to test GET initially
	private static ArrayList<Employee> employeeRepo = new ArrayList<>();
	static {
		Employee emp1 = new Employee("u791231", "employeeName_1", 100000.0d, "d1");
		Employee emp2 = new Employee("u791232", "employeeName_2", 100000.0d, "d2");
		Employee emp3 = new Employee("u791233", "employeeName_3", 100000.0d, "d3");
		Employee emp4 = new Employee("u791234", "employeeName_4", 100000.0d, "d4");
		Employee emp5 = new Employee("u791235", "employeeName_5", 100000.0d, "d5");
		employeeRepo.add(emp1);
		employeeRepo.add(emp2);
		employeeRepo.add(emp3);
		employeeRepo.add(emp4);
		employeeRepo.add(emp5);
	}

	@RequestMapping(value = "/employees")
	public ResponseEntity<Object> getAllEmployees() {
		return new ResponseEntity<>(employeeRepo, HttpStatus.OK);
	}

	@RequestMapping(value = "/api", method = RequestMethod.POST)
	public ResponseEntity<Object> createEmployees(@RequestParam("file") MultipartFile file) {
		List<Employee> returnedResults = new ArrayList<Employee>();
		if (ExcelHelper.hasExcelFormat(file)) {
			try {
				InputStream inputStream =  new BufferedInputStream(file.getInputStream());
				returnedResults = ExcelHelper.excelToEmployeeDetailsFromExcel(inputStream);
				employeeRepo.addAll(returnedResults);
				return ResponseEntity.status(HttpStatus.OK).body(returnedResults);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File not supported!");
	}
}
