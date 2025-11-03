package co.edu.unicauca.frontend.infra.config;

public final class PdfValidator {
    private PdfValidator() {}
    public static void assertPdf(String nombre, byte[] bytes) {
        if (nombre == null || !nombre.toLowerCase().endsWith(".pdf"))
            throw new IllegalArgumentException("Debe adjuntar un archivo .pdf");
        if (bytes == null || bytes.length == 0)
            throw new IllegalArgumentException("El PDF está vacío");
    }
}
