package com.example.grabapp.view.dialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.databinding.DialogFaceCaptureBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceCaptureDialog : BaseDialogFragment<DialogFaceCaptureBinding>() {
    
    private var onFaceCaptured: ((Bitmap) -> Unit)? = null
    private var onDismiss: (() -> Unit)? = null
    
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    
    private val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.3f)
            .enableTracking()
            .build()
        FaceDetection.getClient(options)
    }
    
    private var isProcessing = false
    private var faceDetectedCount = 0
    private val requiredFaceDetections = 5
    
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFaceCaptureBinding {
        return DialogFaceCaptureBinding.inflate(inflater, container, false)
    }

    override fun width() = 0.95f

    override fun setUpInit() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        setupCamera()
        setupListeners()
    }
    
    private fun setupListeners() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }
    
    fun setOnFaceCapturedListener(listener: (Bitmap) -> Unit) {
        onFaceCaptured = listener
    }
    
    fun setOnDismissListener(listener: () -> Unit) {
        onDismiss = listener
    }
    
    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return
        
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
        
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    analyzeImage(imageProxy)
                }
            }
        
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        
        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun analyzeImage(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }
        
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        
        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty() && !isProcessing) {
                    faceDetectedCount++
                    binding.faceOverlay.visibility = View.VISIBLE
                    
                    if (faceDetectedCount >= requiredFaceDetections) {
                        captureImage()
                    }
                } else {
                    faceDetectedCount = 0
                    binding.faceOverlay.visibility = View.GONE
                }
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                imageProxy.close()
            }
    }
    
    private fun captureImage() {
        if (isProcessing) return
        
        val imageCapture = imageCapture ?: return
        isProcessing = true
        
        binding.progressBar.visibility = View.VISIBLE
        binding.tvInstruction.text = "Đang xử lý..."
        
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = imageProxyToBitmap(image)
                    image.close()
                    
                    if (bitmap != null) {
                        onFaceCaptured?.invoke(bitmap)
                        dismiss()
                    } else {
                        isProcessing = false
                        binding.progressBar.visibility = View.GONE
                        binding.tvInstruction.text = "Lỗi khi chụp ảnh. Vui lòng thử lại."
                    }
                }
                
                override fun onError(exception: ImageCaptureException) {
                    isProcessing = false
                    binding.progressBar.visibility = View.GONE
                    binding.tvInstruction.text = "Lỗi: ${exception.message}"
                }
            }
        )
    }
    
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        return try {
            val mediaImage = imageProxy.image ?: return null
            
            // ImageCapture trả về JPEG format trong plane[0]
            val buffer = mediaImage.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            
            // Decode JPEG bytes thành Bitmap
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: return null
            
            // Xoay và mirror cho camera trước
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val matrix = Matrix().apply {
                if (rotationDegrees != 0) {
                    postRotate(rotationDegrees.toFloat())
                }
                // Mirror cho camera trước (flip horizontal)
                postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            }
            
            val transformedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
            
            // Giải phóng bitmap gốc
            bitmap.recycle()
            
            transformedBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        faceDetector.close()
        cameraProvider?.unbindAll()
    }
    
    override fun dismiss() {
        onDismiss?.invoke()
        super.dismiss()
    }
    
    fun showDialog(manager: FragmentManager) {
        if (isAdded || isVisible || isRemoving || isStateSaved) {
            return
        }
        show(manager, "FaceCaptureDialog")
    }
}
