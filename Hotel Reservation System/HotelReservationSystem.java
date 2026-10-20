import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;
public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "Yashswi@9660793037";

    public static void main(String[] args) throws Exception {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner in = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choice = in.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection, in);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, in);
                        break;
                    case 4:
                        updateReservation(connection, in);
                        break;
                    case 5:
                        deleteReservation(connection, in);
                        break;
                    case 0:
                        exit();
                        in.close();
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection, Scanner in){
        try{
            System.out.print("Enter guest name: ");
            String guestName = in.next();
            in.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = in.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = in.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number)" +
                                "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "');";

            try(Statement statement = connection.createStatement()){
                int affectRows = statement.executeUpdate(sql);

                if(affectRows > 0){
                    System.out.println("Reservation successfully!");
                }
                else{
                    System.out.println("Reservation failed!");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection) throws Exception{
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            System.out.println("Current Reservations: ");
            System.out.println("+-------------------+----------------+-------------------+---------------------+-----------------------+");
            System.out.println("| Reservation ID    | Guest          | Room Number       | Contact Number      | Reservation Date       |");
            System.out.println("+-------------------+----------------+-------------------+---------------------+-----------------------+");

            while(resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getString("reservation_date").toString();

                //Format and display the reservation data in a table-like format
                System.out.printf("| %-16d | %-15s | %-12d | %-20s | %-20s |%n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);

            }
            System.out.println("+---------------------+--------------------+----------------------+------------------------+-------------------------+");
        }
    }

    public static void getRoomNumber(Connection connection, Scanner in){
        try{
            System.out.print("Enter reservation ID: ");
            int reservationId = in.nextInt();
            in.nextLine();
            System.out.print("Enter guest name: ");
            String guestName = in.nextLine();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId
                            + " and Guest " + guestName + " is: " + roomNumber);
                }else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void updateReservation(Connection connection, Scanner in){
        try{
            System.out.print("Enter reservation ID to update: ");
            int reservationId = in.nextInt();
            in.nextLine();

            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID: ");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = in.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = in.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = in.next();


            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement()){
                int affectRooms = statement.executeUpdate(sql);

                if(affectRooms > 0){
                    System.out.println("Reservation updated successfully!");
                }else{
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner in){
        try{
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = in.nextInt();

            if (!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given Id.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement()){
                int affectRooms = statement.executeUpdate(sql);

                if(affectRooms > 0){
                    System.out.println("Reservation deleted successfully!");
                }else{
                    System.out.println("Reservation deleted failed.");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean reservationExists(Connection connection, int reservationId){
        try{
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){
                return resultSet.next();
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;  // Handle database errors as needed
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while(i != 0){
            System.out.print(".");
            Thread.sleep((450));
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}