package homerep.homerepoauth.models;

public class Client {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private Status status = Status.CLIENT;

    public Client(long l, String johnDoe) {
        this.id = l;
        this.firstName = johnDoe;
    }
}
