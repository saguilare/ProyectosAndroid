package org.example.test.facebook.Models;


import java.util.jar.Attributes;

/**
 * Represents an item in a ToDo list
 */
public class UsuarioDB {

    @com.google.gson.annotations.SerializedName("id")
    private String id;

    @com.google.gson.annotations.SerializedName("complete")
    private boolean complete;

    @com.google.gson.annotations.SerializedName("name")
    private String name;

    @com.google.gson.annotations.SerializedName("usuario")
    private String usuario;

    @com.google.gson.annotations.SerializedName("email")
    private String email;




    /**
     * ToDoItem constructor
     */
    public UsuarioDB() {

    }

    public UsuarioDB(String name, String usuario, String email) {
        this.setName(name);
        this.setUsuario(usuario);
        this.setEmail(email);

    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UsuarioDB && ((UsuarioDB) o).id == id;
    }

    /**
     * Item Id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Marks the item as completed or incompleted
     */

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    /**
     * Indicates if the item is marked as completed
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Item Name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Item Usuario
     */
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Item Email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}