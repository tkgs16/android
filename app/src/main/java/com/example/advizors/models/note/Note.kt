package com.example.advizors.models.note

import com.example.advizors.Advizors


import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable

data class SerializableLatLng(val latitude: Double, val longitude: Double) : Serializable {
    fun toGoogleLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    companion object {
        fun fromGoogleLatLng(latLng: LatLng): SerializableLatLng {
            return SerializableLatLng(latLng.latitude, latLng.longitude)
        }
    }
}

class LatLngConverter {

    @TypeConverter
    fun fromLatLng(latLng: SerializableLatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    @TypeConverter
    fun toLatLng(latLngString: String): SerializableLatLng {
        val parts = latLngString.split(",")
        val latitude = parts[0].toDouble()
        val longitude = parts[1].toDouble()
        return SerializableLatLng(latitude, longitude)
    }
}

@Entity
data class Note(
    @PrimaryKey
    val id: String,
    val content: String,
    val userId: String,
    @TypeConverters(LatLngConverter::class)
    val position: SerializableLatLng, // Use SerializableLatLng instead of LatLng
    var isDeleted: Boolean = false,
    var imageUrl: String? = null,
    var timestamp: Long? = null,

) : Serializable {

    companion object {
        var lastUpdated: Long
            get() {
                return Advizors.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(NOTE_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                Advizors.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(NOTE_LAST_UPDATED, value)?.apply()
            }

        const val ID_KEY = "id"
        const val USER_ID_KEY = "userId"
        const val LAST_UPDATED_KEY = "timestamp"
        const val CONTENT_KEY = "content"
        const val IS_DELETED_KEY = "is_deleted"
        const val POSITION_KEY = "position"
        private const val NOTE_LAST_UPDATED = "note_last_updated"

        fun fromJSON(json: Map<String, Any>): Note {
            val id = json[ID_KEY] as? String ?: ""
            val content = json[CONTENT_KEY] as? String ?: ""
            val isDeleted = json[IS_DELETED_KEY] as? Boolean ?: false
            val userId = json[USER_ID_KEY] as? String ?: ""
            val positionJson = json[POSITION_KEY] as? Map<String, Double>
            val latitude = positionJson?.get("latitude") ?: 0.0
            val longitude = positionJson?.get("longitude") ?: 0.0
            val position = SerializableLatLng(latitude, longitude)

            val note = Note(id, content, userId, position, isDeleted)

            val timestamp: Timestamp? = json[LAST_UPDATED_KEY] as? Timestamp
            timestamp?.let {
                note.timestamp = it.seconds
            }

            return note
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                USER_ID_KEY to userId,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
                CONTENT_KEY to content,
                IS_DELETED_KEY to isDeleted,
                POSITION_KEY to position
            )
        }

    val deleteJson: Map<String, Any>
        get() {
            return hashMapOf(
                IS_DELETED_KEY to true,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }

    val updateJson: Map<String, Any>
        get() {
            return hashMapOf(
                CONTENT_KEY to content,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }
}