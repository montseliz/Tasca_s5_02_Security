package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception;

public class EmailDuplicatedException extends RuntimeException {

    public EmailDuplicatedException(String message) {
        super(message);
    }

}