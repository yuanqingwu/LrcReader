package com.wyq.lrcreader.model.netmodel.gecimemodel;

/**
 * Created by Uni.W on 2016/8/19.
 */
public class Artist {
    private String profile;
    private String name;
    private String area;
    private String alias;
    private String birthday;
    private String constellation;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "profile='" + profile + '\'' +
                ", name='" + name + '\'' +
                ", area='" + area + '\'' +
                ", alias='" + alias + '\'' +
                ", birthday='" + birthday + '\'' +
                ", constellation='" + constellation + '\'' +
                '}';
    }
}
