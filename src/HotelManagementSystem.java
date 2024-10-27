import java.sql.*;
import java.util.Scanner;

public class HotelManagementSystem {

    // Method for new reservation.
    private static void newReservation(Connection con,Scanner scanner){
        int rowsAffected=0;
        System.out.println("Enter guest name:");
        String guestName=scanner.next();
        System.out.println("Enter the room number:");
        int roomNumber=scanner.nextInt();
        System.out.println("Enter the contact number:");
        String contactNumber=scanner.next();

        String query = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

        try(Statement stmt=con.createStatement()){
            rowsAffected=stmt.executeUpdate(query);
            if(rowsAffected>0) System.out.println("Reservation successfully.");
            else System.out.println("Reservation failed.");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // Method to view reservations.
    private static void viewReservation(Connection con,Scanner scanner){

        String query = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try(Statement stmt=con.createStatement()){
            ResultSet rs= stmt.executeQuery(query);
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (rs.next()) {
                int reservationId = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // Method to get the room number.
    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to update the reservations.
    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Method to delete the reservation.
    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }


    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }



    // Credentials to connect the mysql database with the java application.
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "user_name";
    private static final String password = "password";


    public static void main(String[] args) {
        // Loading the drivers.
        try {
            Class.forName("com.mysql.cj.jdbc.driver");
            System.out.println("Drivers loaded.");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // Making Connection.
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            //Scanner objection declaration.
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println();
                System.out.println("Welcome to HOTEL MANAGEMENT SYSTEM!");
                System.out.println();
                System.out.println("Press 1 for new reservation.");
                System.out.println("Press 2 to view reservation.");
                System.out.println("Press 3 to get room number.");
                System.out.println("Press 4 to update reservation.");
                System.out.println("Press 5 to delete reservation.");
                System.out.println("Press 0 to exit.");
                System.out.println();
                System.out.print("Enter your choice:");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        newReservation(con, scanner);
                        break;
                    case 2:
                        viewReservation(con, scanner);
                        break;
                    case 3:
                        getRoomNumber(con, scanner);
                        break;
                    case 4:
                        updateReservation(con,scanner);
                        break;
                    case 5:
                        deleteReservation(con,scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid input. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (InterruptedException e) {                   // This catch block is used to handle the exception that may develop from the exit() method.
            throw new RuntimeException(e);
        }
    }
}