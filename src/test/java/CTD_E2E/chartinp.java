package CTD_E2E;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

public class chartinp {
	
	public String Filepath = System.getProperty("user.dir") + "/Chart/chart.xlsx";

	@Test
	public LinkedHashSet<String> getfirstaxis() throws IOException, InterruptedException {
		FileInputStream fis = new FileInputStream(Filepath);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet=workbook.getSheetAt(0);
		Iterator<Row>  rows= sheet.iterator();
		int rownum=sheet.getLastRowNum();
		Row firstrow;
		int i=0;
		LinkedHashSet<String> user_number= new LinkedHashSet<String>();
		Iterator<String> it=user_number.iterator();
		
		double value;
		String user=null;
		
		for(i=0; i<=rownum;i++)
		{
			firstrow=sheet.getRow(i);
			Iterator<Cell> ce=firstrow.cellIterator();
			value=ce.next().getNumericCellValue();
			user=String.valueOf((int)value);
			user_number.add(user);
		}
		return user_number;
		
		
		
	}



public LinkedHashSet gety() throws IOException, InterruptedException {
	FileInputStream fis = new FileInputStream(Filepath);
	XSSFWorkbook workbook = new XSSFWorkbook(fis);
	XSSFSheet sheet=workbook.getSheetAt(0);
	LinkedHashSet<Integer> cellval=new LinkedHashSet<Integer>();
	Iterator<Row>  rows= sheet.iterator();
	int rownum=sheet.getLastRowNum();
	Row firstrow;
	int i=0;
	LinkedHashSet<Integer> timediff= new LinkedHashSet<Integer>();
	
	double value;
	Integer timedi=null;
	
	for(i=0; i<=rownum;i++)
	{
		firstrow=sheet.getRow(i);
		Iterator<Cell> ce=firstrow.cellIterator();
		ce.next();
		value=ce.next().getNumericCellValue();
		timedi=Integer.valueOf((int)value);
		timediff.add(timedi);
	}
	
	return timediff;
	
}
	
}