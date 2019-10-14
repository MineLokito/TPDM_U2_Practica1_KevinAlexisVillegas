package mx.edu.ittepic.tpdm_u2_practica1_kevinalexisvillegas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.sql.SQLException

class Main2Activity : AppCompatActivity() {
    var descripcionTa : EditText ?= null
    var fecharealizado : EditText ?= null
    var idlista : EditText ?= null
    var mostrarListaTarea : TextView ?= null
    var mostrarTodasListas : TextView ?= null
    var insertarLista : Button ?= null
    var mostrarTareas : Button ?= null
    var eliminarTareas : Button ?= null
    var regresar : Button ?= null
    var mostrarTodasTa : TextView ?= null
    var mostrarListas : TextView ?= null

    var bdLista = BaseDatos(this,"practica1",null,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        descripcionTa = findViewById(R.id.descripcionTa)
        fecharealizado = findViewById(R.id.fecharealizado)
        idlista = findViewById(R.id.idlista)
        mostrarListaTarea = findViewById(R.id.listaTareas)
        mostrarTodasListas = findViewById(R.id.mostrarTodasListasEnTar)
        insertarLista = findViewById(R.id.insertarLista)
        mostrarTareas = findViewById(R.id.mostrarTareas)
        eliminarTareas = findViewById(R.id.eliminarTareas)
        regresar = findViewById(R.id.regresar)
        mostrarTodasTa = findViewById(R.id.mostrarTodasTa)
        mostrarListas = findViewById(R.id.mostrarListasEnTareas)
        mostrarTodosList()

        regresar?.setOnClickListener(){
            finish()
        }
        insertarLista?.setOnClickListener(){
            insertarTareas(idlista?.text.toString())
        }
        mostrarListas?.setOnClickListener(){
            mostrarTodosList()
        }
        mostrarTareas?.setOnClickListener(){
            mostrarTodas()
        }
        eliminarTareas?.setOnClickListener(){
            pedirID(eliminarTareas?.text.toString())
        }
    }

    fun mensaje(t: String, m: String){
        AlertDialog.Builder(this)
            .setTitle(t)
            .setMessage(m)
            .setPositiveButton("OK")
            { dialogInterface, i ->}.show()
    }

    fun limpiarCampos(){
        descripcionTa?.setText("")
        fecharealizado?.setText("")
        idlista?.setText("")
    }

    fun validaCampos(): Boolean{
        if(descripcionTa?.text!!.toString().isEmpty()||fecharealizado?.text!!.toString().isEmpty()||idlista?.text!!.toString().isEmpty()){
            return false
        }else{
            return true
        }
    }

    fun insertarTareas(idtarea: String) {
        try {
            var trans = bdLista.writableDatabase
            var con = "INSERT INTO TAREAS VALUES(NULL,'DESCRIPCION','REALIZADO',IDLISTA)"

            if (validaCampos() == false) {
                mensaje(
                    "Error!","Existe algun campo vacio ó equivocado (\"Descripción\" y/o \"Fecha realizado\" y/o \"ID lista\")"
                )
                return
            }

            con = con.replace("DESCRIPCION", descripcionTa?.text.toString())
            con = con.replace("REALIZADO", fecharealizado?.text.toString())
            con = con.replace("IDLISTA",idtarea)
            trans.execSQL(con)
            trans.close()
            mensaje("Registro exitoso!", "Se inserto correctamente")
            limpiarCampos()
        } catch (er: SQLException) {
            mensaje("Error!", "No se pudo insertar el registro, datos incorrectos y/o \"ID Lista\" no existe")
        }
    }

    fun mostrarTodosList(){
        var sel = ""
        try {
            var trans = bdLista.readableDatabase
            var con = "SELECT * FROM LISTA"
            var cur = trans.rawQuery(con,null)
            var encEnLiTa = "ID     Descrcipción"
            mostrarListaTarea?.setText(encEnLiTa)
            if(cur != null){
                if(cur.moveToFirst()==true){
                    do{
                        sel += "  ${cur.getString(0)}     ${cur.getString(1)}\n"+
                                "_______________________________________________________\n"
                    }while (cur.moveToNext())
                    mostrarTodasListas?.setText(sel)
                }else{
                    mensaje("Advertencia","No existen listas")
                }
            }
        }catch (er: SQLException){
            mensaje("Error!","No se encuentran registros en la BD")
        }
    }

    fun mostrarTodas(){
        var sel = ""
        try {
            var trans = bdLista.readableDatabase
            var con = "SELECT * FROM TAREAS"
            var cur = trans.rawQuery(con,null)
            if(cur != null) {
                if (cur.moveToFirst() == true) {
                    do{
                        sel +="ID: ${cur.getString(0)}\nDescripción: ${cur.getString(1)}\nFecha de creación: ${cur.getString(2)}\nID Lista: ${cur.getString(3)}\n"+
                                "______________________________________________\n"
                    }while (cur.moveToNext())
                    mostrarTodasTa?.setText(sel)
                }else{
                    mensaje("Advertencia!","No existen tareas")
                }
            }
            cur.close()
        }
        catch (er: android.database.SQLException){
            mensaje("Error!","No se encuentran registros en la BD")
        }
    }

    fun pedirID(etiqueta:String){
        var elemento = EditText(this)
        elemento.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this).setTitle("Atención!").setMessage("Escriba el ID en ${etiqueta}: ").setView(elemento)
            .setPositiveButton("OK"){dialog,which ->
                if(validarCampo(elemento) == false){
                    Toast.makeText(this@Main2Activity, "Error! campo vacío", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(elemento.text.toString(),etiqueta)

            }.setNeutralButton("Cancelar"){dialog, which ->  }.show()
    }

    fun validarCampo(elemento: EditText): Boolean{
        if(elemento.text.toString().isEmpty()){
            return false
        }else{
            return true
        }
    }

    fun buscar(id: String, btnEtiqueta: String){
        try {
            var trans = bdLista.readableDatabase
            var con="SELECT * FROM TAREAS WHERE IDTAREA="+id
            var  cur = trans.rawQuery(con,null)

            if (cur.moveToFirst()==true){
                if (btnEtiqueta.startsWith("e")){
                    var sel = "¿Seguro de eliminar la tarea: \"${cur.getString(1)}\" con el ID \"${cur.getString(0)}\" ?"
                    var alerta = AlertDialog.Builder(this)
                    alerta.setTitle("Atención").setMessage(sel).setNeutralButton("NO"){dialog,which->
                        return@setNeutralButton
                    }.setPositiveButton("si"){dialog,which->
                        eliminar(id)
                        mostrarTodas()
                    }.show()
                }
            }else{
                mensaje("Error!","No existe el id: ${id}")
            }
        }catch (err: SQLException){
            mensaje("Error!","No se encontro el registro")
        }
    }

    fun eliminar(id:String){
        try{
            var trans = bdLista.writableDatabase
            var SQL = "DELETE FROM TAREAS WHERE IDTAREA="+id
            trans.execSQL(SQL)
            trans.close()
            mensaje("Exito!", "Se elimino correctamente el id: ${id}")
        }catch (err: SQLException){
            mensaje("Error!", "No se pudo eliminar")

        }
    }
}
