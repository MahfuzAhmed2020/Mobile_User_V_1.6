package mobile.user.com;


import java.sql.*;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportContactsToExcel {

    static final String URL =
        "jdbc:mysql://localhost:3306/contact_app?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "";

    public static void main(String[] args) {

        String excelFile = "C:\\xampp\\htdocs\\contact-app\\contacts.xlsx";

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to MySQL
            Connection conn = DriverManager.getConnection(URL, USER, PASS);

            // SQL query
            String sql = "SELECT firstName, lastName, phone, email FROM contacts";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Create Excel workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Contacts");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("First Name");
            header.createCell(1).setCellValue("Last Name");
            header.createCell(2).setCellValue("Phone");
            header.createCell(3).setCellValue("Email");

            // Fill data
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getString("firstName"));
                row.createCell(1).setCellValue(rs.getString("lastName"));
                row.createCell(2).setCellValue(rs.getString("phone"));
                row.createCell(3).setCellValue(rs.getString("email"));
            }

            // Auto-size columns
            for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);

            // Write Excel file
            FileOutputStream fos = new FileOutputStream(excelFile);
            workbook.write(fos);
            fos.close();
            workbook.close();

            conn.close();

            System.out.println("Excel file created: " + excelFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
