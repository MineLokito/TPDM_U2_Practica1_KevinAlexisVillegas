package mx.edu.ittepic.tpdm_u2_practica1_kevinalexisvillegas

import android.content.Intent
import android.database.SQLException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    var descripcionLista: EditText ?= null
    var fechacreacionLista : EditText ?= null
    var insertarLista : Button ?= null
    var mostrarTodasListas : Button ?= null
    var abrirTareas : Button ?= null
    var mostrarTodasLi : TextView ?= null
    var bdLista = BaseDatos(this,"practica1",null,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        descripcionLista = findViewById(R.id.descripcionLista)
        fechacreacionLista = findViewById(R.id.fechacreacionLista)
        insertarLista = findViewById(R.id.insertarLista)
        mostrarTodasListas = findViewById(R.id.mostrarTodasListas)
        abrirTareas = findViewById(R.id.abrirTareas)
        mostrarTodasLi = findViewById(R.id.mostrarTodasLi)
        mostrar()

        abrirTareas?.setOnClickListener(){
            val ventanaTarea = Intent(this,Main2Activity::class.java)
            startActivity(ventanaTarea)
        }
        insertarLista?.setOnClickListener(){
            insertarListas()
        }
        mostrarTodasListas?.setOnClickListener(){
            mostrar()
        }
    }

    fun mensaje(a: String, b: String){
        AlertDialog.Builder(this)
            .setTitle(a)
            .setMessage(b)
            .setPositiveButton("OK")
            { dialogInterface, i ->}.show()
    }

    fun limpiarCampos(){
        descripcionLista?.setText("")
        fechacreacionLista?.setText("")
    }

    fun validaCampos(): Boolean{
        if(descripcionLista?.text!!.toString().isEmpty()||fechacreacionLista?.text!!.isEmpty()){
            return false
        }else{
            return true
        }
    }

    fun insertarListas(){
        try {
            var trans = bdLista.writableDatabase
            var SQL = "INSERT INTO LISTA VALUES(NULL,'DESC','FECHACREA')"
            if (validaCampos() == false) {
                mensaje("Error!", "Existe algun campo vacio (\"Descripción\" y/o \"Fecha de creación\")")
                return
            }

            SQL = SQL.replace("DESC", descripcionLista?.text.toString())
            SQL = SQL.replace("FECHACREA", fechacreacionLista?.text.toString())
            trans.execSQL(SQL)
            trans.close()
            mensaje("Registro exitoso!", "Se inserto correctamente")
            limpiarCampos()
        }
        catch (er: SQLException) {
            mensaje("Error!","No se pudo insertar el registro, verifique sus datos!")
        }
    }

    fun mostrar(){
        var sel = ""
        try {
            var transicion = bdLista.readableDatabase
            var con = "SELECT * FROM LISTA"
            var cur = transicion.rawQuery(con,null)
            if(cur != null) {
                if (cur.moveToFirst() == true) {
                    do{
                        sel +="ID: ${cur.getString(0)}\nDescripción: ${cur.getString(1)}\nFecha de creación: ${cur.getString(2)}\n"+
                                "______________________________________________\n"
                    }while (cur.moveToNext())
                    mostrarTodasLi?.setText(sel)
                }else{
                    mensaje("Advertencia!","No existen listas")
                }
            }
            cur.close()
        }
        catch (er: SQLException){
            mensaje("Error!","No se encuentran registros en la BD")
        }
    }

}