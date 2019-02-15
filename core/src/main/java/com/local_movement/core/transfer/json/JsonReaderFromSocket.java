package com.local_movement.core.transfer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_movement.core.AppProperties;
import com.local_movement.core.ui.UIConnector;
import com.local_movement.core.transfer.json.json_entity.AbstractJson;
import com.local_movement.core.transfer.json.json_entity.FileProperties;
import com.local_movement.core.transfer.json.json_entity.JsonType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class JsonReaderFromSocket {

    public JsonReaderFromSocket() {
    }

    public static void readJson(SocketChannel clientSocket)
            throws IOException {
        byte[] bytes = readBytes(clientSocket);


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonType =
                objectMapper.readTree(bytes)
                        .get(AbstractJson.TYPE_FIELD_NAME).asText();
        if (jsonType.equals(JsonType.FILE_PROPERTIES)) {
            FileProperties fileProperties = objectMapper.readValue(bytes, FileProperties.class);
            UIConnector.UI_TRANSFER().acceptFile(fileProperties, clientSocket);

        } else if (jsonType.equals(JsonType.SYSTEM_COMMAND)) {
            //todo system command
        }
    }


    public static byte[] readBytes(SocketChannel clientSocket) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(AppProperties.getJsonBufferSize());
        int byteAmount = 0;
        byte[] bytes = new byte[0];
        while ((byteAmount = clientSocket.read(buffer)) > 0) {
            byte[] tempBytes = Arrays.copyOf(buffer.array(), byteAmount);
            int beforeBitesLength = bytes.length;
            int afterBitesLength = bytes.length + tempBytes.length;
            bytes = Arrays.copyOf(bytes, afterBitesLength);
            System.arraycopy(tempBytes, 0, bytes, beforeBitesLength, tempBytes.length);
            buffer.clear();
        }
        return bytes;
    }
}
