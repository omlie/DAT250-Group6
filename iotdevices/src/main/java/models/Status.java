package models;


public enum Status {
    Online(1),
    Offline(2),
    Available(3),
    Unavailable(4);

    private int status;

    Status(int statusCode) {
        this.status = statusCode;
    }


}
