package com.abedalkareem.games_services

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import com.abedalkareem.games_services.util.PluginError
import com.abedalkareem.games_services.util.errorCode
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.images.ImageManager
import com.google.android.gms.games.Games
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream
import java.util.HashMap

class Player {

  fun getPlayerID(activity: Activity?, result: MethodChannel.Result) {
    activity ?: return
    val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity) ?: return
    Games.getPlayersClient(activity, lastSignedInAccount)
      .currentPlayerId.addOnSuccessListener {
        result.success(it)
      }.addOnFailureListener {
        result.error(PluginError.FailedToGetPlayerId.errorCode(), it.localizedMessage, null)
      }
  }

  fun getPlayerName(activity: Activity?, result: MethodChannel.Result) {
    activity ?: return
    val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity) ?: return
    Games.getPlayersClient(activity, lastSignedInAccount)
      .currentPlayer
      .addOnSuccessListener { player ->
        result.success(player.displayName)
      }.addOnFailureListener {
        result.error(PluginError.FailedToGetPlayerName.errorCode(), it.localizedMessage, null)
      }
  }

  fun getPlayerAvatar(activity: Activity?, result: MethodChannel.Result) {
    activity ?: return
    val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity) ?: return
    Games.getPlayersClient(activity, lastSignedInAccount)
      .currentPlayer
      .addOnSuccessListener { player ->
        readImage(player.hiResImageUri, activity, result)
//        result.success(player.hiResImageUri)
      }.addOnFailureListener {
        result.error(PluginError.FailedToGetPlayerAvatar.errorCode(), it.localizedMessage, null)
      }
  }

  fun readImage(uri: Uri?, activity: Activity?, result: MethodChannel.Result) {
    val data: MutableMap<String, Any?> = HashMap()
    if (uri == null) {
      data["bytes"] = null
      result(data)
    }
    val manager = ImageManager.create(activity!!)
    manager.loadImage({ uri1, drawable, isRequestedDrawable ->
      val bitmap = (drawable as BitmapDrawable?)!!.bitmap
      val stream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
      val bytes = stream.toByteArray()
      data["bytes"] = bytes
      result(data)
    }, uri!!)
  }
}