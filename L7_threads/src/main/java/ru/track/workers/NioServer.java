package ru.track.workers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 *
 */
public class NioServer {

    private Selector selector;

    public NioServer(int port) throws IOException {
        // Создаем неблокирующий channel и привязываем его к порту сервера
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));

        // Мультиплексор для channel'a
        selector = Selector.open();

        // Селектор будет отлавливать события определенного типа на этом channel
        // При инициализации нужно отлавливать события - "Подключение клиента"
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void loop() {
        while (true) {
            try {
                // Ждем событий на channel
                selector.select();

                // Может прийти несколько событий одновременно, для каждого события сохраняется ключ
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    // Получаем и сразу удаляем ключ - мы его обработали
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();

                    // В зависимости от события вызываем обработчик
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        // Если пришел accept, значит используется ServerSocketChannel
        // на accept() можно получить канал к клиенту (по аналогии с Socket)
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        // Регистрируем на селекторе новый канал, сервер пассивный - ждет, когда клиент напишет
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(32);

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(buffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.channel().close();
            key.cancel();
            return;
        }
        buffer.flip();
        byte[] data = new byte[numRead];
        buffer.get(data);


        System.out.println("on read: " + new String(data));
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = null;
        socketChannel.write(buffer);

        key.interestOps(SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        NioServer server = new NioServer(9000);
        server.loop();
    }
}
