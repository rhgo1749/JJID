package com.example.myroom.Items;

public class StuData {

    public StuData(){}

    public StuData(String d_stu_num, String d_name, String d_password, String d_email, String d_phone_num, int d_warn)
    {
            stu_num = d_stu_num;
            name = d_name;
            password = d_password;
            email = d_email;
            phone_num = d_phone_num;
            warn = d_warn;
    }
    public String stu_num;
    public String name;
    public String password;
    public String email;
    public String phone_num;
    public int warn;

    // get method
    public String getStu_num() { return stu_num; }
    public String getName() { return name; }
    public String getpassword() { return password; }
    public String getemail() { return email; }
    public String getphone_num() { return phone_num; }
    public int getwarn() { return warn; }

    // set method
    public void setStu_num(String d_stu_num) { this.stu_num = d_stu_num; };
    public void setName(String d_name) { this.name = d_name; };
    public void setPassword(String d_password) { this.password = d_password; };
    public void setEmail(String d_email) { this.email = d_email; };
    public void setPhone_num(String d_phone_num) { this.phone_num= d_phone_num; };
    public void setWarn(int d_warn) { this.warn = d_warn; };

}
