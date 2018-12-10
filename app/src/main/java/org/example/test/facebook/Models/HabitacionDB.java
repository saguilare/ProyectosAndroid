package org.example.test.facebook.Models;


/**
 * Represents an item in a ToDo list
 */
public class HabitacionDB {

    @com.google.gson.annotations.SerializedName("id")
    private String id;

    @com.google.gson.annotations.SerializedName("complete")
    private boolean complete;

    @com.google.gson.annotations.SerializedName("deleted")
    private boolean deleted;

    @com.google.gson.annotations.SerializedName("descripcion")
    private String descripcion;

    @com.google.gson.annotations.SerializedName("codigo")
    private String codigo;

    @com.google.gson.annotations.SerializedName("imagen")
    private String imagen;

    @com.google.gson.annotations.SerializedName("casa")
    private String casa;




    /**
     * ToDoItem constructor
     */
    public HabitacionDB() {

    }

    public HabitacionDB(String codigo, String casa, String descripcion, String imagen) {
        this.setCodigo(codigo);
        this.setCasa(casa);
        this.setDescripcion(descripcion);
        this.setImagen(imagen);

    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HabitacionDB && ((HabitacionDB) o).id == id;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Item Name
     */
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Item Casa
     */
    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    /**
     * Item Codigo
     */
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Item Imagen
     */
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }



}