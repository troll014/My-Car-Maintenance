package com.pkndegwa.mycarmaintenance.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.pkndegwa.mycarmaintenance.CarMaintenanceApplication
import com.pkndegwa.mycarmaintenance.R
import com.pkndegwa.mycarmaintenance.data.model.Vehicle
import com.pkndegwa.mycarmaintenance.databinding.FragmentVehicleRegistrationBinding
import com.pkndegwa.mycarmaintenance.ui.VehiclesViewModel
import com.pkndegwa.mycarmaintenance.ui.VehiclesViewModelFactory

/**
 * [VehicleRegistrationFragment] allows a user to add details of a vehicle to be registered.
 */
class VehicleRegistrationFragment : Fragment() {
    private var _binding: FragmentVehicleRegistrationBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val navigationArgs: VehicleDetailsFragmentArgs by navArgs()

    private val viewModel: VehiclesViewModel by activityViewModels {
        VehiclesViewModelFactory((activity?.application as CarMaintenanceApplication).database.vehicleDao())
    }
    private lateinit var vehicle: Vehicle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Retrieve and inflate the layout for this fragment.
        _binding = FragmentVehicleRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vehicleId = navigationArgs.vehicleId
        if (vehicleId > 0) {
            viewModel.retrieveVehicle(vehicleId)?.observe(this.viewLifecycleOwner) { selectedVehicle ->
                vehicle = selectedVehicle
                bind(vehicle)
            }
        } else {
            // Setup a click listener for the Save button.
            binding.saveVehicleButton.setOnClickListener {
                addNewVehicle()
            }
        }

        binding.apply {
            // Setup a click listener for the Vehicle type EditText to show a menu.
            vehicleTypeEditText.setOnClickListener {
                showMenu(binding.vehicleTypeEditText, R.menu.popup_menu_vehicle_type)
            }

            // Setup a click listener for the Fuel type EditText to show a menu.
            vehicleFuelTypeEditText.setOnClickListener {
                showMenu(binding.vehicleFuelTypeEditText, R.menu.popup_menu_fuel_type)
            }

            // Setup a click listener for the Cancel button.
            cancelRegisterButton.setOnClickListener {
                cancelRegistration()
            }
        }
    }

    /**
     * Shows a menu to select a certain property of a vehicle.
     */
    private fun showMenu(view: EditText, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            view.setText(menuItem.title)
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    /**
     * Checks if the text input fields have been filled.
     */
    private fun isEntryValid(view: TextInputLayout): Boolean {
        return if (!viewModel.isEntryValid(view.editText?.text.toString())) {
            setError(view)
            removeError(view)
            false
        } else {
            true
        }
    }

    /**
     * Sets the text field error status.
     */
    private fun setError(view: TextInputLayout) {
        view.isErrorEnabled = true
        view.error = "Fill in this field."
    }

    /**
     * Removes the text field error stats.
     */
    private fun removeError(view: TextInputLayout) {
        view.editText?.doOnTextChanged { _, _, _, _ ->
            view.isErrorEnabled = false
            view.error = null
        }
    }

    /**
     * Validates user input before adding the new vehicle in the database using the ViewModel.
     */
    private fun addNewVehicle() {
        if (isEntryValid(binding.vehicleType) &&
            isEntryValid(binding.vehicleManufacturer) &&
            isEntryValid(binding.vehicleModel) &&
            isEntryValid(binding.vehicleLicensePlate) &&
            isEntryValid(binding.vehicleFuelType) &&
            isEntryValid(binding.vehicleMileage)
        ) {
            viewModel.addNewVehicle(
                vehicleType = binding.vehicleTypeEditText.text.toString(),
                vehicleManufacturer = binding.vehicleManufacturerEditText.text.toString(),
                vehicleModel = binding.vehicleModelEditText.text.toString(),
                vehicleLicensePlate = binding.vehicleLicensePlateEditText.text.toString(),
                vehicleFuelType = binding.vehicleFuelTypeEditText.text.toString(),
                vehicleMileage = binding.vehicleMileageEditText.text.toString()
            )
            Toast.makeText(this.context, "Vehicle saved successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_vehicleRegistrationFragment_to_homeFragment)
        }
    }

    /**
     * Cancels the registration.
     */
    private fun cancelRegistration() {
        clearText()
        findNavController().navigate(R.id.action_vehicleRegistrationFragment_to_homeFragment)
    }

    /**
     * Clear the text fields.
     */
    private fun clearText() {
        binding.apply {
            vehicleTypeEditText.text = null
            vehicleManufacturerEditText.text = null
            vehicleModelEditText.text = null
            vehicleLicensePlateEditText.text = null
            vehicleFuelTypeEditText.text = null
            vehicleMileageEditText.text = null
        }
    }

    /**
     * Binds the vehicle data to the TextViews when the Edit menu option has been selected in the
     * VehicleDetailsFragment.
     */
    private fun bind(vehicle: Vehicle) {
        binding.apply {
            vehicleTypeEditText.setText(vehicle.type, TextView.BufferType.SPANNABLE)
            vehicleManufacturerEditText.setText(vehicle.manufacturer, TextView.BufferType.SPANNABLE)
            vehicleModelEditText.setText(vehicle.model, TextView.BufferType.SPANNABLE)
            vehicleLicensePlateEditText.setText(vehicle.licensePlate, TextView.BufferType.SPANNABLE)
            vehicleFuelTypeEditText.setText(vehicle.fuelType, TextView.BufferType.SPANNABLE)
            vehicleMileageEditText.setText(vehicle.mileage.toString(), TextView.BufferType.SPANNABLE)

            saveVehicleButton.setOnClickListener { updateVehicle() }
        }
    }

    /**
     * Validates user input before updating the vehicle details in the database using the ViewModel.
     */
    private fun updateVehicle() {
        if (isEntryValid(binding.vehicleType) &&
            isEntryValid(binding.vehicleManufacturer) &&
            isEntryValid(binding.vehicleModel) &&
            isEntryValid(binding.vehicleLicensePlate) &&
            isEntryValid(binding.vehicleFuelType) &&
            isEntryValid(binding.vehicleMileage)
        ) {
            viewModel.updateVehicle(
                vehicleId = this.navigationArgs.vehicleId,
                vehicleType = this.binding.vehicleTypeEditText.text.toString(),
                vehicleManufacturer = this.binding.vehicleManufacturerEditText.text.toString(),
                vehicleModel = this.binding.vehicleModelEditText.text.toString(),
                vehicleLicensePlate = this.binding.vehicleLicensePlateEditText.text.toString(),
                vehicleFuelType = this.binding.vehicleFuelTypeEditText.text.toString(),
                vehicleMileage = this.binding.vehicleMileageEditText.text.toString()
            )
            Toast.makeText(this.context, "Vehicle updated successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_vehicleRegistrationFragment_to_homeFragment)
        }
    }

    /**
     * Frees the binding object when the Fragment is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}