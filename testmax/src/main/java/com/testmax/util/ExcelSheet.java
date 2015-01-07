/*
 * Copyright (C) 2014 Artitelly Inc,
 *
 * Licensed under the Common Public Attribution License Version 1.0 (CPAL-1.0) (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/CPAL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.testmax.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import com.testmax.framework.WmLog;

import au.com.bytecode.opencsv.CSVReader;


public class ExcelSheet
{

	private String fileName;
	
	private String sheet;
	
	public void setFileName(String file){
		this.fileName=file;
	}
	
	public String getFileName(){
		return this.fileName;
	}
	public ExcelSheet( String file){
		this.fileName=file;
		
	}
	/**
	* This method is used to read the data's from an excel file.
	* @param fileName - Name of the excel file.
	*/
	private List readExcelFile()
	{
			
			List cellDataList = new ArrayList();
			try
			{
			
			FileInputStream fileInputStream = new FileInputStream(this.fileName);			
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);			
			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);			
			Iterator rowIterator = hssfSheet.rowIterator();
			
			while (rowIterator.hasNext())
			{
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();				
				Iterator iterator = hssfRow.cellIterator();
				List cellTempList = new ArrayList();
				while (iterator.hasNext())
				{
					HSSFCell hssfCell = (HSSFCell) iterator.next();
					cellTempList.add(hssfCell);
				}
				cellDataList.add(cellTempList);
				}
			}
			catch (Exception e)
			{
				WmLog.printMessage("Can not read XLs file="+this.fileName);
				e.printStackTrace();
			}
			
		//printToConsole(cellDataList);
		
		return cellDataList;
	}
	
	/**
	* This method is used to print the cell data to the console.
	* @param cellDataList - List of the data's in the spreadsheet.
	*/
	@SuppressWarnings("rawtypes")
	private void printToConsole(List cellDataList)
	{
		for (int i = 0; i < cellDataList.size(); i++)
		{
			List cellTempList = (List) cellDataList.get(i);
			for (int j = 0; j < cellTempList.size(); j++)
			{
				HSSFCell hssfCell = (HSSFCell) cellTempList.get(j);
				String stringCellValue = hssfCell.toString();
				System.out.print(stringCellValue + "\t");
			}
		System.out.println();
		}
	}
	public int getColumnIndex(String columnName){
		List cellColList=null;
		try{
		List cellDataList=this.readExcelFile();
		if(cellDataList.size()>0){
		 cellColList = (List) cellDataList.get(0);
		}
		for(int i=0;i<cellColList.size();i++){
			HSSFCell hssfCol = (HSSFCell) cellColList.get(i);
			String attr = hssfCol.toString();
			if(attr.equals(columnName)){
				return i;
			}
		}
		}catch (Exception e){
			WmLog.printMessage("Can not Find Column Name="+columnName+ " for row="+(cellColList!=null?cellColList.get(0):" No Row"));
		}
		return(-1);
	}
	
	public int getRowIndexByColumnValue(int colIndex,String columnValue){
		List cellRowData=null;
		List cellDataList=this.readExcelFile();
		
		for(int i=1;i<cellDataList.size();i++){
			
			try{
				cellRowData=(List)cellDataList.get(i);
				HSSFCell hssfCell = (HSSFCell) cellRowData.get(colIndex);
				String value = hssfCell.toString();
			
				double y=new Double(value);
				int x=new Double(value).intValue();
				if(x==y){
					value=String.valueOf(x);
				}
			
				if(value.equals(columnValue)){
					return i;
				}
			}catch (Exception e){
				WmLog.printMessage("Could not Find value for Column Index="+colIndex + " and column Value="+columnValue);
			}
		}
		return(-1);
	}
	
	public String getXMLDatasetFromCSV()
	{
		boolean executetest=true;
		List<String[]> cellDataList=new ArrayList<String[]>();
		String[] cellColList=null;
		CSVReader reader=null;
		File xlFile=new File(this.fileName);
		try {
			 reader = new CSVReader(new FileReader(this.fileName));
			 String [] nextLine;
			 while ((nextLine = reader.readNext()) != null) {
				 cellDataList.add(nextLine);
				 
			 }
		} catch (FileNotFoundException e1) {
			
			WmLog.printMessage("$$$$$ ERROR in modifying CSV file ="+this.fileName +" >>"+e1.getMessage());
			e1.printStackTrace();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			WmLog.printMessage("$$$$$ ERROR in modifying CSV file ="+this.fileName +" >>"+e1.getMessage());
			
			e1.printStackTrace();
		} 
		String name=xlFile.getName().substring(0, xlFile.getName().indexOf(".")).trim();
		String xml="<global name=\"xls\">\n\t<"+name+" description=\"XML data from Xls="+this.fileName+"\">\n";
		
		if(cellDataList.size()>0){
		 cellColList =cellDataList.get(0);
		}else{
			return "<dataset></dataset>";
		}
		for (int i = 1; i < cellDataList.size(); i++) 
		{  
			executetest=true;
			String data="\t\t<data ";
			String[] cellTempList = cellDataList.get(i);
			for (int j = 0; j < cellColList.length; j++)
			{	
				//get column as attribute
				
				String attr = cellColList[j].toString();
				
				//get Value
				String value = cellTempList[j].toString();
				
				try{
					double y=new Double(value);
					int x=new Double(value).intValue();
					if(x==y){
						value=String.valueOf(x);
					}
				}catch (Exception e){}
				//System.out.print(value + "\t");
				data+=attr+ "=\""+value+"\" ";
				if(attr.equalsIgnoreCase("run") &&value.equalsIgnoreCase("off")){
					executetest=false;
				}
			}
			if(executetest){
				xml+=data+"/>\n";
			}
		//System.out.println();
		}
		xml+="\t</"+name+">\n</global>";
		//System.out.print(xml);
		return xml;
	}
	public String getXMLDataset()
	{
		boolean executetest=true;
		List cellColList=null;
		File xlFile=new File(this.fileName);
		String name=xlFile.getName().substring(0, xlFile.getName().indexOf(".")).trim();
		String xml="<global name=\"xls\">\n\t<"+name+" description=\"XML data from Xls="+this.fileName+"\">\n";
		List cellDataList=this.readExcelFile();
		if(cellDataList.size()>0){
		 cellColList = (List) cellDataList.get(0);
		}else{
			return "<dataset></dataset>";
		}
		for (int i = 1; i < cellDataList.size(); i++) 
		{  
			executetest=true;
			String data="\t\t<data ";
			List cellTempList = (List) cellDataList.get(i);
			for (int j = 0; j < cellColList.size(); j++)
			{	
				//get column as attribute
				HSSFCell hssfCol = (HSSFCell) cellColList.get(j);
				String attr = hssfCol.toString();
				
				//get Value
				HSSFCell hssfCell = (HSSFCell) cellTempList.get(j);
				String value = hssfCell.toString();
				
				try{
					double y=new Double(value);
					int x=new Double(value).intValue();
					if(x==y){
						value=String.valueOf(x);
					}
				}catch (Exception e){}
				//System.out.print(value + "\t");
				data+=attr+ "=\""+value+"\" ";
				if(attr.equalsIgnoreCase("run") &&value.equalsIgnoreCase("off")){
					executetest=false;
				}
			}
			if(executetest){
				xml+=data+"/>\n";
			}
		//System.out.println();
		}
		xml+="\t</"+name+">\n</global>";
		//System.out.print(xml);
		return xml;
	}
	
	
	/**
	* This method is used to modify data from an excel file.
	* @param sheetIndex - Index of sheet 0,1,2 etc.
	* @param rowIndex - Index of row 0,1,2 etc.
	* @param colIndex - Index of col 0,1,2 etc.
	* @param value - value to be modified
	*/
	public void modifyExcelCol(int sheetIndex,int rowIndex,int colIndex,String value)
	{  
		 try {
				FileInputStream fileInputStream = new FileInputStream(this.fileName);	
				POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);	
				HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
				HSSFSheet sheet = workBook.getSheetAt(sheetIndex);	
				sheet.setForceFormulaRecalculation(true); 
			    Row row = sheet.getRow(rowIndex);			   
			    Cell cell = row.getCell(colIndex);			   
			    cell.setCellValue(value); 
			    FileOutputStream fileOut = new FileOutputStream(this.fileName);	   
			    workBook.write(fileOut);
			    fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	}
	
	/**
	* This method is used to modify data from an excel file.
	* @param sheetIndex - Index of sheet 0,1,2 etc.	* 
	* @param rowColFilter - [filtercol=value]@[modifyCol1=value,modifyCol2=value,...] 
	* filterCol= filter column to identify the row[s] to be modify with a value
	* modifyCol[n]= column to be modified found by filter col with a value
	*/
	public void modifyMultiRowExcel(int sheetIndex,String rowColFilterText)
	{  
		 try {
			    
				FileInputStream fileInputStream = new FileInputStream(this.fileName);	
				POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);	
				HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
				HSSFSheet sheet = workBook.getSheetAt(sheetIndex);	
				sheet.setForceFormulaRecalculation(true); 
				String[]rowColFilters=rowColFilterText.split(";");
				for(String rowColFilter:rowColFilters){
					String filter="";
					String modcol="";
					try{
						filter=rowColFilter.split("@")[0].replace("[", "").replace("]", "").trim();
					    modcol=rowColFilter.split("@")[1].replace("[", "").replace("]", "").trim();
						if(filter!=null &&!filter.isEmpty()){
							String[] filters=filter.split("=");
							int  colIndex=this.getColumnIndex(filters[0]);
							int rowIndex=this.getRowIndexByColumnValue(colIndex, filters[1]);
							if(rowIndex>=0){
							    Row row = sheet.getRow(rowIndex);
							    String []colModList=modcol.split(",");
							    for(String eachCol:colModList){
							    	String[]eachList=eachCol.split("=");
							    	if(eachList.length>0){
								    	int  modColIndex=this.getColumnIndex(eachList[0]);
								    	Cell cell = row.getCell(modColIndex);			   
								    	cell.setCellValue(eachList[1]); 
							    	}
							    }
							}
						}
					}catch (Exception e) {
						WmLog.printMessage("ERROR in modifying XLs file ="+this.fileName+" Excel Sheet Index="+sheetIndex+"  Col Filter="+filter + " Mod Col="+modcol);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			    FileOutputStream fileOut = new FileOutputStream(this.fileName);	   
			    workBook.write(fileOut);
			    fileOut.close();
		} catch (IOException e) {
			WmLog.printMessage("ERROR in modifying XLs file ="+this.fileName+"Excel Sheet Index="+sheetIndex+" with Excel Row Col Filter="+rowColFilterText);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			WmLog.printMessage("ERROR in modifying XLs file ="+this.fileName+"Excel Sheet Index="+sheetIndex+" with Excel Row Col Filter="+rowColFilterText);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	}
	
	/**
	* This method is used to read the data's from an excel file.
	* @param sheetIndex - Index of sheet 0,1,2 etc.
	* @param rowIndex - Index of row 0,1,2 etc.
	* @param colIndex - Index of col 0,1,2 etc.
	* 
	*/
	private String readExcelCol(int sheetIndex,int rowIndex,int colIndex)
	{    String cellContents ="";
		 try {
				FileInputStream fileInputStream = new FileInputStream(this.fileName);	
				POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);	
				HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
				HSSFSheet sheet = workBook.getSheetAt(sheetIndex);				
			    Row row = sheet.getRow(rowIndex);
			    Cell cell = row.getCell(colIndex);
			    cellContents = cell.getStringCellValue(); 
			    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			WmLog.printMessage("ERROR in reading ="+this.fileName+"Excel Sheet Index="+sheetIndex+" Excel Row Index="+ rowIndex+ "Excel Col Index="+colIndex);
			e.printStackTrace();
			return null;
		}
		
		return( cellContents);
	  
	}
	
	/**
	* This method is used to read the data's from an excel file.
	* @param sheetIndex - Index of sheet 0,1,2 etc.
	* @param rowIndex - Index of row 0,1,2 etc.
	* 
	*/
	private List<String> readExcelRow(int sheetIndex,int rowIndex)
	{    String cellContents ="";
	     ArrayList<String> rowVal= new ArrayList<String>();
		 try {
				FileInputStream fileInputStream = new FileInputStream(this.fileName);	
				POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);	
				HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
				HSSFSheet sheet = workBook.getSheetAt(sheetIndex);				
			    Row row = sheet.getRow(rowIndex);
			    Iterator<Cell> colIt=row.cellIterator();
			    while(colIt.hasNext()){
			    	Cell cell = colIt.next();
			    	cellContents = cell.getStringCellValue();
			    	rowVal.add(cellContents);
			    }
			   
		} catch (IOException e) {
			WmLog.printMessage("ERROR in reading Excel File="+this.fileName+" Sheet Index="+sheetIndex+" Excel Row Index="+ rowIndex +" "+ e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return(rowVal);
	  
	}
	
	/**
	* This method is used to read the data's from an excel file.
	* @param sheetIndex - Index of sheet 0,1,2 etc.
	* 
	*/
	private List<ArrayList> readExcel(int sheetIndex)
	{    String cellContents ="";
		 ArrayList<ArrayList> excel= new ArrayList<ArrayList>();
	     ArrayList<String> rowVal= new ArrayList<String>();
		 try {
				FileInputStream fileInputStream = new FileInputStream(this.fileName);	
				POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);	
				HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
				HSSFSheet sheet = workBook.getSheetAt(sheetIndex);	
				Iterator<Row> rowIt=sheet.rowIterator();
				  while(rowIt.hasNext()){
					    Row row = rowIt.next();
					    Iterator<Cell> colIt=row.cellIterator();
					    while(colIt.hasNext()){
					    	Cell cell = colIt.next();
					    	cellContents = cell.getStringCellValue();
					    	rowVal.add(cellContents);
					    }
					    excel.add(rowVal);
				  }
			   
		} catch (IOException e) {
			WmLog.printMessage("ERROR in reading Excel Sheet Index="+sheetIndex+" Excel File="+ this.fileName+" "+ e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return(excel);
	  
	}
	
	 public void addRowWithFormat(String sheetName,int row,  String[] value, String[] format) {
		try {
			String formatV="";
			FileInputStream fileInputStream = new FileInputStream(this.fileName);			
			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);			
			HSSFWorkbook workbook = new HSSFWorkbook(fsFileSystem);	
			HSSFSheet worksheet=workbook.getSheet(sheetName);
			if(worksheet==null){
			 worksheet = workbook.createSheet(sheetName);
			}
			// index from 0,0... cell A1 is cell(0,0)
			HSSFRow row1 = worksheet.createRow(row);
	       
        	for(int col=0;col<value.length;col++){
				HSSFCell cellA1 = row1.createCell(col);
				cellA1.setCellValue(value[col]);
				if(format.length>=col){
					HSSFCellStyle cellStyle = workbook.createCellStyle();				
					cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(format[col]));
					//cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
					//cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					cellA1.setCellStyle(cellStyle);
				}
        	}
        	FileOutputStream fileOut = new FileOutputStream(this.fileName);
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			WmLog.printMessage("ERROR in adding row XLs file ="+this.fileName+"Excel Sheet Index="+sheetName+" with Excel Row ="+row +" "+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			WmLog.printMessage("ERROR in adding row XLs file ="+this.fileName+"Excel Sheet Index="+sheetName+" with Excel Row ="+row +" "+ e.getMessage());
			e.printStackTrace();
		}
	
	}
	 public void addRow(String sheetName,int row, String[] value) {
		 try {
			 	FileInputStream fileInputStream = new FileInputStream(this.fileName);			
				POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);			
				HSSFWorkbook workbook = new HSSFWorkbook(fsFileSystem);				
				HSSFSheet worksheet=workbook.getSheet(sheetName);
				if(worksheet==null){
				 worksheet = workbook.createSheet(sheetName);
				}
				// index from 0,0... cell A1 is cell(0,0)
				HSSFRow row1 = worksheet.createRow(row);
					      
	        	for(int col=0;col<value.length;col++){
					HSSFCell cellA1 = row1.createCell(col);
					cellA1.setCellValue(value[col]);						
	        	}
	        	FileOutputStream fileOut = new FileOutputStream(this.fileName);
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			} catch (FileNotFoundException e) {
				WmLog.printMessage("ERROR in adding row XLs file ="+this.fileName+"Excel Sheet Index="+sheetName+" with Excel Row ="+row +" "+ e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				WmLog.printMessage("ERROR in adding row XLs file ="+this.fileName+"Excel Sheet Index="+sheetName+" with Excel Row ="+row +" "+ e.getMessage());
				e.printStackTrace();
			}
		
	
	}
	 
	 public void addHeaderRow(String sheetName, String[] columnNames) {
		 try {
				FileOutputStream fileOut = new FileOutputStream(this.fileName);
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet worksheet=workbook.getSheet(sheetName);
				HSSFCellStyle cellStyle =setHeaderStyle(workbook);
				if(worksheet==null){
				 worksheet = workbook.createSheet(sheetName);
				}
				// index from 0,0... cell A1 is cell(0,0)
				HSSFRow row1 = worksheet.createRow(0);
				    
	        	for(int col=0;col<columnNames.length;col++){
					HSSFCell cellA1 = row1.createCell(col);
					cellA1.setCellValue(columnNames[col]);	
					cellA1.setCellStyle(cellStyle);
	        	}		        
		        
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
			} catch (FileNotFoundException e) {
				WmLog.printMessage("ERROR in adding row XLs file ="+this.fileName+"Excel Sheet Index="+sheetName+" with Excel Cols ="+columnNames +" "+ e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				WmLog.printMessage("ERROR in adding row XLs file ="+this.fileName+"Excel Sheet Index="+sheetName+" with Excel Cols ="+columnNames +" "+ e.getMessage());
				e.printStackTrace();
			}
		
	
	}
	/**
	* This method is used to set the styles for all the headers
	* of the excel sheet.
	* @param sampleWorkBook - Name of the workbook.
	* @return cellStyle - Styles for the Header data of Excel sheet.
	*/
	private HSSFCellStyle setHeaderStyle(HSSFWorkbook workBook)
	{
			HSSFFont font = workBook.createFont();
			font.setFontName(HSSFFont.FONT_ARIAL);
			font.setColor(IndexedColors.PLUM.getIndex());
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			HSSFCellStyle cellStyle = workBook.createCellStyle();
			cellStyle.setFont(font);
			return cellStyle;
	}
	
	public static void main(String[] args)
		{
			String file="C:\\Users\\sjana\\git\\automation\\data\\global\\sample.csv";
			String sheet="TestSheet";
			ExcelSheet xl= new ExcelSheet(file);
			String[] cols={"Col1","Col2","Col3"};
			String[] values={"Srimanta","Kumar","Jana"};
			//xl.addHeaderRow(sheet,cols );
			//xl.addRow(sheet, 1,  values);
			//xl.modifyExcelCol(0, 1, 2, "Hana");
			//xl.readExcelFile();
			//xl.getXMLDataset();
			String val=xl.getXMLDatasetFromCSV();
			System.out.println(val);
		}
}



