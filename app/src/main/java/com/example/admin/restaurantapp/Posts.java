package com.example.admin.restaurantapp;

/**
 * Created by Admin on 6.11.2017 г..
 */

public class Posts {
   /*
   setting some variables for the title,description,image and username
    */
    private String title;
    private String description;
    private String image;

    private String username;

    public Posts(){

    }

    public Posts(String title, String desc, String image) {
        this.title = title;
        this.description = desc;
        this.image = image;
        this.username = username;
    }
    /*
    displaying the title
     */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    /*
    displaying the description
     */
    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }
    /*
    displaying the image
     */
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    /*
    displaying the username
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
