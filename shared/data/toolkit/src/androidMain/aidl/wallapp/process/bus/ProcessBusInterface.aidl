package wallapp.process.bus;


interface ProcessBusInterface {

    void send(String command, String data);
}