package bgu.spl.mics;


public class DummySubscriber extends Subscriber {
    private Future<Integer> future=new Future<>();
    MessageBroker m=MessageBrokerImpl.getInstance();

    @Override
    protected void initialize() {

    }
    public DummySubscriber(){
        super("Inon");
    }

    public final void sendEvent (Event<Integer> event){
        future=m.sendEvent(event);
    }

    public Future<Integer> getFuture() {
        return future;
    }

    public Integer getResult() { return this.future.result; }
}
