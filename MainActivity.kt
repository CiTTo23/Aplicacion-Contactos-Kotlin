/*
    DAVID MARTÍNEZ LÓPEZ - Aplicación de contactos basada en scaffold y mostrando cada contacto en una columna con cards para cada uno de ellos, permite añadir contactos a favoritos
 */


package com.example.appcontactos


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.appcontactos.ui.theme.APPContactosTheme

//funcion principal en la que llamamos al composable de AppContactos
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            APPContactosTheme {
                AppContactos()
            }
        }
    }
}

//clase contacto en la que definimos la información que tendrá cada contacto
data class Contacto(
    var id: Int,//útil para identificar contactos a la hora de recorrer la lista de contactos que tendremos
    var nombre: String,
    var numeroTelefono: String,
    var favorito: Boolean = false//por defecto los contactos no serán favoritos
)

//composable principal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContactos(){
    var contactos by remember { mutableStateOf(listOf<Contacto>()) }//lista que contiene los contactos que tenemos agregados
    var mostrarPopup by remember { mutableStateOf(false) }//variable que controla cuando se muestra el popup de añadir contacto
    var pantalla by remember { mutableStateOf("Contactos") }//variable que controla la pantalla en la que estamos, cambiará entre Favoritos y Contactos

    Scaffold(//contenedor principal scaffold que nos brinda un topappbarr, un bottomappbar y un botón flotante
        modifier = Modifier.background(Color(0xFF1A0030)),
        //barra de arriba
        topBar ={ TopAppBar(
            modifier = Modifier.padding(0.dp),//quitamos el paddign que tiene por defecto para que el título quede más centrado
            title = {
                Box(//metemos el titulo en un box para poder centrarlo
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pantalla,//el titulo de la app será la pantalla en la que estemos
                        color = Color(0xFFFFC0CB),
                        textAlign = TextAlign.Center
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2D0A4A)
            )

        ) },
        floatingActionButton ={
            FloatingActionButton(
                onClick = {mostrarPopup = true},//si se pulsa, entonces mostramos el popup (cambiando la variable que lo controla)
                containerColor = Color(0xFF2D0A4A)
                ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir contacto", tint = Color(0xFFFFC0CB))
            }
        },
        //barra de abajo, que contiene los botones para cambiar de pantalla de contactos a favoritos
        bottomBar ={
            BottomAppBar(
                modifier = Modifier.background(Color(0xFF2D0A4A)),
                containerColor = Color(0xFF2D0A4A)
            ){
                //fila que contiene los iconos que actuan de botones
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    IconButton(onClick = {pantalla = "Contactos"}) {//cuando se pulsa, cambiamos la pantalla a contactos
                        Icon(Icons.Default.Person, contentDescription = "Contactos", tint = Color(0xFFFFC0CB))
                    }
                    IconButton(onClick = {pantalla = "Favoritos"}) {//cuando se pulsa, cambiamos la pantalla a favoritos
                        Icon(Icons.Default.Star, contentDescription = "Favoritos", tint = Color(0xFFFFC0CB))
                    }
                }
            }
        }
    ){paddingValues ->
        //definimos el contenedor principal de la aplicacion en un box que ocupe todo el espacio disponible
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFF1A0030))){

            if(pantalla == "Contactos"){//si estamos en la pantalla de contactos
                ColumnaContactos(contactos) {
                    contacto ->//llamamos a la funcion de columnacontactos en general, pasándole la lista completa de contactos
                    contactos = actualizarFav(contactos, contacto)
                }

            }else if(pantalla == "Favoritos"){//si estamos en la pantalla de favoritos
                ColumnaContactos(contactos.filter{it.favorito}) {
                    contacto -> //llamamos a la funcion de columna contactos, pero pasándole la lista filtrada por favoritos
                    contactos = actualizarFav(contactos, contacto)
                }
            }

            if(mostrarPopup){//si mostarPopUp es true (es decir, si se pulsa el boton de +)
                PopUpAddContacto(
                    cerrar = {mostrarPopup = false},//definimos qué pasa al cerrar el dialog
                    agregar = { nombre, numero ->//le pasamos a la funcion agregar el nombre y numero recogidos en los textfield
                        val nuevoContacto = Contacto(
                            id = contactos.size+1,//id automático cada vez que se agrega un contacto
                            nombre = nombre,
                            numeroTelefono = numero
                        )
                        contactos = contactos + nuevoContacto//añadimos el contacto recibido a la lista de contactos
                        mostrarPopup = false//reseteamos variable que controla el popup, sino no para de verse
                    }
                )
            }
        }

    }
}

//Composable que define la forma de una card de un contacto
@Composable
fun CardContacto(contacto: Contacto, esFavorito: (Contacto) -> Unit){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),//bordes redondeados
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D0A4A))
    ){
        Row(//fila principal que hace que la card se vea horizontal
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(//icono de la card, por defecto será el icono de persona
                imageVector = Icons.Default.Person,
                contentDescription = "Icono de contacto",
                tint = Color(0xFFFFC0CB),
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))


            Column(//columna que contiene el nombre del contacto y el número de teléfono
                modifier = Modifier.weight(1f)//reparte el espacio de forma equitativa
            ){
                Text(//nombre del contacto
                    text = contacto.nombre,
                    color = Color(0xFFFFC0CB),
                    style = MaterialTheme.typography.bodyLarge//mas grande que el numero
                )
                Text(//numero de telefono del contacto
                    text = contacto.numeroTelefono,
                    color = Color(0xFFFFC0CB),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Switch(//switch que controla si se añade a favoritos el contacto
                checked = contacto.favorito,//estará marcado o desmarcado según si el contacto es favorito o no
                onCheckedChange = {esFavorito(contacto)},//cuando se pulsa, se cambia el favoritismo del contacto
                colors = SwitchDefaults.colors(//colores del switch
                    checkedThumbColor = Color(0xFFFFC107),
                    uncheckedThumbColor = Color(0xFFB39DDB),
                    checkedTrackColor = Color(0xFF7B1FA2),
                    uncheckedTrackColor = Color(0xFF9575CD)
                )
            )

        }
    }
}

//composable que define la columna en la que se muestran las cards de contactos
@Composable
//recibe la lista de contactos y una lambda que comprueba si el contacto es favorito
fun ColumnaContactos(contactos: List<Contacto>, esFavorito: (Contacto) -> Unit){
    //usamos un lazycolumn con un poco de padding pero que ocupe todo el contenedor disponible
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        items(contactos){//los items serán las cartas de contacto, pasando en todo momento el estado del switch que define si el contacto es favorito
            contacto -> CardContacto(contacto = contacto, esFavorito = esFavorito)//le pasamos cada card
            Spacer(modifier = Modifier.height(10.dp))//cada item con una pequeña separacion
        }
    }
}

//composable que contiene el popup que mostramos al pulsar el boton de añadir un contacto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//recibe dos lambdas, la que definirá cómo se cierra el popup y la que recibe el nombre y el numero de teléfono y los agrega como contacto a la lista de contactos
fun PopUpAddContacto(cerrar: () -> Unit, agregar: (String, String) -> Unit){
    var nombre by remember { mutableStateOf("")}//variable para recoger el nombre del contacto
    var numTelef by remember { mutableStateOf("")}//variable para recoger el numero de teléfono del contacto

    var numInvalido by remember { mutableStateOf(false)}//variable que valida un numero de teléfono

    Dialog(//popup principal en el que se le piden los datos al usuario
        onDismissRequest = cerrar//cuando se cierra
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),//redondeamos bordes del popup
            color = Color(0xFF1A0030)//le damos color
        ){
            Column(//columna principal en la que colocaremos los componentes
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ){
                Box(//para centrar el texto que hace de título, lo metemos en una caja para poder ponerle un alingnment
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Añadir contacto",
                        color = Color(0xFFFFC0CB), // Título en amarillo
                        style = MaterialTheme.typography.titleLarge//le damos estilo de título
                    )
                }

                //campo de texto para recoger el nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {nombre = it},//le damos el valor de nombre para que se vaya actualizando según se escriba
                    label = {Text("Nombre")},
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = Color(0xFFBB86FC)),//hacemos que el texto que se escribe salga en morado clarito
                    colors = TextFieldDefaults.outlinedTextFieldColors(//modificamos el estilo de los campos de texto cuando estén seleccionados
                        focusedBorderColor = Color(0xFF6A1B9A),//cuando está seleccionado
                        unfocusedBorderColor = Color(0xFFFFC0CB),//cuando no está seleccionado
                        focusedLabelColor = Color(0xFFFFC0CB), //Color de la etiqueta de nmbre
                        unfocusedLabelColor = Color(0xFFFFC0CB)//cuando no está seleccionado, el color de la etiqueta
                    )
                )


                Spacer(modifier = Modifier.height(16.dp))

                //campo de texto que recoge el número de teléfono, en él validamos la entrada de un número válido, es igual al de nombre pero añadiendo esta validación
                OutlinedTextField(
                    value = numTelef,
                    onValueChange = {
                        numTelef = it
                        numInvalido = !it.matches(Regex("^[0-9]{9}$"))//regex que valida el numero de teléfono, valida que sean 9 digitos del 0 al 9 cada uno, true si es inválido, false si es válido
                    },
                    label = {Text("Número de teléfono")},
                    modifier = Modifier.fillMaxWidth(),
                    isError = numInvalido,
                    textStyle = LocalTextStyle.current.copy(color = Color(0xFFBB86FC)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor =  Color(0xFF6A1B9A),
                        unfocusedBorderColor = Color(0xFFFFC0CB),
                        focusedLabelColor = Color(0xFFFFC0CB),
                        unfocusedLabelColor = Color(0xFFFFC0CB),
                        errorBorderColor = Color.Red//si el numero de telefono no es válido, contorno rojo
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                //fila que contiene los botones de cancelar o agregar al contacto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    //boton de cancelar
                    Button(
                        onClick = cerrar,//si se pulsa, llamamos a la funcion que cierra el dialog
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2D0A4A),
                            contentColor = Color(0xFFFFC0CB)
                        ),
                        modifier = Modifier.padding(end = 8.dp)//espacio entre los dos botones
                    ) {
                        Text("Cancelar")
                    }

                    //boton de añadir contacto
                    Button(
                        onClick = {
                            //si el numero introducido es válido, entonces agregamos el contacto
                            if (!numInvalido) {
                                agregar(nombre, numTelef)
                            }
                        },
                        enabled = nombre.isNotBlank() && numTelef.length==9 && !numInvalido,//solo se podrá pulsar si el nombre no está vacío y el numero de telefono es valido
                        colors = ButtonDefaults.buttonColors(//controlamos los colores que tiene el botón cuando esté marcado/desmarcado
                            containerColor = Color(0xFFFF4081),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFBDBDBD),
                            disabledContentColor = Color.White
                        )
                    ) {
                        Text("Agregar")
                    }
                }

            }
        }
    }
}

//funcion que recibe una lista de contactos y el contacto que se ha seleccionado y que devuelve una lista nueva con el contacto seleccionado con su valor de favoritos cambiado
fun actualizarFav(lista: List<Contacto>, contactoSeleccionado: Contacto): List<Contacto>{
    return lista.map{//funcion map que transforma los elementos de una lista aplicando la funcion que le especificamos
        contacto ->//recorremos los contactos
        if(contacto.id == contactoSeleccionado.id){//si se encuentra el contacto que se ha seleccionado (al que se le ha pulsado el switch) copiamos dicho contacto pero con su atributo de favoritos cambiado
            contacto.copy(favorito = !contacto.favorito)
        }else{
            contacto//si no es el contacto seleccionado, simplemente dejamos el contacto igual
        }
    }
}