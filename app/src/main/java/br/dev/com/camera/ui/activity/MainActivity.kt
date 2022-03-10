package br.dev.com.camera.ui.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import br.dev.com.camera.BuildConfig
import br.dev.com.camera.R
import br.dev.com.camera.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    /* Definição das Variaveis */

    // Variável de databinding
    private lateinit var actMian: ActivityMainBinding

    // Variável de ImageView por Lazy
    private val previewImage by lazy { actMian.imageView }

    // Variável de Uri que pode ser nula
    private var latestTmpUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializando o nosso conteúdo.
        actMian = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //disparo de evento com DataBiding
        actMian.btCamera.setOnClickListener { takeImage() }

    }

    //Resultado da foto da câmera
    private val takeImageResult = registerForActivityResult(TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                previewImage.setImageURI(uri)
            }
        }
    }

    //Funcao para tirar Foto
    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    //Funcao para Criar Foto
    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

}