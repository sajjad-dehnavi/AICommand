package dehnavi.sajjad.aicommand

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dehnavi.sajjad.aicommand.ui.theme.AICommandTheme
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : ComponentActivity() {

    private lateinit var audioRecord: AudioRecord

    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AICommandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        // Release the resources used by the AudioRecord object and the TensorFlow Lite interpreter
        audioRecord.release()
    }


    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startRecording()
            }
        }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        // Start recording audio from the microphone in a background thread
        isRecording = true
        Thread {
            val buffer = ShortArray(bufferSize)
            while (isRecording) {
                audioRecord.read(buffer, 0, bufferSize)
                val output =
                    ByteBuffer.allocateDirect(bufferSize * 2).order(ByteOrder.nativeOrder())
                output.asShortBuffer().put(buffer)
                
            }
        }.start()
    }

    private fun stopRecording() {
        // Stop recording audio from the microphone
        isRecording = false
        audioRecord.stop()
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

