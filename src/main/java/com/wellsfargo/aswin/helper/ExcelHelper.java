package com.wellsfargo.aswin.helper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.wellsfargo.aswin.entity.Employee;

/*
 * @Author - Aswin. A
 * @Email - asweinnovate@gmail.com
 */

public class ExcelHelper {
	public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	static String[] HEADERs = { "employeeId", "employeeName", "employeeSalary", "employeeDept"};
	
	//This name must be same as the Sheet name
	static String SHEET = "data";

	public static boolean hasExcelFormat(MultipartFile file) {

		if (!TYPE.equals(file.getContentType())) {
			return false;
		}
		return true;
	}

	public static List<Employee> excelToEmployeeDetailsFromExcel(InputStream inputStream) {
		try {
			Workbook workbook = new XSSFWorkbook(inputStream);

			Sheet sheet = workbook.getSheet(SHEET);
			Iterator<Row> rows = sheet.iterator();
			
			List<Employee> employeeList = new ArrayList<Employee>();

			int rowNumber = 0;
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber == 0) {
					rowNumber++;
					continue;
				}
	
				Iterator<Cell> cellsInRow = currentRow.iterator();

				Employee employee = new Employee();

				int cellIdx = 0;
				while (cellsInRow.hasNext()) {
					Cell currentCell = cellsInRow.next();
					DataFormatter df = new DataFormatter();
					String value = df.formatCellValue(currentCell);
					value = value.trim();

					switch (cellIdx) {
					case 0:
						employee.setEmployeeId(value);
						break;

					case 1:
						employee.setEmployeeName(value);
						break;

					case 2:
						employee.setEmployeeSalary(Double.parseDouble(value));
						break;

					case 3:
						employee.setEmployeeDept(value);
						break;

					default:
						break;
					}
					cellIdx++;
				}
				employeeList.add(employee);
			}
			workbook.close();
			Collections.sort(employeeList,
	                 new Comparator<Employee>()
	                 {
	                     public int compare(Employee e1,
	                    		 Employee e2)
	                     {
	                         if (e1.getEmployeeSalary() ==
	                                 e2.getEmployeeSalary())
	                         {
	                             return 0;
	                         }
	                         else if (e1.getEmployeeSalary() <
	                                 e2.getEmployeeSalary())
	                         {
	                             return -1;
	                         }
	                         return 1;
	                     }
	                 });
			return employeeList;
		} catch (Exception e) {
			throw new RuntimeException("Parsing error "+e.getMessage());
		}
	}
}