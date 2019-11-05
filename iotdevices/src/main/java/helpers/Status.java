package helpers;

public enum Status {
    ONLINE(0), OFFLINE(1);

    private int code;

    Status(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
