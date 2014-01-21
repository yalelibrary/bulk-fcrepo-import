package edu.yale.library.engine;


import org.junit.Test;

import edu.yale.library.engine.excel.ExcelDataValidator;
import edu.yale.library.engine.excel.POIExcelReader;

import edu.yale.library.engine.excel.ReadMode;

public class POIExcelReaderTest
{

    @Test
    public void testValidaton()
    {
       POIExcelReader poiExcelReader = new POIExcelReader();
       poiExcelReader.read(ReadMode.HALT, new ExcelDataValidator());       
    }
}
