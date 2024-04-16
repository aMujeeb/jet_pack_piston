package com.mujapps.piston.data

data class UserData(
    var userId: String? = null,
    var name: String? = null,
    var userName: String? = null,
    var imageUrl: String? = null,
    var bio: String? = null,
    var gender: String? = null,
    var genderPreference: String? = null,
    var swipeLeft: List<String> = emptyList(),
    var swipeRight: List<String> = emptyList(),
    var matches: List<String> = emptyList(),
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "userName" to userName,
        "imageUrl" to imageUrl,
        "bio" to bio,
        "gender" to gender,
        "genderPreference" to genderPreference,
        "swipeLeft" to swipeLeft,
        "swipeRight" to swipeRight,
        "matches" to matches
    )
}

data class ChatData(
    var chatId: String? = "",
    var user1: ChatUser = ChatUser(),
    var user2: ChatUser = ChatUser()
)

data class ChatUser(
    var userId: String? = "",
    var name: String? = "",
    var imageUrl: String? = ""
)