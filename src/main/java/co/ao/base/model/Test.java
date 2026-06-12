import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Test {
    public static void main(String[] args) {
        String createdAt = "12/06/2026 15:47:27";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime dt = LocalDateTime.parse(createdAt, formatter);
            System.out.println("Success: " + dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
