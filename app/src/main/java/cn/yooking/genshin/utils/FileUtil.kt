package cn.yooking.genshin.utils

import android.content.Context
import java.io.*

/**
 * Created by yooking on 2021/9/24.
 * Copyright (c) 2021 yooking. All rights reserved.
 */
class FileUtil {
    companion object {

        /**
         * 获取文件夹路径
         * @see getFileDirs
         */
        fun getFileDirsPath(context: Context): String {
            return getFileDirs(context)?.absolutePath + File.separator
        }

        /**
         * 将[json]数据存储到[fileName]文件中
         * @see getFileDirsPath 文件夹路径
         * @see createFile 创建文件
         * @see writeFile 写入数据
         */
        fun saveAsJson(context: Context, fileName: String, json: String) {
            val filePath =
                getFileDirsPath(context) + fileName

            val file = File(filePath)
            createFile(file)
            writeFile(file, json)
        }

        /**
         * 读取文件名为[fileName]的文件 -> String内容
         * @see getFileDirsPath 文件夹路径
         * @see readFile 读取文件
         */
        fun readJson(context: Context, fileName: String): String {
            val filePath =
                getFileDirsPath(context) + fileName
            val file = File(filePath)
            return readFile(file)
        }

        /**
         * 删除文件夹下文件名称为[fileName]的文件
         * @param fileName 文件名
         */
        fun delete(context: Context, fileName: String): Boolean {
            val filePath = getFileDirsPath(context) + fileName
            val file = File(filePath)
            return file.delete()
        }

        /**
         * 获取本地存储 文件夹内 所有 .json文件
         */
        fun readFileNameList(context: Context): Array<String> {
            return getFileDirs(context)?.list(FilenameFilter { _, name ->
                return@FilenameFilter name.endsWith(".json")
            }) ?: return emptyArray()
        }

        /**
         * 获取文件夹 ->File类型
         * 路径为：Android/data/cn.yooking.genshin/files/
         */
        private fun getFileDirs(context: Context): File? {
            return context.getExternalFilesDir("")
        }

        /**
         * 创建文件
         */
        private fun createFile(file: File): Boolean {
            return try {
                if (!file.parentFile!!.exists()) {
                    file.parentFile!!.mkdirs()
                }
                file.createNewFile()
                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 文件写入
         */
        private fun writeFile(file: File, data: String): Boolean {
            return try {
                val writer = FileWriter(file, false)
                writer.write(data)
                writer.close()
                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 文件读取 -> String
         */
        private fun readFile(file: File): String {
            var ret = ""
            try {
                if (file.exists()) {
                    val inputStream: InputStream = FileInputStream(file)
                    var line: String? = null
                    val reader = BufferedReader(
                        InputStreamReader(
                            FileInputStream(file), "UTF-8"
                        )
                    )
                    //分行读取
                    while (reader.readLine().also { line = it } != null) {
                        ret += (line ?: "")
                    }
                    inputStream.close()
                }
            } catch (e: Exception) {
                ret = ""
            }

            return ret
        }

        fun copyFile(oldPath: String, newPath: String) {
            val oldFile = File(oldPath)
            if (!oldFile.exists() || !oldFile.isFile || !oldFile.canRead()) return
            val newFile = File(newPath)
            if (!newFile.exists()) {
                newFile.parentFile!!.mkdirs()
                newFile.createNewFile()
            }

            val fileInputStream = FileInputStream(oldPath)
            val fileOutputStream = FileOutputStream(newPath)

            val buffer = ByteArray(1024)
            var byteRead: Int
            while (-1 != (fileInputStream.read(buffer).also { byteRead = it })) {
                fileOutputStream.write(buffer, 0, byteRead)
            }
            fileInputStream.close()
            fileOutputStream.flush()
            fileOutputStream.close()
        }
    }


}