package fis.telegrams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by spiollinux on 07.11.15.
 */
@Service
@ConfigurationProperties(prefix = "telegramserver") //setting hostname, port from application.yml
public class TelegramParser extends Thread {

    @Autowired
    TelegramParserConfig parserConfig;
    private String hostname;
    private int port;
    private Socket server;
    private ConnectionStatus connectionStatus;
    private List<byte[]> telegramQueue;

    public TelegramParser() {
        this.telegramQueue = new LinkedList<>();
        this.connectionStatus = ConnectionStatus.OFFLINE;
    }

    @Override
    public void start() {
        setDaemon(true);
        super.start();
    }

    @Override
    public void run() {
        //try to connect until there is a connection
        //Todo: reconnecting after connection loss
        while (this.connectionStatus != ConnectionStatus.ONLINE) {
            try {
                this.server = connectToHost();
            } catch (IOException e) {
                this.connectionStatus = ConnectionStatus.OFFLINE;
                //TODO: Log connection fail
            }
            try {
                Thread.sleep(parserConfig.getTimeTillReconnect());
            } catch (InterruptedException e) {
                //Todo: Is handling the exception necessary?
            }
        }
        try {
            handleConnection();
        } catch (IOException e) {
            //Todo: handle error
            e.printStackTrace();
        }
        finally {
            try {
                server.close();
                connectionStatus = ConnectionStatus.OFFLINE;
            }
            catch (IOException e) {
                //Todo: handle error
                e.printStackTrace();
            }
        }
    }

    private void handleConnection() throws IOException {
        InputStream in = server.getInputStream();
        while (connectionStatus == ConnectionStatus.ONLINE) {
            Future<byte[]> currentTelegram = parseConnection(in);
            while (!currentTelegram.isDone()) {
                //Parse received telegrams while waiting for next
                if (!telegramQueue.isEmpty()) {
                    parseTelegram(telegramQueue.get(0));
                }
                else
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
            try {
                telegramQueue.add(currentTelegram.get());
            } catch (InterruptedException e) {
                //Todo: handle
                e.printStackTrace();
            } catch (ExecutionException e) {
                //Todo: handle
                e.printStackTrace();
            }
        }
    }

    @Async
    private Future<byte[]> parseConnection(InputStream in) throws IOException {
        int readPos = 0, maxResponseLength = 255;
        byte[] response = new byte[maxResponseLength];
        while (readPos < maxResponseLength) {
            int responseLength = in.read(response, readPos, maxResponseLength - readPos);
            readPos += responseLength;
        }
        //packet read to response
        parseTelegram(response);
        return new AsyncResult<>(response);
    }

    private void parseTelegram(byte[] response) {
        //Todo: add real parser logic
        for (int i = 0; i < 3; ++i) {
            if (response[i] != (byte) 0xFF) {
                throw (new RuntimeException("Byte " + i + " hat falsches Format: " + response[i]));
            }
        }
        int messageLength = response[3];
        for (int i = 4; i < 3 + messageLength; ++i) {
            if (i == 4) {
                //Typangabe
            }
            System.out.println("Byte " + i + ": " + response[5]);
        }
    }

    public Socket connectToHost() throws IOException {
        connectionStatus = ConnectionStatus.CONNECTING;
        SocketAddress hostAddress = new InetSocketAddress(parserConfig.getHostname(), parserConfig.getPort());
        Socket socket = new Socket();
        socket.connect(hostAddress, parserConfig.getTimeout());
        this.connectionStatus = ConnectionStatus.ONLINE;
        return socket;
    }
}
