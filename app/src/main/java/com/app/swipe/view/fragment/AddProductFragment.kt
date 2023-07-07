package com.app.swipe.view.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.app.swipe.R
import com.app.swipe.databinding.FragmentAddProductBinding
import com.app.swipe.model.SuccessResponse
import com.app.swipe.utils.network.NetworkResult
import com.app.swipe.viewmodel.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private val dashboardViewModelNew: DashboardViewModel by activityViewModels()
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageFile: File? = null
    private val storagePermissionCode = 1001
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>
    private var selectedProductType = ""
    private var productTypes = emptyList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //---
        productTypes =
            listOf("Select Product Type", "Laptops", "Dry Fruits", "Mobiles", "Grocery", "Cloths")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, productTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductType.adapter = adapter
        //----
        binding.spinnerProductType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedProductType = productTypes[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        //--
        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    pickImage()
                }
            }
        //---
        binding.uploadImage.setOnClickListener {
            if (!isStoragePermissionGranted()) {
                requestStoragePermission()
            } else {
                pickImage()
            }
        }
        binding.addButton.setOnClickListener {
            if (binding.productName.text!!.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedProductType.isEmpty() || selectedProductType == productTypes[0]) {
                Toast.makeText(requireContext(), "Please select product type", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (binding.productPrice.text!!.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter product price", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (binding.productTax.text!!.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter product tax", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (selectedProductType.isNullOrEmpty()) {
                dashboardViewModelNew.addProduct(
                    files = null,
                    productName = binding.productName.text.toString(),
                    productType = selectedProductType,
                    price = binding.productPrice.text.toString(),
                    tax = binding.productTax.text.toString()
                )
            } else {
                selectedImageFile?.let { it1 ->
                    dashboardViewModelNew.addProduct(
                        files = it1,
                        productName = binding.productName.text.toString(),
                        productType = selectedProductType,
                        price = binding.productPrice.text.toString(),
                        tax = binding.productTax.text.toString()
                    )
                }
            }
        }
        //----
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.data?.let { uri ->
                        selectedImageFile = convertUriToFile(uri)
                        binding.image.setImageURI(Uri.fromFile(selectedImageFile))
                    }
                }
            }
        //---
        dashboardViewModelNew.productAdded.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    binding.progress.visibility = View.GONE
                    // -----
                    val result: SuccessResponse = it.data as SuccessResponse
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                is NetworkResult.Error -> {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun convertUriToFile(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            requireActivity().contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0]) ?: -1
        val filePath: String? = cursor?.getString(columnIndex)
        cursor?.close()
        return filePath?.let { File(it) }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        imagePickerLauncher.launch(intent)
    }

    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        permissionsLauncher.launch(READ_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == storagePermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            }
        }
    }
}