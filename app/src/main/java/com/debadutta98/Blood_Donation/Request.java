package com.debadutta98.Blood_Donation;

public class Request {
    String end_at;
    String views;
    String blood_group;
    String Id;
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }


}
