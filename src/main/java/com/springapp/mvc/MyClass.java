package com.springapp.mvc;


public class MyClass {
    private String name;
    private String login;
    private int id;

    public MyClass(String name, String login, int id) {
        this.name = name;
        this.login = login;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
