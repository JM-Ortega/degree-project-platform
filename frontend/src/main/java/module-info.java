module co.edu.unicauca.frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.unicauca.frontend to javafx.fxml;
    exports co.edu.unicauca.frontend;
}