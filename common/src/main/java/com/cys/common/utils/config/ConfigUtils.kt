package com.cys.common.utils.config

import com.cys.common.utils.JsonUtils
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import java.util.*

object ConfigUtils {

    private const val TAG = "ConfigUtils"
    private val mmkv by lazy { MMKV.defaultMMKV() }

    fun getString(key: String, default: String = ""): String {
        return mmkv?.decodeString(key, default) ?: return default
    }

    fun getInt(key: String, default: Int = -1): Int {
        return mmkv?.decodeInt(key, default) ?: return default
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return mmkv?.decodeBool(key, default) ?: return default
    }

    fun getLong(key: String, default: Long = 0): Long {
        return mmkv?.decodeLong(key, default) ?: return default
    }

    fun getFloat(key: String, default: Float = 0f): Float {
        return mmkv?.decodeFloat(key, default) ?: return default
    }

    fun getDouble(key: String, default: Double = 0.0): Double {
        return mmkv?.decodeDouble(key, default) ?: return default
    }

    fun getStringSet(key: String, default: Set<String> = setOf()): Set<String> {
        return mmkv?.decodeStringSet(key, default) ?: return default
    }

    fun getIntSet(key: String, default: Set<Int> = setOf()): Set<Int> {
        val stringSet = getStringSet(key)
        if (stringSet.isNotEmpty()) {
            val intSet = linkedSetOf<Int>()
            stringSet.forEach {
                val intValue = it.toIntOrNull()
                intValue?.let { intSet.add(intValue) }
            }
            return intSet
        }
        return default
    }

    fun getStringList(key: String, default: LinkedList<String> = LinkedList()): LinkedList<String> {
        val json = getString(key, "")
        val result = LinkedList<String>()
        if (json.isNotEmpty()) {
            try {
                val linkedObjects = JsonUtils.gson.fromJson<LinkedList<LinkedStringObject>>(
                    json,
                    object : TypeToken<LinkedList<LinkedStringObject>>() {}.type
                )
                linkedObjects.forEach { linkedObject ->
                    result.add(linkedObject.index, linkedObject.value.toString())
                }
                if (result.isNotEmpty()) {
                    return result
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return default
    }

    fun getIntList(key: String, default: LinkedList<Int> = LinkedList()): LinkedList<Int> {
        val json = getString(key, "")
        val result = LinkedList<Int>()
        if (json.isNotEmpty()) {
            try {
                val linkedObjects = JsonUtils.gson.fromJson<LinkedList<LinkedNumberObject>>(
                    json,
                    object : TypeToken<LinkedList<LinkedNumberObject>>() {}.type
                )
                linkedObjects.forEach { linkedObject ->
                    result.add(linkedObject.index, linkedObject.value.toInt())
                }
                if (result.isNotEmpty()) {
                    return result
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return default
    }

    fun set(key: String, value: String): Boolean {
        return mmkv?.encode(key, value) ?: false
    }

    fun set(key: String, value: Int): Boolean {
        return mmkv?.encode(key, value) ?: false
    }

    fun set(key: String, value: Boolean): Boolean {
        return mmkv?.encode(key, value) ?: false
    }

    fun set(key: String, value: Long): Boolean {
        return mmkv?.encode(key, value) ?: false
    }

    fun set(key: String, value: Float): Boolean {
        return mmkv?.encode(key, value) ?: false
    }

    fun set(key: String, value: Double): Boolean {
        return mmkv?.encode(key, value) ?: false
    }

    fun setStringSet(key: String, value: Set<String>): Boolean {
        return mmkv?.encode(key, value) ?: false
    }

    fun setIntSet(key: String, value: Set<Int>): Boolean {
        val stringSet = linkedSetOf<String>()
        value.forEach {
            stringSet.add(it.toString())
        }
        return setStringSet(key, stringSet)
    }

    fun setStringLinkedList(key: String, value: LinkedList<String>): Boolean {
        val objList = LinkedList<LinkedStringObject>()
        value.forEachIndexed { index, s ->
            objList.add(LinkedStringObject(index, s))
        }
        return set(key, JsonUtils.gson.toJson(objList))
    }

    fun setIntLinkedList(key: String, value: LinkedList<Int>): Boolean {
        val objList = LinkedList<LinkedNumberObject>()
        value.forEachIndexed { index, s ->
            objList.add(LinkedNumberObject(index, s))
        }
        return set(key, JsonUtils.gson.toJson(objList))
    }

    data class LinkedStringObject(val index: Int = 0, val value: Any)
    data class LinkedNumberObject(val index: Int = 0, val value: Number)
}