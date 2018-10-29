package utl;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    /**
     * 读取excel文件，excel中不含合并单元格
     * @param path
     * @param sheetName
     * excel表格要注意每行的最后一格不能为空
     * 如果要为空，则需要手动随便填写字符，然后再按del键删除才可以
     * 目前没找到这个解决办法，后续会继续查找解决办法
     * @return
     */
    public static String[][] getExpectationData(String path, String sheetName) {
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            POIFSFileSystem POIStream = new POIFSFileSystem(fis);
            HSSFWorkbook workBook = new HSSFWorkbook(POIStream);
            //得到工作表
            HSSFSheet sheet1 = workBook.getSheet(sheetName);
            //得到总行数
            int rowNum = sheet1.getLastRowNum();
            List<String[]> results = new ArrayList<String[]>();
            for (int i=1;i<=rowNum;i++){
                //当前行
                HSSFRow row = sheet1.getRow(i);
                int colNum = row.getLastCellNum();
                String[] data = new String[colNum];
                //当前行所有列
                for (int j = 0; j < colNum; j++) {
                    try {
                        data[j] = getCellValue(row.getCell(j));
                    }catch (NullPointerException e){ //如果单元格为空的时候，则用这个来处理
                        data[j] = "";
                    }
                }
                //把data[]数组的数据存在list<[]>中
                results.add(data);
            }
            fis.close();

            String[][] returnArray = new String[results.size()][rowNum];
            for (int i = 0; i < returnArray.length; i++) {
                returnArray[i] = (String[]) results.get(i);
            }
            return returnArray;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 对Excel的各个单元格的格式进行判断并转换
     */
    public static String getCellValue(HSSFCell cell) {
        String cellValue = "";
        DecimalFormat df = new DecimalFormat("#");
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                cellValue =cell.getRichStringCellValue().getString().trim();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                cellValue =df.format(cell.getNumericCellValue()).toString();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                cellValue =cell.getCellFormula();
                break;
            default:
                cellValue = "";
        }
        return cellValue;
    }

}
