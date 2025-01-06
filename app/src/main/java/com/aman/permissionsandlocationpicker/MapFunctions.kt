package com.aman.permissionsandlocationpicker

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class MapFunctions {

    companion object{
        public fun getLocationName(context: Context, coordinate: LatLng): String {
            var address = ""
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)!!
                    .firstOrNull()?.apply {
                        for (index in 0 until maxAddressLineIndex) {
                            address += getAddressLine(index).appendIfNotBlank(", ")
                        }
                        address += featureName.appendIfNotBlank(", ") //known name
                        address += locality.appendIfNotBlank(", ") // city
                        address += adminArea.appendIfNotBlank(", ") //state
                        address += countryName.appendIfNotBlank(", ") //country
                        address += postalCode.appendIfNotBlank(", ")
                    }
                address = address.trim().removeSuffix(",")
                if (address.isNotBlank()) {
                    return address
                } else return context.getString(R.string.address_not_found)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return address
        }

    }
}

