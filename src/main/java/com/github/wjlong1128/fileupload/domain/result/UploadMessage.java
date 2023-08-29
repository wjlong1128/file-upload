package com.github.wjlong1128.fileupload.domain.result;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/29
 * @desc desc.
 */
public interface UploadMessage {

    public static enum Big implements ResultMessage {
        UNABLE_GET_BUCKET(20001, "无法判断该文件存放的位置"),
        UNABLE_UPLOAD_FILE(20002, "上传该文件时发生错误"),
        UNABLE_READ_FILE(20003, "读取该文件失败"),
        CHUNK_NOT_ALL(20004, "该文件不完整，无法合并"),
        CHUNK_MERGE_FAIL(20005, "文件合并失败"),
        UNABLE_CHECK_FILE(20005, "校验该文件失败");
        private int code;
        private String message;

        Big(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
    public static enum Normal implements ResultMessage {
        UNABLE_GET_BUCKET(10001, "无法判断该文件存放的位置"),
        UNABLE_UPLOAD_FILE(10002, "文件上传失败"),
        UNABLE_READ_FILE(10003, "读取该文件失败"),

        UNABLE_GET_FILE(10004, "文件下载失败"),
        FILE_NOT_EXISTS(10005,"该文件不存在" ),
        FILE_DELETE_FAIL(10006,"文件删除失败" );
        private int code;
        private String message;

        Normal(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
