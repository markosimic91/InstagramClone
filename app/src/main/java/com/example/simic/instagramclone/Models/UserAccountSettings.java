package com.example.simic.instagramclone.Models;

import com.example.simic.instagramclone.Utils.StringManipulation;

/**
 * Created by Simic on 21.3.2018..
 */

public class UserAccountSettings {

    private String description;
    private String display_name;
    private long followers;
    private long following;
    private long posts;
    private String profile_photo;
    private String user_name;
    private String web_site;
    private String user_id;


    public UserAccountSettings() {
    }

    public UserAccountSettings(String description, String display_name,
                               long followers, long following,
                               long posts, String profile_photo, String user_name, String web_site, String user_id) {
        this.description = description;
        this.display_name = display_name;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.profile_photo = profile_photo;
        this.user_name = user_name;
        this.web_site = web_site;
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getWeb_site() {
        return web_site;
    }

    public void setWeb_site(String web_site) {
        this.web_site = web_site;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", profile_photo='" + profile_photo + '\'' +
                ", user_name='" + user_name + '\'' +
                ", web_site='" + web_site + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
