package multiminesweeper.connector;

import multiminesweeper.message.Message;
import multiminesweeper.message.result.ResultMessage;

public class PeerConnector extends AbstractConnector {

    public PeerConnector(String host, int port) {

    }

    @Override
    public String getPartnerName() {
        return null;
    }

    @Override
    public boolean tryFindPartner() {
        return false;
    }

    @Override
    public void waitForPartner(String password) {

    }

    @Override
    public boolean hasPartner() {
        return false;
    }

    @Override
    public void sendChat(String message) {

    }

    @Override
    public boolean hasClosed() {
        return false;
    }

    @Override
    void sendMessage(Message message) {

    }

    @Override
    ResultMessage sendAndWait(Message message) {
        return null;
    }

    @Override
    public void close() {

    }
}
