/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desktopscheduler.model;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
/**
 *
 * @author Dylan
 */
public class DBDriver {
    private static final String URL = "jdbc:mysql://52.206.157.109/U05AXu";
    private static final String USER = "U05AXu";
    private static final String PASS = "53688446515";
    private static int currentUserId = -1;
    
    public static int authenticate(String userName, String password){
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT password, userId FROM user WHERE userName = '" +userName+ "'");
            rs.first();
            if(rs.getString(1).equals(password)){
                currentUserId = Integer.parseInt(rs.getString("userId"));
                return currentUserId;
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return -1;
    }
    
    //Returns the addressId that is generated by the database
    public static int insertAddress(String address, String address2, int cityId, String postal, String phone){
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            String query = String.format("INSERT IGNORE INTO address(address, address2, cityId, postalCode, phone) "
                    + "VALUES("
                    + "'%s', '%s', '%x', '%s', '%s')", address, address2, cityId, postal, phone);
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int newId = rs.getInt(1);
            return newId;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return -1;
    }
    
    public static boolean insertCustomer(String name, int addressId){
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            String query = "INSERT IGNORE INTO customer(customerName, addressId) VALUES('"+name+"', " +"'"+Integer.toString(addressId)+"'"+")";
            stmt.executeUpdate(query);
            return true;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return false;
    }
    
    public static boolean insertAppointment(int customerId, String title, String description, String location, String contact, String type, String url, String start, String end){
        //Convert local time to UTC before submitting to database
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime st = LocalDateTime.parse(start, f);
        LocalDateTime en = LocalDateTime.parse(end, f);
        ZonedDateTime stLocal = st.atZone(ZoneId.systemDefault());
        ZonedDateTime endLocal = en.atZone(ZoneId.systemDefault());
        ZonedDateTime stUtc = stLocal.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUtc = endLocal.withZoneSameInstant(ZoneId.of("UTC"));
        st = stUtc.toLocalDateTime();
        en = endUtc.toLocalDateTime();
        
                
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            String query = "INSERT IGNORE INTO appointment(customerId, userId, title, description, location, contact, type, url, start, end) "
                    + "VALUES('"+Integer.toString(customerId)+"', '" +Integer.toString(currentUserId)+ "', '" +title+ "', '" +description+ "', '"
                    +location+ "', '" +contact+ "', '" +type+ "', '" +url+ "', '" +f.format(st)+ "', '" +f.format(en)+ "')";
            stmt.executeUpdate(query);
            return true;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return false;
    }
    
    public static boolean updateAddress(int addressId, String address, String address2, int cityId, String postal, String phone){
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE address SET address='" +address+ "', "
                    + "address2='" +address2+ "', "+ "cityId=" +Integer.toString(cityId)+ ", "
                    + "postalCode='" +postal+ "', "+ "phone='" +phone+ "'"
                    + "WHERE addressId=" +Integer.toString(addressId));
            return true;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return false;
    }
    
    public static boolean updateCustomer(int customerId, String name, int addressId){
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE customer SET customerName='" +name+ "' WHERE customerId='" +customerId+ "'");
            return true;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return false;
    }
    
    public static boolean updateAppointment(int appointmentId, int customerId, String title, 
            String description, String location, String contact, String type, String url, String start, String end){
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime st = LocalDateTime.parse(start, f);
        LocalDateTime en = LocalDateTime.parse(end, f);
        ZonedDateTime stLocal = st.atZone(ZoneId.systemDefault());
        ZonedDateTime endLocal = en.atZone(ZoneId.systemDefault());
        ZonedDateTime stUtc = stLocal.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUtc = endLocal.withZoneSameInstant(ZoneId.of("UTC"));
        st = stUtc.toLocalDateTime();
        en = endUtc.toLocalDateTime();
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            String query = "UPDATE appointment SET customerId='" +customerId+ "', title='" +title+ "', description='" +description+ "', "
                    + "location='" +location+ "', contact='" +contact+ "', type='" +type+ "', start='" +f.format(st)+ "', end='" +f.format(en)+ "' "
                    + "WHERE appointmentId='" +appointmentId+ "'";
            stmt.executeUpdate(query);
            return true;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return false;
    }
    
    public static boolean removeCustomer(int customerId){
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM customer WHERE customerId='" +customerId+ "'");
            return true;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return false;
    }
    
    public static boolean removeAppointment(int apptId){
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM appointment WHERE appointmentId='" +apptId+ "'");
            return true;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        
        return false;
    }
    
    public static ArrayList<Customer> getCustomerList(){
        ArrayList<Customer> customers = new ArrayList();
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customer");
            
            while(rs.next()){
                int customerId = Integer.parseInt(rs.getString("customerId"));
                String customerName = rs.getString("customerName");
                int addressId = Integer.parseInt(rs.getString("addressId"));
                Customer c = new Customer(customerId, customerName, addressId);
                customers.add(c);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return customers;
    }
    
    public static Address getAddress(int addressId){
        Address toReturn = new Address();
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT addressId, address, address2, cityId,"
                    + "postalCode, phone "
                    + "FROM address "
                    + "WHERE addressId='" +addressId+ "'");
            while(rs.next()){
                int addId = Integer.parseInt(rs.getString("addressId"));
                String address = rs.getString("address");
                String address2 = rs.getString("address2");
                int cityId = Integer.parseInt(rs.getString("cityId"));
                String postal = rs.getString("postalCode");
                String phone = rs.getString("phone");
                toReturn = new Address(addId, address, address2, cityId, postal, phone);
            }
            
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return toReturn;
    }
    
    public static ArrayList<Appointment> getAppointmentList(LocalDate day){
        ArrayList<Appointment> appointments = new ArrayList();
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM appointment WHERE DATE(start) ='" +day.toString()+ "'");
            
            while(rs.next()){
                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDateTime st = LocalDateTime.parse(rs.getString("start"), f);
                LocalDateTime en = LocalDateTime.parse(rs.getString("end"), f);
                ZonedDateTime stUtc = st.atZone(ZoneId.of("UTC"));
                ZonedDateTime endUtc = en.atZone(ZoneId.of("UTC"));
                ZonedDateTime stLocal = stUtc.withZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime endLocal = endUtc.withZoneSameInstant(ZoneId.systemDefault());
                st = stLocal.toLocalDateTime();
                en = endLocal.toLocalDateTime();
                
                
                int apptId = Integer.parseInt(rs.getString("appointmentId"));
                int customerId = Integer.parseInt(rs.getString("customerId"));
                int userId = Integer.parseInt(rs.getString("userId"));
                String title = rs.getString("title");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String type = rs.getString("type");
                String url = rs.getString("url");
                String start = f.format(st);
                String end = f.format(en);
                
                Appointment appt = new Appointment(apptId, customerId, userId, title, description, location, contact, type, url, start, end);
                appointments.add(appt);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        
        return appointments;
    }
    
    public static ArrayList<TypeReport> getAppointmentTypes(){
        ArrayList<TypeReport> toReturn = new ArrayList();
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            String query = "select date_format(start, '%M') as 'Month', count(distinct type) as 'Types' from appointment\n" +
                    "group by Month(start);";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                TypeReport temp = new TypeReport(rs.getString("Month"), rs.getString("Types"));
                toReturn.add(temp);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return toReturn;
    }
    
    public static ArrayList<Appointment> getConsultantSchedule(){
        ArrayList<Appointment> toReturn = new ArrayList();
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            String query = "select userId, title, description, start, end from appointment\n" +
                    "order by userId, start;";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Appointment temp = new Appointment();
                temp.setUserID(Integer.parseInt(rs.getString("userId")));
                temp.setTitle(rs.getString("title"));
                temp.setDescription(rs.getString("description"));
                temp.setStart(rs.getString("start"));
                temp.setEnd(rs.getString("end"));
                toReturn.add(temp);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return toReturn;
    }
    
    public static ArrayList<CityReport> getCityReport(){
        ArrayList<CityReport> toReturn = new ArrayList();
        try(Connection conn = DriverManager.getConnection(URL, USER, PASS)){
            Statement stmt = conn.createStatement();
            String query = "select city.city as 'City', count(*) as 'Appointments'\n" +
                    "from (((city\n" +
                    "	inner join address on address.cityId = city.cityId)\n" +
                    "		inner join customer on customer.addressId = address.addressId)\n" +
                    "			inner join appointment on appointment.customerId = customer.customerId)\n" +
                    "group by city.city\n" +
                    "order by 'Appointments';";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                CityReport temp = new CityReport(rs.getString("City"), rs.getString("Appointments"));
                toReturn.add(temp);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return toReturn;
    }
    
    public static int getCurrentUserId(){
        return currentUserId;
    }
    
    public static void setCurrentUserId(int newId){
        currentUserId = newId;
    }
    
}
