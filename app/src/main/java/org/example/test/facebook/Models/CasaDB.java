package org.example.test.facebook.Models;


/**
 * Represents an item in a ToDo list
 */
public class CasaDB {

    @com.google.gson.annotations.SerializedName("id")
    private String id;

    @com.google.gson.annotations.SerializedName("complete")
    private boolean complete;

    @com.google.gson.annotations.SerializedName("descripcion")
    private String descripcion;

    @com.google.gson.annotations.SerializedName("codigo")
    private String codigo;

    @com.google.gson.annotations.SerializedName("usuario")
    private String usuario;




    /**
     * ToDoItem constructor
     */
    public CasaDB() {

    }

    public CasaDB(String codigo, String usuario, String descripcion) {
        this.setCodigo(codigo);
        this.setUsuario(usuario);
        this.setDescripcion(descripcion);

    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CasaDB && ((CasaDB) o).id == id;
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
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
     * Item Codigo
     */
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }



}