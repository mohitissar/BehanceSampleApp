package com.futureworkshops.domain.data.database.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.futureworkshops.data.model.commons.Stats
import com.futureworkshops.data.model.domain.User

@Entity
data class UserEntity constructor(
        @PrimaryKey
        var id: Long,
        var firstname: String,
        var lastname: String,
        var username: String,
        var city: String,
        var state: String,
        var country: String,
        var company: String,
        var occupation: String,
        var createdon: Long,
        var url: String,
        var images: Map<String, String>,
        var displayname: String,
        var fields: List<String>,
        @Embedded
        var stats: Stats
) {

    fun toModel(): User = User(id, firstname, lastname, username, city, state, country,
            company, occupation, createdon, url, images, displayname, fields, stats)

    companion object {

        fun fromModel(user: User) = UserEntity(
                user.id,
                user.firstName,
                user.lastName,
                user.username,
                user.city,
                user.state,
                user.country,
                user.company,
                user.occupation,
                user.createdOn,
                user.url,
                user.images,
                user.displayName,
                user.fields,
                user.stats)
    }
}
