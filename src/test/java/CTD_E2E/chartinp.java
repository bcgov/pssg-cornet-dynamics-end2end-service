package CTD_E2E;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.apache.commons.collections4.set.ListOrderedSet;
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
		
		for(i=1; i<=rownum;i++)
		{
			firstrow=sheet.getRow(i);
			Iterator<Cell> ce=firstrow.cellIterator();
			value=ce.next().getNumericCellValue();
			user=String.valueOf((int)value);
			user_number.add(user);
		}
		
		//System.out.println(user_number);
		return user_number;
		
		
		
	}


	@Test
	public LinkedList gety() throws IOException, InterruptedException {
	FileInputStream fis = new FileInputStream(Filepath);
	XSSFWorkbook workbook = new XSSFWorkbook(fis);
	XSSFSheet sheet=workbook.getSheetAt(0);
	LinkedHashSet<Integer> cellval=new LinkedHashSet<Integer>();
	Iterator<Row>  rows= sheet.iterator();
	int rownum=sheet.getLastRowNum();
	
	//System.out.println(rownum);
	Row firstrow;
	int i=0;
	LinkedList<Integer> timediff= new LinkedList<Integer>();
	
	double value;
	Integer timedi=null;
	
	for(i=1; i<=rownum;i++)
	{
		firstrow=sheet.getRow(i);
		Iterator<Cell> ce=firstrow.cellIterator();
		ce.next();
		value=ce.next().getNumericCellValue();
		timedi=Integer.valueOf((int)value);
		timediff.add(timedi);
	}
	//System.out.println(timediff);
	return timediff;
	
}
	
}