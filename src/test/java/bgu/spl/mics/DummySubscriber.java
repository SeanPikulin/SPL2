package bgu.spl.mics;

import static javafx.scene.input.KeyCode.T;


public class DummySubscriber extends Subscriber {
    private Future future=new Future();
    MessageBroker m=MessageBrokerImpl.getInstance();

    @Override
    protected void initialize() {

    }
    public DummySubscriber(){
        super("Inon");
    }

    public final <T> void sendEvent (Event<T> event){
        future=m.sendEvent(event);
    }

    public<T> Future<T> getFuture() {
        return future;
    }

    public <T> T getResult(Future<T> f){
        return f.result;
    }
}
