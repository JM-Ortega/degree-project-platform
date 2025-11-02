package co.edu.unicauca.departmentheadservice.presentation;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
