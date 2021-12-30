package e_cigs.system;

public class UserList {

    private String user_id, user_name,user_address;
    private  Boolean isVaper,isSmoker;



    public UserList(String user_id, String user_name, String user_address,Boolean isVaper,Boolean isSmoker) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_address = user_address;
        this.isVaper = isVaper;
        this.isSmoker = isSmoker;



    }

    public String getUser_id() {

        return user_id;
    }

    public String getUser_name() {

        return user_name;
    }

    public String getUser_address() {

        return user_address;
    }
    public Boolean getIsVaper(){
        return isVaper;
    }

    public Boolean getIsSmoker(){
        return isSmoker;
    }






}