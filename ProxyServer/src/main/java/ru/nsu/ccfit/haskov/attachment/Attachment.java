package ru.nsu.ccfit.haskov.attachment;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Attachment {
    static final int BUFFER_SIZE = 8192;

    {
        this.setIn(ByteBuffer.allocate(BUFFER_SIZE));
        this.setState(State.AUTH);
    }

    //Ключ клиента
    SelectionKey key;
    int port;
    State state;
    //Ответ от прокси клиенту на подключение
    ByteBuffer reply;
    //Буфер клиента для сервера
    ByteBuffer in;
    //Буфер сервера для клиента
    ByteBuffer out;
    //Ключ сервера
    SelectionKey destinationKey;
}